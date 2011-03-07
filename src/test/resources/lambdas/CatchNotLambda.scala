object CatchNotLambda {

  val curDir = try {
    (new java.io.File(".")).getCanonicalPath
  }
  catch {
    case e => e.printStackTrace()
  }
}
