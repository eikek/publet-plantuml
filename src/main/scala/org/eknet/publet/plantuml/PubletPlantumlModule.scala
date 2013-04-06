package org.eknet.publet.plantuml

import org.eknet.publet.web.guice.{AbstractPubletModule, PubletModule, PubletBinding}
import org.eknet.publet.vfs.Resource

class PubletPlantumlModule extends AbstractPubletModule with PubletBinding with PubletModule {

  def configure() {
    bind[PubletPlantumlSetup].asEagerSingleton()
    bindDocumentation(docResource("plantuml.md"))
  }

  private[this] def docResource(names: String*) = names.map("org/eknet/publet/plantuml/doc/"+ _).map(Resource.classpath(_)).toList

  val name = "PlantUML Macro"

  override val version = org.eknet.publet.plantuml.Reflect.version
  override val license = org.eknet.publet.plantuml.Reflect.licenses.headOption
}
