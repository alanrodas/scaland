organization := Scaland.organization

name := Scaland.name("core")

version := Scaland.version

scalaVersion := Scaland.scalaVersion

libraryDependencies ++= Scaland.dependencies

publishTo <<= version {Scaland.publishLocation}