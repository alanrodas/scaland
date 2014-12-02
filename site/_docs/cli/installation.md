---
layout: docs
title: Installation
prev_section: cli/home
next_section: cli/usage
permalink: /docs/cli/installation/
---

Follow the instructions in the [Installation Guide](/docs/instalation/).

Now go ahead and download the flavor you want

Git:

{% highlight scala %}
libraryDependencies += "com.alanrodas.scaland" %% "scaland-cli-svn" % "0.1"
{% endhighlight %}

SVN:

{% highlight scala %}
libraryDependencies += "com.alanrodas.scaland" %% "scaland-vcs-svn" % "0.1"
{% endhighlight %}

You are done.

Now you can start [Using](/docs/cli/usage/) the CLI module in your project.

## Donwload

Optionally, you may download the libraries directly from the site and add it to your project.

Go to the [Download page](/docs/downloads)

<div class="note warning">
  <h5>Download all dependencies</h5>
  <p>
    Note that in order to use the CLI module, you also require the core module.
    SBT handles this automatically, but if you are downloading manually, you
    will need to download each one of the required modules.
  </p>
</div>