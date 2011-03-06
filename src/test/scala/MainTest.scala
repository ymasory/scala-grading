package com.yuvimasory.scalagrading

import org.scalatest.FunSuite

class ScalaGradingTests extends FunSuite {

  test("one var") {
    expect(Score(0, 0, 0, 0, 1, 0, 0, 0)) {
      Score.runPlugin("OneVar.scala")
    }
  }
}
