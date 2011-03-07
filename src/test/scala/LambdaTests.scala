package com.yuvimasory.scalagrading

import org.scalatest.FunSuite

class LambdaTests extends FunSuite {

  test("ArrowLambda") {
    val expected = Score(defs=0,
                         lambdas=1,
                         matches=0,
                         deceptions=0,
                         whiles=0,
                         vars=0,
                         arrays=0,
                         nulls=0)
    expect(expected) {Score.runPlugin("lambdas/ArrowLambda.scala")}
  }

  test("UnderscoreLambda") {
    val expected = Score(defs=0,
                         lambdas=1,
                         matches=0,
                         deceptions=0,
                         whiles=0,
                         vars=0,
                         arrays=0,
                         nulls=0)
    expect(expected) {Score.runPlugin("lambdas/UnderscoreLambda.scala")}
  }
}
