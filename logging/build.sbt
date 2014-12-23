organization := Scaland.organization

name := Scaland.name("logging")

version := Scaland.version

scalaVersion := Scaland.scalaVersion

crossScalaVersions := Scaland.crossScalaVersions

publishTo <<= version {Scaland.publishLocation}

libraryDependencies ++= Seq(
  "org.slf4j"      %  "slf4j-api"       % "1.7.7",
  "org.scala-lang" % "scala-reflect" % s"${scalaVersion.value}",
  "org.scalatest"  %% "scalatest"       % "2.2.0" % "test",
  "org.mockito"    %  "mockito-all"     % "1.9.5" % "test",
  "ch.qos.logback" %  "logback-classic" % "1.1.2" % "test"
)

unmanagedSourceDirectories in Compile += (sourceDirectory in Compile).value / s"scala_${scalaBinaryVersion.value}"

sourceDirectories in Compile += (sourceDirectory in Compile).value / s"scala_${scalaBinaryVersion.value}"