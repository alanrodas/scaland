---
layout: docs
title: Roadmap
prev_section: vcs/usage
next_section: contributing
permalink: /docs/roadmap/
---


Scaland is still in it's early stages of development, so there's a lot to do.
This is a list of planned features from here, up to version 1.0.

Note that this list is not comprehensive and may change as new needs raise.

## Module CLI

* Add support for arguments after equal sign (arg=value and arg=value1,value2,value3)
* Interactive mode
* Auto-completion. This involves querying the files and folders and not just relying on bash or zsh
* Auto generated help
* i18n in help files
* Autogeneration of man files
* Add a way to package CLI apps (SBT plugin maybe?) in Linux, OS X or Windows executables

## Module VCS

* Finish GIT and SVN command line support
* Finish jGit integration
* Finish SVNKit pure java integration
* Add support for CVS
* Add support for Mercurial
* Add support for Bazaar
* Other VCSs??
* Support more operations
* Migration support (Pass a git repo to a mercurial repo, etc.)

## Module I/O

* Publish first version
* Handle path creation and management
* Test for file existance, fetching content, properties and other of files and folders
* Implicit conversions to Java Files and buffers
* Handle OS Specific cases
* Download files to disk from HTTP FTP or any other web resource path.

<div class="note info">
  <h5>Suggestions</h5>
  <p>
    If you have suggestuons, please, contact me at <a href="mailto://alanrodas@gmail.com">
    alanrodas@gmail.com</a> or by Twitter at <a href="https://twitter.com/alanrodas">@alanrodas</a>.
    As I said, this is still in an early stage of development.
  </p>
</div>

