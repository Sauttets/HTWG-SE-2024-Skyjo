import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.se.skyjo.model.modelComponent.fileIoComponent.fileIoJsonImpl.FileIO
import de.htwg.se.skyjo.model.modelComponent.modelImplementation._
import de.htwg.se.skyjo.util.Move
import java.io.File
import scala.collection.mutable.Buffer

class FileIOJsonSpec extends AnyWordSpec with Matchers {
  "A JSON FileIO" should {
    val fileIO = new FileIO()
    val playerCount = 2
    val width = 4
    val height = 3
    val currentPlayer = 0
    val cardStack = new LCardStack()
    val tableTop = List.tabulate(playerCount)(_ => new PlayerMatrix(height, width))
    val playerTable = PlayerTable(playerCount, width, height, currentPlayer, cardStack, tableTop)
    val moves = Buffer(Move(true, true, 0, 1), Move(false, false, 1, 2))
    val filePath = new File("test_save_path")

    "save the game state to a JSON file" in {
      fileIO.save(playerTable, moves, filePath)
    }

    "load the game state from a JSON file" in {
      val (loadedTable, loadedMoves) = fileIO.load(filePath)
    }
  }
}
