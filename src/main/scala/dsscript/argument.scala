package dsscript

import scala.language.implicitConversions

abstract class Argument(val typeFlag: String) {
  type T
  def value: T
}

object Argument {
  implicit def b2ba(b: Boolean): BooleanArgument = new BooleanArgument(b)
  implicit def i2ia(v: Int): IntArgument = new IntArgument(v)
  implicit def d2da(v: Double): DoubleArgument = new DoubleArgument(v)
  implicit def s2sa(v: Short): ShortArgument = new ShortArgument(v)
}

class ByteArgument(val value: Int) extends Argument("b") { type T = Int }
class ShortArgument(val value: Int) extends Argument("h") { type T = Int }
class UnsignedByteArgument(val value: Int) extends Argument("B") {
  type T = Int
}
class BooleanArgument(v: Boolean) extends Argument("B") {
  type T = Int
  def value: Int = if (v) 1 else 0
}
class IntArgument(val value: Int) extends Argument("i") { type T = Int }
class UnsignedIntArgument(val value: Int) extends Argument("I") {
  type T = Int
}
class DoubleArgument(val value: Double) extends Argument("f") {
  type T = Double
}

sealed abstract class Register(v: Int) extends ByteArgument(v)

object Register {
  object Main extends Register(0)

  object And1 extends Register(1)
  object And2 extends Register(2)
  object And3 extends Register(3)
  object And4 extends Register(4)
  object And5 extends Register(5)
  object And6 extends Register(6)
  object And7 extends Register(7)

  object Or1 extends Register(-1)
  object Or2 extends Register(-2)
  object Or3 extends Register(-3)
  object Or4 extends Register(-4)
  object Or5 extends Register(-5)
  object Or6 extends Register(-6)
  object Or7 extends Register(-7)
}

sealed abstract class ItemType(v: Int) extends UnsignedByteArgument(v)

object ItemType {
  object Weapon extends ItemType(0)
  object Armor extends ItemType(1)
  object Ring extends ItemType(2)
  object Item extends ItemType(3)
}

case class ItemId(v: Int) extends IntArgument(v)

sealed abstract class OnOffChange(v: Int) extends UnsignedByteArgument(v)

object OnOffChange {
  object Off extends OnOffChange(0)
  object On extends OnOffChange(1)
  object Change extends OnOffChange(2)
}

sealed abstract class FlagType(v: Int) extends UnsignedByteArgument(v)

object FlagType {
  object EventFlag extends FlagType(0)
  object EventId extends FlagType(1)
  object EventIdWithSlot extends FlagType(2)
}

class EntityId(id: Int) extends IntArgument(id)
object EntityId {
  def apply(id: Int): EntityId = new EntityId(id)
}
object Player extends EntityId(10000)

case class ObjectId(id: Int) extends IntArgument(id)

case class Animation(id: Int) extends IntArgument(id)
object Animation {
  val None = Animation(-1)
}

case class SpecialEffect(id: Int) extends IntArgument(id)

case class EventId(id: Int) extends IntArgument(id)

case class MessageId(id: Int) extends IntArgument(id)

case class ItemsLot(id: Int) extends IntArgument(id)

sealed abstract class ComparisonType(v: Int) extends ByteArgument(v)
object ComparisonType {
  object == extends ComparisonType(0)
  object != extends ComparisonType(1)
  object > extends ComparisonType(2)
  object < extends ComparisonType(3)
  object >= extends ComparisonType(4)
  object <= extends ComparisonType(5)
}

sealed abstract class SoundType(v: Int) extends IntArgument(v)
object SoundType {
  object Environmental extends SoundType(0)
  object CharacterMotion extends SoundType(1)
  object Menu extends SoundType(2)
  object Object extends SoundType(3)
  object PolyDrama extends SoundType(4)
  object SFX extends SoundType(5)
  object BFM extends SoundType(6)
  object Voice extends SoundType(7)
  object FloorMaterialDependence extends SoundType(8)
  object ArmorMaterialDependence extends SoundType(9)
  object Ghost extends SoundType(10)
}

case class Sound(id: Int) extends IntArgument(id)

sealed abstract class Category(v: Int) extends IntArgument(v)
object Category {
  object Object extends Category(0)
  object Area extends Category(1)
  object Character extends Category(2)
}

sealed abstract class ReactionAttribute(v: Int) extends UnsignedByteArgument(v)
object ReactionAttribute {
  object SurvivalAndGray extends ReactionAttribute(48)
  object All extends ReactionAttribute(255)
}

sealed abstract class ButtonType(v: Int) extends ShortArgument(v)
object ButtonType {
  object YesNo extends ButtonType(0)
  object OkCancel extends ButtonType(1)
}

sealed abstract class ButtonNumber(v: Int) extends ShortArgument(v)
object ButtonNumber {
  object One extends ButtonNumber(1)
  object Two extends ButtonNumber(2)
  object NoButtons extends ButtonNumber(6)
}

sealed abstract class CharacterType(v: Int) extends ByteArgument(v)
object CharacterType {
  object Survival extends CharacterType(0)
  object WhiteGhost extends CharacterType(1)
  object BlackGhost extends CharacterType(2)
  object GrayGhost extends CharacterType(8)
  object Intruder extends CharacterType(12)
}

sealed abstract class MultiplayerState(v: Int) extends ByteArgument(v)
object MultiplayerState {
  object Host extends MultiplayerState(0)
  object Client extends MultiplayerState(1)
  object Multiplayer extends MultiplayerState(2)
  object Singleplayer extends MultiplayerState(3)
}

