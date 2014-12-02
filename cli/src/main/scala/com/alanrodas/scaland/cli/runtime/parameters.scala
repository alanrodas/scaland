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

package com.alanrodas.scaland.cli.runtime

import com.alanrodas.scaland.cli._

/**
 * This trait is shared by all parameters of a command that can be tested to see if
 * they were passed by the user.
 */
sealed trait Definable {
  def isDefined : Boolean
}

/**
 * This is an abstract implementation of any kind of parameter,
 * weather flags or arguments.
 *
 * This class defines things such as the long and short name
 * of the argument, and weather or not the parameter has been user defined.
 *
 * There are two known subclasses of [[Parameter]]: [[Flag]] and [[Argument]].
 *
 * @param name The name of the argument or flag, weather short or long
 * @param alias An additional optional name for the argument or flag. Should always
 *     be the short name in case that it is defined.
 * @param isDefined ''true'' if it was defined by the user, ''false'' otherwise
 */
abstract class Parameter(val name : String, val alias : Option[String], val isDefined : Boolean)
    extends Definable {

  /** Returns the long name if this AbstractArgument has one, or None otherwise. */
  val longName = if (name.length > 1) Some(name) else alias

  /** Returns ''true'' if it has a long name, ''false'' otherwise. */
  val hasLongName = longName.nonEmpty

  /** Returns the short name if this AbstractArgument has one, or None otherwise. */
  val shortName = if (name.length == 1) Some(name) else alias

  /** Returns ''true'' if it has a short name, ''false'' otherwise. */
  val hasShortName = shortName.nonEmpty

  /** ''true'' if this parameter is a flag, ''false'' otherwise */
  def isFlag : Boolean

  /** ''true'' if this parameter is an argument, ''false'' otherwise */
  def isArgument : Boolean
}

/**
 * This class represents a flag call.
 *
 * Note that flags are always ''false'' when they are not defined by the user,
 * and ''true'' when they are defined. So if you want to test the value of a
 * flag, you should ask weather is defined or not.
 *
 * You can also make use of the [[implicits]] object that provides conversions between
 * a [[Flag]] and a [[Boolean]], so you can use the element as the value it represents.
 *
 * Usually you will not create instances of this class yourself. They will be created by
 * a [[CommandManager]] when processing the flags of a command execution.
 *
 * @param name The name of the argument or flag, weather short or long
 * @param alias An additional optional name for the argument or flag. Should always be
 *     the short name in case that it is defined.
 * @param isDefined ''true'' if it was defined by the user, ''false'' otherwise
 */
case class Flag(override val name : String, override val alias : Option[String], override val isDefined : Boolean)
    extends Parameter(name, alias, isDefined) {
  def isFlag = true
  def isArgument = false
}

/**
 * This class represents an argument call.
 *
 * Note that argument provide one or more arguments as ''values''. This class also provides
 * ways to access those values in an easy fashion.
 *
 * You can also make use of the [[implicits]] object that provides conversions between
 * a [[Argument]] and a [[Boolean]], so you can use the element to test if it was defined or not.
 *
 * Usually you will not create instances of this class yourself. They will be created by
 * a [[CommandManager]] when processing the arguments of a command execution.
 *
 * @param name The name of the argument or flag, weather short or long
 * @param alias An additional optional name for the argument or flag. short if name was long, and long if it was short
 * @param values The sequence of values of the current argument
 * @param isDefined ''true'' if it was defined by the user, ''false'' otherwise
 */
case class Argument[T](override val name : String, override val alias : Option[String], values : Seq[T],
    override val isDefined : Boolean) extends Parameter(name, alias, isDefined) {

  def isFlag = false
  def isArgument = true

  /**
   * Returns the value with the given index.
   *
   * The returned element is an instance of [[Any]]. If the element has
   * been passed to the user, then, it's always a [[String]]. If not, then
   * the actual type depends on the default value. To retrieve the value as
   * an instance of a particular type, use the [[valueAs]] method.
   *
   * Note that the index ''i'' should be a valid index given the passed values,
   * or an [[IllegalArgumentException]] will be thrown.
   *
   * @param i The index of the element to fetch
   */
  def value(i : Int) = {
    require(i >= 0, s"The index $i should be greater or equal to zero")
    require(i < numberOfValues, s"The index $i should be lower than $numberOfValues")
    values(i)
  }

  /** Return the first element of all the values. */
  val value = values(0)

  /**
   * Returns the value with the given index as an instance of [[T]].
   *
   * The returned element is an instance of [[T]]. If the i-th value is an instance of
   * [[T]], that means, it was not defined by the user, it will return the value as is.
   * If the value has been defined by the user, then, it is converted to a [[T]] from
   * a [[String]] using the provided converter.
   *
   * Note that the index ''i'' should be a valid index given the passed values,
   * or an [[IllegalArgumentException]] will be thrown.
   * Additionally, if the value is not of type [[T]] nor a [[String]], then a
   * [[ClassCastException]] is thrown.
   *
   * @param i: The index of the value to retrieve.
   * @param converter The function called to transform the value as an instance of ''R''.
   */
  def valueAs[R](i : Int)(implicit converter : String => R) = convert(value(i))(converter)

  /** Returns the number of values passed to the argument. */
  val numberOfValues = values.length
}

/**
 * Represents a value passed to a command.
 *
 * This class provides properties for the name of the value and
 * a boolean stating if the value was passed as an argument or if it
 * is the default value.
 *
 * @param name The name of the value passed to the command
 * @param value The value passed to the command
 * @param isDefined ''true'' if it was defined by the user, ''false'' otherwise
 */
case class Value[T](name : String, value : T, isDefined : Boolean) extends Definable {

  /**
   * Return the value of this element as an instance of [[R]].
   *
   * If the value is already an instance of [[R]], then it is returned
   * as is. If it is a [[String]] (As in when it was passed by the user)
   * the converter function passed will be used to transform the value
   * to an [[R]]. This method throws a [[ClassCastException]] if the type
   * of the value of this element is not a [[String]] nor an [[R]]
   *
   * @tparam R The type that the value will be tried to cast to before returning it.
   * @param converter The function called to transform the value as an instance of ''R''.
    */
    def valueAs[R](implicit converter : String => R) : R = convert(value)(converter)
}