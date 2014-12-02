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
import com.alanrodas.scaland.cli.definitions._

/**
 * Represents an abstract command call and the values used to call it.
 *
 * This is an abstract representation of a command call. That is, this class holds
 * the values of flags and arguments on which a user has called a given command,
 * included the defaulted values in its definition.
 *
 * It has three known concrete subclasses [[NoValuesCommand]], [[FiniteValuesCommand]]
 * and [[MultipleValuesCommand]] that represents calls to [[NoValuesCommandDefinition]],
 * [[FiniteValuesCommandDefinition]] and a [[MultipleValuesCommandDefinition]] respectively.
 * They last two add the values that a command has been called with in different ways.
 *
 * To create an instance of this class, the values that the user has employed when
 * calling the matching command as well as the default values are expected to be given
 * as an argument to the constructor. The command name is also needed. This involves
 * that in most cases you won't create instances of this class yourself, but they will
 * be created by a [[CommandManager]] as the result of executing a command.
 *
 * @param name The called command name
 * @param flags The [[Flag]] set that were used to call this command
 * @param arguments The [[Argument]] set that were used to call this command
 */
abstract class Command(val name : String, val flags : Iterable[Flag], val arguments : Iterable[Argument[_]]) {

  protected lazy val flagsMap = flags.foldLeft(Map[String, Flag]()){(map, each) =>
    map ++ each.shortName.fold( Map[String, Flag]() ) {s => Map(s->each)} ++
      each.longName.fold( Map[String, Flag]() ){s => Map(s->each)}
  }

  protected lazy val argumentsMap = arguments.foldLeft(Map[String, Argument[_]]()){(map, each) =>
    map ++ each.shortName.fold( Map[String, Argument[_]]() ) {s => Map(s->each)} ++
      each.longName.fold( Map[String, Argument[_]]() ){s => Map(s->each)}
  }

  /** Returns the number of flags defined for this command. */
  lazy val numberOfFlags = flags.size

  /** Returns the number of arguments defined for this command. */
  lazy val numberOfArguments = arguments.size

  /** Returns the number of values that this command holds. */
  def numberOfValues : Int

  /**
   * Returns the [[Flag]] by the given name.
   *
   * Note that a flag with the given name should be defined.
   *
   * @param name: The name of the argument to retrieve
   */
  def flag(name : String) = {
    require(flagsMap.contains(name), s"There is no flag by the name $name")
    flagsMap(name)
  }

  /**
   * Returns the [[Argument]] by the given name.
   *
   * Note that an argument with the given name should be defined.
   *
   * @param name: The name of the argument to retrieve
   */
  def argument(name : String) = {
    require(argumentsMap.contains(name), s"There is no argument by the name $name")
    argumentsMap(name)
  }

}

/**
 * This is a concrete implementation of [[Command]] for commands that do not
 * take any values.
 *
 * @param name The called command name
 * @param flags The Flags that were used to call this command
 * @param arguments  The Arguments that were used to call this command
 */
case class NoValuesCommand(override val name : String, override val flags : Iterable[Flag],
    override val arguments : Iterable[Argument[_]]) extends Command(name, flags, arguments) {

  val numberOfValues = 0
}

/**
 * This is a concrete implementation of [[Command]] for commands that define a concrete number
 * of values, accessible by name.
 *
 * It adds the values that the command was called with as a set of [[Value]].
 *
 * Note that the values may contain also defaulted values, as they should be passed
 * when creating instances of this class.
 *
 * @param name The called command name
 * @param values The values that were used to call this command
 * @param flags The Flags that were used to call this command
 * @param arguments  The Arguments that were used to call this command
 */
case class FiniteValuesCommand(override val name : String, override val flags : Iterable[Flag],
    override val arguments : Iterable[Argument[_]], values : Iterable[Value[_]])
    extends Command(name, flags, arguments) {

  protected lazy val valuesMap =  values.foldLeft(Map[String, Value[_]]()){(map, each) => map + (each.name -> each)}

  lazy val numberOfValues = values.size

  /**
   * Returns the value with the given name.
   *
   * The returned element is an instance of [[Value]]
   * that can be queried to test if it has been passed by the user,
   * it's name or value.
   *
   * Note that a value with the given name needs to be defined or an
   * [[IllegalArgumentException]] will be thrown.
   *
   * @param name: The name of the value to retrieve.
   */
  def value(name : String) = {
    require(valuesMap.contains(name), s"There is no value by the name $name")
    valuesMap(name)
  }

}

/**
 * This is a concrete implementation for [[Command]] for commands that take an unknown
 * number of values, accessible by position.
 *
 * @param name The called command name
 * @param values The values that were used to call this command
 * @param flags The Flags that were used to call this command
 * @param arguments  The Arguments that were used to call this command
 */
case class MultipleValuesCommand(override val name : String, override val flags : Iterable[Flag],
    override val arguments : Iterable[Argument[_]], values : List[_])
    extends Command(name, flags, arguments) {

  lazy val numberOfValues = values.size

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
   * @param i: The index of the value to retrieve.
   */
  def value(i : Int) = {
    require(i >= 0, s"The index $i should be greater or equal to zero")
    require(i < numberOfValues, s"The index $i should be lower than $numberOfValues")
    values(i)
  }

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
   */
  def valueAs[T](i : Int)(implicit converter : String => T) = convert(value(i))(converter)
}