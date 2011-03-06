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
      null
    }
  }
}
