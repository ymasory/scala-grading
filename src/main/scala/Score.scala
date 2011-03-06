package com.yuvimasory.scalagrading

case class Score (
  defs: Int,
  lambdas: Int,
  matches: Int,
  deceptions: Int,
  whiles: Int,
  vars: Int,
  arrays: Int,
  nulls: Int
) {

  val score = {
    import java.lang.Math.min
    min(10, defs) +
    min(20, lambdas * 5) +
    (if (matches > 0) 5 else 0) -
    (deceptions * 15) -
    (whiles * 3) -
    (vars * 3) -
    (arrays * 5) -
    (nulls * 10)
  }

  val LF = "\n"
  override val toString = {
    "DETAILS" + LF +
    "-------" + LF +
    "defs:       " + defs + LF +
    "lambdas:    " + lambdas + LF +
    "matches:    " + matches + LF +
    "deceptions: " + deceptions + LF +
    "whiles:     " + whiles + LF +
    "vars:       " + vars + LF +
    "nulls:      " + nulls + LF +
    "arrays:     " + arrays + LF + LF +
    "SCORE" + LF +
    "-----" + LF + score

  }
}
