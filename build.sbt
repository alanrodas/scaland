organization := Scaland.organization

name := Scaland.name("parent")

version := Scaland.version

crossScalaVersions := Scaland.crossScalaVersions

libraryDependencies ++= Scaland.dependencies

publishTo <<= version {Scaland.publishLocation}

lazy val core = project

lazy val cli = project dependsOn core

lazy val vcs = project dependsOn (core, io)

    lazy val vcs_svn = project in file("vcs/svn") dependsOn vcs

    lazy val vcs_git = project in file("vcs/git") dependsOn vcs

lazy val io = project dependsOn core

    lazy val io_compressed = project in file("io/compressed") dependsOn io

    lazy val io_fs = project in file("io/fs") dependsOn io

    lazy val io_net = project in file("io/net") dependsOn io

//libraryDependencies ++= Seq(
//    "com.propensive" %% "rapture-io" % "0.10.0",
//    "com.propensive" %% "rapture-net" % "0.10.0",
//    "com.propensive" %% "rapture-fs" % "0.10.0"
//)