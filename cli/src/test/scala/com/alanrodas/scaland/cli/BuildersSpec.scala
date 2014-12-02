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

import com.alanrodas.scaland._
import com.alanrodas.scaland.cli.definitions.{ArgumentDefinition, FlagDefinition}
import org.scalatest._
import language.postfixOps

class BuildersSpec extends FlatSpec with Matchers with BeforeAndAfter {

  val emptyString = ""

  val foo = "foo"
  val bar = "bar"
  val baz = "baz"
  val f = "f"
  val b = "b"
  val g = "g"

  val shortSign = "-"
  val longSign = "--"

  val shortSignAlt = "/"
  val longSignAlt = "/"

  implicit var commandManager = new CommandManager()
  before {
    commandManager.setSigns(shortSign, longSign)
  }
  after {
    commandManager = new CommandManager()
  }

  /* ********* Values ************ */

  "A ValueDefinition as mandatory" should "form nicely if a valid name is given" in {
    cli.value named foo mandatory
  }

  it should "form nicely if a name with spaces is given" in {
    cli.value named s"$foo $bar $baz" mandatory
  }

  it should "throw an IllegalArgumentException if no name is passed" in {
    an [IllegalArgumentException] should be thrownBy {
      cli.value named emptyString mandatory
    }
  }

  it should "not contain a default value" in {
    (cli.value named foo mandatory).default should be (None)
  }

  "A ValueDefinition with default value" should "form nicely if a valid name is given" in {
    cli.value named foo as "something"
  }

  it should "form nicely if a name with spaces is given" in {
    cli.value named s"$foo $bar" as "something"
  }

  it should "throw an IllegalArgumentException if no name is passed" in {
    an [IllegalArgumentException] should be thrownBy {
      cli.value named emptyString as "something"
    }
  }

  it should "take any value as default value" in {
    cli.value named s"$foo $bar $baz" as ""
    cli.value named s"$foo $bar" as 4
    cli.value named s"$foo $baz $bar" as Seq(1,2,3,4)
    cli.value named s"$bar $bar" as Some(("four", 4))
  }

  it should "retrieve the same value as passed when queried" in {
    (cli.value named s"$foo $bar $baz" as "").default.get should be ("")
    (cli.value named s"$foo $bar" as 4).default.get should be (4)
    (cli.value named s"$foo $baz $bar" as Seq(1,2,3,4)).default.get should be (Seq(1,2,3,4))
    (cli.value named s"$bar $bar" as Some(("four", 4))).default.get should be (Some(("four", 4)))
  }

  /* ********* Flags ************ */

  "A FlagDefinition" should "form nicely if a valid name is given" in {
    cli.flag named bar : FlagDefinition
  }

  it should "throw an IllegalArgumentException if no name is passed" in {
    an [IllegalArgumentException] should be thrownBy {
      cli.flag named emptyString : FlagDefinition
    }
  }

  it should "throw an IllegalArgumentException if the passed name has spaces" in {
    an [IllegalArgumentException] should be thrownBy {
      cli.flag named s"$bar $baz" : FlagDefinition
    }
    an [IllegalArgumentException] should be thrownBy {
      cli.flag named s" $baz" : FlagDefinition
    }
    an [IllegalArgumentException] should be thrownBy {
      cli.flag named s"$foo  " : FlagDefinition
    }
  }

  it should "throw an IllegalArgumentException if the name starts with the short or long sign of the CommandManager" in {
    an [IllegalArgumentException] should be thrownBy {
      cli.flag named s"$shortSign$foo" : FlagDefinition
    }
    an [IllegalArgumentException] should be thrownBy {
      cli.flag named s"$longSign$foo" : FlagDefinition
    }
    an [IllegalArgumentException] should be thrownBy {
      cli.flag named s"$shortSign$b" : FlagDefinition
    }
    an [IllegalArgumentException] should be thrownBy {
      cli.flag named s"$longSign$f" : FlagDefinition
    }
    an [IllegalArgumentException] should be thrownBy {
      cli.flag named shortSign : FlagDefinition
    }
    an [IllegalArgumentException] should be thrownBy {
      cli.flag named longSign : FlagDefinition
    }
    commandManager.setSigns(shortSignAlt, longSignAlt)
    an [IllegalArgumentException] should be thrownBy {
      cli.flag named s"$shortSignAlt$baz" : FlagDefinition
    }
    an [IllegalArgumentException] should be thrownBy {
      cli.flag named s"$longSignAlt$foo" : FlagDefinition
    }
    an [IllegalArgumentException] should be thrownBy {
      cli.flag named s"$shortSignAlt$g" : FlagDefinition
    }
    an [IllegalArgumentException] should be thrownBy {
      cli.flag named s"$longSignAlt$f" : FlagDefinition
    }
    an [IllegalArgumentException] should be thrownBy {
      cli.flag named shortSignAlt : FlagDefinition
    }
    an [IllegalArgumentException] should be thrownBy {
      cli.flag named longSignAlt : FlagDefinition
    }
  }

  it should "form nicely if the name has short or long sign in the middle or end" in {
    cli.flag named s"$bar$shortSign$baz" : FlagDefinition
    cli.flag named s"$foo$longSign$baz" : FlagDefinition
    cli.flag named s"$baz$shortSign" : FlagDefinition
    cli.flag named s"$bar$longSign" : FlagDefinition
    cli.flag named s"$g$shortSign" : FlagDefinition
    cli.flag named s"$b$longSign" : FlagDefinition
    cli.flag named shortSignAlt : FlagDefinition
    cli.flag named longSignAlt : FlagDefinition
    commandManager.setSigns(shortSignAlt, longSignAlt)
    cli.flag named s"$bar$shortSignAlt$baz" : FlagDefinition
    cli.flag named s"$foo$longSignAlt$baz" : FlagDefinition
    cli.flag named s"$baz$shortSignAlt" : FlagDefinition
    cli.flag named s"$bar$longSignAlt" : FlagDefinition
    cli.flag named s"$g$shortSignAlt" : FlagDefinition
    cli.flag named s"$f$longSignAlt" : FlagDefinition
    cli.flag named shortSign : FlagDefinition
    cli.flag named longSign : FlagDefinition
  }

  it should "form nicely if a valid name and alias are given" in {
    cli.flag named foo alias g : FlagDefinition
    cli.flag named b alias bar : FlagDefinition
  }

  it should "throw an IllegalArgumentException if the alias is empty" in {
    an [IllegalArgumentException] should be thrownBy {
      cli.flag named baz alias emptyString : FlagDefinition
    }
  }

  it should "throw an IllegalArgumentException if the alias has spaces" in {
    an [IllegalArgumentException] should be thrownBy {
      cli.flag named f alias s"$foo $bar" : FlagDefinition
    }
  }

  it should "throw an IllegalArgumentException if the name and alias are both larger than one character" in {
    an [IllegalArgumentException] should be thrownBy {
      cli.flag named baz alias bar : FlagDefinition
    }
  }

  it should "throw an IllegalArgumentException if the name and alias are both of one character" in {
    an [IllegalArgumentException] should be thrownBy {
      cli.flag named b alias f : FlagDefinition
    }
  }

  it should "throw an IllegalArgumentException if the alias starts with the short or long sign of the CommandManager" in {
    an [IllegalArgumentException] should be thrownBy {
      cli.flag named f alias s"$shortSign$baz" : FlagDefinition
    }
    an [IllegalArgumentException] should be thrownBy {
      cli.flag named f alias  s"$longSign$baz" : FlagDefinition
    }
    an [IllegalArgumentException] should be thrownBy {
      cli.flag named foo alias s"$shortSign$b" : FlagDefinition
    }
    an [IllegalArgumentException] should be thrownBy {
      cli.flag named bar alias s"$longSign$f" : FlagDefinition
    }
    an [IllegalArgumentException] should be thrownBy {
      cli.flag named foo alias shortSign : FlagDefinition
    }
    an [IllegalArgumentException] should be thrownBy {
      cli.flag named bar alias longSign : FlagDefinition
    }
    commandManager.setSigns(shortSignAlt, longSignAlt)
    an [IllegalArgumentException] should be thrownBy {
      cli.flag named f alias s"$shortSignAlt$baz" : FlagDefinition
    }
    an [IllegalArgumentException] should be thrownBy {
      cli.flag named b alias s"$longSignAlt$bar" : FlagDefinition
    }
    an [IllegalArgumentException] should be thrownBy {
      cli.flag named bar alias s"$shortSignAlt$f" : FlagDefinition
    }
    an [IllegalArgumentException] should be thrownBy {
      cli.flag named baz alias s"$longSignAlt$b" : FlagDefinition
    }
    an [IllegalArgumentException] should be thrownBy {
      cli.flag named foo alias shortSignAlt : FlagDefinition
    }
    an [IllegalArgumentException] should be thrownBy {
      cli.flag named bar alias longSignAlt : FlagDefinition
    }
  }

  it should "form nicely if the alias has short or long sign in the middle or end" in {
    cli.flag named f alias s"$bar$shortSign$baz" : FlagDefinition
    cli.flag named g alias s"$bar$longSign$baz" : FlagDefinition
    cli.flag named g alias s"$bar$shortSign" : FlagDefinition
    cli.flag named b alias s"$bar$longSign" : FlagDefinition
    cli.flag named foo alias shortSignAlt : FlagDefinition
    cli.flag named bar alias longSignAlt : FlagDefinition
    commandManager.setSigns(shortSignAlt, longSignAlt)
    cli.flag named g alias s"$bar$shortSignAlt$baz" : FlagDefinition
    cli.flag named f alias s"$bar$longSignAlt$baz" : FlagDefinition
    cli.flag named f alias s"$foo$shortSignAlt" : FlagDefinition
    cli.flag named b alias s"$baz$longSignAlt" : FlagDefinition
    cli.flag named foo alias shortSign : FlagDefinition
  }

  /* ********* Arguments ************ */

  "An ArgumentDefinition" should "form nicely if a valid name is given" in {
    cli.arg named foo taking 1 value : ArgumentDefinition[_]
  }

  it should "throw an IllegalArgumentException if no name is passed" in {
    an [IllegalArgumentException] should be thrownBy {
      cli.arg named emptyString taking 1 value : ArgumentDefinition[_]
    }
  }

  it should "throw an IllegalArgumentException if the passed name has spaces" in {
    an [IllegalArgumentException] should be thrownBy {
      cli.arg named s"$foo $baz" taking 1 value : ArgumentDefinition[_]
    }
    an [IllegalArgumentException] should be thrownBy {
      cli.arg named s" $foo" taking 1 value : ArgumentDefinition[_]
    }
    an [IllegalArgumentException] should be thrownBy {
      cli.arg named s"$foo " taking 1 value : ArgumentDefinition[_]
    }
  }

  it should "throw an IllegalArgumentException if the name starts with the short or long sign of the CommandManager" in {
    an [IllegalArgumentException] should be thrownBy {
      cli.arg named s"$shortSign$foo" taking 1 value : ArgumentDefinition[_]
    }
    an [IllegalArgumentException] should be thrownBy {
      cli.arg named s"$longSign$foo" taking 1 value : ArgumentDefinition[_]
    }
    an [IllegalArgumentException] should be thrownBy {
      cli.arg named s"$shortSign$b" taking 1 value : ArgumentDefinition[_]
    }
    an [IllegalArgumentException] should be thrownBy {
      cli.arg named s"$longSign$f" taking 1 value : ArgumentDefinition[_]
    }
    an [IllegalArgumentException] should be thrownBy {
      cli.arg named shortSign taking 1 value : ArgumentDefinition[_]
    }
    an [IllegalArgumentException] should be thrownBy {
      cli.arg named longSign taking 1 value : ArgumentDefinition[_]
    }
    commandManager.setSigns(shortSignAlt, longSignAlt)
    an [IllegalArgumentException] should be thrownBy {
      cli.arg named s"$shortSignAlt$baz" taking 1 value : ArgumentDefinition[_]
    }
    an [IllegalArgumentException] should be thrownBy {
      cli.arg named s"$longSignAlt$foo" taking 1 value : ArgumentDefinition[_]
    }
    an [IllegalArgumentException] should be thrownBy {
      cli.arg named s"$shortSignAlt$g" taking 1 value : ArgumentDefinition[_]
    }
    an [IllegalArgumentException] should be thrownBy {
      cli.arg named s"$longSignAlt$f" taking 1 value : ArgumentDefinition[_]
    }
    an [IllegalArgumentException] should be thrownBy {
      cli.arg named shortSignAlt taking 1 value : ArgumentDefinition[_]
    }
    an [IllegalArgumentException] should be thrownBy {
      cli.arg named longSignAlt taking 1 value : ArgumentDefinition[_]
    }
  }

  it should "form nicely if the name has short or long sign in the middle or end" in {
    cli.arg named s"$bar$shortSign$baz" taking 7 values : ArgumentDefinition[_]
    cli.arg named s"$foo$longSign$baz" taking 1 value : ArgumentDefinition[_]
    cli.arg named s"$baz$shortSign" taking 1 value : ArgumentDefinition[_]
    cli.arg named s"$bar$longSign" taking 4 values : ArgumentDefinition[_]
    cli.arg named s"$g$shortSign" taking 1 value : ArgumentDefinition[_]
    cli.arg named s"$b$longSign" taking 1 value : ArgumentDefinition[_]
    cli.arg named shortSignAlt taking 2 values : ArgumentDefinition[_]
    cli.arg named longSignAlt taking 1 value : ArgumentDefinition[_]
    commandManager.setSigns(shortSignAlt, longSignAlt)
    cli.arg named s"$bar$shortSignAlt$baz" taking 1 value : ArgumentDefinition[_]
    cli.arg named s"$foo$longSignAlt$baz" taking 1 value : ArgumentDefinition[_]
    cli.arg named s"$baz$shortSignAlt" taking 5 values : ArgumentDefinition[_]
    cli.arg named s"$bar$longSignAlt" taking 1 value : ArgumentDefinition[_]
    cli.arg named s"$g$shortSignAlt" taking 2 values : ArgumentDefinition[_]
    cli.arg named s"$f$longSignAlt" taking 1 value : ArgumentDefinition[_]
    cli.arg named shortSign taking 2 values : ArgumentDefinition[_]
    cli.arg named longSign taking 1 value : ArgumentDefinition[_]
  }

  it should "form nicely if a valid name and alias are given" in {
    cli.arg named foo alias g taking 1 value : ArgumentDefinition[_]
    cli.arg named b alias baz taking 1 value : ArgumentDefinition[_]
  }

  it should "throw an IllegalArgumentException if the alias is empty" in {
    an [IllegalArgumentException] should be thrownBy {
      cli.arg named baz alias emptyString taking 1 value : ArgumentDefinition[_]
    }
  }

  it should "throw an IllegalArgumentException if the alias has spaces" in {
    an [IllegalArgumentException] should be thrownBy {
      cli.arg named f alias s"$foo $bar" taking 1 value : ArgumentDefinition[_]
    }
  }

  it should "throw an IllegalArgumentException if the name and alias are both larger than one character" in {
    an [IllegalArgumentException] should be thrownBy {
      cli.arg named baz alias bar taking 1 value : ArgumentDefinition[_]
    }
  }

  it should "throw an IllegalArgumentException if the name and alias are both of one character" in {
    an [IllegalArgumentException] should be thrownBy {
      cli.arg named f alias g taking 1 value : ArgumentDefinition[_]
    }
  }

  it should "throw an IllegalArgumentException if the alias starts with the short or long sign of the CommandManager" in {
    an [IllegalArgumentException] should be thrownBy {
      cli.arg named f alias s"$shortSign$baz" taking 1 values : ArgumentDefinition[_]
    }
    an [IllegalArgumentException] should be thrownBy {
      cli.arg named f alias  s"$longSign$baz" taking 1 value : ArgumentDefinition[_]
    }
    an [IllegalArgumentException] should be thrownBy {
      cli.arg named foo alias s"$shortSign$b" taking 1 value : ArgumentDefinition[_]
    }
    an [IllegalArgumentException] should be thrownBy {
      cli.arg named bar alias s"$longSign$f" taking 5 values : ArgumentDefinition[_]
    }
    an [IllegalArgumentException] should be thrownBy {
      cli.arg named foo alias shortSign taking 1 value : ArgumentDefinition[_]
    }
    an [IllegalArgumentException] should be thrownBy {
      cli.arg named bar alias longSign taking 1 value : ArgumentDefinition[_]
    }
    commandManager.setSigns(shortSignAlt, longSignAlt)
    an [IllegalArgumentException] should be thrownBy {
      cli.arg named f alias s"$shortSignAlt$baz" taking 1 value : ArgumentDefinition[_]
    }
    an [IllegalArgumentException] should be thrownBy {
      cli.arg named b alias s"$longSignAlt$bar" taking 1 value : ArgumentDefinition[_]
    }
    an [IllegalArgumentException] should be thrownBy {
      cli.arg named bar alias s"$shortSignAlt$f" taking 2 values : ArgumentDefinition[_]
    }
    an [IllegalArgumentException] should be thrownBy {
      cli.arg named baz alias s"$longSignAlt$b" taking 1 value : ArgumentDefinition[_]
    }
    an [IllegalArgumentException] should be thrownBy {
      cli.arg named foo alias shortSignAlt taking 4 values : ArgumentDefinition[_]
    }
    an [IllegalArgumentException] should be thrownBy {
      cli.arg named bar alias longSignAlt taking 1 value : ArgumentDefinition[_]
    }
  }

  it should "form nicely if the alias has short or long sign in the middle or end" in {
    cli.arg named f alias s"$bar$shortSign$baz" taking 1 value : ArgumentDefinition[_];
    cli.arg named g alias s"$bar$longSign$baz" taking 2 values : ArgumentDefinition[_];
    cli.arg named g alias s"$bar$shortSign" taking 1 value : ArgumentDefinition[_];
    cli.arg named b alias s"$bar$longSign" taking 1 value : ArgumentDefinition[_];
    cli.arg named foo alias shortSignAlt taking 4 values : ArgumentDefinition[_];
    cli.arg named bar alias longSignAlt taking 1 value : ArgumentDefinition[_];
    commandManager.setSigns(shortSignAlt, longSignAlt)
    cli.arg named g alias s"$bar$shortSignAlt$baz" taking 1 value : ArgumentDefinition[_];
    cli.arg named f alias s"$bar$longSignAlt$baz" taking 3 values : ArgumentDefinition[_];
    cli.arg named f alias s"$foo$shortSignAlt" taking 1 value : ArgumentDefinition[_];
    cli.arg named b alias s"$baz$longSignAlt" taking 2 values : ArgumentDefinition[_];
    cli.arg named foo alias shortSign taking 1 value : ArgumentDefinition[_]
  }

  it should "throw an IllegalArgumentException if the number of arguments passed is lower than zero" in {
    an [IllegalArgumentException] should be thrownBy {
      cli.arg named foo taking -1 values : ArgumentDefinition[_]
    }
    an [IllegalArgumentException] should be thrownBy {
      cli.arg named bar taking -5 values : ArgumentDefinition[_]
    }
    an [IllegalArgumentException] should be thrownBy {
      cli.arg named foo taking -1 value : ArgumentDefinition[_]
    }
    an [IllegalArgumentException] should be thrownBy {
      cli.arg named bar taking -5 value : ArgumentDefinition[_]
    }
  }

  it should "throw an IllegalArgumentException if the number of arguments passed is zero" in {
    an [IllegalArgumentException] should be thrownBy {
      cli.arg named baz taking 0 values : ArgumentDefinition[_]
    }
    an [IllegalArgumentException] should be thrownBy {
      cli.arg named baz taking 0 value : ArgumentDefinition[_]
    }
  }

  it should "throw an IllegalArgumentException if value is called when taking is more than one" in {
    an [IllegalArgumentException] should be thrownBy {
      cli.arg named baz taking 2 value : ArgumentDefinition[_]
    }
    an [IllegalArgumentException] should be thrownBy {
      cli.arg named baz taking 8 value : ArgumentDefinition[_]
    }
  }

  it should "throw an IllegalArgumentException if values is called when taking is one" in {
    an [IllegalArgumentException] should be thrownBy {
      cli.arg named baz taking 1 values : ArgumentDefinition[_]
    }
  }

  it should "throw an IllegalArgumentException if the number of default values is greater than taking" in {
    an [IllegalArgumentException] should be thrownBy {
      cli.arg named foo taking 3 as (1,2,3,4) : ArgumentDefinition[_]
    }
    an [IllegalArgumentException] should be thrownBy {
      cli.arg named bar taking 1 as ("one", "two") : ArgumentDefinition[_]
    }
  }

  it should "form nicely if a the number of arguments is less or equal than the number taken" in {
    cli.arg named foo taking 1 as "one" : ArgumentDefinition[_]
    cli.arg named f  taking 3 as ("one", "two", "three") : ArgumentDefinition[_]
    cli.arg named g taking 5 as (1,2,3,4) : ArgumentDefinition[_]
    cli.arg named bar taking 3 as (None, Some("Wiiii"))  : ArgumentDefinition[_]
  }

  it should "return the same value as inputed" in {
    (cli.arg named baz taking 1 as "one").argumentValues should be (Array("one"))
    (cli.arg named bar  taking 3 as ("one", "two", "three")).argumentValues should be (Array("one", "two", "three"))
    (cli.arg named f taking 5 as (1,2,3,4)).argumentValues should be (Array(1,2,3,4))
    (cli.arg named foo taking 3 as (None, Some("Wiiii"))).argumentValues should be (Array(None, Some("Wiiii")))
  }

  /* ********* Commands ************ */

  "A CommandDefinition" should "form nicely if a valid name is given" in {
    // cli.command named foo receives () does {_=>}
    cli.root does {_=>}
  }

  it should "throw an IllegalArgumentException if no name is passed" in {
    an [IllegalArgumentException] should be thrownBy {
      cli.command named emptyString does {_=>}
    }
  }

  it should "throw an IllegalArgumentException if a name with spaces is passed" in {
    an [IllegalArgumentException] should be thrownBy {
      cli.command named s"$foo $bar" does {_=>}
    }
    an [IllegalArgumentException] should be thrownBy {
      cli.command named s"  $baz" does {_=>}
    }
    an [IllegalArgumentException] should be thrownBy {
      cli.command named s"$foo   " does {_=>}
    }
  }

  it should "throw an IllegalArgumentException if the name starts with the short or long sign of the CommandManager" in {
    an [IllegalArgumentException] should be thrownBy {
      cli.command named s"$shortSign$foo" does {_=>}
    }
    an [IllegalArgumentException] should be thrownBy {
      cli.command named s"$longSign$foo" does {_=>}
    }
    an [IllegalArgumentException] should be thrownBy {
      cli.command named s"$shortSign$b" does {_=>}
    }
    an [IllegalArgumentException] should be thrownBy {
      cli.command named s"$longSign$f" does {_=>}
    }
    an [IllegalArgumentException] should be thrownBy {
      cli.command named shortSign does {_=>}
    }
    an [IllegalArgumentException] should be thrownBy {
      cli.command named longSign does {_=>}
    }
    commandManager.setSigns(shortSignAlt, longSignAlt)
    an [IllegalArgumentException] should be thrownBy {
      cli.command named s"$shortSignAlt$baz" does {_=>}
    }
    an [IllegalArgumentException] should be thrownBy {
      cli.command named s"$longSignAlt$foo" does {_=>}
    }
    an [IllegalArgumentException] should be thrownBy {
      cli.command named s"$shortSignAlt$g" does {_=>}
    }
    an [IllegalArgumentException] should be thrownBy {
      cli.command named s"$longSignAlt$f" does {_=>}
    }
    an [IllegalArgumentException] should be thrownBy {
      cli.command named shortSignAlt does {_=>}
    }
    an [IllegalArgumentException] should be thrownBy {
      cli.command named longSignAlt does {_=>}
    }
  }

  it should "form nicely if the name has short or long sign in the middle or end" in {
    cli.command named s"$bar$shortSign$baz" does {_=>}
    cli.command named s"$foo$longSign$baz" does {_=>}
    cli.command named s"$baz$shortSign" does {_=>}
    cli.command named s"$bar$longSign" does {_=>}
    cli.command named s"$g$shortSign" does {_=>}
    cli.command named s"$b$longSign" does {_=>}
    cli.command named shortSignAlt  does {_=>}
    commandManager.setSigns(shortSignAlt, longSignAlt)
    cli.command named s"$bar$shortSignAlt$baz" does {_=>}
    cli.command named s"$foo$longSignAlt$baz"  does {_=>}
    cli.command named s"$baz$shortSignAlt" does {_=>}
    cli.command named s"$bar$longSignAlt" does {_=>}
    cli.command named s"$g$shortSignAlt" does {_=>}
    cli.command named s"$b$longSignAlt" does {_=>}
    cli.command named shortSign does {_=>}
    cli.command named longSign does {_=>}
  }

  it should "throw an IllegalArgumentException if two values have the same name" in {
    an [IllegalArgumentException] should be thrownBy {
      cli.command named foo receives (
          cli.value named bar mandatory,
          cli.value named bar mandatory
          ) does {_=>}
    }
    an [IllegalArgumentException] should be thrownBy {
      root receives(
          cli.value named bar mandatory,
          cli.value named bar as "barbar"
          ) does { _ =>}
    }
    an [IllegalArgumentException] should be thrownBy {
      cli.command named foo receives (
          cli.value named baz as "one",
          cli.value named baz as "two"
          ) does {_=>}
    }
  }

  it should "form nicely if mandatory values are in correct order" in {
    cli.command named foo receives (
        cli.value named foo mandatory,
        cli.value named bar as "bar",
        cli.value named baz as "baz"
        ) does {_=>}
    root receives (
        cli.value named foo mandatory,
        cli.value named bar mandatory,
        cli.value named baz as "baz"
        ) does {_=>}
  }

  it should "throw an IllegalArgumentException if mandatory values are not in order" in {
    an [IllegalArgumentException] should be thrownBy {
      cli.command named foo receives (
          cli.value named foo as "foo",
          cli.value named bar mandatory,
          cli.value named baz mandatory
          ) does {_=>}
    }
    an [IllegalArgumentException] should be thrownBy {
      cli.command named foo receives (
          cli.value named foo as "foo",
          cli.value named bar mandatory,
          cli.value named baz as "baz"
          ) does {_=>}
    }
    an [IllegalArgumentException] should be thrownBy {
      root receives (
          cli.value named foo mandatory,
          cli.value named bar as "bar",
          cli.value named baz mandatory
          ) does {_=>}
    }
  }

  it should "form nicely if minimum and maximum number of values are positive and maximum is greater than lower" in {
    cli.command named foo minimumOf 1 does {_=>}
    root maximumOf 7 does {_=>}
    cli.command named bar maximumOf 1 does {_=>}
    cli.command named baz minimumOf 4 maximumOf 10 does {_=>}
    cli.command named f minimumOf 1 maximumOf 7 does {_=>}
  }

  it should "throw an IllegalArgumentException if minimum number of values is lower than zero" in {
    an [IllegalArgumentException] should be thrownBy {
      cli.command named foo minimumOf -1 does {_=>}
    }
    an [IllegalArgumentException] should be thrownBy {
      root minimumOf -5 does {_=>}
    }
  }

  it should "throw an IllegalArgumentException if minimum number of values is zero" in {
    an [IllegalArgumentException] should be thrownBy {
      cli.command named foo minimumOf 0 does {_=>}
    }
  }

  it should "throw an IllegalArgumentException if maximum number of values is lower than zero" in {
    an [IllegalArgumentException] should be thrownBy {
      root maximumOf -1 does {_=>}
    }
    an [IllegalArgumentException] should be thrownBy {
      cli.command named foo maximumOf -5 does {_=>}
    }
  }

  it should "throw an IllegalArgumentException if maximum number of values is zero" in {
    an [IllegalArgumentException] should be thrownBy {
      cli.command named foo maximumOf 0 does {_=>}
    }
  }

  it should "throw an IllegalArgumentException if minimum number of values is larger or equal than maximum" in {
    an [IllegalArgumentException] should be thrownBy {
      cli.command named foo minimumOf 2 maximumOf 1 does {_=>}
    }
    an [IllegalArgumentException] should be thrownBy {
      root minimumOf 7 maximumOf 3 does {_=>}
    }
    an [IllegalArgumentException] should be thrownBy {
      cli.command named foo minimumOf 3 maximumOf 3 does {_=>}
    }
  }

  it should "form nicely if a set of valid arguments and flags are passed" in {
    cli.command named foo accepts (
        flag named foo,
        flag named bar,
        arg named baz alias f taking 1 value
        ) does {_=>}
    cli.root accepts (
        flag named g,
        flag named bar alias b,
        arg named foo alias f taking 1 value
        ) does {_=>}
    cli.command named bar accepts (
        flag named foo,
        arg named bar alias b taking 3 as (1, 2, 3),
        arg named baz alias g taking 1 value
        ) does {_=>}
    cli.command named baz accepts (
        flag named f alias foo,
        arg named bar alias b taking 1 as "one",
        arg named baz alias g taking 1 value
        ) does {_=>}
    }

    it should "throw an IllegalArgumentException if a flag or argument have same name" in {
      an [IllegalArgumentException] should be thrownBy {
        cli.command named foo accepts (
            flag named foo,
            flag named foo,
            arg named baz alias f taking 1 value
            ) does {_=>}
      }
      an [IllegalArgumentException] should be thrownBy {
        cli.root accepts (
            flag named g,
            flag named bar alias b,
            arg named bar alias f taking 1 value
            ) does {_=>}
      }
      an [IllegalArgumentException] should be thrownBy {
        cli.root accepts(
            flag named bar alias b,
            arg named f taking 1 as "one",
            arg named f alias baz taking 1 value
            ) does { _ =>}
      }
  }

  it should "throw an IllegalArgumentException if a command with the same name is defined twice" in {
    an[IllegalArgumentException] should be thrownBy {
      cli.command named foo does { _ =>}
      cli.command named foo does { _ =>}
    }
  }

  it should "throw an IllegalArgumentException if the root command is defined twice" in {
    an[IllegalArgumentException] should be thrownBy {
      root does { _ =>}
      root does { _ =>}
    }
  }
}