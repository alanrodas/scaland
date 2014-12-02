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

import scala.util.Try

import com.alanrodas.scaland.vcs._

case class GitRepository(override val rootDir : String, override val connector : GitConnector)
    extends Repository[GitConnector](rootDir, connector) {

  def add(resource : String, recursive : Boolean = true) = connector.add(rootDir, resource)

  def remove(resource : String, recursive : Boolean = true) = connector.remove(rootDir, resource)

  def move(resource : String, to: String) = connector.move(rootDir, resource, to)

  def commit(message : String, commitAll : Boolean = true) = connector.commit(rootDir, message)

  def status : Try[RepositoryStatus] = connector.status(rootDir)

  def branch(name : String, checkout : Boolean = false) = connector.branch(rootDir, name)

  def branches : List[String] = connector.branches(rootDir)

  def switch(name : String) = connector.switch(rootDir, name)

}
