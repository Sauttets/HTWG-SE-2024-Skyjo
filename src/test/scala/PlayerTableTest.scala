import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.se.skyjo.model._

class PlayerTableTest extends AnyWordSpec with Matchers {

  "A PlayerTable" when {

    "created" should {
      "have initial state" in {
        val playerTable = new PlayerTable()
        playerTable.Tabletop.size should be > 0
        playerTable.cardstack.stackCard.value should (be >= -2 and be <= 12)
        playerTable.playerCount should be > 0
        playerTable.currentPlayer shouldEqual 0
      }
    }

    "drawing from the stack" should {
      "open the top card" in {
        val playerTable = new PlayerTable()
        val updatedTable = playerTable.drawFromStack()
        updatedTable.cardstack.stackCard.opened shouldBe true
      }
    }

    "updating a player's matrix" should {
      "update the corresponding position in Tabletop" in {
        val playerTable = new PlayerTable()
        val newMatrix = new PlayerMatrix(4, 4)
        val updatedTable = playerTable.updateMatrix(0, newMatrix)
        updatedTable.Tabletop(0) shouldEqual newMatrix
      }
    }

    "updating the cardstack" should {
      "move the card from stack to trash if specified" in {
        val playerTable = new PlayerTable()
        val handCard = playerTable.cardstack.stackCard
        val updatedTable = playerTable.updateCardstack(handCard, true)
        updatedTable.cardstack.trashCard.value shouldBe handCard.value

        val matrixHandCard = Card(10, opened = true)
        val updatedTable2 = updatedTable.updateCardstack(matrixHandCard, false)
        updatedTable2.cardstack.trashCard shouldBe matrixHandCard
      }
    }

    "padding values for cards" should {
      "pad the values correctly" in {
        val playerTable = new PlayerTable()
        val card = new Card(10, opened = true)
        playerTable.padValue(card) shouldBe "│ 10 "
        val card2 = new Card(1, opened = true)
        playerTable.padValue(card2) shouldBe "│ 01 "
        val card3 = new Card(-2, opened = true)
        playerTable.padValue(card3) shouldBe "│ -2 "
        val card4 = new Card(1, opened = false)
        playerTable.padValue(card4) shouldBe "│ xx "
      }
    }

    "getting Cardstack String" should {
      "return correct string representation" in {
        val playerTable = new PlayerTable()
        val str = playerTable.getCardStackString()
        val shouldString =
          s"""\nCurrent card stack:
             |  ┌────┐  ┌────┐
             | ┌${playerTable.padValue(playerTable.cardstack.stackCard)}│  ${playerTable.padValue(playerTable.cardstack.trashCard)}│
             | │└───┬┘  └────┘
             | └────┘""".stripMargin
        str shouldBe shouldString
      }
    }

    "getting current player string" should {
      "return correct string representation" in {
        val playerTable = new PlayerTable()
        val str = playerTable.getCurrenPlayerString()
        str should include ((playerTable.currentPlayer+1).toString())
        str.toUpperCase() should include ("PLAYER")
      }
    }

    "getting player matrices string" should {
      "return correct string representation" in {
        val playerTable = new PlayerTable()
        val str = playerTable.getPlayerMatricesString() 
        val shouldString1 = 
            s"""|\u001B[31m
                |Player 1:
                |+----+----+----+----+
                |│ xx │ xx │ xx │ xx │
                |+----+----+----+----+
                |│ xx │ xx │ xx │ xx │
                |+----+----+----+----+
                |│ xx │ xx │ xx │ xx │
                |+----+----+----+----+
                |│ xx │ xx │ xx │ xx │
                |+----+----+----+----+
                |\u001B[32m
                |Player 2:
                |+----+----+----+----+
                |│ xx │ xx │ xx │ xx │
                |+----+----+----+----+
                |│ xx │ xx │ xx │ xx │
                |+----+----+----+----+
                |│ xx │ xx │ xx │ xx │
                |+----+----+----+----+
                |│ xx │ xx │ xx │ xx │
                |+----+----+----+----+\u001B[0m""".stripMargin
        str shouldBe shouldString1
      }
      "return string representation of minimal matrix" in {
        val playerTable = new PlayerTable(2, 1, 1)
        val str = playerTable.getPlayerMatricesString()
        val shouldString2 = 
            s"""|\u001B[31m
                |Player 1:
                |+----+
                |│ xx │
                |+----+
                |\u001B[32m
                |Player 2:
                |+----+
                |│ xx │
                |+----+\u001B[0m""".stripMargin
        str shouldBe shouldString2
      }
    }

    "getting full table string" should {
      "return correct string representation" in {
        val playerTable = new PlayerTable()
        val str = playerTable.getTableString()
        val shouldString = playerTable.getPlayerMatricesString() + playerTable.getCardStackString()
        str shouldBe shouldString
      }
    }
    "getting Scores" should{
      "return scores of each player"in{
        val playerTable= new PlayerTable(3,1,1)
        val matrix0=playerTable.Tabletop(0)
        val matrix1=playerTable.Tabletop(1)
        val matrix2=playerTable.Tabletop(2)
        playerTable.getScores() shouldBe List((matrix0.getCard(0,0).value,0),
                                              (matrix1.getCard(0,0).value,1),
                                              (matrix2.getCard(0,0).value,2))                                                       
      }
    }

  }
}