---
layout: docs
title: Installation
prev_section: modules
next_section: cli/home
permalink: /docs/installation/
---

Scaland is divided into modules, so each module is installed independently
from each other.
You can use SBT or Maven to download and install them in your project

### Requirements

Scaland is built against Scala 2.11 so you need an according Scala installation
in order to install the libraries.

Additionally, some modules of Scalands (VCS module for example) may require
that a specific tool is installed in the system (such as git or svn).
Read the module specific documentations for more details.

## Installing with SBT

The best way to install Scaland is via
[SBT](http://www.scala-sbt.org/). If you are already using SBT for your
project, just add the Scaland repository to your projects.

{% highlight scala %}
resolvers += "alanrodas" at "https://alanrodas.com/maven/releases/"
{% endhighlight %}

Now, start adding the modules you want to your project

{% highlight scala %}
libraryDependencies += "com.alanrodas.scaland" %% "scaland-vcs" % "0.1"

libraryDependencies += "com.alanrodas.scaland" %% "scaland-vcs-git" % "0.1"
{% endhighlight %}

<div class="note info">
  <h5>SBT Documentation</h5>
  <p>
    If you’re new to SBT and Scala development you may want to read the
    SBT documentation. You may find it [Here](http://www.scala-sbt.org/documentation.html)
  </p>
</div>

## Building it yourself

If you need to compile Scaland against a different version of Scala, or,
if you want to modify something in it before using it, then you may
want to build the library yourself.

Scaland is an SBT multi-build project and it's hosted at
[GitHub](https://github.com/alanrodas/scaland). Go ahead and download the source
code to your local machine using git.

{% highlight bash %}
~ $ git clone git@github.com:alanrodas/scaland.git
~ $ cd scaliapp
{% endhighlight %}

Once there, feel free to modify any part of the project you may need to, and then
build it using SBT.

{% highlight bash %}
~ $ sbt compile
{% endhighlight %}

You may fetch the generated jar files from the generated `target` directory.
In most cases, you will want to use the generated jars in the same machine, so
a better approach is to publish the library in your local machine, to make it
available for any other project.

Just run

{% highlight bash %}
~ $ sbt publishLocal
{% endhighlight %}

And the libraries will be built and stored at `~/.ivy2/local/`.