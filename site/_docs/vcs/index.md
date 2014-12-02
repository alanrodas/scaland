---
layout: docs
title: Version Control System
prev_section: cli/usage
next_section: vcs/installation
permalink: /docs/vcs/home/
---

Scaland VCS module allows you to manage version control systems such as
git or svn.

For working with a specific type of repository, you will need to download that
specific repository vcs manager.

Currently, you have the following modules:
* scaland-vcs-git
* scaland-vcs-svn

Add that dependency to your SBT file.

{% highlight scala %}
resolvers += "alanrodas" at "https://alanrodas.com/maven/releases/"

libraryDependencies += "com.alanrodas" %% "scaland-vcs-git" % "0.1"
{% endhighlight %}

Now justo import the following:

1. Import `com.alanrodas.scaland.vcs._`
2. Import `com.alanrodas.scaland.vcs.git._`
3. Import `import language.postfixOps`

You are all set, create your commands.

{% highlight scala %}
import com.alanrodas.scaland.vcs._
import com.alanrodas.scaland.vcs.git._
import language.postfixOps

// Import a connector
import com.alanrodas.scaland.vcs.git.connectors.jGitConnector

for(git <- Git clone ("https://github.com/github/testrepo.git" branch "blue") at "testrepo") {
  git add "somefile"
  println( (git list).getOrElse(Nil).mkString("\n") )
}
{% endhighlight %}

<div class="note warning">
  <h5>Unfinished version</h5>
  <p>
    Note that the current version of Scaland VCS is still in early steps
    of development, and not all methods are fully implemented.
  </p>
</div>