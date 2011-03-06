package com.yuvimasory.scalagrading

import org.scalatest.FunSuite

class WhileTests extends FunSuite {

  test("While") {
    val expected = Score(defs=0,
                         lambdas=0,
                         matches=0,
                         deceptions=0,
                         whiles=1,
                         vars=0,
                         arrays=0,
                         nulls=0)
    expect(expected) {Score.runPlugin("whiles/While.scala")}
  }
}
