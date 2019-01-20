package me.benetis.coordinator.downloader

import com.softwaremill.sttp._
import com.typesafe.scalalogging.LazyLogging
import me.benetis.shared.Repository.{PlenaryQuestionRepo, PlenaryRepo}
import me.benetis.shared._
import scala.xml._
import cats._
import scala.collection.immutable

object PlenaryQuestionDownloader extends LazyLogging {
  def fetchAndSave() = {
    fetchLogIfErrorAndSave(PlenaryQuestionRepo.insert,
                           () => fetch(PlenaryId(-501109)))
  }

  private def fetch(plenaryId: PlenaryId)
    : Either[String, Seq[Either[DomainValidation, PlenaryQuestion]]] = {

    val posedzio_id = plenaryId.plenary_id

    val request =
      sttp.get(
        uri"http://apps.lrs.lt/sip/p2b.ad_seimo_posedzio_darbotvarke?posedzio_id=$posedzio_id")

    implicit val backend = HttpURLConnectionBackend()

    val response = request.send().body

    response match {
      case Right(body) =>
        Right(parse(scala.xml.XML.loadString(body), plenaryId))
      case Left(err) => Left(err)
    }
  }

  private def parse(
      body: Elem,
      plenaryId: PlenaryId): Seq[Either[DomainValidation, PlenaryQuestion]] = {
    val agendaQuestions = body \\ "SeimoInformacija" \\ "SeimoPosėdis" \\ "DarbotvarkėsKlausimas"

    val result: Seq[Either[DomainValidation, Seq[PlenaryQuestion]]] =
      agendaQuestions.map((questionNode: Node) =>
        validate(questionNode, plenaryId))

    val rightSeq = result
      .collect {
        case Right(seq) => seq
      }
      .flatten
      .map(Right(_))

    val leftSeq = result.collect {
      case Left(err) => Left(err)
    }

    rightSeq ++ leftSeq

  }

  private def statusDecoder(status: String): PlenaryQuestionStatus = {
    status match {
      case "Tvirtinimas" => Affirmation
      case "Priėmimas"   => Adtoption
      case "Svarstymas"  => Discussion
      case "Pateikimas"  => Presentation
      case _ =>
        logger.error("Not supported status")
        Presentation
    }
  }

  private def validate(
      node: Node,
      plenaryId: PlenaryId): Either[DomainValidation, Seq[PlenaryQuestion]] = {

    val questionStatuses = node \\ "KlausimoStadija"
    val questionSpeakers = node \\ "KlausimoPranešėjas"

    val result = questionStatuses.map((questionStatusNode: Node) => {

      val speakersEith: Seq[Either[DomainValidation, String]] =
        questionSpeakers
          .map((speakerNode: Node) => {
            for {
              person <- speakerNode.validateNonEmpty("asmuo")
            } yield person
          })

      for {
        number   <- node.validateNonEmpty("numeris")
        title    <- node.validateNonEmpty("pavadinimas")
        timeFrom <- node.validateTimeOrEmpty("laikas_nuo")
        timeTo   <- node.validateTimeOrEmpty("laikas_iki")

        status     <- questionStatusNode.validateNonEmpty("pavadinimas")
        questionId <- questionStatusNode.validateInt("darbotvarkės_klausimo_id")
        docLink    <- questionStatusNode.validateNonEmpty("dokumento_nuoroda")
        speakers   <- sequence(speakersEith)

      } yield
        PlenaryQuestion(
          PlenaryQuestionId(questionId),
          PlenaryQuestionGroupId(s"${plenaryId.plenary_id}/$number"),
          PlenaryQuestionTitle(title),
          timeFrom.map(PlenaryQuestionTimeFrom),
          timeTo.map(PlenaryQuestionTimeTo),
          statusDecoder(status),
          PlenaryQuestionDocumentLink(docLink),
          PlenaryQuestionSpeakers(speakers.toVector),
          PlenaryQuestionNumber(number)
        )
    })

    sequence(result)
  }

  def sequence[A, B](s: Seq[Either[A, B]]): Either[A, Seq[B]] =
    s.foldRight(Right(Nil): Either[A, List[B]]) { (e, acc) =>
      for (xs <- acc.right; x <- e.right) yield x :: xs
    }

}
