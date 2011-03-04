package com.yuvimasory.scalagrading

import org.scalatest.FunSuite

class MainTest extends FunSuite {

  test("scala-grading test works") {
    assert(1 === 1)

    expect(1) {
      2 - 1
    }

    intercept[IllegalArgumentException] {
      throw new IllegalArgumentException()
    }
  }
}
