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

import com.alanrodas.scaland.properties

import language.postfixOps

import com.alanrodas.scaland._
import com.alanrodas.scaland.vcs._
import com.alanrodas.scaland.vcs.git._

import com.alanrodas.scaland.vcs.git.connectors.commandLineConnector
// import com.alanrodas.scaland.vcs.git.connectors.jGitConnector

import scala.util.{Try, Success, Failure}

object TestGit extends App {

  val maybeGit : Try[GitRepository] = Git open
      //("https://github.com/github/testrepo.git" branch "blue") at
      (userHome / "Projects" / "testrepo")
  for (git <- maybeGit) {
    val files = (git status).get
    println(Console.MAGENTA + "UNTRACKED " + (git ls).get)
    //println(git add "something")
    //println(git status)
  }
}
