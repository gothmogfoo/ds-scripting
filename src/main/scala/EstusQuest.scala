import dsscript._

object EstusQuest extends DarkSoulsScript {
  import Bosses._
  import Condition._

  def kalameetIsDead = EventId(11210004)

  def asylumBackDoorIsOpen = EventFlagOn(EventId(11810110))

  def kalameetLives = EventFlagOff(kalameetIsDead)

  def calamity = SpecialEffect(2350)

  def questIsActive = asylumBackDoorIsOpen and kalameetLives

  def estusFlasks = 200 to 215 map ItemId

  def firekeeperSouls = 391 to 396 map ItemId

  def applyCalamity: Event =
    Event(9001,
          "Apply calamity effect after leaving Asylum if Kalameet lives")(
      when(questIsActive),
      SetSpecialEffect(Player, calamity)
    )

  def removeEstus: Event =
    Event(9002, "Remove Estus flask after leaving Asylum if Kalameet lives")(
      when(questIsActive and playerOwnsAnyOf(estusFlasks)),
      Wait.Seconds(5.0),
      removeAll(estusFlasks),
      SetEventFlag(EventId(50000082), false),
      BatchSetEventFlags(EventId(8131), EventId(8137), false),
      DisplayStatusMessage(MessageId(140170), true),
      Restart
    )

  def removeFirekeeperSouls: Event =
    Event(9003, "Remove Estus flask after leaving Asylum if Kalameet lives")(
      when(questIsActive and playerOwnsAnyOf(firekeeperSouls)),
      Wait.Seconds(5.0),
      removeAll(firekeeperSouls),
      DisplayStatusMessage(MessageId(140171), true),
      Restart
    )

  def returnEstusWhenKalameetDies: Event =
    Event(kalameetIsDead.id, "Kalameet is dead, v2", replace = true)(
      SkipIfEventId(2, EventId(0), false),
      Disable(kalameet),
      End,
      when(EntityDead(kalameet) or EntityDead(EntityId(1210402))),
      DisplayStatusMessage(MessageId(140175), true),
      SetEventFlag(kalameetIsDead, true),
      CancelSpecialEffect(Player, calamity),
      SetEventFlag(EventId(50000082), false),
      AwardItemsNoClients(ItemsLot(6022)),
      when(EventFlagOn(EventId(50001060))),
      Wait.Seconds(10.0),
      SetEventFlag(EventId(50001060), false),
      AwardItemsNoClients(ItemsLot(2060))
    )

  def updatedOnDeathOfAsylumDemon: Event =
    Event(11810001, "On death of Asylum Demon v2", replace = true)(
      when(EntityHealthBelow(asylumDemon, 0.0)),
      PlaySoundEffect(asylumDemon, SoundType.SFX, Sound(777777777)),
      when(EntityDead(asylumDemon)),
      SetEventFlag(EventId(16), true),
      KillBoss(asylumDemon),
      Disable(ObjectId(1811990)),
      DeleteMapSFX(EntityId(1811991), onlyRoot = true),
      ForceAnimation(EntityId(1811111), Animation(1), waitForCompletion = false),
      SkipIfEventFlag(1, EventId(11810312), false),
      ForceAnimation(EntityId(1811115), Animation(1), waitForCompletion = false),
      ObjectAction(EntityId(1811111), -1, false),
      ObjectAction(EntityId(1811110), -1, false)
    )

  def handleEndOfLautrecInvasion: Event =
    Event(11510544, "Handle the end of the Lautrec invasion", replace = true)(
      SkipIfEventId(2, EventId(0), false),
      EnableTreasure(EntityId(1511601)),
      End,
      Disable(ObjectId(1511601)),
      DisableTreasure(EntityId(1511601)),
      when(PlayerIsMultiplayerHost() and EventFlagOff(EventId(11510700)) and EventFlagOn(EventId(8102))),
      (IsCharacterType(Player, CharacterType.Survival) or IsCharacterType(Player, CharacterType.GrayGhost)).inRegister(Register.Or1),
      SkipIfRegister(3, Register.Or1, false),
      AwardItemsNoClients(ItemsLot(2060)),
      AwardItemsNoClients(ItemsLot(6300)),
      RemoveItem(ItemType.Item, ItemId(115), 0),
      EnableObject(ObjectId(1511601)),
      EnableTreasure(EntityId(1511601)),
      DisableEntity(EntityId(0)).replacing(Replacement(0, 0, 4)),
      BatchSetEventFlags(EventId(0), EventId(0), false).replacing(Replacement(0, 4, 4), Replacement(4, 8, 4)),
      SetEventFlag(EventId(0), true).replacing(Replacement(0, 12, 4)),
      SetEventFlag(EventId(11510544), true),
      when(questIsActive and PlayerOwns(ItemType.Item, ItemId(390), true))(Register.And5),
      Wait.Seconds(3.0),
      RemoveItem(ItemType.Item, ItemId(390), 0),
      DisplayStatusMessage(MessageId(140172), true)
    )

  def startQuest: Event =
    Event(11816000, "Start the quest")(
      when(EventFlagOn(EventId(16)) and PlayerOwns(ItemType.Item, ItemId(2011), true)),
      SkipIfEventFlag(1, EventId(11810110), false),
      End,
      when(ActionButtonState(Category.Object, EntityId(1811110), 60.0, 100, 1.5, 10010400, ReactionAttribute.SurvivalAndGray, 0)),
      DisplayGenericDialog(MessageId(140176), ButtonType.YesNo, ButtonNumber.NoButtons, EntityId(1811110), 3.000),
      Wait.Seconds(10.0),
      DisplayStatusMessage(MessageId(140173), enablePad = true),
      Wait.Seconds(10.0),
      SpecialStandbySetting(Player, Animation(7816)),
      ForceAnimation(Player, Animation(7815)),
      Wait.Seconds(2.0),
      DisplayStatusMessage(MessageId(140174), enablePad = true),
      Wait.Seconds(15.0),
      SetSpecialEffect(Player, calamity),
      SpecialStandbySetting(Player, Animation.None),
      ForceAnimation(Player, Animation(7817)),
      Wait.Seconds(5.0),
      ForceAnimation(EntityId(1811110), Animation(1)),
      SetEventFlag(EventId(11810110), true)
    )

  def events: Map[Area, Seq[Event]] = Map(
    Common -> Seq(
      applyCalamity,
      removeEstus,
      removeFirekeeperSouls
    ),
    SanctuaryGarden -> Seq(
      returnEstusWhenKalameetDies
    ),
    AnorLondo -> Seq(
      handleEndOfLautrecInvasion
    ),
    NorthernUndeadAsylum -> Seq(
      updatedOnDeathOfAsylumDemon,
      startQuest
    )
  )
}
