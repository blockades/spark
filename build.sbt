import sbtassembly.AssemblyPlugin.autoImport._
import sbtsparksubmit.SparkSubmitPlugin.autoImport._

name := "openblockchain"

version := "1.0.0"

scalaVersion := "2.11.8"

libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-core" % "2.0.0" % "provided",
  "org.apache.spark" %% "spark-sql" % "2.0.0" % "provided",
  "com.datastax.spark" %% "spark-cassandra-connector" % "2.0.0-M1",
  "org.json4s" %% "json4s-jackson" % "3.3.0.RC2"
//  "org.scalaj" %% "scalaj-http" % "2.0.0"
)

dependencyOverrides ++= Set(
  "com.fasterxml.jackson.core" % "jackson-databind" % "2.6.5",
  "com.fasterxml.jackson.core" % "jackson-annotations" % "2.6.5",
  "org.scala-lang" % "scala-compiler" % scalaVersion.value,
  "org.slf4j" % "slf4j-api" % "1.7.16"
)

assemblyMergeStrategy in assembly := {
  case x if x.endsWith(".class") => MergeStrategy.last
  case x if x.endsWith(".properties") => MergeStrategy.last
  case x if x.contains("/resources/") => MergeStrategy.last
  case x if x.startsWith("META-INF/mailcap") => MergeStrategy.last
  case x if x.startsWith("META-INF/mimetypes.default") => MergeStrategy.first
  case x if x.startsWith("META-INF/maven/org.slf4j/slf4j-api/pom.") => MergeStrategy.first
  case x =>
    val oldStrategy = (assemblyMergeStrategy in assembly).value
    if (oldStrategy == MergeStrategy.deduplicate)
      MergeStrategy.first
    else
      oldStrategy(x)
}

assemblyShadeRules in assembly := Seq(
  ShadeRule.rename("com.google.**" -> "shadeio.@1").inAll
)

// Use assembly to create the uber jar
sparkSubmitJar := assembly.value.getAbsolutePath

// Import submit tasks
SparkSubmit.configurations

connectInput in run := true

