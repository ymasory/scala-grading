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

  override val name = "scala-grading"
  override val description = "grades functional style"
  override val components = List(ScalaGradingComponnet)
  
  object ScalaGradingComponnet extends PluginComponent {
    override val global: ScalaGrading.this.global.type = ScalaGrading.this.global
    override val runsAfter = List[String]("parser");
    override val runsRightAfter = Some("parser");
    override val phaseName = "scala-grading component"
    override def newPhase(_prev: Phase) = new ScalaGradingPhase(_prev)
    
    class ScalaGradingPhase(prev: Phase) extends StdPhase(prev) {
      override def name = "scala-grading phase"
      override def apply(unit: CompilationUnit) {

        //iterate over every subtree in the body of the compilation unit
        var curScore = Score.EmptyScore
        for (tree <- unit.body) {
          curScore = analyzeTree(tree, curScore)
        }
        println("\n" + curScore.report)
      }
    }
  }

  def analyzeTree(tree: Tree, score: Score): Score = {
    def info(msg: String) = global.reporter.info(tree.pos, msg, true)
    
    tree match {
      
      //find defs
      case tree @ DefDef(_, name, _, _, _, _)
        if (name.startsWith("<") == false) => {
          info("function def: " + tree.keyword)
          score.copy(defs = score.defs + 1)
      }

      //find _literal_ nulls
      case Literal(Constant(null)) => {
        info("null literal")
        score.copy(nulls = score.nulls + 1)
      }

      //find vars
      case tree @ ValDef(_, name, _, _) if (tree.keyword == "var") => {
        info("var: " + tree.keyword)
        score.copy(vars = score.vars + 1)
      }

      //find whiles
      case LabelDef(_, _, _) => {
        info("while")
        score.copy(whiles = score.whiles + 1)
      }

      //find matches
      case Match(_, _) => {
        info("match")
        score.copy(matches = score.matches + 1)
      }

      //find lambdas
      case Function(_, _) => {
        info("lambda")
        score.copy(lambdas = score.lambdas + 1)
      }

      case _ => score
    }
  }
}
