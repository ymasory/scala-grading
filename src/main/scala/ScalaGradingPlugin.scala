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

  def report = {
    "FINAL REPORT" +
    "Bonus points: " + bonusPoints +
    "Deductions:   " + pointDeductions + "\n" +
    "Total: " + (bonusPoints - pointDeductions)
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
          (tree: @unchecked) match {

            case DefDef(_, name, _, _, _, _) if (name.startsWith("<") == false)
              && (tree.symbol.isSourceMethod)  => {
                global.reporter.info(tree.pos, "function def, +1", true)
                bonusPoints += 1
            }

            case Literal(Constant(null)) => {
              global.reporter.info(tree.pos, "null literal, -10", true)
              pointDeductions += 10
            }

            case _ =>
          }
        }
      }
    }
  }
}
