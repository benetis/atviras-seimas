package me.benetis.shared

case class InitiatedLegislationIntervalFrom(date: DateTimeOnlyDate)
case class InitiatedLegislationIntervalTo(date: DateTimeOnlyDate)
case class InitiatedLegislationTotal(count_total: Int)
case class InitiatedLegislationIndividual(count_individual: Int)
case class InitiatedLegislationGroup(count_group: Int)

case class InitiatedLegislation(
    personId: PersonId,
    personName: PersonName,
    personSurname: PersonSurname,
    legislationIntervalFrom: InitiatedLegislationIntervalFrom,
    legislationIntervalTo: InitiatedLegislationIntervalTo,
    countTotal: InitiatedLegislationTotal,
    countIndividual: InitiatedLegislationIndividual,
    countGroup: InitiatedLegislationGroup,
)
