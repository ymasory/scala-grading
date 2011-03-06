package com.yuvimasory.scalagrading

import scala.tools.nsc
import nsc.Global
import nsc.Phase
import nsc.plugins.Plugin
import nsc.plugins.PluginComponent

class ScalaGrading(val global: Global) extends Plugin {
  import global._

  var bonusPoints = 0
  var pointDeductions = 0

  override val name = "scala-grading"
  override val description = "grades functional style"
  
  override val components = List(ScalaGradingComponnet)

  val LF = "\n"
  def report = {
    "FINAL REPORT" + LF +
    "Bonus points: " + bonusPoints + LF +
    "Deductions:   " + pointDeductions + LF +
    "Total: " + (bonusPoints - pointDeductions) + LF
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
                bonusPoints += 1
            }

            case Literal(Constant(null)) => {
              info("null literal, -10")
              pointDeductions += 10
            }

            case _ =>
          }
        }

        println("\n" + report)
      }
    }
  }
}
