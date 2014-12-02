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

package com.alanrodas.scaland

import scala.util.Try

package object vcs {
  def currentPath = properties.user.dir
  def userHome = properties.user.home

  implicit def string2repository(remote : String) : RepoUri = RepoUri(remote)
  implicit def string2revision(revision : String) : Revision = new Revision{def name = revision}
  implicit def int2revision(version : Int) : Revision = new Revision{def name = version.toString}

  /**
   * This class is intended to provide a more elegant DSL when
   * cloning a repository from a remote location.
   *
   * @param vcs The VCS that has created this cloner.
   * @param repository The remote repository to download.
   * @tparam R The repository type of the VCS.
   * @tparam C The connector type ofthe VCS.
   */
  case class Cloner[R <: Repository[C],C <: Connector[R]]
  (vcs : VCS[R, C], repository : RepoUri)(implicit connector : C) {
    def here = at(currentPath)
    def atDefault : Try[R] = at(currentPath / repository.uri.filename.split("\\.").head)
    def at(localPath : String) : Try[R] = vcs.clone(repository, localPath)
  }

  /** Transforms a [[Cloner]] to a Try. */
  implicit def cloner2TRepo[R <: Repository[C], C <: Connector[R]](cloner : Cloner[R, C]) : Try[R] =
    cloner.at(currentPath)
}
