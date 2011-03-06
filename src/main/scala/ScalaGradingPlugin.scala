package com.yuvimasory.scalagrading

import scala.tools.nsc
import nsc.Global
import nsc.Phase
import nsc.plugins.Plugin
import nsc.plugins.PluginComponent

class ScalaGrading(val global: Global) extends Plugin {
  import global._

  val name = "scala-grading"
  val description = "grades functional style"

  var bonusPoints = 0
  var pointDeductions = 0
  
  val components = List[PluginComponent](ScalaGradingComponent)
  
  private object ScalaGradingComponent extends PluginComponent {
    val global: ScalaGrading.this.global.type = ScalaGrading.this.global
    val runsAfter = List[String]("refchecks");
    val phaseName = ScalaGrading.this.name
    def newPhase(_prev: Phase) = new SGDefPhase(_prev)
    
    class SGDefPhase(prev: Phase) extends StdPhase(prev) {
      override def name = ScalaGrading.this.name
      def apply(unit: CompilationUnit) {
        for (t @ DefDef(_, name, _, _, _, _) <- unit.body) {
          unit.warning(t.pos, "function def " + name + ", +1")
          bonusPoints += 1
        }
      }
    }
  }
}
