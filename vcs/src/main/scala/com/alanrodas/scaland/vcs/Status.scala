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

package com.alanrodas.scaland.vcs

sealed trait Status {
  def filename : String
  def status : String
  override def toString = status + ": " +filename
}
object Status {

  val unmodified = "Unmodified"
  val modified = "Modified"
  val added = "Added"
  val deleted = "Deleted"
  val renamed = "Renamed"
  val moved = "Moved"
  val untracked = "Untracked"

  def apply(filename : String) : Status = apply("", filename)
  def apply(status : String, filename : String) : Status = {
    status match {
      case "M" => Modified(filename)
      case "A" => Added(filename)
      case "D" => Deleted(filename)
      case "R" => Renamed(filename)
      case "U" => Unmodified(filename)
      case "??" => Untracked(filename)
      case _ => Unmodified(filename)
    }
  }
}
case class Unmodified(filename : String) extends Status {val status = "Unmodified"}
case class Modified(filename : String) extends Status {val status = "Modified"}
case class Added(filename : String) extends Status {val status = "Added"}
case class Deleted(filename : String) extends Status {val status ="Deleted"}
case class Renamed(filename : String)extends Status {val status ="Renamed"}
case class Moved(filename : String) extends Status {val status ="Moved"}
case class Untracked(filename : String) extends Status {val status ="Untracked"}

case class RepositoryStatus(val all : Seq[Status]) {
  private lazy val mappedStatuses = all.groupBy(_.status).toMap
  lazy val unmodified = mappedStatuses(Status.unmodified).map(_.filename)
  lazy val modified = mappedStatuses(Status.modified).map(_.filename)
  lazy val added = mappedStatuses(Status.added).map(_.filename)
  lazy val deleted = mappedStatuses(Status.deleted).map(_.filename)
  lazy val renamed = mappedStatuses(Status.renamed).map(_.filename)
  lazy val moved = mappedStatuses(Status.moved).map(_.filename)
  lazy val untracked = mappedStatuses(Status.untracked).map(_.filename)
  lazy val tracked =all.filterNot(_.status == Status.untracked).map(_.filename)

  override def toString = {
    ( for ( (statusName, statuses) <- all.groupBy(_.status) )
      yield statusName + statuses.mkString("\n" +
          statusName.replaceAll(".", "-") + "\n", "\n", "\n") ).mkString("\n")
  }
}