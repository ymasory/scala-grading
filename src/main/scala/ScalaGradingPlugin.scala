package com.yuvimasory.scalagrading

import scala.tools.nsc
import nsc.Global
import nsc.Phase
import nsc.plugins.Plugin
import nsc.plugins.PluginComponent

class ScalaGrading(val global: Global) extends Plugin {
  import global._

  var numDefs = 0
  var numLambdas = 0
  var numMatches = 0
  var numDeceptions = 0
  var numWhiles = 0
  var numVars = 0
  var numArrays = 0
  var numNulls = 0

  override val name = "scala-grading"
  override val description = "grades functional style"
  
  override val components = List(ScalaGradingComponnet)

  val LF = "\n"
  def report = {
    "DETAILS" + LF +
    "-------" + LF +
    "defs:       " + numDefs + LF +
    "lambdas:    " + numLambdas + LF +
    "matches:    " + numMatches + LF +
    "deceptions: " + numDeceptions + LF +
    "whiles:     " + numWhiles + LF +
    "vars:       " + numVars + LF +
    "nulls:      " + numNulls + LF +
    "arrays:     " + numArrays + LF + LF +
    "SCORE" + LF +
    "-----" + LF + score
  }

  def score = {
    import java.lang.Math.{min, max}
    min(10, numDefs) +
    min(20, numLambdas * 5) +
    (if (numMatches > 0) 5 else 0) -
    (numDeceptions * 15) -
    (numWhiles * 3) -
    (numVars * 3) -
    (numArrays * 5) -
    (numNulls * 10)
  }
  
  object ScalaGradingComponnet extends PluginComponent {
    override val global: ScalaGrading.this.global.type = ScalaGrading.this.global
    override val runsAfter = List[String]("refchecks");
    override val phaseName = "scala-grading component"
    override def newPhase(_prev: Phase) = new ScalaGradingPhase(_prev)
    
    class ScalaGradingPhase(prev: Phase) extends StdPhase(prev) {
      override def name = "scala-grading phase"
      override def apply(unit: CompilationUnit) {
        for (tree <- unit.body) {
          def info(msg: String) = global.reporter.info(tree.pos, msg, true)

          (tree: @unchecked) match {

            case DefDef(_, name, _, _, _, _) if (name.startsWith("<") == false)
              && (tree.symbol.isSourceMethod)  => {
                info("function def, +1")
                numDefs += 1
            }

            case Literal(Constant(null)) => {
              info("null literal, -10")
              numNulls += 1
            }

            case _ =>
          }
        }

        println(LF + report)
      }
    }
  }
}
