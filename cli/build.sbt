organization := Scaland.organization

name := Scaland.name("cli")

version := Scaland.version

crossScalaVersions := Scaland.crossScalaVersions

libraryDependencies ++= Scaland.dependencies

publishTo <<= version {Scaland.publishLocation}