import org.scalatest.wordspec.AnyWordSpec
import de.htwg.se.skyjo.model.Card
import de.htwg.se.skyjo.model.Cardstack
import org.scalatest.matchers.should._

class CardStackTest extends AnyWordSpec with Matchers{ 
 "A CardStack" when{
    "created should have initial state" should{
      "without parameters" in{
        val stack=new Cardstack
        stack.stackCard should not be null
        stack.stackCard.opened shouldBe false
      }
      "with Cards" in{
        val (a,b)=(new Card,new Card)
        val stack=new Cardstack(a,b)
        stack.stackCard should equal (a)
        stack.trashCard should equal (b)
      }
    }
    "drawn from Stack" should{
      val stack=new Cardstack
      val sCard=stack.stackCard
      val tCard=stack.trashCard
      "flip stackcard" in{
        val drawnStack=stack.drawFromStack()
        drawnStack.trashCard shouldEqual tCard
        drawnStack.stackCard.value shouldBe sCard.value
        drawnStack.stackCard.opened shouldBe true
      }
    }
    "drawn from trash" should{
      val stack=new Cardstack
      val sCard=stack.stackCard
      val tCard=stack.trashCard
      "give Trashcard" in{
        val (tCard,cardStack)=stack.drawFromTrash()
        stack.trashCard shouldEqual tCard
        cardStack.trashCard should not equal (stack.trashCard)
      }
    }
    "discarding" should{
      val stack=new Cardstack
      val sCard=stack.stackCard
      "discard card" in{
        val dCard=new Card
        val dStack=stack.discard(dCard)
        dStack.trashCard.value shouldEqual dCard.value
        dStack.trashCard.opened shouldBe true
        dStack.stackCard shouldEqual sCard
      }
    }
  }
}