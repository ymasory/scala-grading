package com.yuvimasory.scalagrading

import org.scalatest.FunSuite

class DefTests extends FunSuite {

  test("FooDef") {
    val expected = Score(defs=1,
                         lambdas=0,
                         matches=0,
                         deceptions=0,
                         whiles=0,
                         vars=0,
                         arrays=0,
                         nulls=0)
    expect(expected) {Score.runPlugin("FooDef.scala")}
  }

  test("MainDef") {
    val expected = Score(defs=1,
                         lambdas=0,
                         matches=0,
                         deceptions=0,
                         whiles=0,
                         vars=0,
                         arrays=0,
                         nulls=0)
    expect(expected) {Score.runPlugin("MainDef.scala")}
  }
}
