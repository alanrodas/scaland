organization := Scaland.organization

name := Scaland.name("vcs-svn")

version := Scaland.version

scalaVersion := Scaland.scalaVersion

libraryDependencies ++= Scaland.dependencies

libraryDependencies ++= Seq(
  "org.tmatesoft.svnkit" % "svnkit" % "1.8.3"
)

publishTo <<= version {Scaland.publishLocation}