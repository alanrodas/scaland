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

class Collections {

  /*
  trait NTreeNode[+T] {}
  class NNode[+T](val value : T, val parent : NTreeNode[T], val children : Seq[NTreeNode[T]]) extends NTreeNode[T] {
    override def equals(other : Any) =
      other match {
        case NNode(value, _, _) => this.value == value
        case _ => false
      }
  }
  object NNode {
    def unapply[T](node : NNode[T]) : Option[(T, NTreeNode[T], Seq[NTreeNode[T]])] =
      Some(node.value, node.parent, node.children)
    def apply[T](value : T) : NTreeNode[T] = new NNode(value, NEmptyNode, Nil)
  }
  case object NEmptyNode extends NTreeNode[Nothing]

  case class NTree[T](root : NTreeNode[T]) {
    var node : NTreeNode[Int] = NEmptyNode
    node = NNode(1)
  }
  */
}
