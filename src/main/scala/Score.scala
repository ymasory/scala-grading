package com.yuvimasory.scalagrading

case class Score (
  defs: Int,
  lambdas: Int,
  matches: Int,
  deceptions: Int,
  whiles: Int,
  vars: Int,
  arrays: Int,
  nulls: Int
) {

  val score = {
    import java.lang.Math.min
    min(10, defs) +
    min(20, lambdas * 5) +
    (if (matches > 0) 5 else 0) -
    (deceptions * 15) -
    (whiles * 3) -
    (vars * 3) -
    (arrays * 5) -
    (nulls * 10)
  }

  val LF = "\n"
  val report  = {
    LF +
    "DETAILS" + LF +
    "-------" + LF +
    "defs:       " + defs + LF +
    "lambdas:    " + lambdas + LF +
    "matches:    " + matches + LF +
    "deceptions: " + deceptions + LF +
    "whiles:     " + whiles + LF +
    "vars:       " + vars + LF +
    "nulls:      " + nulls + LF +
    "arrays:     " + arrays + LF +
    LF +
    "SCORE" + LF +
    "-----" + LF + score + LF +
    LF

  }
}

case object Score {

  val EmptyScore = Score(0, 0, 0, 0, 0, 0, 0, 0)
  val curDir = (new java.io.File(".")).getCanonicalPath
  val pluginLoc = curDir + "/target/scala_2.8.1/scala-grading-alpha.jar"
  val testPrefix = curDir + "/src/test/resources/"

  def parse(in: List[String]): Score = {
    var curScore = EmptyScore
    println(in)
    for (line <- in) {
      val els = line.split("\\s")
      curScore = els.head match {
        case "defs:"       => curScore.copy(defs = els.last.toInt)
        case "lambdas:"    => curScore.copy(lambdas = els.last.toInt)
        case "matches:"    => curScore.copy(matches = els.last.toInt)
        case "deceptions:" => curScore.copy(deceptions = els.last.toInt)
        case "whiles:"     => curScore.copy(whiles = els.last.toInt)
        case "vars:"       => curScore.copy(vars = els.last.toInt)
        case "nulls:"      => curScore.copy(nulls = els.last.toInt)
        case "arrays:"     => curScore.copy(arrays = els.last.toInt)
        case _             => curScore
      }
    }
    curScore
  }

  def runPlugin(fileName: String): Score = {
    val cmd = Array("scalac",
                    "-Xplugin:" + pluginLoc,
                    testPrefix + fileName)
    val (res, lines) = execp(cmd)
    parse(lines)
  }

  /** from scala-utilities, LGPL
    * http://code.google.com/p/scala-utilities/
    */
  def execp (cmd : Array[String]) = {
    import java.io._
    val runTime = Runtime.getRuntime
    val process = runTime.exec(cmd)
    val resultBuffer = new BufferedReader(
      new InputStreamReader(process.getInputStream))
    var line : String = null
    var lineList : List[String] = Nil


    do {
        line = resultBuffer.readLine
        if (line != null) {
            lineList = line :: lineList
        }
    } while (line != null)

    process.waitFor
    resultBuffer.close

    (process.exitValue, lineList.reverse)
  }
}
