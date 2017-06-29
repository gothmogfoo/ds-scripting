# ds-scripting
A Scala-based DSL for Dark Souls event scripting

Based on HotPocketRemix's work on reverse engineering the event scripting in Dark Souls (check out https://github.com/HotPocketRemix/DSEventScriptTools)

My intention for this is to be an easier way to write event scripts than manually writing the numeric input format; implementing
it as a DSL in Scala allows us to leverage all the power of a general-purpose language to abstract and name.

As an example, here is how you'd write the "Nuke any firekeeper souls picked up by the player" event from HotPocketRemix's Estus Quest:

```scala
  def removeFirekeeperSouls: Event =
    Event(9003, "Remove Estus flask after leaving Asylum if Kalameet lives")(
      when(questIsActive and playerOwnsAnyOf(firekeeperSouls)),
      Wait.Seconds(5.0),
      removeAll(firekeeperSouls),
      DisplayStatusMessage(MessageId(140171), true),
      Restart
    )
```

For comparison, here is the "verbose" format:

```
Event ID: 9003, Int: 0
Parameters: {} ()
      0 CONDITION: IF Event Flag ID: 11810110 is ON --> Register AND(01)
      1 CONDITION: IF Event Flag ID: 11210004 is OFF --> Register AND(01)
      2 CONDITION: IF player Owns Item (Item Type: Item, Item ID: 00000391) --> Register OR(01)
      3 CONDITION: IF player Owns Item (Item Type: Item, Item ID: 00000392) --> Register OR(01)
      4 CONDITION: IF player Owns Item (Item Type: Item, Item ID: 00000393) --> Register OR(01)
      5 CONDITION: IF player Owns Item (Item Type: Item, Item ID: 00000394) --> Register OR(01)
      6 CONDITION: IF player Owns Item (Item Type: Item, Item ID: 00000395) --> Register OR(01)
      7 CONDITION: IF player Owns Item (Item Type: Item, Item ID: 00000396) --> Register OR(01)
      8 CONDITION: IF register OR(01) is TRUE --> Register AND(01)
      9 CONDITION: IF register AND(01) is TRUE --> Register MAIN
     10 WAIT 5.000s
     11 Remove 0 (0=All) of Item (Item Type: Item, Item ID: 391) from player
     12 Remove 0 (0=All) of Item (Item Type: Item, Item ID: 392) from player
     13 Remove 0 (0=All) of Item (Item Type: Item, Item ID: 393) from player
     14 Remove 0 (0=All) of Item (Item Type: Item, Item ID: 394) from player
     15 Remove 0 (0=All) of Item (Item Type: Item, Item ID: 395) from player
     16 Remove 0 (0=All) of Item (Item Type: Item, Item ID: 396) from player
     17 Display Status Explanation Message (Message ID: 140171, ENABLE Pad)
     18 RESTART event
```

And the "numeric" format that you'd have to actually write:

```
9003, 0
    3[00] (bBBi)[1, 1, 0, 11810110]
    3[00] (bBBi)[1, 0, 0, 11210004]
    3[04] (bBiB)[-1, 3, 391, 1]
    3[04] (bBiB)[-1, 3, 392, 1]
    3[04] (bBiB)[-1, 3, 393, 1]
    3[04] (bBiB)[-1, 3, 394, 1]
    3[04] (bBiB)[-1, 3, 395, 1]
    3[04] (bBiB)[-1, 3, 396, 1]
    0[00] (bBb)[1, 1, -1]
    0[00] (bBb)[0, 1, 1]
 1001[00] (f)[5.0]
 2003[24] (iii)[3, 391, 0]
 2003[24] (iii)[3, 392, 0]
 2003[24] (iii)[3, 393, 0]
 2003[24] (iii)[3, 394, 0]
 2003[24] (iii)[3, 395, 0]
 2003[24] (iii)[3, 396, 0]
 2007[03] (iB)[140171, 1]
 1000[04] (B)[1]
```

A complete reimplementation of the event scripts from the Estus Quest is [over here](src/main/scala/EstusQuest.scala).

To compile it, you need [sbt](http://www.scala-sbt.org/) installed; you can then type `sbt run` and your terminal will be filled with stuff.

## Things to do

* Read the existing `foo.unpack.txt` files and write the new events into them instead of outputting fragments
* Maybe remove the typed wrappers again, for less verbosity
* Implement more instructions!
* Provide more prebuilt thingies `Bosses` (and complete that); maybe they can be built from some list somewhere?
