package org.eknet.publet.plantuml

import org.eknet.publet.web.guice.PubletStartedEvent
import com.google.inject.{Inject, Singleton}
import com.google.common.eventbus.{EventBus, Subscribe}
import org.eknet.publet.Publet
import org.eknet.publet.vfs.util.ByteSize
import org.eknet.publet.vfs.{ContentResource, Path}
import java.util.regex.Matcher
import net.sourceforge.plantuml.SourceStringReader
import java.io.File
import org.eknet.publet.web.Config
import org.eknet.publet.vfs.fs.{FileResource, FilesystemPartition}
import org.fusesource.scalamd.{MacroDefinition, Markdown}
import com.google.common.hash.Hashing
import org.eknet.publet.web.util.PubletWebContext
import com.google.common.cache._
import org.eknet.publet.web.guice.PubletStartedEvent
import scala.Some
import org.fusesource.scalamd.MacroDefinition
import java.util.concurrent.Callable

@Singleton
class PubletPlantumlSetup @Inject() (publet: Publet, config: Config)  {

  val plantUmlDir = config.newStaticTempDir("plantumlimages")

  val plantumlMount = Path("/publet/plantumlimages")

  private val sizeRegex = """((\d+)(\.\d+)?)(.*)""".r

  private val sizer = new Weigher[String, File] {
    def weigh(key: String, value: File) = (value.length() / 1000).toInt
  }
  private val remover = new RemovalListener[String, File] {
    def onRemoval(notification: RemovalNotification[String, File]) {
      notification.getValue.delete()
    }
  }

  private val cache: Cache[String, File] = {
    val diskSize = config("plantuml.maxDiskSize") flatMap (str => str match {
      case sizeRegex(num, i, p, unit) => {
        if (unit.isEmpty) Some(i.toLong)
        else Some(ByteSize.fromString(unit).toBytes(num.toDouble))
      }
      case _ => None
    }) getOrElse (ByteSize.mib.toBytes(50))

    CacheBuilder.newBuilder().maximumWeight(diskSize)
      .weigher(sizer)
      .removalListener(remover)
      .build()
  }

  @Subscribe
  def mountResources(event: PubletStartedEvent) {
    val tmpimages = new FilesystemPartition(plantUmlDir, new EventBus(), true)
    tmpimages.children.foreach( c => c match {
      case cc: FileResource => cache.put(cc.file.getName, cc.file)
      case _ =>
    })
    publet.mountManager.mount(plantumlMount, tmpimages)
    Markdown.macros :::= List(
      MacroDefinition( """(@startuml[:\s]{1,2}.*?@enduml)\s""", "s", createPlantumlImage, true)
    )
  }

  def createPlantumlImage(m: Matcher): String = {
    val body = m.group(1)
    if (body.startsWith("@startuml::")) {
      "<pre>"+body.replaceFirst("@startuml::", "@startuml")+"</pre>"
    }
    else if (body.startsWith("@startuml:")) {
      body.replaceFirst("@startuml:", "@startuml")
    }
    else {
      val file = new File(plantUmlDir, createFilename(body))
      cache.get(file.getName, new Callable[File] {
        def call() = {
          if (file.exists()) {
            file
          } else {
            val reader = new SourceStringReader(body)
            reader.generateImage(file)
            file
          }
        }
      })

      val url = PubletWebContext.urlOf(plantumlMount / file.getName)
      """<img src="%s"/>""".format(url)
    }
  }

  private[this] def createFilename(uml: String): String = {
    Hashing.sha1().hashString(uml).toString + ".png"
  }
}
