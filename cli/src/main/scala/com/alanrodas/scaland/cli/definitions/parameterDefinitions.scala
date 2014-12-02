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

package com.alanrodas.scaland.cli.definitions

import com.alanrodas.scaland.cli._
import com.alanrodas.scaland.cli.runtime.{Argument, Flag, Parameter}

/**
 * Represents a parameter definition for any type of
 * parameter, weather flags or arguments.
 *
 * This class defines the long and short names for the parameter, as well as
 * it provides validations to check that the defined parameters are formed correcly.
 *
 * This class has two known subclasses, [FlagDefinition]] and [[ArgumentDefinition]].
 *
 * @param name The name of the parameter, short or long version
 * @param alias An alternative name for the parameter, short if ''name'' is long,
 *     long if it's short.
 */
abstract class ParameterDefinition(val name : String, val alias : Option[String])
    (implicit val commandManager : CommandManager) {

  require(!name.isEmpty, "The name of a parameter cannot be empty")
  require(!name.contains(" "), "The name of a parameter cannot have spaces")
  require(!name.startsWith(commandManager.longParamSign),
    "The name of a parameter cannot start with the long parameter sign")
  require(!name.startsWith(commandManager.shortParamSign),
    "The name of a parameter cannot start with the short parameter sign")
  require(alias.isEmpty || (name.length == 1 || alias.get.length == 1),
      "One of the name or alias should be of only one letter (short form flag)")
  require(alias.isEmpty || !(name.length == 1 && alias.get.length == 1),
    "One of the name or alias should be of more than one letter (long form flag)")
  require(alias.isEmpty || !alias.get.contains(" "), "The alias of a parameter cannot have spaces")
  require(alias.isEmpty || !alias.get.startsWith(commandManager.longParamSign),
    "The alias of a parameter cannot start with the long parameter sign")
  require(alias.isEmpty || !alias.get.startsWith(commandManager.shortParamSign),
    "The alias of a parameter cannot start with the long parameter sign")

  /** The long name of this parameter if it has one, ''None'' otherwise. */
  val longName = if (name.length > 1) Some(name) else alias

  /** Returns ''true'' if this parameter has a long name, ''false'' otherwise. */
  val hasLongName = longName.nonEmpty

  /** The short name of this parameter if it has one, ''None'' otherwise. */
  val shortName = if (name.length == 1) Some(name) else alias

  /** Returns ''true'' if this parameter has a short name, ''false'' otherwise. */
  val hasShortName = shortName.nonEmpty

  /**
   * Parses a parameter based on the provided arguments.
   *
   * Note that, for performance sake, the arguments passed should contain
   * the argument as it's first element. So if the argument ''foo'' is
   * going to be parsed, the arguments should be ''["--foo", ...]''.
   *
   * @param args The arguments to parse
   */
  def parse(args : Array[String]) : (Parameter, Array[String])

  /**
   * Return the parameter definition as a parameter that has not been
   * defined by the user.
   *
   * That means, use it's default values. In case of arguments, this
   * method can only be called in case it is not mandatory and given
   * that it provides all mandatory values.
   */
  def toParameter : Parameter
}

/**
 * Represents a flag definition for a command.
 *
 * This is a concrete implementation of [[ParameterDefinition]]. Flags are a kind
 * of parameter that represent a [[Boolean]] value. When passed by the user, the
 * value is taken as ''true'', and if they where not passed, ''false''.
 *
 * @param name The name of the parameter, short or long version
 * @param alias An alternative name for the parameter, short if ''name'' is long,
 *     long if it's short.
 */
case class FlagDefinition(override val name : String, override val alias : Option[String])
    (implicit override val commandManager : CommandManager) extends ParameterDefinition(name, alias)(commandManager) {

  def parse(args : Array[String]) = {
    (Flag(name, alias, isDefined = true), args.tail)
  }

  def toParameter = toFlag
  def toFlag = Flag(name, alias, isDefined = false)
}


/**
 * Represents an argument definition for a command.
 *
 * This is a concrete implementation of [[ParameterDefinition]]. Arguments are a form
 * of parameter that take one or more values. Some arguments may be mandatory, that is
 * they should be present in the command call for the call to be correct. Additionally,
 * arguments may define a set of default values. In case that the values are not passed,
 * then, they are defaulted to those values.
 *
 * If an argument takes more values than the default, then, first values are mandatory when
 * the argument is present in a command call. i.e. if an argument x is defined taking 3 values,
 * and the defaults are "second" and "third", then, one of the values is mandatory. So, the
 * first value should be passed when the command is call with argument x.
 *
 * @param name The name of the parameter, short or long version
 * @param alias An alternative name for the parameter, short if ''name'' is long, long if it's short.
 * @param isMandatory A boolean stating if this argument is required
 * @param numberOfTakenArguments The number of argument values that this argument takes
 * @param argumentValues A list of default values for the argument values that this argument takes.
 */
case class ArgumentDefinition[T](override val name : String, override val alias : Option[String], isMandatory : Boolean,
    numberOfTakenArguments : Int, argumentValues : Seq[T])(implicit override val commandManager : CommandManager)
    extends ParameterDefinition(name, alias)(commandManager) {

  require(numberOfTakenArguments > 0,
    s"The number of taken values should be greater than zero: $name")
  require(numberOfTakenArguments >= argumentValues.length,
    s"The number of taken values should be greater than the number of default values passed: $name")

  val numberOfMandatoryValues = numberOfTakenArguments - argumentValues.size

  def parse(args : Array[String]) = {
    val argValuesPassed = args.tail.take(numberOfTakenArguments).takeWhile(arg =>
      !(arg.startsWith(commandManager.shortParamSign) || arg.startsWith(commandManager.longParamSign))
    )

    clRequire(argValuesPassed.size >= numberOfMandatoryValues,
      s"There are missing mandatory values for the argument $name")

    val argValuesWithDefaults = argValuesPassed ++ argumentValues.drop(argumentValues.size - (numberOfTakenArguments - argValuesPassed.size))
    (Argument(name, alias, argValuesWithDefaults, isDefined = true), args.drop(argValuesPassed.size + 1))
  }

  def toParameter = toArgument
  def toArgument = {
    clRequire(!isMandatory,
      "A mandatory argument cannot be converted directly if it's mandatory")

    val argsValues = argumentValues ++
        (if (numberOfMandatoryValues != 0) List.fill(numberOfMandatoryValues)(None) else Nil)
    Argument(name, alias, argsValues, isDefined = false)
  }
}


/**
 * This class represents a value for a command.
 *
 * Commands may (or not) accept values in different forms. When the
 * number of values to be passed is unknown, then you cannot name the
 * values, or check for their presence. Other command provide a well
 * known number of values to accept.
 *
 * This class represents the latest, as it allows to specify a name
 * and a default value for the value that the command accepts,
 *
 * @param name The name of the value definition
 * @param default The default value that this definition takes
 */
case class ValueDefinition[T](name : String, default : Option[T])(implicit val commandManager : CommandManager) {

  require(!name.isEmpty, "The value name cannot be empty")

  /** ''true'' if the value is required for any call, ''false'' otherwise */
  val isMandatory = default.isEmpty
}