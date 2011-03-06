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
  override val toString = {
    "DETAILS" + LF +
    "-------" + LF +
    "defs:       " + defs + LF +
    "lambdas:    " + lambdas + LF +
    "matches:    " + matches + LF +
    "deceptions: " + deceptions + LF +
    "whiles:     " + whiles + LF +
    "vars:       " + vars + LF +
    "nulls:      " + nulls + LF +
    "arrays:     " + arrays + LF + LF +
    "SCORE" + LF +
    "-----" + LF + score

  }
}

case object Score {

  val EmptyScore = Score(0, 0, 0, 0, 0, 0, 0, 0)
  val curDir = (new java.io.File(".")).getCanonicalPath
  val pluginLoc = curDir + "/target/scala_2.8.1/scala-grading-alpha.jar"
  val testPrefix = curDir + "/src/test/resources/"

  def parse(in: List[String]): Score = {
    val curScore = EmptyScore
    for (line <- in) {
      println(line.split("\\s"))
    }
    curScore
  }

  def runPlugin(fileName: String): Score = {
    val cmd = "scala -Xplugin:" + pluginLoc + " " + testPrefix + fileName
    println("cmd: " + cmd)
    val (_, lines) = execp(cmd)

    val curScore = Score.EmptyScore
    for (line <- lines) {
      println(line.split("\\s"))
    }
    curScore
  }

  /** from scala-utilities, LGPL
    * http://code.google.com/p/scala-utilities/
    */
  def execp (cmd : String) = {
    import java.io._
    var currDir : Option[File] = None
    val runTime = Runtime.getRuntime
    val process = if (currDir.isDefined)
      runTime.exec (cmd, null, currDir.get) else runTime.exec(cmd)
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
