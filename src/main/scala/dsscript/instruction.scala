package dsscript

trait Instruction {
  def replacements: Seq[Replacement]

  def compiled: String
}

class SimpleInstruction(cls: Int,
                        index: Int,
                        arguments: Seq[Argument],
                        val replacements: Seq[Replacement] = Nil)
    extends Instruction {
  def compiled: String = {
    val (types, args) = arguments.map(a => a.typeFlag -> a.value).unzip
    val typeString = types.mkString("(", "", ")")
    val argString = args.mkString("[", ", ", "]")
    val subString =
      if (replacements.isEmpty) ""
      else
        replacements.map(_.compiled).mkString("\n", "\n", "")
    f"$cls%5s[$index%02d] $typeString$argString$subString"
  }

  def replacing(replacements: Replacement*): Instruction =
    new SimpleInstruction(cls,
                          index,
                          arguments,
                          this.replacements ++ replacements)
}

case class Replacement(replace: Int, start: Int, length: Int) {
  def compiled = s"    ^($replace <- $start, $length)"

}

class CompoundInstruction(components: Seq[Instruction]) extends Instruction {
  def andThen(instruction: Instruction): CompoundInstruction =
    new CompoundInstruction(components ++ Seq(instruction))

  def compiled: String = components.map(_.compiled).mkString("\n")

  def replacements = components.flatMap(_.replacements)
}

object CompoundInstruction {
  def apply(instructions: Instruction*): CompoundInstruction =
    new CompoundInstruction(instructions)
}

case class CopyIfTrue(from: Register, to: Register)
    extends SimpleInstruction(0, 0, Seq(to, true, from))

class Wait(typ: Int, args: Seq[Argument])
    extends SimpleInstruction(1001, typ, args)

object Wait {
  case class Seconds(v: Double) extends Wait(0, Seq(v))
  case class Frames(v: Int) extends Wait(1, Seq(v))
  case class RandomSeconds(min: Double, max: Double)
      extends Wait(2, Seq(min, max))
  // I guess?
  case class RandomFrames(min: Int, max: Int) extends Wait(3, Seq(min, max))
}

case class RequestAnimation(entity: EntityId,
                            animation: Animation,
                            loop: Boolean,
                            waitForCompletion: Boolean)
    extends SimpleInstruction(2003,
                              1,
                              Seq(entity, animation, loop, waitForCompletion))

case class ForceAnimation(entity: EntityId,
                          animation: Animation,
                          loop: Boolean = false,
                          waitForCompletion: Boolean = true,
                          skipTransition: Boolean = false)
    extends SimpleInstruction(
      2003,
      18,
      Seq(entity, animation, loop, waitForCompletion, skipTransition))

// itemType.value forces compilation as IntArgument
case class RemoveItem(itemType: ItemType, itemId: ItemId, quantity: Int)
    extends SimpleInstruction(2003, 24, Seq(itemType.value, itemId, quantity))

case class SetSpecialEffect(entity: EntityId, specialEffect: SpecialEffect)
    extends SimpleInstruction(2004, 8, Seq(entity, specialEffect))

case class CancelSpecialEffect(entity: EntityId, specialEffect: SpecialEffect)
    extends SimpleInstruction(2004, 21, Seq(entity, specialEffect))

class End(restart: Boolean) extends SimpleInstruction(1000, 4, Seq(restart))
object Restart extends End(true)
object End extends End(false)

case class SetEventFlag(eventId: EventId, value: Boolean)
    extends SimpleInstruction(2003, 2, Seq(eventId, value))

case class BatchSetEventFlags(from: EventId, to: EventId, value: Boolean)
    extends SimpleInstruction(2003, 22, Seq(from, to, value))

case class DisplayStatusMessage(messageId: MessageId, enablePad: Boolean)
    extends SimpleInstruction(2007, 3, Seq(messageId, enablePad))

case class SkipIfEventId(skipLines: Int, eventId: EventId, value: Boolean)
    extends SimpleInstruction(1003,
                              1,
                              Seq(
                                new UnsignedByteArgument(skipLines),
                                value,
                                FlagType.EventId,
                                eventId
                              ))

case class SkipIfEventFlag(skipLines: Int, eventId: EventId, value: Boolean)
    extends SimpleInstruction(1003,
                              1,
                              Seq(
                                new UnsignedByteArgument(skipLines),
                                value,
                                FlagType.EventFlag,
                                eventId
                              ))

case class SkipIfRegister(skipLines: Int, register: Register, value: Boolean)
    extends SimpleInstruction(1000, 1, Seq(new UnsignedByteArgument(skipLines), value, register))

case class DisableEntity(entity: EntityId)
    extends SimpleInstruction(2004, 5, Seq(entity, false))

case class DisableObject(objectId: ObjectId)
    extends SimpleInstruction(2005, 3, Seq(objectId, false))

case class EnableObject(objectId: ObjectId)
    extends SimpleInstruction(2005, 3, Seq(objectId, true))

object Disable {
  def apply(entity: EntityId): DisableEntity = DisableEntity(entity)
  def apply(objectId: ObjectId): DisableObject = DisableObject(objectId)
}

case class AwardItemsNoClients(itemsLot: ItemsLot)
    extends SimpleInstruction(2003, 36, Seq(itemsLot))

case class PlaySoundEffect(entityId: EntityId,
                           soundType: SoundType,
                           sound: Sound)
    extends SimpleInstruction(2010, 2, Seq(entityId, soundType, sound))

case class KillBoss(entityId: EntityId)
    extends SimpleInstruction(2003, 12, Seq(entityId))

case class DeleteMapSFX(entityId: EntityId, onlyRoot: Boolean)
    extends SimpleInstruction(2006, 1, Seq(entityId, onlyRoot))

case class ObjectAction(entityId: EntityId, parameterId: Int, state: Boolean)
    extends SimpleInstruction(2005, 6, Seq(entityId, parameterId, state))

case class SpecialStandbySetting(entityId: EntityId,
                                 standbyAnimation: Animation,
                                 damageAnimation: Animation = Animation.None,
                                 cancelAnimation: Animation = Animation.None,
                                 deathAnimation: Animation = Animation.None,
                                 standbyReturnAnimation: Animation =
                                   Animation.None)
    extends SimpleInstruction(2004,
                              9,
                              Seq(entityId,
                                  standbyAnimation,
                                  damageAnimation,
                                  cancelAnimation,
                                  deathAnimation,
                                  standbyReturnAnimation))

case class DisplayGenericDialog(messageId: MessageId,
                                buttonType: ButtonType,
                                buttonNumber: ButtonNumber,
                                entityId: EntityId,
                                distance: Double)
    extends SimpleInstruction(2007,
                              1,
                              Seq(
                                messageId,
                                buttonType,
                                buttonNumber,
                                entityId,
                                distance
                              ))

case class EnableTreasure(entityId: EntityId)
    extends SimpleInstruction(2005, 4, Seq(entityId, true))

case class DisableTreasure(entityId: EntityId)
    extends SimpleInstruction(2005, 4, Seq(entityId, false))
