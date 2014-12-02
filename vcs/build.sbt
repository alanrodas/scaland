organization := Scaland.organization

name := Scaland.name("vcs")

version := Scaland.version

scalaVersion := Scaland.scalaVersion

libraryDependencies ++= Scaland.dependencies

publishTo <<= version {Scaland.publishLocation}

lazy val core = (project in file("../core"))

lazy val vcs = (project in file(".")).dependsOn(core)

lazy val svn = project.dependsOn(vcs)

lazy val git = project.dependsOn(vcs)

//libraryDependencies ++= Seq(
//  "com.propensive" %% "rapture-io" % "0.9.1",
//  "com.propensive" %% "rapture-fs" % "0.9.1",
//  "com.propensive" %% "rapture-net" % "0.9.0"
//)
