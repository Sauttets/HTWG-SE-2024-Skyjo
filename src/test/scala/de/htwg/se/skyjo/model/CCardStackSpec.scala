import org.scalatest.wordspec.AnyWordSpec
import de.htwg.se.skyjo.model._
import de.htwg.se.skyjo.model.CCardStack
import org.scalatest.matchers.should._

class CCardStackSpec extends AnyWordSpec with Matchers{ 
 "A CCardStack" when{
    "created should have initial state" should{
      "without parameters" in{
        val stack=new CCardStack
        stack.getStackCard() should not be null
        stack.getStackCard().opened shouldBe false
      }
      "with Cards" in{
        val (a,b)=(CardBuilder().build(),CardBuilder().build())
        val stack=new CCardStack(a,b)
        stack.getStackCard() should equal (a)
        stack.getTrashCard() should equal (b)
      }
    }
    "drawn from Stack" should{
      val stack=new CCardStack
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
      val stack=new CCardStack
      val sCard=stack.getStackCard()
      val tCard=stack.getTrashCard()
      "give Trashcard" in{
        val (tCard,cardStack)=(stack.getTrashCard(),stack.drawFromTrash())
        stack.getTrashCard() shouldEqual tCard
        cardStack.getTrashCard() should not be (stack.getTrashCard())
      }
    }
    "discarding" should{
      val stack=new CCardStack
      val sCard=stack.getStackCard()
      "discard card" in{
        val dCard=CardBuilder().build()
        val dStack=stack.discard(dCard)
        dStack.getTrashCard().value shouldEqual dCard.value
        dStack.getTrashCard().opened shouldBe true
        dStack.getStackCard() shouldEqual sCard
      }
    }
  }
}