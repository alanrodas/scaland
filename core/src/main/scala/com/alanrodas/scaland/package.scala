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

package com.alanrodas

import scala.sys.process._

package object scaland {

  implicit class OptionableMap[K,V](map : Map[K,V]) {
    def +[T]( maybeAssoc : Option[T])(implicit f : T => (K, V)) : Map[K, V] =
      if (maybeAssoc.isDefined) map + f(maybeAssoc.get) else map
  }
  implicit class OptionableSet[T](set : Set[T]) {
    def +[S]( maybeElem : Option[S])(implicit f : S => T) : Set[T] =
      if (maybeElem.isDefined) set + f(maybeElem.get) else set
  }

  implicit class Path(rootPath : String) {
    def /(path : String) : String = {
      rootPath + properties.file.separator + path
    }
    def filename : String = rootPath.split(properties.file.separator).last
  }

  implicit def str2extProcBuilder(str : String) =
    new ExtendedProcessBuilder(str)
}
