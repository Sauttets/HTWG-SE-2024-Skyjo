import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.se.skyjo.model.modelComponent.modelImplementation.*

class PlayerTableSpec extends AnyWordSpec with Matchers {
  "A PlayerTable" when {
    "new" should {
      val playerCount = 2
      val width = 4
      val height = 3
      val currentPlayer = 0
      val cardStack = new LCardStack()
      val tableTop = List.tabulate(playerCount)(_ => new PlayerMatrix(height, width))
      val playerTable = PlayerTable(playerCount, width, height, currentPlayer, cardStack, tableTop)
      "change card stackstrategy " in{
        playerTable.setCardStackStrategy(new CCardStack).cardstack shouldBe a[CCardStack]
      }
      "have the correct number of players" in {
        playerTable.playerCount shouldBe playerCount
      }

      "have the correct width and height" in {
        playerTable.width shouldBe width
        playerTable.height shouldBe height
      }

      "have the correct current player" in {
        playerTable.currentPlayer shouldBe currentPlayer
      }

      "have a card stack" in {
        playerTable.cardstack shouldBe cardStack
      }

      "have a table top with player matrices" in {
        playerTable.Tabletop.size shouldBe playerCount
        all(playerTable.Tabletop) shouldBe a[PlayerMatrix]
      }

      "draw a card from the stack" in {
        val newTable = playerTable.drawFromStack()
        newTable.cardstack.getStackCard().opened shouldBe true
      }

      "draw a card from the trash" in {
        val newTable = playerTable.drawFromTrash()
      }

      "swap a card" in {
        val row = 0
        val col = 0
        val newCard = CardBuilder().value(5).opened(true).build()
        val (newTable, oldCard) = playerTable.swapCard(currentPlayer, row, col, newCard)
        oldCard shouldBe playerTable.Tabletop(currentPlayer).getCard(row, col)
        newTable.Tabletop(currentPlayer).getCard(row, col) shouldBe newCard
      }

      "flip a card" in {
        val row = 0
        val col = 0
        val newTable = playerTable.flipCard(currentPlayer, row, col)
        newTable.Tabletop(currentPlayer).getCard(row, col).opened shouldBe true
      }

      "update the card stack" in {
        val card = CardBuilder().value(5).opened(true).build()
        val newStack = playerTable.updateCardstack(card,true).cardstack
        newStack.getTrashCard() shouldBe card
        val newStackFromtrash = playerTable.updateCardstack(card,false).cardstack
        newStack.getTrashCard() shouldBe card
      }

      "move to the next player" in {
        val newTable = playerTable.nextPlayer()
        newTable.currentPlayer shouldBe (currentPlayer + 1) % playerCount
      }

      "check if the game has ended" in {
        playerTable.gameEnd() shouldBe false
        val finishedTable = playerTable.copy(Tabletop = tableTop.map(_.copy(rows = tableTop.head.rows.map(_.map(_.open())))))
        finishedTable.gameEnd() shouldBe true
      }

      "get the correct scores" in {
        val scores = playerTable.getScores()
        scores.size shouldBe playerCount
        all(scores.map(_._2)) shouldBe >= (0)
      }

      "return the correct card stack string" in {
        playerTable.getCardStackString() should include("Current card stack")
      }

      "return the correct player matrices string" in {
        playerTable.getPlayerMatricesString() should include("Player")
      }

      "return the correct current player string" in {
        playerTable.getCurrenPlayerString() should include(s"Player ${currentPlayer + 1}")
      }

      "return the correct player string" in {
        playerTable.getPlayerString(0) should include("Player 1")
      }

      "return the correct table string" in {
        playerTable.getTableString() should include("Current card stack")
      }

      "get parity" in {
        val parity = playerTable.getParitys()
        parity shouldBe a[List[?]]
      }

      "open all cards" in {
        val newTable = playerTable.openAll()
        all(newTable.Tabletop.flatMap(_.rows.flatten)) should be(Symbol("opened"))
      }
    }
  }
}
