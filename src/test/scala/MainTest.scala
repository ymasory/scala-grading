package com.yuvimasory.scalagrading

import org.scalatest.FunSuite

class ScalaGradingTests extends FunSuite {

  test("NullFieldLiteral") {
    expect(Score(defs=0,
                 lambdas=0,
                 matches=0,
                 deceptions=0,
                 whiles=0,
                 vars=0,
                 nulls=1,
                 arrays=0)) {
      Score.runPlugin("NullFieldLiteral.scala")
    }
  }
  
  test("OneVar") {
    expect(Score(defs=0,
                 lambdas=0,
                 matches=0,
                 deceptions=0,
                 whiles=0,
                 vars=1,
                 nulls=0,
                 arrays=0)) {
      Score.runPlugin("OneVar.scala")
      pending
    }
  }
}
