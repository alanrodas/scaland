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
import com.alanrodas.scaland.cli.definitions.{ArgumentDefinition, FlagDefinition}
import com.alanrodas.scaland.cli.definitions._


/**
 * An abstract class definition for constructing instances
 * of subclasses of [[ParameterDefinition]].
 * using this class known subclasses.
 *
 * There are two known subclasses for this class,
 * [[ArgumentDefinitionBuilder]] and [[FlagDefinitionBuilder]].
 *
 * This class is used for constructing instances using the library's DSL.
 */
protected abstract class ParameterDefinitionBuilder() {

  var name : String = ""
  var alias : Option[String] = None
  var isRequired : Boolean = false

  /**
   * Set the name of the instance to build.
   *
   * @param name The name to use in the constructed instance
   * @return this
   */
  def named(name : String) : this.type = {
    this.name = name
    this
  }

  /**
   * Set the alternative name of the instance to build.
   *
   * @param altName The name to use in the constructed instance
   * @return this
   */
  def alias(altName : String) : this.type = {
    this.alias = Some(altName)
    this
  }
}

/**
 * A builder to construct instances of [[FlagDefinition]].
 *
 * This class is used for constructing instances using the library's DSL.
 */
class FlagDefinitionBuilder(implicit val commandManager : CommandManager)
    extends ParameterDefinitionBuilder

/**
 * A builder to construct instances of [[ArgumentDefinition]].
 *
 * This class is used for constructing instances using the library's DSL.
 */
class ArgumentDefinitionBuilder(implicit val commandManager : CommandManager)
    extends ParameterDefinitionBuilder {

  var numberOfArgumentValues : Int = 0
  var defaults = Seq[Any]()

  /**
   * Set the created instance as a required argument.
   *
   * @return this
   */
  def mandatory : this.type = {
    this.isRequired = true
    this
  }

  /**
   * Set the number of argument values that the instance will take.
   *
   * @param n The number of arguments to accept as a positive integer
   * @return this
   */
  def taking(n : Int) : this.type = {
    this.numberOfArgumentValues = n
    this
  }

  /**
   * Set the default values for the argument values that this instance will take.
   *
   * Note that {{{defaults.length <= n}}} where ''n'' is the number
   * of argument that the argument will take, passed using the
   * ''accepting'' command.
   *
   * @param defaults the default values for the arguments that this argument will take.
   * @return The constructed instance
   */
  def as[T](defaults : T*) = {
    this.defaults = defaults
    this
  }

  /** Constructs the instance with no defaults and returns it. */
  def values = {
    require(!(numberOfArgumentValues == 1),
      "The number of taken values is 1, you should use 'value' instead of 'values'")
    as()
  }

  /** Constructs the instance with no defaults and returns it. */
  def value = {
    require(!(numberOfArgumentValues > 1),
      "The number of taken values larger than 1, you should use 'values' instead of 'value'")
    as()
  }
}

/**
 * A builder to construct instances of
 * [[com.alanrodas.scaland.cli.definitions.ValueDefinition ValueDefinition]].
 *
 * This class is used for constructing instances using the library's DSL.
 */
class ValueDefinitionBuilder(implicit val commandManager : CommandManager) {
  var name : String = ""
  var default : Option[String] = None

  /**
   * Set the name of the instance to build.
   *
   * @param name The name to use in the constructed instance
   * @return this
   */
  def named(name : String) = {
    this.name = name
    this
  }

  /**
   * Set the default value of the instance to build and create the instance.
   *
   * @param default The default value to use in the constructed instance
   * @return The constructed instance
   */
  def as[T](default : T) = {
    new ValueDefinition(name, Some(default))
  }

  /**
   * Create an instance without default value.
   *
   * @return The constructed instance
   */
  def mandatory = {
    new ValueDefinition(name, None)
  }
}