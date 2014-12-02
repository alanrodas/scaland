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

package com.alanrodas.scaland.cli.builders


import com.alanrodas.scaland.cli.CommandManager
import com.alanrodas.scaland.cli.definitions._
import com.alanrodas.scaland.cli.runtime._

/**
 * A builder to construct instances of [[CommandDefinition]].
 *
 * This class is used for constructing instances using the library's DSL. At the point
 * of creating a new command with the DSL, there is no certainty if it will be a finite
 * values command, or a multiple values command. This forces this class to be a concrete
 * one, and return a most specified version when a certain condition is met such as stating
 * that it receives multiple values.
 *
 * @param commandManager The CommandManager to add the created CommandDefinition to. If using CLIApp,
 *          by using the provided DSL ''commands add "myCommand"'', the CLIApp command manager is used.
 */
abstract class CommandDefinitionBuilder(implicit val commandManager : CommandManager) {

  type TCommandDefinition <: CommandDefinition
  type TCommand <: Command

  var arguments = List[ArgumentDefinition[_]]()
  var flags = List[FlagDefinition]()
  var name = ""

  /**
   * Set the name of the instance to build.
   *
   * @param name The name to use in the constructed instance
   * @return this
   */
  def named(name : String) : this.type = {
    require(!name.isEmpty, "The name of a command cannot be empty. Are trying to create a root command? " +
      "Use 'root' instead of 'command named \"\"'")
    this.name = name
    this
  }

  /** Adds the given parameters as to the instance to be constructed. */
  def accepts(params : ParameterDefinition*) : this.type = {
    for(param <- params) {
      param match {
        case flag : FlagDefinition => flags = flags :+ flag
        case arg : ArgumentDefinition[_] => arguments = arguments :+ arg
      }
    }
    this
  }

  def does(callback : TCommand => Unit) : TCommandDefinition
}

object NoValuesCommandDefinitionBuilder {
  /**
   * Constructs an instance of [[FiniteValuesCommandDefinitionBuilder]],
   * from the given [[CommandDefinitionBuilder]].
   */
  def apply(builder : CommandDefinitionBuilder) = {
    val newBuilder = new FiniteValuesCommandDefinitionBuilder()(builder.commandManager)
    newBuilder.name = builder.name
    newBuilder.arguments = builder.arguments
    newBuilder.flags = builder.flags
    newBuilder
  }
}
/**
 * A builder to construct instances of [[FiniteValuesCommandDefinition]].
 *
 * This class is used for constructing instances using the library's DSL.
 *
 * @param commandManager The CommandManager to add the created CommandDefinition to. If using CLIApp,
 *          by using the provided DSL ''commands add "myCommand"'', the CLIApp command manager is used.
 */
class NoValuesCommandDefinitionBuilder(implicit override val commandManager : CommandManager)
    extends CommandDefinitionBuilder {

  type TCommandDefinition = NoValuesCommandDefinition
  type TCommand = NoValuesCommand

  /** Returns this command definition builder as a finite values command definition builder. */
  def receives(values : ValueDefinition[_]*) : FiniteValuesCommandDefinitionBuilder = {
    FiniteValuesCommandDefinitionBuilder(this).receives(values:_*)
  }

  /** Returns this command definition builder as a multiple values command definition builder. */
  def multipleValues : MultipleValuesCommandDefinitionBuilder = {
    MultipleValuesCommandDefinitionBuilder(this)
  }

  /**
   * Convert this instance in a multiple values definition builder and sets it's
   * minimum number of values.
   */
  def minimumOf(n : Int) : MultipleValuesCommandDefinitionBuilder = multipleValues.minimumOf(n)

  /**
   * Convert this instance in a multiple values definition builder and sets it's
   * maximum number of values.
   */
  def maximumOf(n : Int) : MultipleValuesCommandDefinitionBuilder = multipleValues.maximumOf(n)

  /** Constructs and adds a FiniteValuesCommandDefinition to this builder CommandManager. */
  def does(callback : NoValuesCommand => Unit) = {
    new NoValuesCommandDefinition(name, arguments, flags, callback)
  }
}

object FiniteValuesCommandDefinitionBuilder {
  /**
   * Constructs an instance of [[FiniteValuesCommandDefinitionBuilder]],
   * from the given [[CommandDefinitionBuilder]].
   */
  def apply(builder : CommandDefinitionBuilder) = {
    val newBuilder = new FiniteValuesCommandDefinitionBuilder()(builder.commandManager)
    newBuilder.name = builder.name
    newBuilder.arguments = builder.arguments
    newBuilder.flags = builder.flags
    newBuilder
  }
}
/**
 * A builder to construct instances of [[FiniteValuesCommandDefinition]].
 *
 * This class is used for constructing instances using the library's DSL.
 *
 * @param commandManager The CommandManager to add the created CommandDefinition to. If using CLIApp,
 *          by using the provided DSL ''commands add "myCommand"'', the CLIApp command manager is used.
 */
class FiniteValuesCommandDefinitionBuilder(implicit override val commandManager : CommandManager)
  extends CommandDefinitionBuilder {

  type TCommandDefinition = FiniteValuesCommandDefinition
  type TCommand = FiniteValuesCommand

  var values : List[ValueDefinition[_]] = Nil

  /** Adds a new set of value definitions to the constructed instance. */
  def receives(values : ValueDefinition[_]*) : this.type = {
    for(value <- values) {
      this.values = this.values :+ value
    }
    this
  }

  /** Constructs and adds a FiniteValuesCommandDefinition to this builder CommandManager. */
  def does(callback : FiniteValuesCommand => Unit) = {
    new FiniteValuesCommandDefinition(name, values, arguments, flags, callback)
  }
}

object MultipleValuesCommandDefinitionBuilder {
  /**
   * Constructs an instance of [[FiniteValuesCommandDefinitionBuilder]],
   * from the given [[CommandDefinitionBuilder]].
   */
  def apply(builder : CommandDefinitionBuilder) = {
    val newBuilder = new MultipleValuesCommandDefinitionBuilder()(builder.commandManager)
    newBuilder.name = builder.name
    newBuilder.arguments = builder.arguments
    newBuilder.flags = builder.flags
    newBuilder
  }
}
/**
 * A builder to construct instances of [[MultipleValuesCommandDefinition]].
 *
 * This class is used for constructing instances using the library's DSL.
 *
 * @param commandManager The CommandManager to add the created CommandDefinition to. If using CLIApp,
 *          by using the provided DSL ''commands add "myCommand"'', the CLIApp command manager is used.
 */
class MultipleValuesCommandDefinitionBuilder(implicit override val commandManager : CommandManager)
  extends CommandDefinitionBuilder {

  type TCommandDefinition = MultipleValuesCommandDefinition
  type TCommand = MultipleValuesCommand

  private var minMultiArgs : Option[Int] = None
  private var maxMultiArgs : Option[Int] = None

  /** Sets the number of minimum argument for the constructed instance. */
  def minimumOf(n : Int) : this.type = {
    minMultiArgs = Some(n)
    this
  }

  /** Sets the number of maximum argument for the constructed instance. */
  def maximumOf(n : Int) : this.type = {
    maxMultiArgs = Some(n)
    this
  }

  /** Constructs and adds a MultipleValuesCommandDefinition to this builder CommandManager. */
  def does(callback : MultipleValuesCommand => Unit) = {
    new MultipleValuesCommandDefinition(name, minMultiArgs, maxMultiArgs, arguments, flags, callback)
  }
}

