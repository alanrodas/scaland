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

package com.alanrodas.scaland.logging

import org.slf4j.{ Logger => Underlying }

trait LogLevel {
  val isTraceEnabled = false
  val isDebugEnabled = false
  val isInfoEnabled = false
  val isWarnEnabled = false
  val isErrorEnabled = false
}
object LogLevel {
  def apply(underlying : Underlying) : LogLevel = {
    if (underlying.isTraceEnabled) Trace
    else if (underlying.isDebugEnabled) Debug
    else if (underlying.isInfoEnabled) Info
    else if (underlying.isWarnEnabled) Warn
    else if (underlying.isErrorEnabled) Error
    else Off
  }
}
case object Off extends  LogLevel
case object Trace extends LogLevel {
  override val isTraceEnabled = true
  override val isDebugEnabled = true
  override val isInfoEnabled = true
  override val isWarnEnabled = true
  override val isErrorEnabled = true
}
case object Debug extends LogLevel {
  override val isDebugEnabled = true
  override val isInfoEnabled = true
  override val isWarnEnabled = true
  override val isErrorEnabled = true
}
case object Info extends LogLevel {
  override val isInfoEnabled = true
  override val isWarnEnabled = true
  override val isErrorEnabled = true
}
case object Warn extends LogLevel {
  override val isWarnEnabled = true
  override val isErrorEnabled = true
}
case object Error extends LogLevel {
  override val isErrorEnabled = true
}