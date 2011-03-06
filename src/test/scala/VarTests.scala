package com.yuvimasory.scalagrading

import org.scalatest.FunSuite

class VarTests extends FunSuite {

  test("VarField") {
    val expected = Score(defs=0,
                         lambdas=0,
                         matches=0,
                         deceptions=0,
                         whiles=0,
                         vars=1,
                         arrays=0,
                         nulls=0)
    expect(expected) {Score.runPlugin("VarField.scala")}
  }
}
