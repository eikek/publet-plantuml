import _root_.sbtassembly.Plugin.AssemblyKeys
import _root_.sbtassembly.Plugin.AssemblyKeys._
import sbt._
import Keys._
import Dependencies._
import sbtassembly.Plugin._
import AssemblyKeys._

object Resolvers {
  val eknet = "eknet.org" at "https://eknet.org/maven2"
  val milton = "milton.io" at "http://milton.io/maven"
}
object Version {
  val slf4j = "1.7.2"
  val logback = "1.0.9"
  val scalaTest = "2.0.M6-SNAP3"
  val grizzled = "0.6.9"
  val scala = "2.9.2"
  val servlet = "3.0.1"
  val plantuml = "7961"
  val publet = "1.0.1"
}

object Dependencies {

  val scalaTest = "org.scalatest" %% "scalatest" % Version.scalaTest % "test"
  val publetAppDev = "org.eknet.publet" %% "publet-app" % Version.publet exclude("org.restlet.jse", "org.restlet.ext.fileupload") exclude("org.restlet.jse", "org.restlet") exclude("org.slf4j", "slf4j-log4j12")
  val publetAppPlugin = publetAppDev % "publet" exclude("org.restlet.jse", "org.restlet.ext.fileupload") exclude("org.restlet.jse", "org.restlet") exclude("org.slf4j", "slf4j-log4j12")

  val publetWeb = "org.eknet.publet" %% "publet-web" % Version.publet % "provided" exclude("org.restlet.jse", "org.restlet.ext.fileupload") exclude("org.restlet.jse", "org.restlet") exclude("org.slf4j", "slf4j-log4j12")
  val servletApiProvided = "javax.servlet" % "javax.servlet-api" % Version.servlet % "provided"

  val plantuml = "net.sourceforge.plantuml" % "plantuml" % Version.plantuml
}

// Root Module 

object RootBuild extends Build {
  import org.eknet.publet.sbt.PubletPlugin

  lazy val root = Project(
    id = "publet-plantuml",
    base = file("."),
    settings = buildSettings
  )

  //an empty project to create a classpath to be
  //able to start publet from the ide via a "main class"
  lazy val runner = Project(
    id = "publet-runner",
    base = file("runner"),
    settings = Project.defaultSettings ++ Seq(
      name := "publet-runner",
      libraryDependencies ++= Seq(publetAppDev)
    )
  ) dependsOn (root)

  val exludedFiles = Set(
    "javax.servlet-3.0.0.v201112011016.jar"
  )
  def isExcluded(n: String) = exludedFiles contains (n)

  val deps = Seq(publetWeb, publetAppPlugin, servletApiProvided, plantuml)

  val buildSettings = Project.defaultSettings ++ assemblySettings ++ ReflectPlugin.allSettings ++ Seq(
    name := "publet-plantuml",
    ReflectPlugin.reflectPackage := "org.eknet.publet.plantuml",
    sourceGenerators in Compile <+= ReflectPlugin.reflect,
    libraryDependencies ++= deps,
    assembleArtifact in packageScala := false,
    excludedJars in assembly <<= (fullClasspath in assembly) map { cp =>
      cp filter { f => isExcluded(f.data.getName) }
    }
  ) ++ PubletPlugin.publetSettings

  override lazy val settings = super.settings ++ Seq(
    version := "0.1.0-SNAPSHOT",
    organization := "org.eknet.publet.plantuml",
    scalaVersion := Version.scala,
    exportJars := true,
    pomIncludeRepository := (_ => false),
    scalacOptions ++= Seq("-unchecked", "-deprecation"),
    resolvers ++= Seq(Resolvers.eknet, Resolvers.milton),
    licenses := Seq(("GPLv3", new URL("http://www.gnu.org/licenses/gpl-3.0.html"))),

    // see https://jira.codehaus.org/browse/JETTY-1493
    ivyXML := <dependency org="org.eclipse.jetty.orbit" name="javax.servlet" rev="3.0.0.v201112011016">
        <artifact name="javax.servlet" type="orbit" ext="jar"/>
    </dependency>
  )

}


