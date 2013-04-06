# extension template 

This is an extension template for [publet](https://eknet.org/main/projects/publet/index.html). It
contains necessary build files for sbt and a silly example.

Clone this project

    git clone https://eknet.org/git/publet-extension-template.git

and then execute the `install.scala` script

    scala src/install.scala

This will ask for a `groupId` and `projectId` and then renames and filters
the files. After that, just remove the `intall.scala` script (and the `.git/`
directory) and start  [sbt](https://github.com/harrah/xsbt). The script needs 
Java 7 to run.

The [publet-sbt-plugin](https://eknet.org/gitr/?r=publet-sbt-plugin.git) is 
configured and you can start publet with the new extension using `publet:start`
sbt task.
