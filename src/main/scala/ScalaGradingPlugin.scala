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
  
  val components = List[PluginComponent](ScalaGradingComponent)
  
  private object ScalaGradingComponent extends PluginComponent {
    val global: ScalaGrading.this.global.type = ScalaGrading.this.global
    val runsAfter = List[String]("refchecks");
    val phaseName = ScalaGrading.this.name
    def newPhase(_prev: Phase) = new SGNullPhase(_prev)
    
    class SGNullPhase(prev: Phase) extends StdPhase(prev) {
      override def name = ScalaGrading.this.name
      def apply(unit: CompilationUnit) {
        for ( tree @ Apply(Select(rcvr, nme.DIV),
                           List(Literal(Constant(0)))) <- unit.body;
             if rcvr.tpe <:< definitions.IntClass.tpe) 
          {
            unit.error(null, "definitely division by zero")
          }
        for (el <- unit.body) {
          println(el)
          println(el.getClass)
          println("#############")
        }
      }
    }
  }
}
