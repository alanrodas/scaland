---
layout: docs
title: Basic Usage
prev_section: cli/installation
next_section: vcs/home
permalink: /docs/cli/usage/
---

Scaland CLI provides a simple set of functions and object builders in order to provide a simple
way to define commands, arguments and flags in the body of the main object.
This set of functions and methods end up generating a DSL like language that you can use to
configure your application expected behavior when called from the command line with different
sets of arguments.


#### Defining the short and long parameter signs

Parameters are usually values that are not required by the application, but that, when given,
they modify the behavior of the program. Common examples of such parameters include `--force`
or `-f` modifiers, or the `--verbose` or `-v`.

As it can be seen, this elements take a special sign before their identifier. This sign is
what identifies a parameter as such and distinguishes it from a value passed to the command.
Typically, in unix systems the sign for short parameters (that is, one letter only) is `"-"`
(dash). Windows systems usually use the `"/"` sign (slash) for this parameters. For long name
parameters (more than one character), a double dash is used in unix systems (`"--"`) while the
same slash is used in windows (`"/"`).

The library allows you to change the long and short signs used by the application by overriding the
`paramSigns` variable defined in `CLIApp`.

So to change it, just assign your desired sign as the following example:

{% highlight scala %}
object MainApp extends CLIApp {
    paramSigns = ("-", "--")
}
{% endhighlight %}

Scaland CLI uses the unix standard as a default, no matter if you are in a Windows or a Unix system.
In the rest of the document you will find references to flags and arguments called in this format,
but be aware that this can be changed if you wish.

<div class="note unreleased">
  <h5>Default sign</h5>
  <p>
    In the future you will be able to define the default to UNIX or Windows without having to
    set it manually for changing. By default the system will select to the current OS style.
  </p>
</div>

#### Defining flags

A flag is a boolean value that is passed to the application in the form of a parameter.
Common examples of flags in a command line app are `--verbose` or `--force` which also usually have
their short versions as `-v` and `-f`. Flags may be created by the `flag` function that is in
scope when importing the `com.alanrodas.scaland.cli` package

The `flag function returns a builder object that needs to be configured correctly in order to
properly define a flag as you want it.

The following is a flag definition for verbose that can be called as `--verbose` or as `-v`:

{% highlight scala %}
flag named "verbose" alias "v"
{% endhighlight %}

Defining a short version as the name is also valid

{% highlight scala %}
flag named "v" alias "verbose"
{% endhighlight %}

The name is always needed, but the alias may be omitted. Here
is a last example of a flag `-f`.

{% highlight scala %}
flag named "f"
{% endhighlight %}

Flags are intended to be added to a command so that, when called from the command line,
those arguments are checked for existence. If they were defined, they will return `true`
as their value, and if they where not passed, `false`.

<div class="note info">
  <h5>Naming</h5>
  <p>
    Note that if both the name and the alias is given, then one of them should be
    the short form, as `v`, while the other should be the long form such as `verbose`.
    Providing both name and alias as short or long form will throw an exception.
  </p>
</div>

#### Defining arguments

Arguments are another form of parameters that may takes values which may need or not
to be present. They are pretty much like flags, but they can define values of other types
aside from boolean values.

Consider for example the argument `"-m"` in the `git commit` command. This command takes one
argument, that is the commit message. The commit message cannot be omitted, so it has no default
value. An argument such as that will be defined as:

{% highlight scala %}
arg named "m" taking 1 value
{% endhighlight %}

<div class="note warning">
  <h5>value vs. values</h5>
  <p>
    The DSL distinguishes between `value` and `values`. Use `value` only if the argument
    accepts 1 value, use `values` in cases where it receives more than one.
  </p>
</div>

Now imagine that, in our application, we want to configure a default commit message
in case that it's not given. In that case, we will define the argument as:

{% highlight scala %}
arg named "m" taking 1 as "My Default Message"
{% endhighlight %}

A value may define multiple default values, one for each value it takes actually.

{% highlight scala %}
arg named "test" taking 3 as ("first", "second", "third")
{% endhighlight %}

The argument `test` takes three values, but they all have default values, so they are not required if
you don't want to pass them. That means that you can call your application as:

{% highlight bash %}
~ $ myApp --test
{% endhighlight %}

or by passing three values:

{% highlight bash %}
~ $ myApp --test 1st 2nd 3rd
{% endhighlight %}

One last option, is to define only some of the arguments. This has as the limitation
that you may only define the values in order, so you cannot give a value to the third
element without given one to the second. So:

{% highlight bash %}
~ $ myApp --test 1st 2nd
{% endhighlight %}

will be translated to an argument with the values `1st 2nd third` when executed.

You can define defaults for less values than the number it receives.

{% highlight scala %}
arg named "test" taking 4 as ("third", "fourth")
{% endhighlight %}

This implies that the argument receives two mandatory values, but it may receive up to four.
So, a call to:

{% highlight bash %}
~ $ myApp --test 1st
{% endhighlight %}

will result in an error as only one value has been given. A valid call will be:

{% highlight bash %}
~ $ myApp --test 1st 2nd
{% endhighlight %}

Resulting in an argument with `1st 2nd third fourth` as values. A call to:

{% highlight bash %}
~ $ myApp --test 1st 2nd 3rd
{% endhighlight %}

Will result in `1st 2nd 3rd fourth` as values.

#### Defining values

Some commands expect a specific amount of values passed to them. This values may be mandatory,
or optional and have a default value.

An example is the *git push* command, which expects a **"target"** repository and a **"branch"**
as optional values that default to **"origin"** and **"master"** respectively (I know that this
is not exactly true, but please allow me to simplify *"git push"* behavior for the sake of this guide)

So, a value for this cases also have a name and a default value. Such values may be defined with
the **value** function, and configuring the name and the default value. See the following example:

{% highlight scala %}
value named "target" as 'origin'
{% endhighlight %}


If you expect a mandatory value, then use **"mandatory"** to build the value instead of **"as"**,
as shown in the following example:

{% highlight scala %}
value named "fileName" mandatory
{% endhighlight %}

When adding values to a command definition you should consider that the expected order is that the
mandatory values are added first. If you don't follow the right order an exception warning about an
incorrect declaration will raise.

#### Defining commands

Command are the main unit of execution. Whenever you execute an application built with Scaland CLI,
a command is executed.

Each command contains it's own set of flags, arguments and values as well as a callback function to
executed when called. There are three kind of commands, the ones that do not take any value,
the ones that take a small limited amount of values (and thus, they can be named for a better semantic,
and filled with a default value), and the ones that take multiple number of values.

Commands have a name, that matches the first argument that is passed as a command call. So a call to

{% highlight scala %}
~ $ myApp myCommand
{% endhighlight %}

will result in the execution of the command `myCommand`. There is one exception, the root command. The root
command does not have a name, and it's executed when the first element in the call does not match to
a command defined.

For creating the root command the `root` function is provided. For commands with a name, the `command`
function is defined.

So, if you want to call your application as

{% highlight bash %}
~ $ myApp <fileName> [-f | --force][-v | --verbose]
{% endhighlight %}

with optional flags `-v` or `--verbose` and `-f` or `--force` and that takes one mandatory
value by the name of `fileName`, we will need to define a root command with a named value,
as follows:

{% highlight scala %}
root accepts (
    flag named "verbose" alias "v",
    flag named "force" alias "f"
  ) receives
    value named "fileName" mandatory
  ) does {command =>
    // Your action to perform on command call here
  }
{% endhighlight %}

The `accepts` method in the CommandBuilder defines the flags and arguments for the command.
You may call `accepts` with jany number of parameters. The `receives` method takes a sequence
of values. Finally, the `does` method takes the function to execute when this command is called
and also constructs the actual command from the builder.

 We will return to this command in a later
section, for now, just consider that you need to have the **"does"** method at the end of
your command definition.

If your command takes you may want to use the `multipleValues` method instead of `receives`.

{% highlight scala %}
root accepts (
    flag named "verbose" alias "v",
    flag named "force" alias "f"
) rmultipleValues does {command =>
    // Your action to perform on command call here
}
{% endhighlight %}

In this case, we are defining the command to accept an unlimited number of values, but we may
want to declare an expected minimum or maximum amount of values. For example, `rm` needs at
least one file name to be passed. This can be achieves with `minimumOf` and `maximumOf`
methods. So for a command that takes one or more values, you may define it as:

{% highlight scala %}
root accepts (
    flag named "verbose" alias "v",
    flag named "force" alias "f"
) minimumOf 1 does {command =>
    // Your action to perform on command call here
}
{% endhighlight %}

Let's say that we do not want this to be the root command, but a command named 'rm', we
would define it as:

{% highlight scala %}
command named "rm" accepts (
    flag named "verbose" alias "v",
    flag named "force" alias "f"
) minimumOf 1 does {command =>
    // Your action to perform on command call here
}
{% endhighlight %}

<div class="note info">
  <h5>Much more</h5>
  <p>
    Of course the DSL is larger. You may avoid passing the `accepts` function, or call
    `root does {...}` directly if no values are taken and much more.
  </p>
</div>


## Performing actions and using the data

As we have seen, each command takes a function in the `does` method
that is the function to call when that given command is called. This function
takes a `Command`, which holds all the values passed to the command by the user,
as well as the default values.

You may execute different inspection actions on `command` in order to get the passed
values.

{% highlight scala %}
commandBuilder does {command =>
  command.flags // Return all flags
  command.flag("flagName") // Return a flag by the name "flagName"
  command.arguments // Return all arguments
  command.argument("argName") // Return an arg by the name "argName"
  // and much more
}
{% endhighlight %}

Check the [API](/api/) in order to  know more about what actions you could perform over a command.


## Exceptions

Scaland CLI throws a two different exceptions. It will throw IllegalArgumentsException if you
try to construct a command with invalid arguments. This exceptions are mostly indications that
you are using the DSL wrongly. This type of exceptions will not occur once trying to execute the
commands of passed by the user.
If the user calls the command in an unexpected way, that is, passing incorrect values to arguments,
or passing unexpected parameters, missing mandatory arguments, etc. an IllegalCommandLineArgumentsException
will raise.

The CLIApp trait provides the variable, `onIllegalCommandLineArgument` that you may override with a
function of `IllegalCommandLineArgumentsException => Unit`. In the case that an error in raised
by an invalid user call, this function will be executed. This allows to do things like printing the
help to the user in case of invalid usage.

{% highlight scala %}
object MainApp extends CLIApp {
    onCommandNotFound = {e => printGeneralHelp()}
}
{% endhighlight %}

Go ahead and read the [API](/api/) in order to learn more about the library.