package com.yuvimasory.scalagrading

import org.scalatest.FunSuite

class NullTests extends FunSuite {

  test("NullFieldLiteral") {
    val expected = Score(defs=0,
                         lambdas=0,
                         matches=0,
                         deceptions=0,
                         whiles=0,
                         vars=0,
                         arrays=0,
                         nulls=1)
    expect(expected) {Score.runPlugin("NullFieldLiteral.scala")}
  }
}
