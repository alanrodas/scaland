/*
 * Copyright 2014 Alan Rodas Bonjour
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language governing permissions
 * and limitations under the License.
 */

import sbt.Keys._
import sbt._

object Scaland {
  val organization = "com.alanrodas"
  val version = "0.2"
  val scalaVersion = "2.11.4"
  val crossScalaVersions = Seq("2.10.4", "2.11.4")
  def name = "scaland"
  def name(name : String) = "scaland-" + name
  def dependencies = Seq(
    "org.scalatest" %% "scalatest" % "2.2.1" % "test"
  )

  private def getFileResolver(location : String) : Option[Resolver] = {
    Some(Resolver.file("file",
      new File("../alanrodas.github.io/maven/" + location)))
  }

  val publishLocation = {(v: String) =>
    if (v.trim.endsWith("SNAPSHOT")) {getFileResolver("snapshots")}
    else {getFileResolver("releases")}
  }

}