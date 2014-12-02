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

import com.alanrodas.scaland.cli.definitions._
import com.alanrodas.scaland.cli.runtime._

/**
 * This kind of exception is thrown whenever a command line application is called with
 * invalid arguments.
 *
 * If you are in the need for throwing this kind of exception, you should consider
 * the function [[clRequire]] at this same package.
 *
 * @param msg The error message for the current exception.
 */
class IllegalCommandLineArgumentsException(msg : String) extends RuntimeException(msg) {
  def this() = this("")
}


/**
 * Defines a set of values and functions in order to identify parameters
 * and work with them.
 */
object CommandManager{
  /** The default value for the short parameter sign. Defaults to ''"-''. */
  val defaultShortParamSign = "-"
  /** The default value for the long parameter sign. Defaults to ''"--''. */
  val defaultLongParamSign = "--"

}

/**
 * Instances of this class are in charge of holding the defined
 * commands, the short and long parameter signs, as well as
 * parsing the arguments and executing the according code.
 *
 * @param shortParamSign The short parameter sign to check for
 * @param longParamSign The long parameter sign to check for
 * @param autoAddCommands ''true'' if you want commands automatically added to the
 *     command manager when they are declared, ''false'' otherwise.
 */
class CommandManager(var shortParamSign : String = CommandManager.defaultShortParamSign,
    var longParamSign : String = CommandManager.defaultLongParamSign, val autoAddCommands : Boolean = true) {

  private var commands = Map[String, CommandDefinition]()
  private var rootCommand : Option[CommandDefinition] = None

  /**
   * Set the signs that are going to be used by this command
   * manager.
   *
   * That is, set the arguments that are checked when parsing
   * the argument inputed by the user. If this command is not
   * called it defaults to CommandManager.SHORT_PARAM_SIGN and
   * CommandManager.LONG_PARAM_SIGN as in ''"-"'' and ''"--''.
   *
   * @param shortParamSign The short parameter sign
   * @param longParamSign The long parameter sign
   */
  def setSigns(shortParamSign : String, longParamSign : String): Unit = {
    this.shortParamSign = shortParamSign
    this.longParamSign = longParamSign
  }

  /** Return the list of all declared commands. */
  def definedCommands = commands.values.toSet ++
      (if (rootCommand.isDefined) Set(rootCommand.get) else Set())

  /** Return the number of declared commands. */
  def numberOfDefinedCommands = definedCommands.size

  /** Add a command to the list of commands of this instance */
  def addCommand(command : CommandDefinition) : CommandDefinition = {
    require(!command.isRoot || (command.isRoot && !rootCommand.isDefined),
      "The root command cannot be defined twice")
    require(command.isRoot || (!command.isRoot && !commands.contains(command.name)),
      "There cannot be two command with the same name: " + command.name)
    if (command.isRoot) rootCommand = Some(command)
    else commands = commands + (command.name -> command)
    command
  }

  /**
   * Execute the command corresponding to the arguments passed as ''args''.
   *
   * The first argument passed in the array is matched against the
   * declared commands. If there are no matches, the root command is
   * executed. If there is no root command, then, an error is thrown.
   * If the root command is executed, the argument array is used as
   * values and arguments for the root command. If another command is executed
   * the argument array without the first element is used.
   *
   * Imagine the declared commands ''root'' and ''foo'', then
   * ''bar baz'' will execute the root command with ''bar baz'' as passed arguments.
   * If ''foo bar doo'' is passed, then the command ''foo'' with arguments
   * ''bar doo'' is called.
   *
   * Once parsed, the callback of the command is executed with the analyzed
   * arguments in the form of a [[Command]], which holds all the information
   * of the invocation.
   *
   * The ''args'' array passed to this method is usually the value passed to
   * the application by the shell through the main method. In case of extending
   * [[CLIApp]], this is done automatically.
   *
   * @param args The arguments passed as a command line call
   */
  def execute(args : Array[String]) : Unit = {
    val (command, arguments) = getCommandAndArguments(args)
    command match {
      case cmd : NoValuesCommandDefinition => cmd.call(cmd.parse(arguments))
      case cmd : FiniteValuesCommandDefinition => cmd.call(cmd.parse(arguments))
      case cmd : MultipleValuesCommandDefinition => cmd.call(cmd.parse(arguments))
    }
  }

  /** Return the command to call based on the given arguments */
  private def getCommandAndArguments(args : Array[String]) = {
    clRequire(args.nonEmpty || (args.isEmpty && rootCommand.isDefined),
      "There is no root command defined")
    clRequire(rootCommand.isDefined || (args.nonEmpty && commands.get(args(0)).isDefined),
      "There is no command defined by the name " + (if (args.nonEmpty) args(0) else "") )
    if (shouldCallRoot(args)) (rootCommand.get, args)
    else (commands(args(0)), args.tail)
  }

  /** ''true'' if the root command should be called, ''false'' otherwise. */
  private def shouldCallRoot(args : Array[String]) : Boolean = {
    rootCommand.isDefined && (args.isEmpty || (args.nonEmpty && !commands.contains(args(0))))
  }

}