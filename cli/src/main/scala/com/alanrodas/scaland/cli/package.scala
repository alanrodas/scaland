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

import language.implicitConversions

import com.alanrodas.scaland.cli.builders._
import com.alanrodas.scaland.cli.definitions.{ArgumentDefinition, FlagDefinition}
import com.alanrodas.scaland.cli.runtime._

/**
 * This package defines most of the DSL of Scaland CLI.
 */
package object cli {

  /**
   * Returns a [[ArgumentDefinitionBuilder]] to construct a new argument definition.
   */
  def arg(implicit commandManager : CommandManager) = new ArgumentDefinitionBuilder()

  /**
   * Returns a [[ValueDefinitionBuilder]] to construct a new value definition.
   */
  def value(implicit commandManager : CommandManager) = new ValueDefinitionBuilder()

  /**
   * Returns a [[FlagDefinitionBuilder]] to construct a new flag definition.
   */
  def flag(implicit commandManager : CommandManager) = new FlagDefinitionBuilder()

  /**
   * Returns a [[CommandDefinitionBuilder]] to construct a new command definition.
   */
  def command(implicit commandManager : CommandManager) = new NoValuesCommandDefinitionBuilder

  /**
   * Returns an [[CommandDefinitionBuilder]] for the root command (empty name) to
   * construct a new command definition.
   */
  def root(implicit commandManager : CommandManager) = new NoValuesCommandDefinitionBuilder

  /**
   * Transform a FlagDefinitionBuilder into a FlagDefinition.
   *
   * This is used by the DSL like language.
   * @param builder The builder to transform
   * @return The proper FlagDefinition
   */
  implicit def flagBuilder2FlagDefinition(builder : FlagDefinitionBuilder) : FlagDefinition =
    new FlagDefinition(builder.name, builder.alias)(builder.commandManager)

  /**
   * Transform a ArgumentDefinitionBuilder into a ArgumentDefinition.
   *
   * This is used by the DSL like language.
   * @param builder The builder to transform
   * @return The proper ArgumentDefinition
   */
  implicit def argBuilder2ArgDefinition(builder : ArgumentDefinitionBuilder) : ArgumentDefinition[_] = {
    require(builder.numberOfArgumentValues > 0,
      "The number of taken values has not been set for " + builder.name +
      ". You should use 'taking' to set it")
    new ArgumentDefinition(builder.name, builder.alias, builder.isRequired,
      builder.numberOfArgumentValues, builder.defaults)(builder.commandManager)
  }


  /**
   * Transform an AbstractArgument to a Boolean
   *
   * This allows you to test for an argument or flag to check if it has been
   * defined by the user.
   *
   * @param arg The abstract argument to test
   * @return ''true'' is user defined, ''false'' otherwise.
   */
  implicit def param2Boolean(arg : Parameter) : Boolean = arg.isDefined

  /**
   * This class provides an implicit conversion from a [[String]] to an [[Array]]
   * of strings.
   *
   * The conversion is performed parsing the string in the same fashion that the
   * shell parses a string to pass it to the main method of an application.
   *
   * @param str The string to wrap.
   */
  implicit class String2Args(str : String) {

    /**
     * Convert the wrapped [[String]] to an [[Array]] of string
     *
     * The conversion is performed parsing the string in the same fashion that the
     * shell parses a string to pass it to the main method of an application.
     */
    def toArgs = {
      // TODO This still needs to take things such as quoted strings, escaped spaces, and more
      if (str.isEmpty) Array[String]() else str.split("\\s")
    }
  }

  /**
   * Tests an expression, throwing an [[IllegalCommandLineArgumentsException]] if false.
   *
   * This method is similar to require, but it works at the application level, blaming
   * the caller of the application for any error.
   */
  def clRequire(bool : Boolean) { if (!bool) throw new IllegalCommandLineArgumentsException() }

  /**
   * Tests an expression, throwing an [[IllegalCommandLineArgumentsException]] if false.
   *
   * This method is similar to require, but it works at the application level, blaming
   * the caller of the application for any error.
   *
   * @param msg The error message for the exception.
   */
  def clRequire(bool : Boolean, msg : String = "") { if (!bool) throw new IllegalCommandLineArgumentsException(msg) }

  /**
   * Print a command execution result.
   *
   * This is only intended for debugging until proper testing is added to
   * this project. Try hard not to use this.
   *
   * @param cmd The command to print
   */
  @deprecated
  def dump(cmd : Command)(implicit commandManager : CommandManager) = {
    val longParamSign = commandManager.longParamSign
    val shortParamSign = commandManager.shortParamSign
    Terminal.show("Called command: " + Console.BLUE + (if (cmd.name.isEmpty) "<root>" else cmd.name) + Console.RESET +
        // Display all values
        "\nValues are: ("+ Console.BLUE + cmd.numberOfValues + Console.RESET + ")\n" +
        (cmd match {
          case c: FiniteValuesCommand => {
            for (value <- c.values) yield {
              Console.BLUE + value.name + Console.RESET + ": (" +
                  (if (value.isDefined) Console.GREEN + "User defined"
                  else Console.RED + "Default") + Console.RESET + ") " + value.value
            }
          }.mkString("\n")
          case c : MultipleValuesCommand =>
            Console.GREEN + c.values.mkString("\n") + Console.RESET
        }) +
        // Display all flags
        "\nFlags Are: (" + Console.BLUE + cmd.numberOfFlags + Console.RESET + ")\n" +
        (for (flag <- cmd.flags) yield {
          Console.BLUE + (
              flag.longName.fold("") { s => longParamSign + s } +
              flag.longName.fold("") { _ => " " } +
              flag.shortName.fold("") { s => shortParamSign + s }
          ) + Console.RESET + ": (" +
              (if (flag.isDefined) Console.GREEN + "Passed" else Console.RED + "Not Passed") +
           Console.RESET + ")"
        }).mkString("\n") +
        // Display all arguments
        "\nArguments are: (" + Console.BLUE + cmd.numberOfArguments + Console.RESET + ")\n" +
        (for (arg <- cmd.arguments) yield
          Console.BLUE + (
              arg.longName.fold("") { s => longParamSign + s } +
                  arg.longName.fold("") { _ => " " } +
                  arg.shortName.fold("") { s => shortParamSign + s }
          ) + Console.RESET + ": (" +
              (if (arg.isDefined) Console.GREEN + "User defined"
              else Console.RED + "Default") +
          Console.RESET + ") " + arg.value.toString
         ).mkString("\n")
    )
  }
}