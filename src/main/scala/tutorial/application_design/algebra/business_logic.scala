package tutorial.application_design.algebra

import java.time.Instant
import java.time.temporal.ChronoUnit

import scalaz.Scalaz._
import scalaz.{Monad, _}

import scala.concurrent.duration.{FiniteDuration, _}
import scala.language.higherKinds

final class DynAgents[F[_]]
(
  implicit M: Monad[F],
  d: Drone[F],
  m: Machines[F]
) {

  def initial: F[WorldView] =
    for {
      db <- d.getBacklog
      da <- d.getAgents
      mm <- m.getManaged
      ma <- m.getAlive
      mt <- m.getTime
    } yield WorldView(db, da, mm, ma, Map.empty, mt)

  def initial2: F[WorldView] =
    (d.getBacklog |@| d.getAgents |@| m.getManaged |@| m.getAlive |@| m.getTime) {
      case (db, da, mm, ma, mt) => WorldView(db, da, mm, ma, Map.empty, mt)
    }

  def update(old: WorldView): F[WorldView] =
    for {
      snap <- initial
      changed = symdiff(old.alive.keySet, snap.alive.keySet)
      pending = (old.pending -- changed).filterNot {
        case (_, started) => timediff(started, snap.time) >= 10.minutes
      }
      update = snap.copy(pending = pending)
    } yield update

  def act(world: WorldView): F[WorldView] =
    world match {
      case NeedsAgent(node) =>
        for {
          _ <- m.start(node)
          update = world.copy(pending = Map(node -> world.time))
        } yield update
      case Stale(nodes) =>
        nodes.foldLeftM(world) {
          (world, n) =>
            for {
              _ <- m.stop(n)
              update = world.copy(pending = world.pending + (n -> world.time))
            } yield update
        }
      case _ => world.pure[F]
    }

  def act2(world: WorldView): F[WorldView] =
    world match {
      case NeedsAgent(node) =>
        for {
          _ <- m.start(node)
          update = world.copy(pending = Map(node -> world.time))
        } yield update
      case Stale(nodes) =>
        for {
          stopped <- nodes.traverse(m.stop)
          updates = stopped.map(_ -> world.time).toList.toMap
          update = world.copy(pending = world.pending ++ updates)
        } yield update
      case _ => world.pure[F]
    }

  private def symdiff[T](a: Set[T], b: Set[T]): Set[T] =
    (a union b) -- (a intersect b)

  private def timediff(from: Instant, to: Instant): FiniteDuration =
    ChronoUnit.MINUTES.between(from, to).minutes

  private object NeedsAgent {
    def unapply(world: WorldView): Option[MachineNode] =
      world match {
        case WorldView(backlog, 0, managed, alive, pending, _)
          if backlog > 0 && alive.isEmpty && pending.isEmpty
        => Option(managed.head)
        case _ => None
      }
  }

  private object Stale {
    def unapply(world: WorldView): Option[NonEmptyList[MachineNode]] =
      world match {
        case WorldView(backlog, _, _, alive, pending, time)
          if alive.nonEmpty =>
          (alive -- pending.keys).collect {
            case (n, started)
              if backlog == 0 && timediff(started, time).toMinutes % 60 >= 58 =>
              n
            case (n, started)
              if timediff(started, time) >= 5.hours =>
              n
          }.toList.toNel
        case _ => None
      }
  }

}



