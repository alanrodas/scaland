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

import java.text.ParseException

import language.implicitConversions

import com.alanrodas.scaland.cli.runtime._

/**
 * Provides a set of implicit conversions between different types of values.
 *
 * Among others, it provides conversions between [[Flag]], [[Argument]] and
 * [[Value]] to a [[Boolean]] and [[Option]]
 *
 * It also provides a set of conversions between [[String]] and different
 * basic types. This is specially useful when using the ''valueAs'' methods,
 * provided in some classes.
 */
object implicits {

  /**
   * This exception is thrown when a string is tried to parse to a given
   * type but fails. This a more general case of exceptions such as
   * [[NumberFormatException]], as this covers not only cases for numbers
   * but also for any other type conversion stated in [[implicits]].
   *
   * @param message The error message to show
   */
  class ParseException(message : String) extends RuntimeException(message) {
    def this() = this("")
  }

  /**
   * Transforms a [[Definable]] into a [[Boolean]]
   *
   * This allow you to test for a definable value to test for existence just by fetching it. i.e.
   * {{{
   *  val arg : Argument
   *  if (arg) "Defined as " + arg.value else "Not Defined"
   * }}}
   *
   * this is specially useful when defining the callback of a command. o.e.
   *
   * {{{
   *  root accepts (arg named "test" as "Default") does {cmd =>
   *    println(
   *      if (cmd argument "test") "Defined as " + (cmd argument "test").value
   *      else "Not defined"
   *    )
   *  }
   * }}}
   */
  implicit def definable2bool(arg : Definable) : Boolean = arg.isDefined

  /**
   * Transforms an [[Argument]] into an [[Option]].
   *
   * This allow you to work with arguments as optional value, only defined when
   * the argument was user defined. i.e.
   * {{{
   *  val arg : Argument
   *  arg.fold("Not Defined"){value => "Defined as " + value}
   * }}}
   *
   * this is specially useful when defining the callback of a command. o.e.
   *
   * {{{
   *  root accepts (arg named "test" as "Default") does {cmd =>
   *    println(
   *      arg.fold("Not Defined"){value => "Defined as " + value}
   *    )
   *  }
   * }}}
   */
  implicit def argument2option[T](arg : Argument[T]) : Option[Seq[T]] = {
    if (arg.isDefined) Some(arg.values)
    else None
  }

  /**
   * Transforms an [[Value]] into an [[Option]].
   *
   * This allow you to work with arguments as optional value, only defined when
   * the argument was user defined. i.e.
   * {{{
   *  val arg : Value
   *  arg.fold("Not Defined"){value => "Defined as " + value}
   * }}}
   *
   * this is specially useful when defining the callback of a command. o.e.
   *
   * {{{
   *  root receives (value named "test" as "Default") does {cmd =>
   *    println(
   *      arg.fold("Not Defined"){value => "Defined as " + value}
   *    )
   *  }
   * }}}
   */
  implicit def value2option[T](value : Value[T]) : Option[T] =
    if (value.isDefined) Some(value.value) else None


  /**
   * Transforms from [[String]] to [[Int]]
   *
   * @throws NumberFormatException if the string does not represent a valid Int.
   */
  implicit def stringToInt(s : String) : Int = {
    val maybeInt = stringToSomeInt(s)
    if (maybeInt.isEmpty) throw new ParseException(s"$s cannot be parsed as an Int.")
    maybeInt.get
  }

  /**
   * Transforms from [[String]] to [[Double]]
   *
   * @throws NumberFormatException if the string does not represent a valid Double.
   */
  implicit def stringToDouble(s : String) : Double = {
    val maybeDouble = stringToSomeDouble(s)
    if (maybeDouble.isEmpty) throw new ParseException(s"$s cannot be parsed as a Double.")
    maybeDouble.get
  }

  /**
   * Transforms from [[String]] to [[Float]]
   *
   * @throws NumberFormatException if the string does not represent a valid Float.
   */
  implicit def stringToFloat(s : String) : Float = {
    val maybeFloat = stringToSomeFloat(s)
    if (maybeFloat.isEmpty) throw new ParseException(s"$s cannot be parsed as a Float.")
    maybeFloat.get
  }

  /**
   * Transforms from [[String]] to [[Boolean]]
   *
   * This method return ''true'' if the string matches any of ''true'', ''t'',
   * ''yes'', ''y'', ''on'' or ''1'', returns ''false'' otherwise.
   */
  implicit def stringToBool(s : String) : Boolean = {
    val maybeBool = stringToSomeBool(s)
    if (maybeBool.isEmpty) throw new ParseException(s"$s cannot be parsed as a Boolean.")
    maybeBool.get
  }

  /**
   * Transforms from [[String]] to [[Option]] of [[Int]]
   *
   * Returns ''Some(x)'' if the string represents a valid Int ''x'', return ''None''
   * otherwise.
   */
  implicit def stringToSomeInt(s : String) : Option[Int] =
    try {Some(Integer.decode(s))} catch {case nfe : NumberFormatException => None }

  /**
   * Transforms from [[String]] to [[Option]] of [[Double]]
   *
   * Returns ''Some(x)'' if the string represents a valid Double ''x'', return ''None''
   * otherwise.
   */
  implicit def stringToSomeDouble(s : String) : Option[Double] =
    try {Some(java.lang.Double.valueOf(s))} catch {case nfe : NumberFormatException => None }

  /**
   * Transforms from [[String]] to [[Option]] of [[Float]]
   *
   * Returns ''Some(x)'' if the string represents a valid Float ''x'', return ''None''
   * otherwise.
   */
  implicit def stringToSomeFloat(s : String) : Option[Float] =
    try {Some(java.lang.Float.valueOf(s))} catch {case nfe : NumberFormatException => None }

  /**
   * Transforms from [[String]] to [[Option]] of [[Boolean]]
   *
   * Returns ''Some(true)'' if the string matches any of ''true'', ''t'',
   * ''yes'', ''y'', ''on'' or ''1'', returns ''Some(false)'' if matches
   * ''false'', ''f'', ''no'', ''n'', ''off'' or ''0'' and ''None'' otherwise.
   */
  implicit def stringToSomeBool(s : String) : Option[Boolean] = {
    if (Set("true", "on", "yes", "t", "y", "1").contains(s.toLowerCase)) Some(true)
    else if (Set("false", "off", "no", "f", "n", "0").contains(s.toLowerCase)) Some(false)
    else None
  }

  /**
   * Transforms from [[String]] to [[Option]] of [[String]]
   *
   * Returns ''Some(s)'' if the string is not empty, return ''None'' otherwise.
   */
  implicit def stringToSomeString(s : String) : Option[String] =
    if (s.isEmpty) None else Some(s)
}
