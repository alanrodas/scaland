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

import language.implicitConversions

import com.alanrodas.scaland._
import com.alanrodas.scaland.cli._
import com.alanrodas.scaland.cli.CommandManager
import com.alanrodas.scaland.cli.runtime._

/**
 * Represents a command definition for any kind of command weather named or not.
 *
 * This has two known concrete subclasses, [[FiniteValuesCommand]] and [[MultipleValuesCommand]].
 *
 * This class provides defines the name of the command and common arguments such as flags
 * and arguments of the form --arg.
 *
 * @param name The name of the command definition
 * @param argumentDefinitions The arguments that this command accepts
 * @param flagDefinitions The flags that this command accepts
 */
abstract class CommandDefinition(val name : String, val argumentDefinitions : Iterable[ArgumentDefinition[_]],
    val flagDefinitions : Iterable[FlagDefinition])(implicit val commandManager : CommandManager) {

  private val allArgNames =
    argumentDefinitions.foldLeft(List[String]()){(l, e) => if (e.alias.isDefined) e.alias.get :: e.name :: l else e.name :: l} ++
        flagDefinitions.foldLeft(List[String]()){(l, e) => if (e.alias.isDefined) e.alias.get :: e.name :: l else e.name :: l}

  require(!name.contains(" "), "The name of a command cannot contain spaces")
  require(!name.startsWith(commandManager.longParamSign),
    "The name of a command cannot start with the long parameter sign")
  require(!name.startsWith(commandManager.shortParamSign),
    "The name of a command cannot start with the short parameter sign")
  require(allArgNames.size == allArgNames.distinct.size,
    "The following arguments are duplicated: " + allArgNames.diff(allArgNames.distinct).distinct +
        ". There cannot be two arguments with the same name or alias.")

  if (commandManager.autoAddCommands) commandManager.addCommand(this)

  private val argumentDefinitionsMap = argumentDefinitions.foldLeft(Map[String, ArgumentDefinition[_]]()) {(map, each) =>
      implicit def nameToTuple(name : String) : (String, ArgumentDefinition[_]) = name -> each
      map + each.shortName + each.longName
  }
  private val flagDefinitionsMap = flagDefinitions.foldLeft(Map[String, FlagDefinition]()) {(map, each) =>
      implicit def nameToTuple(name : String) : (String, FlagDefinition) = name -> each
      map + each.shortName + each.longName
  }

  protected type CmdArg <: Command

  /** Returns ''true'' if this is the root command, ''false'' otherwise. */
  lazy val isRoot = name.isEmpty

  /** Returns ''true'' if this command has any ''required'' values.*/
  def definesRequiredValues : Boolean

  /** Returns ''true'' if this command defines an argument by the name ''name'', ''false'' otherwise. */
  def isArgumentDefined(name : String) = argumentDefinitionsMap.contains(name)
  
  /**
   * Returns some argument definition by the name of ''name'' or ''None'' if there
   * are no argument definition matching that name.
   */
  def argumentDefinition(name : String) = argumentDefinitionsMap.get(name)

  /** Returns ''true'' if this command defines a flag by the name ''name'', ''false'' otherwise. */
  def isFlagDefined(name : String) = flagDefinitionsMap.contains(name)

  /**
   * Returns some flag definition by the name of ''name'' or ''None'' if there
   * are no flag definition matching that name.
   */
  def flagDefinition(name : String) = flagDefinitionsMap.get(name)


  /**
   * Parse the parameters passed in the short or long form, returning
   * the flags, the arguments, and the elements not parsed.
   *
   */
  protected def parseArguments(args : Array[String]) : (Set[Flag], Set[Argument[_]], List[String]) = {


    implicit class ParameterString(str : String) {
      def isParameter(implicit commandManager : CommandManager) = {
        str.startsWith(commandManager.shortParamSign) ||
        str.startsWith(commandManager.longParamSign)
      }
      def unParametrize(implicit commandManager : CommandManager) = {
        if ( str.startsWith(commandManager.longParamSign) )
          str.drop(commandManager.longParamSign.length)
        else if ( str.startsWith(commandManager.shortParamSign) )
          str.drop(commandManager.shortParamSign.length)
        else str
      }
    }



    val parameters = args.filter(_.isParameter)

    clRequire(parameters.size == parameters.toSet.size,
      "A parameter cannot be defined twice")

    val (_, params, values) = parameters.foldLeft {
      (args.dropWhile(!_.isParameter), Set[Parameter](), List[String]())
    }{ case ((notProcessed, params, values), current) =>
      if (current.isParameter) {
        // It's a parameter
        apply(current.unParametrize) match {
          case Some(paramDefinition) => {
            // It's defined, parse it
            val (param, rest) = paramDefinition.parse(notProcessed)
            // Do not allow long and short form of a parameter in the same call
            clRequire(!params.map(_.name).contains(param.name),
                "A parameter cannot be defined in both long and short form: " +
                param.name + " and " + param.alias.getOrElse(""))
            (rest.dropWhile(!_.isParameter), params + param, values ++ rest.takeWhile(!_.isParameter))
          }
          case None => {
            // They could be all flags with just one dash, if that's not
            // the case, it's an invalid parameter
            clRequire(current.unParametrize.forall(c => isFlagDefined(c.toString)),
                s"The parameter $current is not valid")
            clRequire(!current.startsWith(commandManager.longParamSign),
                s"Multiple flags can be passed only with the short form parameter sign (" +
                commandManager.shortParamSign + ")")
            // Otherwise, return each character as a flag
            (notProcessed,
                params ++ current.unParametrize.map(c =>
              flagDefinition(c.toString).get.parse(notProcessed)._1.asInstanceOf[Flag]),
                values)
          }
        }
      } else {
        // If it's not a parameter, that means it is a value
        (if (notProcessed.nonEmpty) notProcessed.tail else notProcessed,
        params, values :+ notProcessed.head)
      }
    }

    val processedParams = params.map(_.name)

    val parsedFlags =  params.filter(_.isFlag).asInstanceOf[Set[Flag]] ++
        flagDefinitions.filterNot(f => processedParams.contains(f.name)).map(_.toFlag)

    val parsedArguments = params.filter(_.isArgument).asInstanceOf[Set[Argument[_]]] ++
        argumentDefinitions.filterNot(a => processedParams.contains(a.name)).map(_.toArgument)

    val parsedValues = args.takeWhile(!_.isParameter).toList ++ values

    (parsedFlags, parsedArguments, parsedValues)
  }

  def parse(args : Array[String]) : CmdArg

  /**
   * Execute the command callback with the given [[Command]].
   *
   * @param cmd The CalledCommand with all the arguments and values
   *          passed as an argument to the command
   */
  def call(cmd : CmdArg) : Unit

  /**
   * Returns some ArgumentParameter or some FlagParameter by the name ''name'' if it exists, None otherwise.
   *
   * @param name The name of the argument parameter or flag parameter to fetch
   */
  def apply(name : String) : Option[ParameterDefinition] = {
    if (isFlagDefined(name)) flagDefinition(name)
    else if (isArgumentDefined(name)) argumentDefinition(name)
    else None
  }
}

/**
 * This class represents a command definition for commands that do not receive any value.
 *
 * This is a concrete implementation of [[CommandDefinition]].
 *
 * @param name The name of the command definition
 * @param argumentDefinitions The arguments that this command accepts
 * @param flagDefinitions The flags that this command accepts
 * @param callback The function to execute when this command is called
 */
case class NoValuesCommandDefinition(override val name : String,
    override val argumentDefinitions : Iterable[ArgumentDefinition[_]],
    override val flagDefinitions : Iterable[FlagDefinition],
    private val callback : NoValuesCommand => Unit)
    (implicit override val commandManager : CommandManager)
    extends CommandDefinition(name, argumentDefinitions, flagDefinitions)(commandManager)  {

  override protected type CmdArg = NoValuesCommand

  val definesRequiredValues = false

  def parse(args : Array[String]) : CmdArg = {
    val (flags, arguments, notProcessedArgs) = parseArguments(args)
    clRequire(notProcessedArgs.isEmpty, "The command " + (if (name.isEmpty) "'root'" else name) + " does not take values")

    NoValuesCommand(name, flags, arguments)
  }

  def call(cmd : CmdArg) = callback(cmd)
}


/**
 * This class represents a command definition for commands that receive a
 * finite amount of values.
 *
 * This is a concrete implementation of [[CommandDefinition]].
 *
 * @param name The name of the command definition
 * @param valueDefinitions The values that this command accept
 * @param argumentDefinitions The arguments that this command accepts
 * @param flagDefinitions The flags that this command accepts
 * @param callback The function to execute when this command is called
 */
case class FiniteValuesCommandDefinition(override val name : String, valueDefinitions : List[ValueDefinition[_]],
    override val argumentDefinitions : Iterable[ArgumentDefinition[_]], override val flagDefinitions : Iterable[FlagDefinition],
    private val callback : FiniteValuesCommand => Unit)
    (implicit override val commandManager : CommandManager)
    extends CommandDefinition(name, argumentDefinitions, flagDefinitions)(commandManager)  {

  require(valueDefinitions.map(_.name).toSet.size == valueDefinitions.size,
    "There cannot be two values with the same name defined for a command")
  require(!valueDefinitions.dropWhile(_.isMandatory).exists(_.isMandatory), "Required values should be grouped at the " +
      "beginning of the definition. There is a non mandatory value before a mandatory one.")

  //private val valueDefinitionsMap : Any = valueDefinitions.foldLeft(Map[String, ValueDefinition[_]]()){(map, each) =>
  //  map +=(each.name -> each)
  //}

  override protected type CmdArg = FiniteValuesCommand
  
  lazy val definesRequiredValues = valueDefinitions.exists(_.isMandatory)

  /** Returns ''true'' if this command takes a value, ''false'' otherwise. */
  lazy val isValueDefined = valueDefinitions.nonEmpty

  /** Returns a list of required value definitions. */
  lazy val requiredValues = valueDefinitions.filter(_.isMandatory)

  /** Returns the number of defined values. */
  lazy val numberOfValues = valueDefinitions.size

  /** Returns the number of required values. */
  lazy val numberOfRequiredValues = requiredValues.size

  def parse(args : Array[String]) : CmdArg = {
    val (flags, arguments, notProcessedArgs) = parseArguments(args)
    val numberOfGivenArgs = notProcessedArgs.size
    clRequire(numberOfValues >= numberOfGivenArgs,
        s"The command $name accepts $numberOfValues values, but $numberOfGivenArgs were given")
    clRequire(numberOfRequiredValues <= numberOfGivenArgs,
      s"The command $name requires $numberOfRequiredValues values, but $numberOfGivenArgs were given")

    val values = this.valueDefinitions.zipWithIndex.foldRight(List[Value[_]]()) { (tuple, list) =>
      val (each, i): (ValueDefinition[_], Int) = tuple
      val value = if (notProcessedArgs.size > i) notProcessedArgs(i) else each.default.get
      Value(each.name, value, notProcessedArgs.size > i) +: list
    }
    FiniteValuesCommand(name, flags, arguments, values)
  }

  def call(cmd : CmdArg) = callback(cmd)
}

/**
 * This class represents a command definition for commands that receive a
 * finite amount of values.
 *
 * This is a concrete implementation of [[CommandDefinition]].
 *
 * @param name The name of the command definition
 * @param minNumberOfValues The minimum amount of values that this command takes
 * @param maxNumberOfValues The maximum amount of values that this command takes
 * @param argumentDefinitions The arguments that this command accepts
 * @param flagDefinitions The flags that this command accepts
 * @param callback The function to execute when this command is called
 */
case class MultipleValuesCommandDefinition(override val name : String, minNumberOfValues : Option[Int],
    maxNumberOfValues : Option[Int], override val argumentDefinitions : Iterable[ArgumentDefinition[_]],
    override val flagDefinitions : Iterable[FlagDefinition], private val callback : MultipleValuesCommand => Unit)
    (implicit override val commandManager : CommandManager)
    extends CommandDefinition(name, argumentDefinitions, flagDefinitions)(commandManager)  {

  require(minNumberOfValues.isEmpty || minNumberOfValues.get > 0,
      "The minimum number of accepted values should be a positive number.")
  require(maxNumberOfValues.isEmpty || maxNumberOfValues.get > 0,
      "The maximum number of accepted values should be a positive number.")
  require(minNumberOfValues.isEmpty || maxNumberOfValues.isEmpty||
      minNumberOfValues.get < maxNumberOfValues.get ,
    "The minimum number of values should be lower than the maximum number of values.")

  override protected type CmdArg = MultipleValuesCommand

  lazy val definesRequiredValues = minNumberOfValues.isDefined

  /** Returns ''true'' if this command takes a maximum amount of values, ''false'' otherwise. */
  lazy val hasMaximumNumberOfValues = maxNumberOfValues.isDefined

  /** Returns the maximum amount of values that this command accepts. */
  lazy val maximumNumberOfValues = maxNumberOfValues.get

  /** Returns ''true'' if this command takes a minimum amount of values, ''false'' otherwise. */
  lazy val hasMinimumNumberOfValues = definesRequiredValues

  /** Returns the minimum amount of values that this command accepts. */
  lazy val minimumNumberOfValues = minNumberOfValues.get

  def parse(args : Array[String]) : CmdArg = {
    val (flags, arguments, notProcessedArgs) = parseArguments(args)
    val numberOfGivenArgs = notProcessedArgs.size
    clRequire(!hasMinimumNumberOfValues || minimumNumberOfValues <= numberOfGivenArgs,
        s"The command $name accepts " + minNumberOfValues.getOrElse("") + s" values, but $numberOfGivenArgs were given")
    clRequire(!hasMaximumNumberOfValues || maximumNumberOfValues >= numberOfGivenArgs,
        s"The command $name requires " + maxNumberOfValues.getOrElse("") + s" values, but $numberOfGivenArgs were given")

    MultipleValuesCommand(name, flags, arguments, notProcessedArgs.toList)
  }

  def call(cmd : CmdArg) = callback(cmd)
}