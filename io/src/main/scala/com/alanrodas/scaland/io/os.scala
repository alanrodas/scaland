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

package com.alanrodas.scaland.io

import com.alanrodas.scaland.properties

trait Platform {
  def separator : String
  def root : String
}

object platforms {
  implicit object windows extends Platform {
    def separator = "\\"
    def root = "C:\\"
  }
  implicit object posix extends Platform {
    def separator = "/"
    def root = "/"
  }
  implicit object adaptive extends Platform {
    def separator = properties.file.separator
    def root = ""
  }
}