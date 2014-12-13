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

import java.io.File

import org.tmatesoft.svn.core.SVNURL
import org.tmatesoft.svn.core.wc.{SVNRevision, SVNClientManager}

import scala.sys.process._
import scala.util.{Try, Success, Failure}

import com.alanrodas.scaland._
import com.alanrodas.scaland.vcs._

trait SvnConnector extends Connector[SvnRepository]

class SvnCommandLineConnector extends SvnConnector {

  class SVNException(message : String) extends RuntimeException(message)

  def exec[T](localPath : String, operation : String)(callback : (Int, String) => T) = ???

  def open(localPath : String) = ???

  def clone(localPath : String, repoUri : RepoUri) = ???

  def init(localPath : String) = ???

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


class SvnSVNKitConnector extends SvnConnector {

  private val clientManager = SVNClientManager.newInstance();

  def open(localPath : String) = ???

  def clone(localPath : String, repoUri : RepoUri) = {
    def isAllDigits(x: String) = x forall Character.isDigit
    try {
      val updateClient = clientManager.getUpdateClient()
      updateClient.setIgnoreExternals(false);
      val rev = repoUri.revision match {
        case HEAD => SVNRevision.HEAD
        case PREV => SVNRevision.PREVIOUS
        case other if isAllDigits(other.name) => SVNRevision.create(other.name.toInt)
        case other => SVNRevision.parse(other.name)
      }
      var res = updateClient.doCheckout(SVNURL.parseURIEncoded(repoUri.uri), new File(localPath), rev, rev, true)
      Success(SvnRepository(localPath, this))
    } catch {case e: Exception => Failure(e)}
  }

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