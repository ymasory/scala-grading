package com.yuvimasory.scalagrading

import org.scalatest.FunSuite

class ScalaGradingTests extends FunSuite {

  case class Score (
    defs: Int,
    lambdas: Int,
    matches: Int,
    whiles: Int,
    vars: Int,
    arrays: Int,
    nulls: Int
  )

  def parseScore(in: String) = {
    Score(0, 0, 0, 0, 0, 0, 0)
  }

  test("one var") {
    expect(Score(0, 0, 0, 0, 1, 0, 0)) {
      execp("ls")
    }
  }

  /** from scala-utilities, LGPL
    * http://code.google.com/p/scala-utilities/
    */
  def execp (cmd : String) = {
    import java.io._
    var currDir : Option[File] = None
    val runTime = Runtime.getRuntime
    val process = if (currDir.isDefined) runTime.exec (cmd, null, currDir.get) else runTime.exec(cmd)
    val resultBuffer = new BufferedReader(new InputStreamReader(process.getInputStream))
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
