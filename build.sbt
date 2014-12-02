import GhPagesKeys.ghpagesNoJekyll

organization := Scaland.organization

name := Scaland.name()

version := Scaland.version

scalaVersion := Scaland.scalaVersion

libraryDependencies ++= Scaland.dependencies

publishTo <<= version {Scaland.publishLocation}

lazy val core = project

lazy val cli = project.dependsOn(core)

lazy val vcs = project.dependsOn(core)

//override def managedStyle = ManagedStyle.Maven

//lazy val publishTo = Resolve  .sftp("My Maven Repo", "maven.example.org", "/var/www/maven/html")