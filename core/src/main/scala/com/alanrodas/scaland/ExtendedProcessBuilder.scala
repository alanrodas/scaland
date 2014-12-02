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

import scala.sys.process.{Process, ProcessBuilder, BasicIO, ProcessLogger}

class ExtendedProcessBuilder(val command : String, maybeCwd : Option[String] = None) {

  private[this] def slurp(withIn: Boolean) : (Int, String) = {
    val buffer = new StringBuffer()
    val log = Some(ProcessLogger(s =>  buffer append s))
    val process = Process(command, maybeCwd.map(new java.io.File(_)))
    val code   = (process run BasicIO(withIn, buffer, log) ).exitValue()
    (code, buffer.toString)
  }

  def cwd(dir : String) = new ExtendedProcessBuilder(command, Some(dir))
  def !!! : (Int, String) = slurp(withIn = false)
  def !!!< : (Int, String) = slurp(withIn = true)
}