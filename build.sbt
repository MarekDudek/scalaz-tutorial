name := "scalaz-tutorial"

version := "0.1"

scalaVersion in ThisBuild := "2.12.6"

scalacOptions in ThisBuild ++= Seq(
  "-language:_",
  "-Ypartial-unification",
  "-Xfatal-warnings"
)

libraryDependencies ++= Seq(
  "com.github.mpilquist" %% "simulacrum"  % "0.12.0",
  "org.scalaz"           %% "scalaz-core" % "7.2.22"
)

libraryDependencies += "org.scalatest" % "scalatest_2.12" % "3.0.5" % "test"

libraryDependencies += "eu.timepit" %% "refined-scalaz" % "0.8.7"

addCompilerPlugin("org.spire-math" %% "kind-projector" % "0.9.6")
addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.1" cross CrossVersion.full)
addCompilerPlugin("com.olegpy" %% "better-monadic-for" % "0.2.4")
