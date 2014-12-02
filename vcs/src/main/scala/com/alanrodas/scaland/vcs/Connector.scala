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

trait Connector[TRepository <: Repository[_ <: Connector[TRepository]]] {

  /**
   * Return a directory for the given local path.
   *
   * It fails in case the localPath is not a valid VCS repository.
   */
  def open(localPath : String) : Try[TRepository]

  /**
   * Clone a remote repository at a given revision into a local path.
   *
   * @param repoUri The repository URL to download
   */
  def clone(localPath : String, repoUri : RepoUri) : Try[TRepository]

  /**
   * Init a repository in a local path.
   *
   * @param localPath The local path where to start the repository.
   */
  def init(localPath : String) : Try[TRepository]

  /**
   * Add a given resource to the repository.
   *
   * @param resource The resource to add.
   * @param recursive Add recursively if it's a folder.
   * @return a Try or an Error depending if the operation was successful or not.
   */
  def add(localPath : String, resource : String, recursive : Boolean = true) : Try[TRepository]


  /**
   * Remove a given resource to the repository.
   *
   * @param resource The resource to remove.
   * @param recursive Remove recursively if it's a folder.
   * @return a Try or an Error depending if the operation was successful or not.
   */
  def remove(localPath : String, resource : String, recursive : Boolean = true) : Try[TRepository]

  /**
   * Move/Rename a given resource to a new name.
   *
   * @param resource The resource to add.
   * @return a Try or an Error depending if the operation was successful or not.
   */
  def move(localPath : String, resource : String, to: String) : Try[TRepository]

  /**
   * Commit/Save the current state of the repository.
   *
   * @param message The commit message.
   * @param commitAll Commit all modified values or only the added ones.
   * @return a Try or an Error depending if the operation was successful or not.
   */
  def commit(localPath : String, message : String, commitAll : Boolean = true) : Try[TRepository]

  /**
   * List the status of the current repository.
   *
   * That is, the elements that where added, modified, deleted,
   * renamed, and the ones that are not under version control.
   */
  def status(localPath : String) : Try[RepositoryStatus]

  /**
   * Create a new branch by the given name.
   *
   * @param name The name of the new branch.
   * @param checkout Checkout the newly created branch.
   * @return a Try or an Error depending if the operation was successful or not.
   */
  def branch(localPath : String, name : String, checkout : Boolean = false) : Try[TRepository]

  /** List all the available branches. */
  def branches(localPath : String) : List[String]

  /**
   * Switch to a branch by the given name.
   *
   * @param name The name of the branch to switch to.
   * @return a Try or an Error depending if the operation was successful or not.
   */
  def switch(localPath : String, name : String) : Try[TRepository]
}