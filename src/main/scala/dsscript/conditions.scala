package dsscript

abstract class Condition(cls: Int, typ: Int, args: Seq[Argument])
    extends SimpleInstruction(cls, typ, args) {
  def inRegister(register: Register): Condition
}

object Condition {
  case class PlayerOwns(itemType: ItemType,
                        itemId: ItemId,
                        ownedState: Boolean,
                        register: Register = Register.Main)
      extends Condition(3,
                        4,
                        Seq(
                          register,
                          itemType,
                          itemId,
                          ownedState
                        )) {
    def inRegister(register: Register): Condition = copy(register = register)
  }

  case class EventFlag(register: Register,
                       state: OnOffChange,
                       flagType: FlagType,
                       eventId: EventId)
      extends Condition(3,
                        0,
                        Seq(
                          register,
                          state,
                          flagType,
                          eventId
                        )) {
    def inRegister(register: Register): Condition = copy(register = register)
  }

  def EventFlagOn(eventId: EventId,
                  register: Register = Register.Main): EventFlag =
    EventFlag(register, OnOffChange.On, FlagType.EventFlag, eventId)

  def EventFlagOff(eventId: EventId,
                   register: Register = Register.Main): EventFlag =
    EventFlag(register, OnOffChange.Off, FlagType.EventFlag, eventId)

  case class EntityDead(entity: EntityId, register: Register = Register.Main)
      extends Condition(4, 0, Seq(register, entity, true)) {
    def inRegister(register: Register): Condition = copy(register = register)
  }

  case class EntityHealthBelow(entity: EntityId,
                               ratio: Double,
                               register: Register = Register.Main)
      extends EntityHealthRatio(entity, ratio, ComparisonType.<=, register) {
    def inRegister(register: Register) = copy(register = register)
  }

  abstract class EntityHealthRatio(entity: EntityId,
                                   ratio: Double,
                                   operation: ComparisonType,
                                   register: Register)
      extends Condition(4,
                        2,
                        Seq(
                          register,
                          entity,
                          operation,
                          ratio
                        ))

  case class ActionButtonState(category: Category,
                               entityId: EntityId,
                               angle: Double,
                               damiPoly: Short,
                               distance: Double,
                               helpId: Int,
                               reactionAttribute: ReactionAttribute,
                               padId: Int,
                               register: Register = Register.Main)
      extends Condition(3,
                        5,
                        Seq(register,
                            category,
                            entityId,
                            angle,
                            damiPoly,
                            distance,
                            helpId,
                            reactionAttribute,
                            padId)) {
    def inRegister(register: Register) = copy(register = register)
  }

  case class PlayerIsMultiplayerHost(register: Register = Register.Main)
      extends Condition(3,
                        6,
                        Seq(
                          register,
                          MultiplayerState.Host
                        )) {
    def inRegister(register: Register) = copy(register = register)
  }

  case class IsCharacterType(entityId: EntityId,
                             characterType: CharacterType,
                             register: Register = Register.Main)
      extends Condition(4, 3, Seq(register, entityId, characterType)) {
    def inRegister(register: Register) = copy(register = register)
  }

  implicit class RichCondition(v: Condition) {
    def and(that: Condition): ConjunctCondition =
      new ConjunctCondition(Seq(v, that))
    def or(that: Condition): DisjunctCondition =
      new DisjunctCondition(Seq(v, that))
  }
}

trait CompoundCondition {
  def defaultRegister: Register

  def inRegister(register: Register): CompoundInstruction
}

class DisjunctCondition(bits: Seq[Condition]) extends CompoundCondition {
  def defaultRegister = Register.Or1

  def inRegister(register: Register) =
    new CompoundInstruction(bits.map(_.inRegister(register)))
}

class ConjunctCondition(bits: Seq[Condition]) extends CompoundCondition {
  def and(next: Condition) = new ConjunctCondition(bits ++ Seq(next))

  def and(next: DisjunctCondition) = {
    val outer = this
    new CompoundCondition {
      def defaultRegister = outer.defaultRegister

      def inRegister(register: Register) =
        outer
          .inRegister(register)
          .andThen(next.inRegister(next.defaultRegister))
          .andThen(CopyIfTrue(next.defaultRegister, register))
    }
  }

  def inRegister(register: Register): CompoundInstruction = {
    new CompoundInstruction(bits.map(_.inRegister(register)))
  }

  def defaultRegister: Register = Register.And1
}
