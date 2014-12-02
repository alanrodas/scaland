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
 * This class represents a repository in the local path.
 *
 * Repository provides all the common operations that you
 * can perform over a local repository.
 *
 * @param rootDir The root directory where this repository is located.
 */
abstract class Repository[TConnector <: Connector[_ <: Repository[TConnector]]]
    (val rootDir : String, val connector : TConnector) {

  /**
   * Add a given resource to the repository.
   *
   * @param resource The resource to add.
   * @param recursive Add recursively if it's a folder.
   * @return a Try or an Error depending if the operation was successful or not.
   */
  def add(resource : String, recursive : Boolean = true) : Try[_ <: Repository[TConnector]]

  /**
   * Add a given set of resources to the repository.
   *
   * @param resources The resources to add.
   * @param recursive Add recursively if it's a folder.
   * @return a Try or an Error depending if the operation was successful or not.
   */
  def addAll(resources : Seq[String], recursive : Boolean = true) = {
    for (resource <- resources) yield add(resource)
  }

  /**
   * Remove a given resource to the repository.
   *
   * @param resource The resource to remove.
   * @param recursive Remove recursively if it's a folder.
   * @return a Try or an Error depending if the operation was successful or not.
   */
  def remove(resource : String, recursive : Boolean = true) : Try[_ <: Repository[TConnector]]

  /**
   * Remove a given resource to the repository.
   *
   * @param resource The resource to remove.
   * @param recursive Remove recursively if it's a folder.
   * @return a Try or an Error depending if the operation was successful or not.
   */
  def rm(resource : String, recursive : Boolean = true) = remove(resource)

  /**
   * Remove a given resource to the repository.
   *
   * @param resource The resource to remove.
   * @param recursive Remove recursively if it's a folder.
   * @return a Try or an Error depending if the operation was successful or not.
   */
  def delete(resource : String, recursive : Boolean = true) = remove(resource)

  /**
   * Move/Rename a given resource to a new name.
   *
   * @param resource The resource to add.
   * @return a Try or an Error depending if the operation was successful or not.
   */
  def move(resource : String, to: String) : Try[_ <: Repository[TConnector]]

  /**
   * Move/Rename a given resource to a new name.
   *
   * @param resource The resource to add.
   * @return a Try or an Error depending if the operation was successful or not.
   */
  def mv(resource : String, to: String) = move(resource, to)

  /**
   * Move/Rename a given resource to a new name.
   *
   * @param resource The resource to add.
   * @return a Try or an Error depending if the operation was successful or not.
   */
  def rename(resource : String, to: String) = mv(resource, to)

  /**
   * Commit/Save the current state of the repository.
   *
   * @param message The commit message.
   * @param commitAll Commit all modified values or only the added ones.
   * @return a Try or an Error depending if the operation was successful or not.
   */
  def commit(message : String, commitAll : Boolean = true) : Try[_ <: Repository[TConnector]]

  /**
   * List the status of the current repository.
   *
   * That is, the elements that where added, modified, deleted,
   * renamed, and the ones that are not under version control.
   */
  def status : Try[RepositoryStatus]

  /** List all files that are currently under version control. */
  def list = status.map(_.tracked)

  /** List all files that are currently under version control. */
  def ls = list

  /**
   * Create a new branch by the given name.
   *
   * @param name The name of the new branch.
   * @param checkout Checkout the newly created branch.
   * @return a Try or an Error depending if the operation was successful or not.
   */
  def branch(name : String, checkout : Boolean = false) : Try[_ <: Repository[TConnector]]

  /** List all the available branches. */
  def branches : List[String]

  /**
   * Switch to a branch by the given name.
   *
   * @param name The name of the branch to switch to.
   * @return a Try or an Error depending if the operation was successful or not.
   */
  def switch(name : String) : Try[_ <: Repository[TConnector]]

  /**
   * Switch to a branch by the given name.
   *
   * @param name The name of the branch to switch to.
   * @return a Try or an Error depending if the operation was successful or not.
   */
  def checkout(name : String) = switch(name)

  /* *** EXPERIMENTAL *** */
  /*
  def log() : String
  def blame(resource : String, revision : Revision) : Unit
  def annotate(resource : String, revision : Revision) = blame(resource, revision)
  def export(to : String) : Boolean
  def archive(to:String) = export(to)
  def lock(resource : String) : Boolean
  def show(resource : String) : String
  def info(resource : String) = show(resource)
  def reset : Boolean
  def revert = reset
  */
}
