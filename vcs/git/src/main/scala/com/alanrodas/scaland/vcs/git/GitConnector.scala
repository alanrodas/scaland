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

package com.alanrodas.scaland.vcs.git

import java.io.File

import scala.sys.process._
import scala.util.{Try, Success, Failure}

import com.alanrodas.scaland._
import com.alanrodas.scaland.vcs._

trait GitConnector extends Connector[GitRepository]

class GitCommandLineConnector extends GitConnector {

  class GitException(message : String) extends RuntimeException(message)

  def exec[T](localPath : String, operation : String)(callback : (Int, String) => T) = {
    val (code, message) = (operation cwd localPath) !!!;
    if (code == 0) {Success(callback(code, message))}
    else {Failure(new GitException(message))}
  }

  def open(localPath : String) = {
    exec(localPath, "git rev-parse --is-inside-work-tree")
    {(_,_) => GitRepository(localPath, this)}
  }

  def clone(localPath : String, repoUri : RepoUri) = {
    val RepoUri(uri, branch, rev) = repoUri
    val call = s"git clone $uri $localPath" + (if (branch.nonEmpty) " --branch " + branch else "")
    exec(localPath, call) { (_,_) => GitRepository(localPath, this) }
  }

  def init(localPath : String) =
    exec(localPath, s"git init $localPath") { (_,_) => GitRepository(localPath, this) }

  def add(localPath : String, resource : String, recursive : Boolean = true) = ???

  def remove(localPath : String, resource : String, recursive : Boolean = true) = ???

  def move(localPath : String, resource : String, to: String) = ???

  def commit(localPath : String, message : String, commitAll : Boolean = true) = ???


  def status(localPath : String) = {
    val maybeAllManaged : Try[Seq[Status]] = exec(localPath, s"git ls-tree -r --name-only HEAD") { (code, message) =>
      message.split("\n").map(Status(_))
    }
    val maybeModified : Try[Seq[Status]] = exec(localPath, "git status -s") { (code, message) =>
      message.split("\n").map{e => val splitted = e.trim.split(" ")
        Status(splitted.head, splitted.last)
      }
    }
    (maybeAllManaged, maybeModified) match {
      case (_, Failure(e)) => Failure(e)
      case (Failure(e), _) => Failure(e)
      case (Success(allManaged), Success(modified)) => {
        Success(RepositoryStatus(allManaged.filterNot(modified.contains(_)) ++ modified))
      }
    }
  }

  def branch(localPath : String, name : String, checkout : Boolean = false) = ???

  def branches(localPath : String) = ???

  def switch(localPath : String, name : String) = ???
}


class GitJGitConnector extends GitConnector {

  var repository : org.eclipse.jgit.api.Git = null

  private def gitClone(repoUri : RepoUri, localPath : String) = {
      val builder = org.eclipse.jgit.api.Git.cloneRepository()
          .setURI(repoUri.uri)
          .setDirectory(new File(localPath))
      if (repoUri.branch.nonEmpty) builder.setBranch(repoUri.branch)
      repository = builder.call()
      new GitRepository(localPath, this)
  }

  def open(localPath : String) = ???

  def clone(localPath : String, repoUri : RepoUri) =
    Try(gitClone(repoUri, localPath))

  def init(localPath : String) = ???

  def add(localPath : String, resource : String, recursive : Boolean = true) = ???

  def remove(localPath : String, resource : String, recursive : Boolean = true) = ???

  def move(localPath : String, resource : String, to: String) = ???

  def commit(localPath : String, message : String, commitAll : Boolean = true) = ???

  def status(localPath : String) = ???

  def branch(localPath : String, name : String, checkout : Boolean = false) = ???

  def branches(localPath : String) = ???

  def switch(localPath : String, name : String) = ???
}