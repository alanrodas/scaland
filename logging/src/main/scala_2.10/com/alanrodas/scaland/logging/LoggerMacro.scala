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

import org.slf4j.Marker
import scala.annotation.switch

private object LoggerMacro {

  type LoggerContext = scala.reflect.macros.Context { type PrefixType = Logger }

  // Error

  def errorMessage(c: LoggerContext)(message: c.Expr[String]) =
    c.universe.reify(
      if (c.prefix.splice.level.isErrorEnabled)
        c.prefix.splice.underlying.error(message.splice)
    )

  def errorMessageCause(c: LoggerContext)(message: c.Expr[String], cause: c.Expr[Throwable]) =
    c.universe.reify(
      if (c.prefix.splice.level.isErrorEnabled)
        c.prefix.splice.underlying.error(message.splice, cause.splice)
    )

  def errorMessageArgs(c: LoggerContext)(message: c.Expr[String], args: c.Expr[AnyRef]*) =
    (args.length: @switch) match {
      case 1 =>
        c.universe.reify(
          if (c.prefix.splice.level.isErrorEnabled)
            LoggerSupport.error(c.prefix.splice.underlying, message.splice, args(0).splice)
        )
      case 2 =>
        c.universe.reify(
          if (c.prefix.splice.level.isErrorEnabled)
            LoggerSupport.error(c.prefix.splice.underlying, message.splice, args(0).splice, args(1).splice)
        )
      case _ =>
        logParams(c)(message, args)("error")
    }

  def errorMessageMarker(c: LoggerContext)(marker: c.Expr[Marker], message: c.Expr[String]) =
    c.universe.reify(
      if (c.prefix.splice.level.isErrorEnabled)
        c.prefix.splice.underlying.error(marker.splice, message.splice)
    )

  def errorMessageCauseMarker(c: LoggerContext)(marker: c.Expr[Marker], message: c.Expr[String], cause: c.Expr[Throwable]) =
    c.universe.reify(
      if (c.prefix.splice.level.isErrorEnabled)
        c.prefix.splice.underlying.error(marker.splice, message.splice, cause.splice)
    )

  def errorMessageArgsMarker(c: LoggerContext)(marker: c.Expr[Marker], message: c.Expr[String], args: c.Expr[AnyRef]*) =
    (args.length: @switch) match {
      case 1 =>
        c.universe.reify(
          if (c.prefix.splice.level.isErrorEnabled)
            LoggerSupport.error(c.prefix.splice.underlying, marker.splice, message.splice, args(0).splice)
        )
      case 2 =>
        c.universe.reify(
          if (c.prefix.splice.level.isErrorEnabled)
            LoggerSupport.error(c.prefix.splice.underlying, marker.splice, message.splice, args(0).splice, args(1).splice)
        )
      case _ =>
        logParams(c)(message, args)("error")
    }

  // Warn

  def warnMessage(c: LoggerContext)(message: c.Expr[String]) =
    c.universe.reify(
      if (c.prefix.splice.level.isWarnEnabled)
        c.prefix.splice.underlying.warn(message.splice)
    )

  def warnMessageArgs(c: LoggerContext)(message: c.Expr[String], args: c.Expr[AnyRef]*) =
    (args.length: @switch) match {
      case 1 =>
        c.universe.reify(
          if (c.prefix.splice.level.isWarnEnabled)
            LoggerSupport.warn(c.prefix.splice.underlying, message.splice, args(0).splice)
        )
      case 2 =>
        c.universe.reify(
          if (c.prefix.splice.level.isWarnEnabled)
            LoggerSupport.warn(c.prefix.splice.underlying, message.splice, args(0).splice, args(1).splice)
        )
      case _ =>
        logParams(c)(message, args)("warn")
    }

  def warnMessageCause(c: LoggerContext)(message: c.Expr[String], cause: c.Expr[Throwable]) =
    c.universe.reify(
      if (c.prefix.splice.level.isWarnEnabled)
        c.prefix.splice.underlying.warn(message.splice, cause.splice)
    )

  def warnMessageMarker(c: LoggerContext)(marker: c.Expr[Marker], message: c.Expr[String]) =
    c.universe.reify(
      if (c.prefix.splice.level.isErrorEnabled)
        c.prefix.splice.underlying.warn(marker.splice, message.splice)
    )

  def warnMessageCauseMarker(c: LoggerContext)(marker: c.Expr[Marker], message: c.Expr[String], cause: c.Expr[Throwable]) =
    c.universe.reify(
      if (c.prefix.splice.level.isErrorEnabled)
        c.prefix.splice.underlying.warn(marker.splice, message.splice, cause.splice)
    )

  def warnMessageArgsMarker(c: LoggerContext)(marker: c.Expr[Marker], message: c.Expr[String], args: c.Expr[AnyRef]*) =
    (args.length: @switch) match {
      case 1 =>
        c.universe.reify(
          if (c.prefix.splice.level.isErrorEnabled)
            LoggerSupport.warn(c.prefix.splice.underlying, marker.splice, message.splice, args(0).splice)
        )
      case 2 =>
        c.universe.reify(
          if (c.prefix.splice.level.isErrorEnabled)
            LoggerSupport.warn(c.prefix.splice.underlying, marker.splice, message.splice, args(0).splice, args(1).splice)
        )
      case _ =>
        logParams(c)(message, args)("warn")
    }

  // Info

  def infoMessage(c: LoggerContext)(message: c.Expr[String]) =
    c.universe.reify(
      if (c.prefix.splice.level.isInfoEnabled)
        c.prefix.splice.underlying.info(message.splice)
    )

  def infoMessageArgs(c: LoggerContext)(message: c.Expr[String], args: c.Expr[AnyRef]*) =
    (args.length: @switch) match {
      case 1 =>
        c.universe.reify(
          if (c.prefix.splice.level.isInfoEnabled)
            LoggerSupport.info(c.prefix.splice.underlying, message.splice, args(0).splice)
        )
      case 2 =>
        c.universe.reify(
          if (c.prefix.splice.level.isInfoEnabled)
            LoggerSupport.info(c.prefix.splice.underlying, message.splice, args(0).splice, args(1).splice)
        )
      case _ =>
        logParams(c)(message, args)("info")
    }

  def infoMessageCause(c: LoggerContext)(message: c.Expr[String], cause: c.Expr[Throwable]) =
    c.universe.reify(
      if (c.prefix.splice.level.isInfoEnabled)
        c.prefix.splice.underlying.info(message.splice, cause.splice)
    )

  def infoMessageMarker(c: LoggerContext)(marker: c.Expr[Marker], message: c.Expr[String]) =
    c.universe.reify(
      if (c.prefix.splice.level.isErrorEnabled)
        c.prefix.splice.underlying.info(marker.splice, message.splice)
    )

  def infoMessageCauseMarker(c: LoggerContext)(marker: c.Expr[Marker], message: c.Expr[String], cause: c.Expr[Throwable]) =
    c.universe.reify(
      if (c.prefix.splice.level.isErrorEnabled)
        c.prefix.splice.underlying.info(marker.splice, message.splice, cause.splice)
    )

  def infoMessageArgsMarker(c: LoggerContext)(marker: c.Expr[Marker], message: c.Expr[String], args: c.Expr[AnyRef]*) =
    (args.length: @switch) match {
      case 1 =>
        c.universe.reify(
          if (c.prefix.splice.level.isErrorEnabled)
            LoggerSupport.info(c.prefix.splice.underlying, marker.splice, message.splice, args(0).splice)
        )
      case 2 =>
        c.universe.reify(
          if (c.prefix.splice.level.isErrorEnabled)
            LoggerSupport.info(c.prefix.splice.underlying, marker.splice, message.splice, args(0).splice, args(1).splice)
        )
      case _ =>
        logParams(c)(message, args)("info")
    }

  // Debug

  def debugMessage(c: LoggerContext)(message: c.Expr[String]) =
    c.universe.reify(
      if (c.prefix.splice.level.isDebugEnabled)
        c.prefix.splice.underlying.debug(message.splice)
    )

  def debugMessageArgs(c: LoggerContext)(message: c.Expr[String], args: c.Expr[AnyRef]*) =
    (args.length: @switch) match {
      case 1 =>
        c.universe.reify(
          if (c.prefix.splice.level.isDebugEnabled)
            LoggerSupport.debug(c.prefix.splice.underlying, message.splice, args(0).splice)
        )
      case 2 =>
        c.universe.reify(
          if (c.prefix.splice.level.isDebugEnabled)
            LoggerSupport.debug(c.prefix.splice.underlying, message.splice, args(0).splice, args(1).splice)
        )
      case _ =>
        logParams(c)(message, args)("debug")
    }

  def debugMessageCause(c: LoggerContext)(message: c.Expr[String], cause: c.Expr[Throwable]) =
    c.universe.reify(
      if (c.prefix.splice.level.isDebugEnabled)
        c.prefix.splice.underlying.debug(message.splice, cause.splice)
    )

  def debugMessageMarker(c: LoggerContext)(marker: c.Expr[Marker], message: c.Expr[String]) =
    c.universe.reify(
      if (c.prefix.splice.level.isErrorEnabled)
        c.prefix.splice.underlying.debug(marker.splice, message.splice)
    )

  def debugMessageCauseMarker(c: LoggerContext)(marker: c.Expr[Marker], message: c.Expr[String], cause: c.Expr[Throwable]) =
    c.universe.reify(
      if (c.prefix.splice.level.isErrorEnabled)
        c.prefix.splice.underlying.debug(marker.splice, message.splice, cause.splice)
    )

  def debugMessageArgsMarker(c: LoggerContext)(marker: c.Expr[Marker], message: c.Expr[String], args: c.Expr[AnyRef]*) =
    (args.length: @switch) match {
      case 1 =>
        c.universe.reify(
          if (c.prefix.splice.level.isErrorEnabled)
            LoggerSupport.debug(c.prefix.splice.underlying, marker.splice, message.splice, args(0).splice)
        )
      case 2 =>
        c.universe.reify(
          if (c.prefix.splice.level.isErrorEnabled)
            LoggerSupport.debug(c.prefix.splice.underlying, marker.splice, message.splice, args(0).splice, args(1).splice)
        )
      case _ =>
        logParams(c)(message, args)("debug")
    }
  // Trace

  def traceMessage(c: LoggerContext)(message: c.Expr[String]) =
    c.universe.reify(
      if (c.prefix.splice.level.isTraceEnabled)
        c.prefix.splice.underlying.trace(message.splice)
    )

  def traceMessageArgs(c: LoggerContext)(message: c.Expr[String], args: c.Expr[AnyRef]*) =
    (args.length: @switch) match {
      case 1 =>
        c.universe.reify(
          if (c.prefix.splice.level.isTraceEnabled)
            LoggerSupport.trace(c.prefix.splice.underlying, message.splice, args(0).splice)
        )
      case 2 =>
        c.universe.reify(
          if (c.prefix.splice.level.isTraceEnabled)
            LoggerSupport.trace(c.prefix.splice.underlying, message.splice, args(0).splice, args(1).splice)
        )
      case _ =>
        logParams(c)(message, args)("trace")
    }

  def traceMessageCause(c: LoggerContext)(message: c.Expr[String], cause: c.Expr[Throwable]) =
    c.universe.reify(
      if (c.prefix.splice.level.isTraceEnabled)
        c.prefix.splice.underlying.trace(message.splice, cause.splice)
    )

  def traceMessageMarker(c: LoggerContext)(marker: c.Expr[Marker], message: c.Expr[String]) =
    c.universe.reify(
      if (c.prefix.splice.level.isErrorEnabled)
        c.prefix.splice.underlying.trace(marker.splice, message.splice)
    )

  def traceMessageCauseMarker(c: LoggerContext)(marker: c.Expr[Marker], message: c.Expr[String], cause: c.Expr[Throwable]) =
    c.universe.reify(
      if (c.prefix.splice.level.isErrorEnabled)
        c.prefix.splice.underlying.trace(marker.splice, message.splice, cause.splice)
    )

  def traceMessageArgsMarker(c: LoggerContext)(marker: c.Expr[Marker], message: c.Expr[String], args: c.Expr[AnyRef]*) =
    (args.length: @switch) match {
      case 1 =>
        c.universe.reify(
          if (c.prefix.splice.level.isErrorEnabled)
            LoggerSupport.trace(c.prefix.splice.underlying, marker.splice, message.splice, args(0).splice)
        )
      case 2 =>
        c.universe.reify(
          if (c.prefix.splice.level.isErrorEnabled)
            LoggerSupport.trace(c.prefix.splice.underlying, marker.splice, message.splice, args(0).splice, args(1).splice)
        )
      case _ =>
        logParams(c)(message, args)("trace")
    }

  // Common

  private def logParams(c: LoggerContext)(
      message: c.Expr[String],
      params: Seq[c.Expr[AnyRef]])(
      level: String) = {
    import c.universe._
    val isEnabled = Select(
      Select(c.prefix.tree, newTermName("underlying")),
      newTermName(s"is${level.head.toUpper +: level.tail}Enabled")
    )
    val paramsWildcard = Typed(
      Apply(
        Ident(newTermName("List")),
        (params map (_.tree)).toList
      ),
      Ident(tpnme.WILDCARD_STAR)
    )
    val log = Apply(
      Select(Select(c.prefix.tree, newTermName("underlying")), newTermName(level)),
      message.tree +: List(paramsWildcard)
    )
    c.Expr(If(isEnabled, log, Literal(Constant(())))).asInstanceOf[c.universe.Expr[Unit]]
  }
}