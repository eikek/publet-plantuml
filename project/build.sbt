resolvers += "sbt-idea-repo" at "http://mpeltonen.github.com/maven/"

resolvers += "eknet.org" at "https://eknet.org/maven2"

addSbtPlugin("com.github.mpeltonen" % "sbt-idea" % "1.1.0")

addSbtPlugin("org.eknet.publet" % "publet-sbt-plugin" % "1.0.0")

addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "0.8.8")