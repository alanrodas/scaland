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

import language.postfixOps
import org.scalatest._

import com.alanrodas.scaland._
import com.alanrodas.scaland.cli.implicits._
import com.alanrodas.scaland.cli.runtime.{MultipleValuesCommand, FiniteValuesCommand, NoValuesCommand}


class CallSpec extends FlatSpec with Matchers with BeforeAndAfter {

  val shortSign = "-"
  val longSign = "--"
  val shortSignAlt = "/"
  val longSignAlt = "/"

  implicit var commandManager = new CommandManager()

  var root : NoValuesCommand = null
  var foo : NoValuesCommand = null
  var bar : FiniteValuesCommand = null
  var baz : MultipleValuesCommand = null

  before {
    commandManager.setSigns(shortSign, longSign)
    // <ROOT>
    cli.root does { cmd => root = cmd}
    // FOO
    cli.command named "foo" accepts(
        flag named "var" alias "v",
        flag named "zaz" alias "Z"
        ) does { cmd => foo = cmd}
    // BAR
    cli.command named "bar" accepts(
        flag named "t",
        arg named "pop" alias "p" taking 1 as "POP",
        arg named "push" taking 2 as(1, 2)
        ) receives(
        cli.value named "first" as "1",
        cli.value named "second" as 2
        ) does { cmd => bar = cmd}
    // BAZ
    cli.command named "baz" accepts(
        flag named "taz" alias "t",
        arg named "pop" alias "p" taking 1 as "POP",
        arg named "push" taking 2 as(1, 2)
        ) minimumOf 1 does { cmd => baz = cmd}
  }
  after {
    commandManager = new CommandManager()
    root = null; foo = null; bar = null; baz = null
  }

  /* ********* Basic calls ************ */

  "A CommandManager" should "only execute the root command when nothing is given" in {
    commandManager.execute("".toArgs)
    root should not be null
    foo should be (null)
    bar should be (null)
    baz should be (null)
  }

  it should "only execute the foo command when 'foo' is given" in {
    commandManager.execute("foo".toArgs)
    root should be (null)
    foo should not be null
    bar should be (null)
    baz should be (null)
  }

  it should "only execute the bar command when 'bar' is given" in {
    commandManager.execute("bar".toArgs)
    root should be (null)
    foo should be (null)
    bar should not be null
    baz should be (null)
  }

  it should "only execute the bar command when 'baz' is given" in {
    commandManager.execute("baz 1".toArgs)
    root should be (null)
    foo should be (null)
    bar should be (null)
    baz should not be null
  }

  /* ********* No values command ************ */

  "A CommandManager on no arguments command" should "return all default values for 'root' if nothing is passed" in {
    commandManager.execute("".toArgs)
    root.name should be (empty)
    root.numberOfFlags should equal (0)
    root.flags should be (empty)
    root.numberOfArguments should equal (0)
    root.arguments should be (empty)
  }

  it should "return all default values for 'foo' if nothing is passed" in {
    commandManager.execute("foo".toArgs)
    foo.name should be ("foo")
    foo.numberOfFlags should equal (2)
    foo.flags should have size 2
    foo.flag("var") should have ('name ("var"), 'alias (Some("v")), 'defined (false))
    foo.flag("v") should have ('name ("var"), 'alias (Some("v")), 'defined (false))
    foo.flag("var") should equal (foo.flag("v"))
    foo.flag("zaz") should have ('name ("zaz"), 'alias (Some("Z")), 'defined (false))
    foo.flag("Z") should have ('name ("zaz"), 'alias (Some("Z")), 'defined (false))
    foo.flag("zaz") should equal (foo.flag("Z"))
    foo.numberOfArguments should equal (0)
    foo.arguments should be (empty)
  }


  it should "throw a IllegalCommandLineArgumentsException if a command is called with not defined flags" in {
    an [IllegalCommandLineArgumentsException] should be thrownBy {
      commandManager.execute("-v".toArgs)
    }
    an [IllegalCommandLineArgumentsException] should be thrownBy {
      commandManager.execute("--flag".toArgs)
    }
    an [IllegalCommandLineArgumentsException] should be thrownBy {
      commandManager.execute("foo -J".toArgs)
    }
    an [IllegalCommandLineArgumentsException] should be thrownBy {
      commandManager.execute("bar --someFlag".toArgs)
    }
    an [IllegalCommandLineArgumentsException] should be thrownBy {
      commandManager.execute("bar -v -f".toArgs)
    }
    an [IllegalCommandLineArgumentsException] should be thrownBy {
      commandManager.execute("baz --Z".toArgs)
    }
  }

  it should "throw a IllegalCommandLineArgumentsException if a command is called with not defined arguments" in {
    an [IllegalCommandLineArgumentsException] should be thrownBy {
      commandManager.execute("-a some".toArgs)
    }
    an [IllegalCommandLineArgumentsException] should be thrownBy {
      commandManager.execute("--arg Something".toArgs)
    }
    an [IllegalCommandLineArgumentsException] should be thrownBy {
      commandManager.execute("foo -J 1".toArgs)
    }
    an [IllegalCommandLineArgumentsException] should be thrownBy {
      commandManager.execute("bar --someArg foooo".toArgs)
    }
    an [IllegalCommandLineArgumentsException] should be thrownBy {
      commandManager.execute("bar -v 1 2 3 -foo FOOO".toArgs)
    }
    an [IllegalCommandLineArgumentsException] should be thrownBy {
      commandManager.execute("baz --Z pop".toArgs)
    }
  }

  it should "throw a IllegalCommandLineArgumentsException if a command is called with more values than defined" in {
    an [IllegalCommandLineArgumentsException] should be thrownBy {
      commandManager.execute("one".toArgs)
    }
    an [IllegalCommandLineArgumentsException] should be thrownBy {
      commandManager.execute("one two".toArgs)
    }
    an [IllegalCommandLineArgumentsException] should be thrownBy {
      commandManager.execute("foo one".toArgs)
    }
    an [IllegalCommandLineArgumentsException] should be thrownBy {
      commandManager.execute("foo one two".toArgs)
    }
    an [IllegalCommandLineArgumentsException] should be thrownBy {
      commandManager.execute("bar one two three".toArgs)
    }
  }

  it should "throw a IllegalCommandLineArgumentsException if a command is called with less values than required" in {
    an [IllegalCommandLineArgumentsException] should be thrownBy {
      commandManager.execute("baz".toArgs)
    }
    cli.command named "tap" accepts(
        ) receives(
        cli.value named "first" mandatory,
        cli.value named "second" mandatory,
        cli.value named "three" as 3
        ) does { cmd => bar = cmd}
    an [IllegalCommandLineArgumentsException] should be thrownBy {
      commandManager.execute("tap".toArgs)
    }
    an [IllegalCommandLineArgumentsException] should be thrownBy {
      commandManager.execute("tap one".toArgs)
    }
  }

  it should "return all passed arguments for 'foo' if they are passed" in {
    commandManager.execute("foo -v --zaz".toArgs)
    foo.name should be ("foo")
    foo.numberOfFlags should equal (2)
    foo.flags should have size 2
    foo.flag("var") should have ('name ("var"), 'alias (Some("v")), 'defined (true))
    foo.flag("v") should have ('name ("var"), 'alias (Some("v")), 'defined (true))
    foo.flag("var") should equal (foo.flag("v"))
    foo.flag("zaz") should have ('name ("zaz"), 'alias (Some("Z")), 'defined (true))
    foo.flag("Z") should have ('name ("zaz"), 'alias (Some("Z")), 'defined (true))
    foo.flag("zaz") should equal (foo.flag("Z"))
  }

  /* ********* Finite Values Command ************ */

  "A CommandManager in finites argument commands" should "return all passed arguments for 'bar' if they are passed" in {
    commandManager.execute("bar --pop thePop".toArgs)
    bar.name should be ("bar")
    bar.numberOfArguments should equal (2)
    bar.arguments should have size 2
    bar.argument("pop") should have ('name ("pop"), 'alias (Some("p")), 'defined (true), 'value ("thePop"))
    bar.argument("p") should have ('name ("pop"), 'alias (Some("p")), 'defined (true), 'value ("thePop"))
    bar.argument("pop") should equal (bar.argument("p"))
  }

  it should "return all default values for 'bar' if nothing is passed" in {
    commandManager.execute("bar".toArgs)
    bar.name should be ("bar")
    bar.numberOfFlags should equal (1)
    bar.flags should have size 1
    bar.flag("t") should have ('name ("t"), 'alias (None), 'defined (false))
    bar.numberOfArguments should equal (2)
    bar.arguments should have size 2
    bar.argument("pop") should have ('name ("pop"), 'alias (Some("p")), 'defined (false), 'value ("POP"))
    bar.argument("p") should have ('name ("pop"), 'alias (Some("p")), 'defined (false), 'value ("POP"))
    bar.argument("pop") should equal (bar.argument("p"))
    bar.argument("push") should have ('name ("push"), 'alias (None), 'defined (false), 'values (Seq(1,2)))
    bar.numberOfValues should equal (2)
    bar.values should have size 2
    bar.value("first") should have ('name ("first"), 'defined (false), 'value ("1"))
    bar.value("second") should have ('name ("second"), 'defined (false), 'value (2))
  }

  it should "accept values before flags or arguments" in {
    commandManager.execute("bar FIRST --pop thePop".toArgs)
    bar.name should be ("bar")
    bar.numberOfArguments should equal (2)
    bar.arguments should have size 2
    bar.argument("pop") should have ('name ("pop"), 'alias (Some("p")), 'defined (true), 'value ("thePop"))
    bar.argument("p") should have ('name ("pop"), 'alias (Some("p")), 'defined (true), 'value ("thePop"))
    bar.argument("pop") should equal (bar.argument("p"))
    bar.numberOfValues should equal (2)
    bar.values should have size 2
    bar.value("first") should have ('name ("first"), 'defined (true), 'value ("FIRST"))
    bar.value("second") should have ('name ("second"), 'defined (false), 'value (2))
  }

  it should "accept values after flags or arguments" in {
    commandManager.execute("bar --pop thePop FIRST".toArgs)
    bar.name should be ("bar")
    bar.numberOfArguments should equal (2)
    bar.arguments should have size 2
    bar.argument("pop") should have ('name ("pop"), 'alias (Some("p")), 'defined (true), 'value ("thePop"))
    bar.argument("p") should have ('name ("pop"), 'alias (Some("p")), 'defined (true), 'value ("thePop"))
    bar.argument("pop") should equal (bar.argument("p"))
    bar.numberOfValues should equal (2)
    bar.values should have size 2
    bar.value("first") should have ('name ("first"), 'defined (true), 'value ("FIRST"))
    bar.value("second") should have ('name ("second"), 'defined (false), 'value (2))
  }

  it should "accept values before and after flags or arguments" in {
    commandManager.execute("bar FIRST --pop thePop SECOND".toArgs)
    bar.name should be ("bar")
    bar.numberOfArguments should equal (2)
    bar.arguments should have size 2
    bar.argument("pop") should have ('name ("pop"), 'alias (Some("p")), 'defined (true), 'value ("thePop"))
    bar.argument("p") should have ('name ("pop"), 'alias (Some("p")), 'defined (true), 'value ("thePop"))
    bar.argument("pop") should equal (bar.argument("p"))
    bar.numberOfValues should equal (2)
    bar.values should have size 2
    bar.value("first") should have ('name ("first"), 'defined (true), 'value ("FIRST"))
    bar.value("second") should have ('name ("second"), 'defined (true), 'value ("SECOND"))
  }

  it should "accept values in the middle of flags or arguments" in {
    commandManager.execute("bar -t FIRST --pop thePop SECOND".toArgs)
    bar.name should be ("bar")
    bar.numberOfFlags should equal (1)
    bar.flags should have size 1
    bar.flag("t") should have ('name ("t"), 'alias (None), 'defined (true))
    bar.numberOfArguments should equal (2)
    bar.arguments should have size 2
    bar.argument("pop") should have ('name ("pop"), 'alias (Some("p")), 'defined (true), 'value ("thePop"))
    bar.argument("p") should have ('name ("pop"), 'alias (Some("p")), 'defined (true), 'value ("thePop"))
    bar.argument("pop") should equal (bar.argument("p"))
    bar.numberOfValues should equal (2)
    bar.values should have size 2
    bar.value("first") should have ('name ("first"), 'defined (true), 'value ("FIRST"))
    bar.value("second") should have ('name ("second"), 'defined (true), 'value ("SECOND"))
  }

  it should "retrieve values in in any format" in {
    commandManager.execute("bar 1 2.3".toArgs)
    bar.value("first").value should be ("1")
    bar.value("second").value should be ("2.3")

    bar.value("first").valueAs[String] should be ("1")
    bar.value("first").valueAs[Int] should be (1)
    bar.value("first").valueAs[Double] should be (1.0)
    bar.value("first").valueAs[Float] should be (1.0f)
    bar.value("first").valueAs[Boolean] should be (true)

    bar.value("first").valueAs[Option[String]] should be (Some("1"))
    bar.value("first").valueAs[Option[Int]] should be (Some(1))
    bar.value("first").valueAs[Option[Double]] should be (Some(1.0))
    bar.value("first").valueAs[Option[Float]] should be (Some(1.0f))
    bar.value("first").valueAs[Option[Boolean]] should be (Some(true))

    bar.value("second").valueAs[String] should be ("2.3")
    bar.value("second").valueAs[Double] should be (2.3)
    bar.value("second").valueAs[Float] should be (2.3f)
    bar.value("second").valueAs[Option[String]] should be (Some("2.3"))
    bar.value("second").valueAs[Option[Double]] should be (Some(2.3))
    bar.value("second").valueAs[Option[Float]] should be (Some(2.3f))
    bar.value("second").valueAs[Option[Boolean]] should be (None)
  }

  it should "throw a ParseException when trying to fetch of an invalid type" in {
    commandManager.execute("bar true 2.3".toArgs)

    an [ParseException] should be thrownBy {
      bar.value("first").valueAs[Int]
    }
    an [ParseException] should be thrownBy {
      bar.value("first").valueAs[Float]
    }
    an [ParseException] should be thrownBy {
      bar.value("first").valueAs[Double]
    }
    an [ParseException] should be thrownBy {
      bar.value("second").valueAs[Int]
    }
    an [ParseException] should be thrownBy {
      bar.value("second").valueAs[Boolean]
    }
  }

  it should "never fail parsing when querying for an option" in {
    commandManager.execute("bar whoops".toArgs)

    bar.value("first").valueAs[Option[Int]] should be (None)
    bar.value("first").valueAs[Option[Double]] should be (None)
    bar.value("first").valueAs[Option[Float]] should be (None)
    bar.value("first").valueAs[Option[Boolean]] should be (None)
  }

  it should "return the defaulted named value in its original type" in {
    commandManager.execute("bar whoops".toArgs)
    bar.value("second").value should be (2)
    bar.value("second").valueAs[String] should be ("2")
    bar.value("second").valueAs[Int] should be (2)
    bar.value("second").valueAs[Double] should be (2.0)
    bar.value("second").valueAs[Float] should be (2.0f)
    an [ParseException] should be thrownBy {
      bar.value("second").valueAs[Boolean]
    }
    bar.value("second").valueAs[Option[String]] should be (Some("2"))
    bar.value("second").valueAs[Option[Int]] should be (Some(2))
    bar.value("second").valueAs[Option[Double]] should be (Some(2.0))
    bar.value("second").valueAs[Option[Float]] should be (Some(2.0f))
    bar.value("second").valueAs[Option[Boolean]] should be (None)
  }

  /* ********* Multiple Values Command ************ */

  it should "return all default values for 'baz' if only the mandatory arg is passed" in {
    commandManager.execute("baz some".toArgs)
    baz.name should be ("baz")
    baz.numberOfFlags should equal (1)
    baz.flags should have size 1
    baz.flag("taz") should have ('name ("taz"), 'alias (Some("t")), 'defined (false))
    baz.flag("t") should have ('name ("taz"), 'alias (Some("t")), 'defined (false))
    baz.flag("taz") should equal (baz.flag("t"))
    baz.numberOfArguments should equal (2)
    baz.arguments should have size 2
    baz.argument("pop") should have ('name ("pop"), 'alias (Some("p")), 'defined (false), 'value ("POP"))
    baz.argument("p") should have ('name ("pop"), 'alias (Some("p")), 'defined (false), 'value ("POP"))
    baz.argument("pop") should equal (baz.argument("p"))
    baz.argument("push") should have ('name ("push"), 'alias (None), 'defined (false), 'values (Seq(1,2)))
    baz.numberOfValues should equal (1)
    baz.values should have size 1
    baz.value(0) should be ("some")
  }

  "A CommandManager on multiple arguments command" should "return all passed arguments for 'baz' if they are passed" in {
    commandManager.execute("baz some -t --push 10 23".toArgs)
    baz.name should be ("baz")
    baz.numberOfFlags should equal (1)
    baz.flags should have size 1
    baz.flag("taz") should have ('name ("taz"), 'alias (Some("t")), 'defined (true))
    baz.flag("t") should have ('name ("taz"), 'alias (Some("t")), 'defined (true))
    baz.flag("taz") should equal (baz.flag("t"))
    baz.numberOfArguments should equal (2)
    baz.arguments should have size 2
    baz.argument("push") should have ('name ("push"), 'alias (None), 'defined (true), 'values (Seq("10","23")))
    baz.numberOfValues should equal (1)
    baz.values should have size 1
    baz.value(0) should be ("some")
  }

  it should "accept multiple values after flags or arguments" in {
    commandManager.execute("baz -t --push 10 23 some".toArgs)
    baz.name should be ("baz")
    baz.numberOfFlags should equal (1)
    baz.flags should have size 1
    baz.flag("taz") should have ('name ("taz"), 'alias (Some("t")), 'defined (true))
    baz.flag("t") should have ('name ("taz"), 'alias (Some("t")), 'defined (true))
    baz.flag("taz") should equal (baz.flag("t"))
    baz.numberOfArguments should equal (2)
    baz.arguments should have size 2
    baz.argument("push") should have ('name ("push"), 'alias (None), 'defined (true), 'values (Seq("10","23")))
    baz.numberOfValues should equal (1)
    baz.values should have size 1
    baz.value(0) should be ("some")
  }

  it should "accept multiple values after flags or arguments when multiple passed" in {
    commandManager.execute("baz -t --push 10 23 some other another".toArgs)
    baz.name should be ("baz")
    baz.numberOfFlags should equal (1)
    baz.flags should have size 1
    baz.flag("taz") should have ('name ("taz"), 'alias (Some("t")), 'defined (true))
    baz.flag("t") should have ('name ("taz"), 'alias (Some("t")), 'defined (true))
    baz.flag("taz") should equal (baz.flag("t"))
    baz.numberOfArguments should equal (2)
    baz.arguments should have size 2
    baz.argument("push") should have ('name ("push"), 'alias (None), 'defined (true), 'values (Seq("10","23")))
    baz.numberOfValues should equal (3)
    baz.values should have size 3
    baz.value(0) should be ("some")
    baz.value(1) should be ("other")
    baz.value(2) should be ("another")
  }

  it should "accept multiple values before flags or arguments when multiple passed" in {
    commandManager.execute("baz some other another -t --push 10".toArgs)
    baz.name should be ("baz")
    baz.numberOfFlags should equal (1)
    baz.flags should have size 1
    baz.flag("taz") should have ('name ("taz"), 'alias (Some("t")), 'defined (true))
    baz.flag("t") should have ('name ("taz"), 'alias (Some("t")), 'defined (true))
    baz.flag("taz") should equal (baz.flag("t"))
    baz.numberOfArguments should equal (2)
    baz.arguments should have size 2
    baz.argument("push") should have ('name ("push"), 'alias (None), 'defined (true), 'values (Seq("10", 2)))
    baz.numberOfValues should equal (3)
    baz.values should have size 3
    baz.value(0) should be ("some")
    baz.value(1) should be ("other")
    baz.value(2) should be ("another")
  }

  it should "accept multiple values before and after flags or arguments when multiple passed" in {
    commandManager.execute("baz some other -t --push 10 10 another".toArgs)
    baz.name should be ("baz")
    baz.numberOfFlags should equal (1)
    baz.flags should have size 1
    baz.flag("taz") should have ('name ("taz"), 'alias (Some("t")), 'defined (true))
    baz.flag("t") should have ('name ("taz"), 'alias (Some("t")), 'defined (true))
    baz.flag("taz") should equal (baz.flag("t"))
    baz.numberOfArguments should equal (2)
    baz.arguments should have size 2
    baz.argument("push") should have ('name ("push"), 'alias (None), 'defined (true), 'values (Seq("10", "10")))
    baz.numberOfValues should equal (3)
    baz.values should have size 3
    baz.value(0) should be ("some")
    baz.value(1) should be ("other")
    baz.value(2) should be ("another")
  }

  it should "accept multiple values in the middle of flags or arguments when multiple passed" in {
    commandManager.execute("baz -t some other --push 10 10 another".toArgs)
    baz.name should be ("baz")
    baz.numberOfFlags should equal (1)
    baz.flags should have size 1
    baz.flag("taz") should have ('name ("taz"), 'alias (Some("t")), 'defined (true))
    baz.flag("t") should have ('name ("taz"), 'alias (Some("t")), 'defined (true))
    baz.flag("taz") should equal (baz.flag("t"))
    baz.numberOfArguments should equal (2)
    baz.arguments should have size 2
    baz.argument("push") should have ('name ("push"), 'alias (None), 'defined (true), 'values (Seq("10", "10")))
    baz.numberOfValues should equal (3)
    baz.values should have size 3
    baz.value(0) should be ("some")
    baz.value(1) should be ("other")
    baz.value(2) should be ("another")
  }

  it should "throw a IllegalCommandLineArgumentsException if a multivalue command is called with more values than defined" in {
    cli.command named "pop" accepts(
        ) maximumOf 2 does { cmd => baz = cmd}
    an [IllegalCommandLineArgumentsException] should be thrownBy {
      commandManager.execute("pop one two three".toArgs)
    }
  }

  it should "return the defaulted values in orden on the specified type" in {
    commandManager.execute("baz little 3 2.3 0".toArgs)
    baz.value(0) should be ("little")
    baz.valueAs[String](0) should be ("little")

    baz.value(1) should be ("3")
    baz.valueAs[String](1) should be ("3")
    baz.valueAs[Int](1) should be (3)
    baz.valueAs[Double](1) should be (3.0)
    baz.valueAs[Float](1) should be (3.0f)

    baz.value(2) should be ("2.3")
    baz.valueAs[String](2) should be ("2.3")
    baz.valueAs[Double](2) should be (2.3)
    baz.valueAs[Float](2) should be (2.3f)

    baz.value(3) should be ("0")
    baz.valueAs[String](3) should be ("0")
    baz.valueAs[Int](3) should be (0)
    baz.valueAs[Double](3) should be (0.0)
    baz.valueAs[Float](3) should be (0.0f)
    baz.valueAs[Boolean](3) should be (false)
  }

  it should "throw a ParseException when trying to fetch of an invalid type" in {
    commandManager.execute("baz little".toArgs)

    an [ParseException] should be thrownBy {
      baz.valueAs[Int](0)
    }
    an [ParseException] should be thrownBy {
      baz.valueAs[Float](0)
    }
    an [ParseException] should be thrownBy {
      baz.valueAs[Double](0)
    }
    an [ParseException] should be thrownBy {
      baz.valueAs[Boolean](0)
    }
  }

  /* ********* Argument processing ************ */

  "A Command with arguments" should "parse argument defaults correctly" in {
    var taz : NoValuesCommand = null
    command named "taz" accepts(
        arg named "arg" taking 4 as (1, "two")
        ) does { cmd => taz = cmd }
    commandManager.execute("taz --arg first second third fourth".toArgs)

    taz.numberOfArguments should be (1)

    taz.argument("arg") should have ('name ("arg"),
      'alias (None), 'values (Seq("first", "second", "third", "fourth")))

    taz.argument("arg").values should be (Seq("first", "second", "third", "fourth"))
    taz.argument("arg").value should be ("first")
    taz.argument("arg").value(0) should be ("first")
    taz.argument("arg").value(1) should be ("second")
    taz.argument("arg").value(2) should be ("third")
    taz.argument("arg").value(3) should be ("fourth")
  }

  it should "throw an IllegalCommandLineArgumentsException if no mandatory values are passed to the argument" in {
    var taz : NoValuesCommand = null
    command named "taz" accepts(
        arg named "arg" taking 4 as (1, "two")
        ) does { cmd => taz = cmd }

    an [IllegalCommandLineArgumentsException] should be thrownBy {
      commandManager.execute("taz --arg ".toArgs)
    }
    an [IllegalCommandLineArgumentsException] should be thrownBy {
      commandManager.execute("taz --arg first".toArgs)
    }
  }

  it should "Use default values when they are not given" in {
    var taz : NoValuesCommand = null
    command named "taz" accepts(
        arg named "arg" taking 4 as (1, "two")
        ) does { cmd => taz = cmd }
    commandManager.execute("taz --arg first second".toArgs)

    taz.numberOfArguments should be (1)

    taz.argument("arg") should have ('name ("arg"),
      'alias (None), 'values (Seq("first", "second", 1, "two")))

    taz.argument("arg").values should be (Seq("first", "second", 1, "two"))
    taz.argument("arg").value should be ("first")
    taz.argument("arg").value(0) should be ("first")
    taz.argument("arg").value(1) should be ("second")
    taz.argument("arg").value(2) should be (1)
    taz.argument("arg").value(3) should be ("two")
  }

  it should "throw an IllegalCommandLineArgumentsException if a mandatory argument is not given" in {
    var taz : NoValuesCommand = null
    command named "taz" accepts(
        arg named "arg" taking 4 as (1, "two") mandatory
        ) does { cmd => taz = cmd }

    an [IllegalCommandLineArgumentsException] should be thrownBy {
      commandManager.execute("taz ".toArgs)
    }
  }

  /* ********* Flags processing ************ */

  it should "fetch all flags that have been passed" in {
    var taz : NoValuesCommand = null
    command named "taz" accepts(
        flag named "f1",
        flag named "f2",
        flag named "f3"
        ) does { cmd => taz = cmd }
    commandManager.execute("taz --f1 --f3".toArgs)

    taz.numberOfFlags should be (3)

    (taz.flag("f1") : Boolean) should be (true)
    (taz.flag("f2") : Boolean) should be (false)
    (taz.flag("f3") : Boolean) should be (true)
    taz.flag("f1").isDefined should be (true)
    taz.flag("f2").isDefined should be (false)
    taz.flag("f3").isDefined should be (true)
  }

  it should "fetch all flags that have been passed using short form" in {
    var taz : NoValuesCommand = null
    command named "taz" accepts(
        flag named "f1" alias "d",
        flag named "f2" alias "f",
        flag named "f3"
        ) does { cmd => taz = cmd }
    commandManager.execute("taz -d -f".toArgs)

    taz.numberOfFlags should be (3)

    (taz.flag("d") : Boolean) should be (true)
    (taz.flag("f") : Boolean) should be (true)
    (taz.flag("f3") : Boolean) should be (false)
    taz.flag("d").isDefined should be (true)
    taz.flag("f").isDefined should be (true)
    taz.flag("f3").isDefined should be (false)
  }

  it should "fetch all flags that have been passed using short mixed form" in {
    var taz : NoValuesCommand = null
    command named "taz" accepts(
        flag named "f1" alias "d",
        flag named "f2" alias "f",
        flag named "f3"
        ) does { cmd => taz = cmd }
    commandManager.execute("taz -df".toArgs)

    taz.numberOfFlags should be (3)

    (taz.flag("d") : Boolean) should be (true)
    (taz.flag("f") : Boolean) should be (true)
    (taz.flag("f3") : Boolean) should be (false)
    taz.flag("d").isDefined should be (true)
    taz.flag("f").isDefined should be (true)
    taz.flag("f3").isDefined should be (false)
  }

  it should "throw an IllegalCommandLineArgumentsException fetch all flags that have been passed using short mixed form with long form" in {
    var taz : NoValuesCommand = null
    command named "taz" accepts(
        flag named "f1" alias "d",
        flag named "f2" alias "f",
        flag named "f3"
        ) does { cmd => taz = cmd }

    an [IllegalCommandLineArgumentsException] should be thrownBy {
      commandManager.execute("taz --df".toArgs)
    }
  }

  it should "throw an IllegalCommandLineArgumentsException fetch all flags mixed long" in {
    var taz : NoValuesCommand = null
    command named "taz" accepts(
        flag named "f1" alias "d",
        flag named "f2" alias "f",
        flag named "f3"
        ) does { cmd => taz = cmd }

    an [IllegalCommandLineArgumentsException] should be thrownBy {
      commandManager.execute("taz -df3".toArgs)
    }
  }

  it should "throw an IllegalCommandLineArgumentsException if a flag is defined twice" in {
    var taz : NoValuesCommand = null
    command named "taz" accepts(
        flag named "f1" alias "d",
        flag named "f2" alias "f",
        flag named "f3"
        ) does { cmd => taz = cmd }

    an [IllegalCommandLineArgumentsException] should be thrownBy {
      commandManager.execute("taz -d -d".toArgs)
    }
  }

  it should "throw an IllegalCommandLineArgumentsException if a flag is defined twice in different forms" in {
    var taz : NoValuesCommand = null
    command named "taz" accepts(
        flag named "f1" alias "d",
        flag named "f2" alias "f",
        flag named "f3"
        ) does { cmd => taz = cmd }

    an [IllegalCommandLineArgumentsException] should be thrownBy {
      commandManager.execute("taz -d --f1".toArgs)
    }
  }

}
