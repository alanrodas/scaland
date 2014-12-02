---
layout: docs
title: Command Line Interface
prev_section: installation
next_section: cli/installation
permalink: /docs/cli/home/
---

Scaland CLI module allows to create command line applications easily.

Let's have an easy tour.

Add this to your build.sbt file:

{% highlight scala %}
resolvers += "alanrodas" at "https://alanrodas.com/maven/releases/"

libraryDependencies += "com.alanrodas" %% "scaland-cli" % "0.1"
{% endhighlight %}

Now, follow the following steps:

1. Import `com.alanrodas.scaland.cli._`
2. Import `import language.postfixOps`
3. Create an object extending `CLIApp`

You are all set, create your commands.

{% highlight scala %}
import com.alanrodas.scaland.cli._
import language.postfixOps

object MainApp extends CLIApp {
	root accepts (
		flag named "verbose" alias "v",
		flag named "quiet" alias "q",
		flag named "dry-run",
		flag named "force" alias "f",
		arg named "repo" taking 1 value,
		arg named "recurse-submodules" taking 1 as "check",
		arg named "exec" taking 2 values
	) receives (
		value named "target" mandatory,
		value named "branch" as "master"
	) does { commandCall =>
		dump(commandCall)
	}
	command named "add" accepts (
		flag named "quiet" alias "q",
		flag named "dry-run" alias "n",
		flag named "force" alias "f",
		flag named "intent-to-add",
		flag named "all" alias "A"
	) minimumOf 1 does { commandCall =>
		dump(commandCall)
	}
}
{% endhighlight %}

Now you can call your application as

{% highlight bash %}
~ $ sbt "run theOrigin --verbose -f"
Called command: <root>
Values are: (2)
target: (User defined) theOrigin
branch: (Default) master
Flags Are: (4)
--verbose -v: (Passed)
--force -f: (Passed)
--quiet -q: (Not Passed)
--dry-run : (Not Passed)
Arguments are: (3)
--repo : (Default) None
--recurse-submodules : (Default) check
--exec : (Default) None
{% endhighlight %}

or as

{% highlight bash %}
~ $ sbt "run add theOrigin -f"
Called command: add
Values are: (1)
theOrigin
Flags Are: (5)
--quiet -q: (Not Passed)
--intent-to-add : (Not Passed)
--all -A: (Not Passed)
--dry-run -n: (Not Passed)
--force -f: (Passed)
Arguments are: (0)
{% endhighlight %}

Check the [Usage](/docs/usage) to learn more about how to use it.