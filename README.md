# Overview #
scala-grading is a `scalac` compiler plugin implementing the following [homework grading policy](http://www.cis.upenn.edu/~matuszek/cis700-2010/Assignments/02-Playfair.html):


- You will gain 1 point for each function defined with def, up to a maximum of 10 points.
- You will gain 5 points for each literal function defined with =>, up to a maximum of 20 points.
- You will gain 5 points if your program uses a match.
- You will lose 15 points for every function or pattern match that isn't actually useful in the program, but is just there for no apparent reason other than to get extra points. Plus, we will get annoyed at you.
- You will lose 3 points for each occurrence of one of the keywords var and while, 5 points for each occurrence of Array (except in def main), and 10 points for each occurrence of null. (This is to get you to use for, List, and maybe Some).

# Build #
1. Install [sbt](http://code.google.com/p/simple-build-tool/wiki/Setup).
2. `cd scala-grading`
3. `sbt package`

You should now have `target/scala_2.8.1/scala-grading-alpha.jar`.

# Test #
The testers use `Runtime.exec` with "scalac" as an argument. This may or may not be the same `scalac` you get on the command line or in  your favorite IDE, or in sbt, or whatever. You have been warned.

    sbt
    trace 2
    test

# Run #
    cd scala-grading
    scalac -Xplugin:target/scala_2.8.1/scala-grading-alpha.jar src/test/resources/Playfair.scala

Alternatively, you can put the jar in `$SCALA_HOME/misc/scala-devel/plugins` and it will always be used, without any special arguments to `scalac`.

