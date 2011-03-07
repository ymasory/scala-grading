package com.yuvimasory.scalagrading

import org.scalatest.FunSuite

class ArrayTests extends FunSuite {

  test("ArrayFactory") {
    val expected = Score(defs=0,
                         lambdas=0,
                         matches=0,
                         deceptions=0,
                         whiles=0,
                         vars=0,
                         arrays=1,
                         nulls=0)
    expect(expected) {Score.runPlugin("arrays/ArrayFactory.scala")}
    pending
  }
}
