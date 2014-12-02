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

/**
 * This package defines all the values processed after parsing the user call.
 *
 * Classes defined in this package are passed to the callback
 * functions in order to be able to fetch the user data. You
 * should specially check the [[com.alanrodas.scaland.cli.runtime.Command]]
 * class and it's subclasses.
 */
package object runtime {

  /**
   * Convert a given value to another type using the provided converter
   */
  def convert[T, R](value : T)(implicit converter : String => R) : R = {
    value match {
      case v: String => converter(v)
      case _ =>
        if (converter.isInstanceOf[String => String]) value.toString
        else value.asInstanceOf[R]
    }
  }
}