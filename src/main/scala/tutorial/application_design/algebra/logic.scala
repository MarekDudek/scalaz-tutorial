package tutorial.application_design.algebra

import java.time.Instant

import scalaz.NonEmptyList

final case class WorldView
(
  backlog: Int,
  agents: Int,
  managed: NonEmptyList[MachineNode],
  alive: Map[MachineNode, Instant],
  pending: Map[MachineNode, Instant],
  time: Instant
)