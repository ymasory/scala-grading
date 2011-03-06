package com.yuvimasory.scalagrading

import org.scalatest.FunSuite

class MatchTests extends FunSuite {

  test("Match") {
    val expected = Score(defs=0,
                         lambdas=0,
                         matches=1,
                         deceptions=0,
                         whiles=0,
                         vars=0,
                         arrays=0,
                         nulls=0)
    expect(expected) {Score.runPlugin("matches/Match.scala")}
  }
}
