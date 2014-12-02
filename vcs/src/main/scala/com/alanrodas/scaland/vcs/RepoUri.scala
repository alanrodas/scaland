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

case class RepoUri(uri : String, branch : String = "", revision : Revision = HEAD) {
  def branch(branch : String) : RepoUri = RepoUri(uri, branch, revision)
  def rev(revision : Revision) = RepoUri(uri, branch, revision)
  def revision(revision : Revision) = rev(revision)
  def ver(revision : Revision) = rev(revision)
  def version(revision : Revision) = rev(revision)
}