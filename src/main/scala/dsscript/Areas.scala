package dsscript

trait Areas {
  sealed abstract class Area(val id: String)

  object Common extends Area("common")
  object Depths extends Area("m10_00_00_00")
  object UndeadBurg extends Area("m10_01_00_00")
  object FirelinkShrine extends Area("m10_02_00_00")
  object PaintedWorld extends Area("m11_00_00_00")
  object DarkrootGarden extends Area("m12_00_00_00")
  object SanctuaryGarden extends Area("m12_01_00_00")
  object Catacombs extends Area("m13_00_00_00")
  object TombOfTheGiants extends Area("m13_01_00_00")
  object GreatHollow extends Area("m13_02_00_00")
  object BlightTown extends Area("m14_00_00_00")
  object DemonRuins extends Area("m14_01_00_00")
  object SensFortress extends Area("m15_00_00_00")
  object AnorLondo extends Area("m15_01_00_00")
  object NewLondoRuins extends Area("m16_00_00_00")
  object DukesArchives extends Area("m17_00_00_00")
  object KilnOfTheFirstFlame extends Area("m18_00_00_00")
  object NorthernUndeadAsylum extends Area("m18_01_00_00")
}
