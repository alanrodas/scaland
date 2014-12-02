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

package com.alanrodas.scaland.cli

import scala.annotation._

/**
 * A `TerminalStyle` consists in a set of decorators for
 * printing in the Terminal with different styles.
 *
 *
 */
@implicitNotFound(msg = "No implicit style was available in scope. " +
    "Please import a member of com.alanrodas.scaland.cli.style, e.g. style.Tagged.")
trait TerminalStyle {
  def plainStyle(message : Any) : String
  def highlightStyle(message : Any) : String
  def infoStyle(message : Any) : String
  def successStyle(message : Any) : String
  def warnStyle(message : Any) : String
  def errorStyle(message : Any) : String
}

/**
 *
 */
object style {
  implicit def Plain : TerminalStyle = new TerminalStyle {
    override def plainStyle(message: Any): String = message.toString
    override def highlightStyle(message: Any): String = message.toString
    override def infoStyle(message: Any): String = message.toString
    override def successStyle(message: Any): String = message.toString
    override def warnStyle(message: Any): String = message.toString
    override def errorStyle(message: Any): String = message.toString
  }
  implicit def Simple : TerminalStyle = new TerminalStyle {
    override def plainStyle(message: Any): String = message.toString
    override def highlightStyle(message: Any): String = Console.UNDERLINED + message
    override def infoStyle(message: Any): String = message.toString
    override def successStyle(message: Any): String = Console.GREEN + message
    override def warnStyle(message: Any): String = Console.YELLOW + message
    override def errorStyle(message: Any): String = Console.RED + message
  }
  implicit def Colored : TerminalStyle = new TerminalStyle {
    override def plainStyle(message: Any): String = message.toString
    override def highlightStyle(message: Any): String = Console.YELLOW_B + message
    override def infoStyle(message: Any): String = Console.BLUE + message
    override def successStyle(message: Any): String = Console.GREEN + message
    override def warnStyle(message: Any): String = Console.YELLOW + message
    override def errorStyle(message: Any): String = Console.RED + message
  }
  implicit def Tagged : TerminalStyle = new TerminalStyle {
    override def plainStyle(message: Any): String = message.toString
    override def highlightStyle(message: Any): String = Console.UNDERLINED + message
    override def infoStyle(message: Any): String = "[" + Console.BLUE + "info" + Console.RESET + "] " + message
    override def successStyle(message: Any): String = "[" + Console.GREEN + "success" + Console.RESET + "] " + message
    override def warnStyle(message: Any): String = "[" + Console.YELLOW + "warn" + Console.RESET + "] " + message
    override def errorStyle(message: Any): String = "[" + Console.RED + "error" + Console.RESET + "] " + message
  }
}

/**
 *
 * @param plain
 * @param highlight
 * @param info
 * @param success
 * @param warning
 * @param error
 */
@implicitNotFound(msg = "No implicit level was available in scope. " +
    "Please import a member of com.alanrodas.scaland.cli.level, e.g. level.All.")
case class TerminalLevel(plain : Boolean = false, highlight : Boolean = false, info : Boolean = false,
    success : Boolean = false, warning : Boolean = false, error : Boolean = false)

object level {
  implicit def None : TerminalLevel = TerminalLevel()
  implicit def Basic : TerminalLevel = TerminalLevel(plain = true, highlight = true)
  implicit def Info : TerminalLevel = TerminalLevel(plain = true, highlight = true,
      info = true, success = true)
  implicit def Warn : TerminalLevel = TerminalLevel(warning = true, error = true)
  implicit def Error : TerminalLevel = TerminalLevel(error = true)
  implicit def All : TerminalLevel = TerminalLevel(plain = true, highlight = true,
      info = true, success = true, warning = true, error = true)
}

/**
 *
 */
trait DefaultTerminalValues {
  implicit val defaultLevel = level.All
  implicit val defaultStyle = style.Tagged
}

/**
 *
 * @param levels
 */
class Terminal(private val levels : TerminalLevel) {

  private def printContinued(message : String) = print(message + Console.RESET)
  private def printLine(message : String) = println(message + Console.RESET)
  private def getAction(continued : Boolean) = if (continued) printContinued _ else printLine _
  private def getMessage(message : Any) = {
    message match {
      case f : (() => Any) => f.apply()
      case _ => message
    }
  }
  private def doLevel(levelActive : Boolean, continued : Boolean, style : Any => String, message : Any) =
    if (levelActive) (this getAction continued)(style(this getMessage message))

  val plainActive = levels.plain
  val highlightActive = levels.highlight
  val infoActive = levels.info
  val successActive = levels.success
  val warnActive = levels.warning
  val errorActive = levels.error

  def show(message : Any, continued : Boolean = false)(implicit style : TerminalStyle) =
    getAction(continued)(message.toString)
  def plain(message : Any, continued : Boolean = false)(implicit style : TerminalStyle) =
    doLevel(plainActive, continued, style.plainStyle _ , message)
  def highlight(message : Any, continued : Boolean = false)(implicit style : TerminalStyle) =
    doLevel(highlightActive, continued, style.highlightStyle _ , message)
  def info(message : Any, continued : Boolean = false)(implicit style : TerminalStyle)  =
    doLevel(infoActive, continued, style.infoStyle _ , message)
  def success(message : Any, continued : Boolean = false)(implicit style : TerminalStyle) =
    doLevel(successActive, continued, style.successStyle _ , message)
  def warn(message : Any, continued : Boolean = false)(implicit style : TerminalStyle) =
    doLevel(warnActive, continued, style.warnStyle _ , message)
  def error(message : Any, continued : Boolean = false)(implicit style : TerminalStyle) =
    doLevel(errorActive, continued, style.errorStyle _ , message)

  def read(msg : String = "") = scala.io.StdIn.readLine(msg)
}

/**
 *
 */
object Terminal {
  private val defaultLevel = level.All
  private val defaultStyle = style.Tagged

  private var current : Terminal = null
  def apply(implicit level : TerminalLevel) = {
    if (current == null) current = new Terminal(level)
    current
  }

  def show(message : Any)(implicit level : TerminalLevel = defaultLevel, style : TerminalStyle = defaultStyle) =
    apply(level).show(message)(style)
  def plain(message : Any)(implicit level : TerminalLevel = defaultLevel, style : TerminalStyle = defaultStyle) =
    apply(level).plain(message)(style)
  def highlight(message : Any)(implicit level : TerminalLevel = defaultLevel, style : TerminalStyle = defaultStyle) =
    apply(level).highlight(message)(style)
  def info(message : Any)(implicit level : TerminalLevel = defaultLevel, style : TerminalStyle = defaultStyle) =
    apply(level).info(message)(style)
  def success(message : Any)(implicit level : TerminalLevel = defaultLevel, style : TerminalStyle = defaultStyle) =
    apply(level).success(message)(style)
  def warning(message : Any)(implicit level : TerminalLevel = defaultLevel, style : TerminalStyle = defaultStyle) =
    apply(level).warn(message)(style)
  def error(message : Any)(implicit level : TerminalLevel = defaultLevel, style : TerminalStyle = defaultStyle) =
    apply(level).error(message)(style)
  def read(msg : String = "")(implicit level : TerminalLevel = defaultLevel) =
    apply(level).read(msg)
}