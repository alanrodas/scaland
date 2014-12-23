organization := Scaland.organization

name := Scaland.name("vcs")

version := Scaland.version

scalaVersion := Scaland.scalaVersion

crossScalaVersions := Scaland.crossScalaVersions

libraryDependencies ++= Scaland.dependencies

publishTo <<= version {Scaland.publishLocation}