organization := Scaland.organization

name := Scaland.name("io-compressed")

version := Scaland.version

crossScalaVersions := Scaland.crossScalaVersions

libraryDependencies ++= Scaland.dependencies

publishTo <<= version {Scaland.publishLocation}