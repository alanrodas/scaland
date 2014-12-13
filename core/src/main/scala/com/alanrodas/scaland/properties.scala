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

/**
 * This object hold all values that can be retrieved via the System.getProperty
 * method for an easier access.
 */
object properties {

  object file {
    /** File separator ("/" on UNIX) */
    lazy val separator = System.getProperty("file.separator")
  }
  object java {
    /** Java installation directory */
    lazy val home = System.getProperty("java.home")
    /** Java Runtime Environment version */
    lazy val version = System.getProperty("java.version")
    /** Java Runtime Environment vendor */
    lazy val vendor = System.getProperty("java.vendor")
    /** Java vendor URL */
    lazy val vendorUrl = System.getProperty("java.vendor.url")
    /** Name of JIT compiler to use */
    lazy val compiler = System.getProperty("java.compiler")

    object clazz {
      /** Java class format version number */
      lazy val version = System.getProperty("java.class.version")
      /** Java class path */
      lazy val path = System.getProperty("java.class.path")
    }
    object ext {
      /** Path of extension directory or directories */
      lazy val dirs = System.getProperty("ext.dirs")
    }
    object io {
      /** Default temp file path */
      lazy val tmpdir = System.getProperty("io.tmpdir")
    }
    object library {
      /** List of paths to search when loading libraries */
      lazy val path = System.getProperty("library.path")
    }
    object specification {
      /** Java Runtime Environment specification version */
      lazy val version = System.getProperty("java.specification.version")
      /** Java Runtime Environment specification vendor */
      lazy val vendor = System.getProperty("java.specification.vendor")
      /** Java Runtime Environment specification name */
      lazy val name = System.getProperty("java.specification.name")
    }
    object vm {
      /** Java Virtual Machine implementation version */
      lazy val version = System.getProperty("java.vm.version")
      /** JJava Virtual Machine implementation vendor */
      lazy val vendor = System.getProperty("java.vm.vendor")
      /** Java Virtual Machine implementation name */
      lazy val name = System.getProperty("java.vm.name")
      object specification {
        /** Java Virtual Machine specification version */
        lazy val version = System.getProperty("java.vm.specification.version")
        /** Java Virtual Machine specification vendor */
        lazy val vendor = System.getProperty("java.vm.specification.vendor")
        /** Java Virtual Machine specification name */
        lazy val name = System.getProperty("java.vm.specification.name")
      }
    }
  }
  object line {
    /** Line separator ("\n" on UNIX) */
    lazy val separator = System.getProperty("line.separator")
  }
  object os {
    /** Operating system name */
    lazy val name = System.getProperty("os.name")
    /** Operating system architecture */
    lazy val arch = System.getProperty("os.arch")
    /** Operating system version */
    lazy val version = System.getProperty("os.version")

    lazy val isWindows = name.toLowerCase.contains("win")
    lazy val isMac = name.toLowerCase.contains("mac")
    lazy val isSolaris = name.toLowerCase.contains("sunos")
    lazy val isUnix =
      name.toLowerCase.contains("nix") || name.toLowerCase.contains("nux") || name.toLowerCase.contains("aix")

  }
  object path {
    /** Path separator (":" on UNIX) */
    lazy val separator = System.getProperty("path.separator")
  }
  object user {
    /** User's account name */
    lazy val name = System.getProperty("user.name")
    /** User's home directory */
    lazy val home = System.getProperty("user.home")
    /** User's current working directory */
    lazy val dir = System.getProperty("user.dir")
  }
}