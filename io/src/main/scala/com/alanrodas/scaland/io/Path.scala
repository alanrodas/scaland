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


/*
Path
  - RelativePath
  - AbsolutePath
    - Drive.C / "WINDOWS" -> same as File if default disk
    - Root / "etc" -> same as File
    - UserHome / "Images"
    - Current /temp"
    - File / "etc"

 */

object Drive {
  val A = "A:"
  val B = "B:"
  val C = "C:"
  val D = "D:"
  val E = "E:"
  val F = "F:"
  val G = "G:"
  val H = "H:"
  val I = "I:"
  val J = "J:"
  val K = "K:"
  val L = "L:"
  val M = "M:"
  val N = "N:"
  val O = "O:"
  val P = "P:"
  val Q = "O:"
  val R = "R:"
  val S = "S:"
  val T = "T:"
  val U = "U:"
  val V = "V:"
  val W = "W:"
  val X = "X:"
  val Y = "Y:"
  val Z = "Z:"

  def mapped = (for(f <- java.io.File.listRoots()) yield f.getAbsolutePath.replace("\\","")).toSeq
}

trait Path[+PathType <: Path[PathType]] {
  // def root : PathRoot
  // def /(element : String) : PathType
  def parent : PathType = ???
  def children : Seq[Path[PathType]] = ???
}

case class RelativePath(val elements : Seq[String]) extends Path[RelativePath] {
  def /(element : String) : RelativePath = RelativePath(element +: elements)
}

/*
case class AbsolutePath[+PathType](val root : PathRoot[PathType], val elements : Seq[String]) {
  def /(element : String) = AbsolutePath(root, element +: elements)
}
*/

trait PathRoot[+PathType <: PathRoot[PathType]] {
  def scheme : String
  //def makePath() : Path[PathType]
}

class FileRoot extends PathRoot[FileRoot] {
  def scheme : String = "file://"
  //def makePath() : Path[FilePath] = ???
}
case class FilePath(elements : Seq[String], absolutePath : String)
    extends Path[FilePath] {
  def root : PathRoot[_] = ???
  def /(element : String)(implicit platform : Platform) : FilePath =
    FilePath(element +: elements, absolutePath + platform.separator)
  def /(path : RelativePath)(implicit platform : Platform) = ???
  override def toString = "file://" + absolutePath
}


object File extends FileRoot {
  def currentDir(implicit platform : Platform) = apply(properties.user.dir)
  def home(implicit platform : Platform) = apply(properties.user.home)
  def root(implicit platform : Platform) = apply("/")

  def apply(path : String)(implicit platform : Platform) : FilePath = {
    FilePath(path.split(platform.separator), path)
  }
}

object Main extends App {
  import platforms.adaptive
  println("Relative" / "path" / "is" / "here")
  println(Current / "first" / "second")
  println(Home / "library" / "this")
  println(Root / "OOPPPPSSS")
}