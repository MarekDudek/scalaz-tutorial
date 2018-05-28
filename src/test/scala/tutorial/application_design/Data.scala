package tutorial.application_design

import java.time.Instant

import scalaz.NonEmptyList
import scalaz.Scalaz._
import tutorial.application_design.algebra.{Drone, MachineNode, WorldView, _}

object Data {

  val node1 = MachineNode("1243d1af-828f-4ba3-9fc0-a19d86852b5a")
  val node2 = MachineNode("550c4943-229e-47b0-b6be-3d686c5f013f")
  val managed = NonEmptyList(node1, node2)

  import java.time.Instant.parse

  val time1: Instant = parse("2017-03-03T18:07:00.00Z")
  val time2: Instant = parse("2017-03-03T18:59:00.00Z")
  val time3: Instant = parse("2017-03-03T19:06:00.00Z")
  val time4: Instant = parse("2017-03-03T23:07:00.00Z")

  val needsAgents = WorldView(5, 0, managed, Map.empty, Map.empty, time1)
}

class Mutable(state: WorldView) {

  var started, stopped: Int = 0

  implicit val drone: Drone[Id] = new Drone[Id] {

    def getBacklog: Int = state.backlog

    def getAgents: Int = state.agents
  }

  implicit val machines: Machines[Id] = new Machines[Id] {

    def getTime: Instant =
      state.time

    def getManaged: NonEmptyList[MachineNode] = state.managed

    def getAlive: Map[MachineNode, Instant] = state.alive

    def start(node: MachineNode): MachineNode = {
      started += 1
      node
    }

    def stop(node: MachineNode): MachineNode = {
      stopped += 1
      node
    }
  }

  val program = new DynAgents[Id]
}