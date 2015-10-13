muCommander
===========

fork of fathersson/muCommander

This repository is used as part of a Software Maintenance course in the University of the Southern Denmark. Please note that this repository might contain unfinished code.

We try to use these practises: http://nvie.com/posts/a-successful-git-branching-model/

Current repository contains also sources of all required libraries:
* collections
* conf
* file
* io
* runtime
* util

Key difference from v0.9.0:
* supports HDFS 2.0.0
* copy, deletion, move of files / dirs is in background

===========
Sources could be built right after git clone.
To get runnable version the one must

* run mvn clean install* 
* in ./manager/target directory run manager-{version}.jar (it uses target/lib folder)
* run **ant bundle** (can get standalone app)
* run **com.mucommander.Launcher** in eclipse to debug

===========

