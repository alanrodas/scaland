---
layout: docs
title: Contributing
prev_section: roadmap
next_section: history
permalink: /docs/contributing/
---

Scaland is still in it's early stages of development, so all help is appreciated.
If you want to contribute, go ahead, but keep the following in mind:

* All code that you submit should be documented. Be sure to add comments and
  proper Scala Docs to your code.
* If your tools are going to modify core behavior of the library, pleas document it
  and test the code.
* The testing in the library is quite poor, but they are much appreciated. Update
  the actual test accordingly to make them more extensible to the actual code. Also,
  add your own set of tests if you need to.
* Do not hesitate in change core code. The code in the library is poorly written at
  points, do not hesitate to correct it when necessary.


<div class="note warning">
  <h5>Contributions will not be accepted without documentation</h5>
  <p>
    Whatever code you are submitting, be sure to add a comprehensive
    documentation on what you code does.
  </p>
</div>

Test Dependencies
-----------------

To run the test suite and build the jar you'll need to install Scaland's
dependencies. Scala uses SBT, so a quick run of the `sbt compile` command and
you're all set!

{% highlight bash %}
$ sbt compile
{% endhighlight %}

Before you start, run the tests and make sure that they pass (to confirm your
environment is configured properly):

{% highlight bash %}
$ sbt test
{% endhighlight %}

Workflow
--------

Here's the most direct way to get your work merged into the project:

* Fork the project.
* Clone down your fork:

{% highlight bash %}
git clone git://github.com/<username>/scaland.git
{% endhighlight %}

* Create a topic branch to contain your change:

{% highlight bash %}
git checkout -b my_awesome_feature
{% endhighlight %}


* Hack away, add tests. Not necessarily in that order.
* Make sure everything still passes by running `sbt test`.
* If necessary, rebase your commits into logical chunks, without errors.
* Push the branch up:

{% highlight bash %}
$ git push origin my_awesome_feature
{% endhighlight %}

* Create a pull request against alanrodas/scaland:master and describe what your
  change does and the why you think it should be merged.

<div class="note info">
  <h5>Talk to me</h5>
  <p>
    I'm always available to talk about a feature or something. You can contact me
    at <a href="mailto://alanrodas@gmail.com">alanrodas@gmail.com</a> or by Twitter
    at <a href="https://twitter.com/alanrodas">@alanrodas</a>.
  </p>
</div>

Updating Documentation
----------------------

I want the Scaland documentation to be the best it can be.

You can find the documentation expressed in this site in the
[GitHub Repo site](https://github.com/alanrodas/scaland/site) folder.

All documentation pull requests should be directed at `master`.  Pull
requests directed at another branch will not be accepted.

The [Scaland wiki]({{ site.repository }}/wiki) on GitHub
can be freely updated without a pull request as all
GitHub users have access.