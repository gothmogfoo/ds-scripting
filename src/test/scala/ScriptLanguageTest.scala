import org.scalatest.{FunSpec, MustMatchers}

import dsscript._

class ScriptLanguageTest extends FunSpec with MustMatchers {
  import ItemType._

  describe("The instruction compiler") {
    it("must compile an instruction to the numerical format") {
      Condition
        .PlayerOwns(Item, ItemId(400), true, Register.Main)
        .compiled must be(
        """    3[04] (bBiB)[0, 3, 400, 1]"""
      )
    }
    it("must compile a complete event to the numerical format") {
      Event(8099, "Consume soul of a lost undead 3 seconds after acquisition")(
        Condition.PlayerOwns(ItemType.Item, ItemId(400), true, Register.Main),
        Wait.Seconds(3.0),
        RequestAnimation(Player,
                         Animation(7501),
                         loop = false,
                         waitForCompletion = true),
        RemoveItem(Item, ItemId(400), 1),
        SetSpecialEffect(Player, SpecialEffect(3270)),
        Restart
      ).compiled must be(
        """8099, 0 -- Consume soul of a lost undead 3 seconds after acquisition
          |    3[04] (bBiB)[0, 3, 400, 1]
          | 1001[00] (f)[3.0]
          | 2003[01] (iiBB)[10000, 7501, 0, 1]
          | 2003[24] (iii)[3, 400, 1]
          | 2004[08] (ii)[10000, 3270]
          | 1000[04] (B)[1]""".stripMargin)
    }
    it("must provide the constructor bits for an event") {
      Event(1234)().construction must be(" 2000[00] (iII)[0, 1234, 0]")
    }
    it("must handle one replacement when providing constructor bits") {
      Event(1234)(DisableEntity(EntityId(0)).replacing(Replacement(0, 0, 4))).construction must be(
        " 2000[00] (iII)[<slot>, 1234, <1st>]"
      )
    }
    it("must handle multiple replacements when providing constructor bits") {
      Event(1234)(
        DisableEntity(EntityId(0)).replacing(Replacement(0, 0, 4)),
        DisableEntity(EntityId(0)).replacing(Replacement(0, 0, 4)),
        DisableEntity(EntityId(0)).replacing(Replacement(0, 4, 4)),
        DisableEntity(EntityId(0)).replacing(Replacement(0, 8, 4))
      ).construction must be(
        " 2000[00] (iII|II)[<slot>, 1234, <1st>, <2nd>, <3rd>]"
      )
    }
    it("must compile an instruction with one replacement") {
      DisableObject(ObjectId(0))
        .replacing(Replacement(0, 0, 4))
        .compiled must be(
        """ 2005[03] (iB)[0, 0]
          |    ^(0 <- 0, 4)""".stripMargin
      )
    }
    it("must compile an instruction with multiple replacements") {
      BatchSetEventFlags(EventId(0), EventId(0), true)
        .replacing(Replacement(0, 4, 4), Replacement(4, 8, 4))
        .compiled must be(
        """ 2003[22] (iiB)[0, 0, 1]
          |    ^(0 <- 4, 4)
          |    ^(4 <- 8, 4)""".stripMargin
      )
    }
  }
}
