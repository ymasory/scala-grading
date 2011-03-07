import java.io.File
import scala.io.Source

/** Implementation of the Playfair Cipher
  * @author Yuvi Masory */
object Playfair {

  /** Program entry point.
    * @param args Not used */
  def main(args: Array[String]) {
    import javax.swing.JFileChooser

    if (args.length > 0) Console.err println("ignoring command line arguments")

    val chooser = new JFileChooser
    val result = chooser showOpenDialog(null)
    if (result == JFileChooser.APPROVE_OPTION) {
      val file = chooser.getSelectedFile()
      file match {
        case f: File => {
          run(f)
        }
        case _ => Console.err.println("invalid selection!")
      }
    }
  }

  /** Non-gui interface to facilitate testing.
    * @param file The input file */
  def run(file: File) {
    try {
      prettyPrint(CipherIterator.fromFile(file))
    }
    catch {
      case ex: FileFormatException => Console.err println(ex);
    }
  }

  /** Print output of `iter` iterator in blocks of 5 chars
    * with up to 10 blocks per line. */
  private def prettyPrint(iter: Iterator[(Char, Char)]) {
    import scala.math.ceil
    val builder = new StringBuilder
    iter.toList.foreach { pair => 
      builder.append(pair._1)
      builder.append(pair._2)    
    }
    val out = builder.toString
    val numFull = out.length / 50
    for (i <- 0 until numFull) {
      val line = out.substring(i * 50, i * 50 + 50)
      for (j <- 0 until 10) {
        val start = j * 5
        val endPlusOne = start + 5
        print(line.substring(start, endPlusOne))
        if (j < 9) print(" ")
      }
      println()
    }

    val start = numFull * 50
    val endPlusOne = out.length
    if (start < out.length) {
      for (i <- start until endPlusOne) {
        print(out(i))
        if ((i + 1) % 5 == 0 && i + 1 != out.length) {
          print(" ")
        }
      }
      println()
    }
  }
}

/** Program constants. */
object Constants {

  /** The two possible directives. */
  object Directive extends Enumeration {
    val Encipher = Value("encipher")
    val Decipher = Value("decipher")
  }

  val DirectivePattern = ("""(\S*) (\S*)""").r
  
  val a = 97     //ascii 'a'
  val z = 122    //ascii 'z'
  val j = 106    //ascii 'j'
  val sub = 'x'  //letter to use when file has odd number of characters, or two letters appear consecutively
}


object Utils {
  
  /** Returns a version of `in` that consists of only lower a-z, other characters being removed. */
  def normalize(in: String) = {
    val buf = new StringBuilder
    in.foreach { c =>
      val lower = c.toLower
      if (lower >= Constants.a && lower <= Constants.z) buf.append(lower)
    }
    buf.toString
  }

  /** Returns a `String` created by concatenating all of the `Iterator's` elements. */
  def concat(in: Iterator[String]) = {
    val buf = new StringBuilder
    in.foreach { el =>
      buf.append(el)
    }
    buf.toString
  }

  /** Returns a version of `in` with `Constants.sub` inserted wherevere needed to pairs with identical elements. */
  def insertSub(in: String) = {
    val buf = new StringBuilder
    var insertedPos = new scala.collection.mutable.ListBuffer[Int]()
    for (i <- 0 until in.length - 1) {
      buf append(in(i))
      if (in(i) == in(i + 1)) {
        if ((isEven(insertedPos.length) && isEven(i)) || (!isEven(insertedPos.length) && isOdd(i))) {
          buf append(Constants.sub)
          insertedPos += i
        }
      }

    }
    if (buf.last == in.last && isOdd(buf.length)) {
      buf append in.last
      buf append Constants.sub
    }
    else {
      buf append in.last
    }
    buf.toString
  }

  /** Returns whether `i` is even. */
  @inline def isEven(i: Int) = i % 2 == 0
  /** Returns whether `i` is odd. */
  @inline def isOdd(i: Int) = !isEven(i)
}


class CipherIterator private (file: File, key: String, cmd: Constants.Directive.Value) extends Iterator[(Char, Char)] {

  private val charIter = {
    import Utils.{insertSub, concat, normalize}
    val iter = Source.fromFile(file).getLines
    iter.next //discard directive line
    val processedDoc = insertSub(normalize(concat(iter).toLowerCase))
    Source.fromString(processedDoc)
  }

  private val table = new KeyTable(key)

  // println(table)

  /** Return next pair of chars. The second one will use Constants.sub if not available. */
  override def next = {
    val first = charIter.next
    val second = if (charIter.hasNext) charIter.next else Constants.sub
    // println((first, second) + " encrypts to " + table.encrypt(first, second))
    // println((first, second) + " decrypts to " + table.decrypt(first, second))
    if (cmd == Constants.Directive.Encipher) table.encrypt(first, second) else table.decrypt(first, second)
  }

  /** @return `true` if the `Iterator` has more pairs to return. */
  override def hasNext: Boolean = charIter.hasNext

  final private class KeyTable(key: String) {

    private val char2Entry: Map[Char, Entry] = {
      val keyVals = key.distinct.toList
      val letterVals = for {
        i <- Constants.a to Constants.z
        if i != Constants.j
        c = i.toChar
        if (keyVals.contains(c) == false)
      } yield c
      val allVals = {
        keyVals ++ letterVals.slice(0, 25 - keyVals.length)
      } ensuring (res => res.length == 25)

      val entries = (0 until allVals.length) map {
        num => {
          val c: Char = allVals(num);
          (c, Entry(c, num % 5, num / 5))
         }
      }
      val jLessMap = Map.empty ++ entries
      val iEntry = jLessMap('i')
      jLessMap + Tuple2('j', Entry('j', iEntry.row, iEntry.col))
    }

    private val coord2Entry: Map[(Int, Int), Entry] = {
      Map.empty ++ {
        for((_, entry) <- char2Entry) yield {
          ((entry.row, entry.col), entry)
        }
      }
    }

    final case class Entry(el: Char, row: Int, col: Int)
    /** Module incrementing, wrapping after 4 back to 0. */
    @inline private def wrapUp(i: Int) = if (i < 4) i + 1 else 0
    /** Module decrementing, wrapping after 4 back to 0. */
    @inline private def wrapDown(i: Int) = if (i > 0) i - 1 else 4
    /** Returns an `Element` to the right of `e`, possibly wrapping to start of row. */
    @inline private def rightOf(e: Entry): Entry =  coord2Entry(e.row, wrapUp(e.col))
    /** Returns an `Element` to the left of `e`, possibly wrapping to end of row. */
    @inline private def leftOf(e: Entry): Entry =  coord2Entry(e.row, wrapDown(e.col))
    /** Returns an `Element` below `e`, possibly wrapping to top of column. */
    @inline private def below(e: Entry): Entry = coord2Entry(wrapDown(e.row), e.col)
    /** Returns `Element` agove `e`, possibly wrapping to bottom of column. */
    @inline private def above(e: Entry): Entry = coord2Entry(wrapUp(e.row), e.col)

    /** Encrypt the pair `(first, second)` using Playfair cipher.
      * @return Encrypted pair. */
    def encrypt(first: Char, second: Char): (Char, Char) = {
      if (first == Constants.sub && second == Constants.sub)
        throw FileFormatException("not handling case of consecutive " + first)
      else if (first == second)
        encrypt(first, Constants.sub)
      else {
        val firstEntry = char2Entry(first)
        val secondEntry = char2Entry(second)
        if (firstEntry.row == secondEntry.row)
          (rightOf(firstEntry).el, rightOf(secondEntry).el)
        else if(firstEntry.col == secondEntry.col)
          (above(firstEntry).el, above(secondEntry).el)
        else
          (coord2Entry(secondEntry.row, firstEntry.col).el, coord2Entry(firstEntry.row, secondEntry.col).el)
      }        
    }

    /** Decrypt the pair `(first, second)` using Playfair cipher.
      * @return Decrypted pair. */
    def decrypt(first: Char, second: Char): (Char, Char) = {
      if (first == Constants.sub && second == Constants.sub)
        throw FileFormatException("not handling case of consecutive " + first)
      else if (first == second)
        encrypt(first, Constants.sub)
      else {
        val firstEntry = char2Entry(first)
        val secondEntry = char2Entry(second)
        if (firstEntry.row == secondEntry.row)
          (leftOf(firstEntry).el, leftOf(secondEntry).el)
        else if(firstEntry.col == secondEntry.col)
          (below(firstEntry).el, below(secondEntry).el)
        else
          (coord2Entry(secondEntry.row, firstEntry.col).el, coord2Entry(firstEntry.row, secondEntry.col).el)
      }
    }

    /** Lookup `Entry` with provided coordinates. */
    def apply(row: Int, col: Int): Entry = coord2Entry(row, col)     

    /** Lookup `Entry` with provided character. */
    def apply(c: Char): Entry = char2Entry(c)

    /** Display table nicely for debugging. */
    override val toString = {
      val builder = new StringBuilder      
      builder.append("-----------\n")
      for {
        i <- 0 until 5
        j <- 0 until 5
      } {
        if (j == 0) builder.append("|")
        builder.append(coord2Entry(j, i).el.toString + "|")
        if (j == 4) {
          builder.append("\n")
          builder.append("-----------\n")
        }
      }
      builder.toString
    }
  }
}

/** Companion class, contains factory method. */
object CipherIterator {
  import Utils.normalize

  /** Returns an encryption/decryption iterator as appropriate for the provided
    * file.
    * @param file Input file, must be correctly formatted with first-line directive
    * @return An iterator that returns tuples (Char, Char)
    * @throws FileFormatException If `file` is not in expected format */
  def fromFile(file: File): Iterator[(Char, Char)] = {

    val lineIter = Source.fromFile(file).getLines
    if (lineIter.hasNext) {
      val firstLine = lineIter next()
      try {
        val Constants.DirectivePattern(cmd, key) = firstLine
          val adjustedKey = normalize { key match {
            case s: String => s.toLowerCase
            case _ => ""
          }
        }
        if (cmd == Constants.Directive.Encipher.toString)
          new CipherIterator(file, adjustedKey, Constants.Directive.Encipher)
        else if (cmd == Constants.Directive.Decipher.toString)
          new CipherIterator(file, adjustedKey, Constants.Directive.Decipher)
        else
          throw FileFormatException("unknown directive: " + cmd)
      }
      catch {
        case me: MatchError => throw FileFormatException("directive line not formatted correctly")
      }
    }
    else {
      throw FileFormatException(file + " is empty")
    }
  }
}


/**
  * Exception to indicate input file's format is not according to spec.
  * @param err The error message. */
case class FileFormatException(err: String) extends RuntimeException(err)
