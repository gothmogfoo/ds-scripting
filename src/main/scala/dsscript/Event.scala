package dsscript

case class Event(id: Int,
                 description: String,
                 execution: Int,
                 replace: Boolean,
                 instructions: Seq[Instruction]) {
  def construction: String = {
    val replacements = instructions.flatMap(_.replacements)
    replacements.groupBy(_.start).keySet.size match {
      case 0 =>
        s" 2000[00] (iII)[0, $id, 0]"
      case 1 =>
        s" 2000[00] (iII)[<slot>, $id, <1st>]"
      case x =>
        val extraTypes = "I"*(x - 1)
        val args = 1 to x collect {
          case 1 => "1st"
          case 2 => "2nd"
          case 3 => "3rd"
          case i => s"${i}th"
        } mkString("<", ">, <", ">")
        s" 2000[00] (iII|$extraTypes)[<slot>, $id, $args]"
    }
  }

  def compiled: String = {
    val header = s"$id, $execution -- $description"
    val body = instructions.map(_.compiled).mkString("\n")
    s"$header\n$body"
  }
}

object Event {
  def apply(id: Int, description: String = "", replace: Boolean = false)(
      instructions: Instruction*): Event =
    Event(id, description, 0, replace, instructions)
}
