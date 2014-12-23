organization := Scaland.organization

name := Scaland.name("vcs-git")

version := Scaland.version

scalaVersion := Scaland.scalaVersion

crossScalaVersions := Scaland.crossScalaVersions

libraryDependencies ++= Scaland.dependencies

libraryDependencies += "org.eclipse.jgit" % "org.eclipse.jgit" % "3.3.2.201404171909-r"

publishTo <<= version {Scaland.publishLocation}