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
  
  override val components = List[PluginComponent] (
    SGDefComponent,
    SGNullComponent
  )
  

  private object SGNullComponent extends PluginComponent {
    override val global: ScalaGrading.this.global.type = ScalaGrading.this.global
    override val runsAfter = List[String]("refchecks");
    override val phaseName = getClass.getName
    override def newPhase(_prev: Phase) = new SGNullPhase(_prev)
    
    class SGNullPhase(prev: Phase) extends StdPhase(prev) {
      override def name = getClass.getName
      override def apply(unit: CompilationUnit) {
        for (t @ DefDef(_, name, _, _, _, _) <- unit.body) {
          unit.warning(t.pos, "null def " + name + ", +1")
          bonusPoints += 1
        }
      }
    }
  }


  private object SGDefComponent extends PluginComponent {
    override val global: ScalaGrading.this.global.type = ScalaGrading.this.global
    override val runsAfter = List[String]("refchecks");
    override val phaseName = getClass.getName
    override def newPhase(_prev: Phase) = new SGDefPhase(_prev)
    
    class SGDefPhase(prev: Phase) extends StdPhase(prev) {
      override def name = getClass.getName
      override def apply(unit: CompilationUnit) {
        for (t @ DefDef(_, name, _, _, _, _) <- unit.body) {
          unit.warning(t.pos, "function def " + name + ", +1")
          bonusPoints += 1
        }
      }
    }
  }
}
