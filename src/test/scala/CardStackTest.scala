import org.scalatest.wordspec.AnyWordSpec
import de.htwg.se.skyjo.model.Card
import de.htwg.se.skyjo.model.Cardstack
import org.scalatest.matchers.should._

class CardStackTest extends AnyWordSpec with Matchers{ 
 "A CardStack" when{
    "created should have initial state" should{
      "without parameters" in{
        val stack=new Cardstack
        stack.getStackCard() should not be null
        stack.getStackCard().opened shouldBe false
      }
      "with Cards" in{
        val (a,b)=(new Card,new Card)
        val stack=new Cardstack(a,b)
        stack.getStackCard() should equal (a)
        stack.getTrashCard() should equal (b)
      }
    }
    "drawn from Stack" should{
      val stack=new Cardstack
      val sCard=stack.getStackCard()
      val tCard=stack.getTrashCard()
      "flip stackcard" in{
        val drawnStack=stack.drawFromStack()
        drawnStack.getTrashCard() shouldEqual tCard
        drawnStack.getStackCard().value shouldBe sCard.value
        drawnStack.getStackCard().opened shouldBe true
      }
    }
    "drawn from trash" should{
      val stack=new Cardstack
      val sCard=stack.getStackCard()
      val tCard=stack.getTrashCard()
      "give Trashcard" in{
        val (tCard,cardStack)=stack.drawFromTrash()
        stack.getTrashCard() shouldEqual tCard
        cardStack.getTrashCard() should not equal (stack.getTrashCard())
      }
    }
    "discarding" should{
      val stack=new Cardstack
      val sCard=stack.getStackCard()
      "discard card" in{
        val dCard=new Card
        val dStack=stack.discard(dCard)
        dStack.getTrashCard().value shouldEqual dCard.value
        dStack.getTrashCard().opened shouldBe true
        dStack.getStackCard() shouldEqual sCard
      }
    }
  }
}