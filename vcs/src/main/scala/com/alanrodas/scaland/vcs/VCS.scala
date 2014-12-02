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

import scala.util.Try

/**
 * This trait is an abstract representation of a version control system.
 *
 * This trait should be mixed with objects that provide the common operations
 * to start a version control repository. It provides a set of methods that act
 * as a DSL for creating a common language when downloading or creating a repository.
 *
 * This trait requires two type arguments. The first is the type of repository that
 * this object returns.
 *
 * @tparam TRepository The repository for this VCS.
 * @tparam TConnector The connector for this repository
 */
trait VCS[TRepository <: Repository[TConnector], TConnector <: Connector[TRepository]] {

  /**
   * Returns a TRepository for the given localPath.
   *
   * If the local path is not a valid repository, then return a
   * Failure.
   *
   * @param connector The connector to use to perform the actual action.
   */
  def open(localPath : String)(implicit connector : TConnector) : Try[TRepository]

  /**
   * Clone a repository from a remote location to a given directory.
   *
   * Implement this method to provide behavior for clone and checkout.
   * This method returns a Try for the repository. An error may be returned
   * for several different reasons, such as a non existent repository, a not
   * available local path, not enough privileges or other.
   *
   * @param repoUrl The remote repository url.
   * @param localPath The directory url where to download the repository.
   * @param connector The connector to use to perform the actual action.
   */
  def clone(repoUrl : RepoUri, localPath : String)(implicit connector : TConnector) : Try[TRepository]

  /**
   * Returns a [[Cloner]] for the given repository.
   *
   * The [[Cloner]] allows to specify a location to download the repository
   * to, weather is at the current directory, or at a given path.
   * This method is pretty much provided by the behavior of clone.
   *
   * @param repoUrl The remote repository url.
   * @param connector The connector to use to perform the actual action.
   */
  def clone(repoUrl : RepoUri)(implicit connector : TConnector) = Cloner(this, repoUrl)

  /**
   * Clone a repository from a remote location to a given directory.
   *
   * This method returns a Try for the repository. An error may be returned
   * for several different reasons, such as a non existent repository, a not
   * available local path, not enough privileges or other.
   *
   * @param repoUrl The remote repository url.
   * @param localPath The directory url where to download the repository.
   * @param connector The connector to use to perform the actual action.
   */
  def checkout(repoUrl : RepoUri, localPath : String)(implicit connector : TConnector) = clone(repoUrl, localPath)


  /**
   * Returns a [[Cloner]] for the given repository.
   *
   * The [[Cloner]] allows to specify a location to download the repository
   * to, weather is at the current directory, or at a given path.
   * This method is pretty much provided by the behavior of clone.
   *
   * @param repository The remote repository url.
   * @param connector The connector to use to perform the actual action.
   */
  def checkout(repository : RepoUri)(implicit connector : TConnector)  = clone(repository)

  /**
   * Start a repository in the current directory.
   *
   * This method returns a Try for the repository. An error may be returned
   * for several different reasons, such as a non available local path, not
   * enough privileges or other.
   */
  def init(implicit connector : TConnector) : Try[TRepository] = init(currentPath)

  /**
   * Start a repository in the current directory.
   *
   * This method returns a Try for the repository. An error may be returned
   * for several different reasons, such as a non available local path, not
   * enough privileges or other.
   *
   * Implement this method to provide behavior for init and checkout
   * methods.
   *
   * @param localPath The directory url where to download the repository.
   */
  def init(localPath : String)(implicit connector : TConnector) : Try[TRepository]

  /**
   * Start a repository in the current directory.
   *
   * This method returns a Try for the repository. An error may be returned
   * for several different reasons, such as a non available local path, not
   * enough privileges or other.
   */
  def create(implicit connector : TConnector) = init

  /**
   * Start a repository in the current directory.
   *
   * This method returns a Try for the repository. An error may be returned
   * for several different reasons, such as a non available local path, not
   * enough privileges or other.
   *
   * @param localPath The directory url where to download the repository.
   */
  def create(localPath : String)(implicit connector : TConnector) = init(localPath)

}