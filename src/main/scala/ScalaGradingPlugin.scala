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
  
  override val components = List (
    SGDefComponent,
    SGNullComponent
  )

  def report = {
    "FINAL REPORT" +
    "Bonus points: " + bonusPoints +
    "Deductions:   " + pointDeductions + "\n" +
    "Total: " + (bonusPoints - pointDeductions)
  }
  

  protected abstract class SGAbstractComponent extends PluginComponent {
    override val global: ScalaGrading.this.global.type = ScalaGrading.this.global
    override val runsAfter = List[String]("refchecks");
    override val phaseName = getClass.getName.split("\\$").last
  }

  object SGNullComponent extends SGAbstractComponent {
    override def newPhase(_prev: Phase) = new SGNullPhase(_prev)
    
    class SGNullPhase(prev: Phase) extends StdPhase(prev) {
      override def apply(unit: CompilationUnit) {
        for (t @ DefDef(_, name, _, _, _, _) <- unit.body) {
          global.reporter.info(t.pos, "null def " + name + ", +1", true)
          bonusPoints += 1
        }
      }
    }
  }

  object SGDefComponent extends SGAbstractComponent {
    override def newPhase(_prev: Phase) = new SGDefPhase(_prev)
    
    class SGDefPhase(prev: Phase) extends StdPhase(prev) {
      override def name = getClass.getName.split("\\$").last
      override def apply(unit: CompilationUnit) {
        for (t @ DefDef(_, name, _, _, _, _) <- unit.body) {
          global.reporter.info(t.pos, "null def " + name + ", +1", true)
          bonusPoints += 1
        }
      }
    }
  }


}
