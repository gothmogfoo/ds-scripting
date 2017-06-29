package dsscript

import scala.language.implicitConversions

trait DarkSoulsScript extends Areas with App {
  def when(condition: Condition): Instruction =
    condition.inRegister(Register.Main)

  def when(condition: CompoundCondition)(
      implicit
      register: Register = condition.defaultRegister): Instruction = {
    condition
      .inRegister(register)
      .andThen(CopyIfTrue(register, Register.Main))
  }

  def playerOwnsAnyOf(items: Seq[ItemId]) = new DisjunctCondition(
    for {
      item <- items
    } yield Condition.PlayerOwns(ItemType.Item, item, true)
  )

  def removeAll(items: IndexedSeq[ItemId]) = new CompoundInstruction(
    for {
      item <- items
    } yield RemoveItem(ItemType.Item, item, 0)
  )

  def events: Map[Area, Seq[Event]]

  private def header(text: String) = "-" * 20 + f"8<--$text%20s--8<" + "-" * 20

  for {
    (area, evts) <- events
  } {
    println(s"\nModify ${area.id}.unpack.txt: ")
    val (replacedEvents, newEvents) = evts.partition(_.replace)
    if (newEvents.nonEmpty) {
      println(header("Add to constructor "))
      println(newEvents.map(_.construction).mkString("\n"))
      println(header("Add to bottom    "))
      println(newEvents.map(_.compiled).mkString("\n\n"))
    }
    if (replacedEvents.nonEmpty) {
      println(header("Replace these events"))
      println(replacedEvents.map(_.compiled).mkString("\n\n"))
    }
    println(header("-" * 20))
  }
}
