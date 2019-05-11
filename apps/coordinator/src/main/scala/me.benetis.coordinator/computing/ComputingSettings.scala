package me.benetis.coordinator.computing

sealed trait ComputingSettings

case object ComputeMDS    extends ComputingSettings
case object ComputeKMeans extends ComputingSettings
case object ComputeMultiFactionsList
    extends ComputingSettings
