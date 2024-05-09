import org.scalatest.funsuite.AnyFunSuite
import de.htwg.se.skyjo.model._

class PlayerTableTest extends AnyFunSuite {

  test("Creating a new PlayerTable") {
    val playerTable = new PlayerTable()
    assert(playerTable.Tabletop.size == 2) 
    assert(playerTable.cardstack.stackCard.value >= -2 && playerTable.cardstack.stackCard.value <= 12)
    assert(playerTable.playerCount == 2)
    assert(playerTable.currentPlayer == 0)
  }

  test("Drawing from the stack") {
    val playerTable = new PlayerTable()
    val updatedTable = playerTable.drawFromStack()
    assert(updatedTable.cardstack.stackCard.opened)
  }

  test("Updating a player's matrix") {
    val playerTable = new PlayerTable()
    val newMatrix = new PlayerMatrix(4, 4)
    val updatedTable = playerTable.updateMatrix(0, newMatrix)
    assert(updatedTable.Tabletop(0) == newMatrix)
  }

  test("Updating the cardstack") {
    //card from stack put on trash
    val playerTable = new PlayerTable()
    val handCard = playerTable.cardstack.stackCard
    val updatedTable = playerTable.updateCardstack(handCard, true)
    assert(updatedTable.cardstack.trashCard.value == handCard.value)

    //card from matrix
    val matrixHandCard = Card(10, opened = true)
    val updatedTable2 = updatedTable.updateCardstack(matrixHandCard, false)
    assert(updatedTable2.cardstack.trashCard == matrixHandCard)
  }
}
