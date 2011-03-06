package com.yuvimasory.scalagrading

import scala.tools.nsc
import nsc.Global
import nsc.Phase
import nsc.plugins.Plugin
import nsc.plugins.PluginComponent

/** Compiler plugin implementing the homework grading guidelines found at
  * http://www.cis.upenn.edu/~matuszek/cis700-2010/Assignments/02-Playfair.html
  */
class ScalaGrading(val global: Global) extends Plugin {
  import global._

  //things we're looking for in the homeworks
  var numDefs = 0
  var numLambdas = 0
  var numMatches = 0
  var numDeceptions = 0
  var numWhiles = 0
  var numVars = 0
  var numArrays = 0
  var numNulls = 0

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

  //calculate score using guidelines
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

  override val name = "scala-grading"
  override val description = "grades functional style"
  override val components = List(ScalaGradingComponnet)
  
  object ScalaGradingComponnet extends PluginComponent {
    override val global: ScalaGrading.this.global.type = ScalaGrading.this.global
    override val runsAfter = List[String]("parser");
    override val phaseName = "scala-grading component"
    override def newPhase(_prev: Phase) = new ScalaGradingPhase(_prev)
    
    class ScalaGradingPhase(prev: Phase) extends StdPhase(prev) {
      override def name = "scala-grading phase"
      override def apply(unit: CompilationUnit) {

        //iterate over every subtree in the body of the compilation unit
        for (tree <- unit.body) {
          def info(msg: String) = global.reporter.info(tree.pos, msg, true)

          tree match {

            //find defs
            case DefDef(_, name, _, _, _, _) if (name.startsWith("<") == false)
              && (tree.symbol.isSourceMethod)  => {
                info("function def")
                numDefs += 1
            }

            //find _literal_ nulls
            case Literal(Constant(null)) => {
              info("null literal")
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
