package me.benetis.coordinator.downloader

import com.softwaremill.sttp._
import com.typesafe.scalalogging.LazyLogging
import me.benetis.coordinator.repository.{AgendaQuestionRepo, DiscussionEventRepo, PlenaryRepo}
import me.benetis.shared._
import scala.xml._
import cats._
import me.benetis.coordinator.utils.dates.{DateUtils, SharedDateDecoders}
import me.benetis.shared.encoding.EncodersDecoders
import scala.collection.immutable
import scala.util.Try

object AgendaQuestionDownloader extends LazyLogging {
  def fetchAndSave(plenaries: List[Plenary]) = {
    plenaries.map(
      plenary =>
        fetchLogIfErrorAndSaveWithSleep(
          AgendaQuestionRepo.insert,
          () => fetch(plenary)
      ))

  }

  private def fetch(plenary: Plenary)
    : Either[FileOrConnectivityError,
             Seq[Either[DomainValidation, AgendaQuestion]]] = {

    val posedzio_id = plenary.id.plenary_id

    val uri =
      uri"http://apps.lrs.lt/sip/p2b.ad_seimo_posedzio_darbotvarke?posedzio_id=$posedzio_id"

    val request          = sttp.get(uri)
    implicit val backend = HttpURLConnectionBackend()

    val response = request.send().body

    response match {
      case Right(body) =>
        val xmlEith = Try(scala.xml.XML.loadString(body)).toEither

        xmlEith match {
          case Right(xml: Elem) =>
            Right(parse(xml, plenary))
          case Left(err) => Left(BadXML(uri.toString(), err.getMessage))
        }
      case Left(err) => Left(CannotReachWebsite(uri.toString(), err))
    }
  }

  private def parse(
      body: Elem,
      plenary: Plenary): Seq[Either[DomainValidation, AgendaQuestion]] = {
    val agendaQuestions = body \\ "SeimoInformacija" \\ "SeimoPosėdis" \\ "DarbotvarkėsKlausimas"

    val result: Seq[Either[DomainValidation, Seq[AgendaQuestion]]] =
      agendaQuestions.map((questionNode: Node) =>
        validate(questionNode, plenary))

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

  private def validate(
      node: Node,
      plenary: Plenary): Either[DomainValidation, Seq[AgendaQuestion]] = {

    val questionStatuses = node \\ "KlausimoStadija"
    val questionSpeakers = node \\ "KlausimoPranešėjas"

    val result = questionStatuses.map((questionStatusNode: Node) => {

      val speakersEith: Seq[Either[DomainValidation, String]] =
        if (questionSpeakers.isEmpty)
          Seq.empty[Either[DomainValidation, String]]
        else
          questionSpeakers
            .map((speakerNode: Node) => {
              for {
                person <- speakerNode.validateNonEmpty("asmuo")
              } yield person
            })

      plenary.timeStart match {
        case Some(plenaryStart) =>
          for {
            number   <- node.validateNonEmpty("numeris")
            title    <- node.validateNonEmpty("pavadinimas")
            timeFrom <- node.validateTimeOrEmpty("laikas_nuo")
            timeTo   <- node.validateTimeOrEmpty("laikas_iki")

            statusRawV <- Right(questionStatusNode.tagText("pavadinimas"))
            status     <- Right(questionStatusNode.stringOrNone("pavadinimas"))
            questionId <- questionStatusNode.validateInt(
              "darbotvarkės_klausimo_id")
            docLink <- Right(
              questionStatusNode.stringOrNone("dokumento_nuoroda"))
            speakers <- sequence(speakersEith)

          } yield
            AgendaQuestion(
              id = AgendaQuestionId(questionId),
              groupId =
                AgendaQuestionGroupId(s"${plenary.id.plenary_id}/$number"),
              title = AgendaQuestionTitle(title),
              timeFrom = timeFrom.map(AgendaQuestionTimeFrom),
              timeTo = timeTo.map(AgendaQuestionTimeTo),
              dateTimeFrom = timeFrom
                .map(t =>
                  DateUtils.timeWithDateToDateTime(t, plenaryStart.time_start))
                .map(AgendaQuestionDateTimeFrom),
              dateTimeTo = timeTo
                .map(t =>
                  DateUtils.timeWithDateToDateTime(t, plenaryStart.time_start))
                .map(AgendaQuestionDateTimeTo),
              date = SharedDateDecoders.sharedDTToDateOnly(plenaryStart.time_start),
              statusRaw = AgendaQuestionStatusRaw(statusRawV),
              status = status.map(EncodersDecoders.agendaQuestionStatus),
              documentLink = docLink.map(AgendaQuestionDocumentLink),
              speakers = AgendaQuestionSpeakers(speakers.toVector),
              number = AgendaQuestionNumber(number),
              plenaryId = plenary.id
            )
        case None => Left(PlenaryShouldBeStarted(plenary.id))
      }
    })

    sequence(result)
  }

  def sequence[A, B](s: Seq[Either[A, B]]): Either[A, Seq[B]] =
    s.foldRight(Right(Nil): Either[A, List[B]]) { (e, acc) =>
      for (xs <- acc.right; x <- e.right) yield x :: xs
    }

}
