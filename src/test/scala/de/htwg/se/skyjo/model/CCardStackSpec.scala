import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.se.skyjo.model.modelComponent.modelImplementation.{Card, CardBuilder, CCardStack}
import de.htwg.se.skyjo.model.modelComponent.CardInterface

class CCardStackSpec extends AnyWordSpec with Matchers {
  "A CCardStack" when {
    "new" should {
      val stackCard = CardBuilder().build()
      val trashCard = CardBuilder().build()
      val stack = CCardStack(stackCard, trashCard)

      "have a stack card" in {
        stack.getStackCard() shouldBe a[CardInterface]
      }

      "have a trash card" in {
        stack.getTrashCard() shouldBe a[CardInterface]
      }

      "open the stack top card" in {
        val openedStack = stack.openStackTop()
        openedStack.getStackCard().opened shouldBe true
      }

      "close the stack top card" in {
        val closedStack = stack.closeStackTop()
        closedStack.getStackCard().opened shouldBe false
      }


      "discard a card to the trash" in {
        val card = CardBuilder().value(5).opened(true).build()
        val discardedStack = stack.discard(card)
        discardedStack.getTrashCard() shouldBe card
      }

    }
  }
}
