import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.se.skyjo.controller.controllerComponent.controllerimplementation.TableController
import de.htwg.se.skyjo.model.modelComponent.modelImplementation.PlayerTable
import de.htwg.se.skyjo.util.Move
import com.google.inject.Guice
import de.htwg.se.skyjo.SkyjoModule
import net.codingwell.scalaguice.InjectorExtensions._
import de.htwg.se.skyjo.model.modelComponent.{CardInterface, ModelInterface}
import java.io.File

class TableControllerSpec extends AnyWordSpec with Matchers {
  "A TableController" when {
    "new" should {
      val injector = Guice.createInjector(new SkyjoModule)
      val playerTable = injector.instance[PlayerTable]
      val controller = injector.instance[TableController]
      
      "check if game ends" in {
        controller.gameEnd() shouldBe a[Boolean]
      }
      
      "return a string representation of the table" in {
        controller.toString shouldBe a[String]
      }
      
      "draw from stack" in {
        val initialStackCard = controller.getStackCard()
        controller.drawFromStack()
        val newStackCard = controller.getStackCard()
        newStackCard should not equal initialStackCard
      }
      
      "draw from trash" in {
        // Assuming there is a card in trash to draw.
        controller.drawFromTrash()
        val trashCard = controller.getTrashCard()
        trashCard shouldBe a[CardInterface]
      }
      
      "do a move" in {
        val move = new Move(true, true, 0, 1) // example move
        controller.doMove(move)
        controller.moves should contain(move)
      }
      
      "return player string" in {
        controller.getPlayerString(0) shouldBe a[String]
      }
      
      "return current player string" in {
        controller.getCurrenPlayerString() shouldBe a[String]
      }
      
      "return current player" in {
        controller.getCurrenPlayer() shouldBe a[Integer]
      }
      
      "return player count" in {
        controller.getPLayerCount() shouldBe a[Integer]
      }
      
      "return stack card" in {
        controller.getStackCard() shouldBe a[CardInterface]
      }
      
      "return trash card" in {
        controller.getTrashCard() shouldBe a[CardInterface]
      }
      
      "return tabletop" in {
        controller.getTabletop() shouldBe a[List[?]]
      }
      
      "undo a move" in {
        controller.undo()
      }
      
      "redo a move" in {
        controller.redo()
      }
      
      "reset the table" in {
        controller.reset()
        controller.table shouldBe a[ModelInterface]
      }
      
      "return parity" in {
        controller.getParitys() shouldBe a[List[?]]
      }
      
      "open all cards" in {
        controller.openAll()
      }
      
      "save the game state" in {
        val path = new File("test_save_path")
        controller.save(path)
      }
      
      "load the game state" in {
        val path = new File("test_save_path")
        controller.load(path)
      }
    }
  }
}
