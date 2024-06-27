// File: de/htwg/se/skyjo/SkyjoModule.scala
package de.htwg.se.skyjo

import com.google.inject.AbstractModule
import com.google.inject.name.Names
import net.codingwell.scalaguice.ScalaModule
import de.htwg.se.skyjo.controller.controllerComponent.ControllerInterface
import de.htwg.se.skyjo.controller.controllerComponent.controllerimplementation.TableController
import de.htwg.se.skyjo.model.modelComponent.ModelInterface
import de.htwg.se.skyjo.model.modelComponent.modelImplementation.PlayerTable
import de.htwg.se.skyjo.model.modelComponent.fileIoComponent.fileIoJsonImpl.FileIO as JsonFileIO
import de.htwg.se.skyjo.model.modelComponent.fileIoComponent.fileIoXmlImpl.FileIO as XmlFileIO
import de.htwg.se.skyjo.model.modelComponent.FileIOInterface
import de.htwg.se.skyjo.util.{CardStackStrategy}
import de.htwg.se.skyjo.model.modelComponent.modelImplementation.LCardStack
import de.htwg.se.skyjo.model.modelComponent.modelImplementation.PlayerMatrix

class SkyjoModule extends AbstractModule with ScalaModule {

  val defaultPlayerCount: Int = 2
  val defaultWidth: Int = 4
  val defaultHeight: Int = 3
  val defaultCardStackStrategy: CardStackStrategy = new LCardStack
  val defaultCurrentPlayer: Int = 0
  val defaultTabletop: List[PlayerMatrix] = List.tabulate(defaultPlayerCount)(_ => new PlayerMatrix(defaultHeight, defaultWidth))

  override def configure(): Unit = {
    bindConstant().annotatedWith(Names.named("DefaultPlayerCount")).to(defaultPlayerCount)
    bindConstant().annotatedWith(Names.named("DefaultWidth")).to(defaultWidth)
    bindConstant().annotatedWith(Names.named("DefaultHeight")).to(defaultHeight)
    bind[CardStackStrategy].annotatedWith(Names.named("CardStackStrategy")).toInstance(defaultCardStackStrategy)
    bind[List[PlayerMatrix]].annotatedWith(Names.named("Tabletop")).toInstance(defaultTabletop)
    bind[Int].annotatedWith(Names.named("DefaultCurrentPlayer")).toInstance(defaultCurrentPlayer)

    bind[ModelInterface].to[PlayerTable]
    bind[ControllerInterface].to[TableController]

    bind[FileIOInterface].to[JsonFileIO]  // Use JSON implementation
    // bind[FileIOInterface].to[XmlFileIO]  // Use XML implementation
  }
}
