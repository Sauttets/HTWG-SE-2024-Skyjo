package de.htwg.se.skyjo

import com.google.inject.name.Names
import com.google.inject.{AbstractModule, Guice, Inject}
import net.codingwell.scalaguice.ScalaModule
import de.htwg.se.skyjo.controller.controllerComponent._
import de.htwg.se.skyjo.model.modelComponent.ModelInterface
import de.htwg.se.skyjo.model.modelComponent.modelImplementation.PlayerTable
import de.htwg.se.skyjo.controller._

  class SkyjoModule extends AbstractModule with ScalaModule {

    val defaultSize:Int = 4

    override def configure() = {
      bindConstant().annotatedWith(Names.named("DefaultSize")).to(defaultSize)
      bind[ModelInterface].to[PlayerTable]
      bind[ControllerInterface].to[controllerimplementation.TableController]
      bind[ModelInterface].annotatedWithName("tiny").toInstance(new PlayerTable(2,1,1))
      bind[ModelInterface].annotatedWithName("small").toInstance(new PlayerTable(2,2,3))
      bind[ModelInterface].annotatedWithName("normal").toInstance(new PlayerTable(2,3,4))

    }

}
