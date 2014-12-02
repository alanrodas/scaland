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


import scala.collection.mutable.ListBuffer
import scala.compat.Platform.currentTime

/** The `CLIApp` trait can be used to quickly turn objects
  *  into executable programs that executes command line argument
  *  command. Here is an example:
  *  {{{
  *  object Main extends CLIApp {
  *    Console.println("Hello World: " + (args mkString ", "))
  *  }
  *  }}}
  *  Here, object `Main` inherits the `main` method of `App`.
  *
  *  `args` returns the current command line arguments as an array.
  *
  *  ==Caveats==
  *
  *  '''''It should be noted that this trait is implemented using the [[DelayedInit]]
  *  functionality, which means that fields of the object will not have been initialized
  *  before the main method has been executed.'''''
  *
  *  It should also be noted that the `main` method should not be overridden:
  *  the whole class body becomes the “main method”.
  *
  *  Future versions of this trait will no longer extend `DelayedInit`.
  *
  *  This is based on Martin Odersky's code for App (ver 2.1 15/02/2011)
  *
  *  @author  Alan Rodas Bonjour
  *  @version 0.1
  */
trait CLIApp extends DelayedInit {

  /** ***************** START DELAYED INIT IMPL ************************/

  /** The time when the execution of this program started, in milliseconds since 1
    * January 1970 UTC. */
  // @deprecatedOverriding("executionStart should not be overridden", "2.11.0")
  val executionStart: Long = currentTime

  /** The command line arguments passed to the application's `main` method.
    */
  // @deprecatedOverriding("args should not be overridden", "2.11.0")
  protected def args: Array[String] = _args

  private var _args: Array[String] = _

  private val initCode = new ListBuffer[() => Unit]

  /** The init hook. This saves all initialization code for execution within `main`.
    * This method is normally never called directly from user code.
    * Instead it is called as compiler-generated code for those classes and objects
    * (but not traits) that inherit from the `DelayedInit` trait and that do not
    * themselves define a `delayedInit` method.
    * @param body the initialization code to be stored for later execution
    */
  @deprecated("The delayedInit mechanism will disappear.", "2.11.0")
  override def delayedInit(body: => Unit) {
    initCode += (() => body)
  }

  /** The main method.
    * This stores all arguments so that they can be retrieved with `args`
    * and then executes all initialization code segments in the order in which
    * they were passed to `delayedInit`.
    * @param args the arguments passed to the main method
    */
  // @deprecatedOverriding("main should not be overridden", "2.11.0")
  def main(args: Array[String]) = {
    this._args = args
    for (proc <- initCode) proc()
    try {
      commandManager.setSigns(paramSigns._1, paramSigns._2)
      commandManager.execute(args)
    } catch {
      case e: IllegalCommandLineArgumentsException => onIllegalCommandLineArgument(e)
    }
    if (util.Properties.propIsSet("scala.time")) {
      val total = currentTime - executionStart
      Console.println("[total " + total + "ms]")
    }
  }

  /** ***************** END DELAYED INIT IMPL ************************/

  /** Define the short and long parameter sign to use. Defaults to (''"-'', ''--''). */
  protected var paramSigns : (String, String) =
      (CommandManager.defaultShortParamSign, CommandManager.defaultLongParamSign)
  /** Hold the command that the user defined. */
  implicit protected val commandManager = new CommandManager()
  /** Define the action to take in case the program was called with an invalid set of values. */
  protected val onIllegalCommandLineArgument = (e: IllegalCommandLineArgumentsException) => {
    Terminal.error(e.getMessage)
  }
}
