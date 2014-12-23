organization := Scaland.organization

name := Scaland.name("io-fs")

version := Scaland.version

scalaVersion := Scaland.scalaVersion

crossScalaVersions := Scaland.crossScalaVersions

libraryDependencies ++= Scaland.dependencies

publishTo <<= version {Scaland.publishLocation}