import org.scalatest.wordspec.AnyWordSpec
import de.htwg.se.skyjo.model._
import de.htwg.se.skyjo.model.LCardStack
import org.scalatest.matchers.should._

class LCardStackSpec extends AnyWordSpec with Matchers{ 
 "A LCardstack" when{
    "created should have initial state" should{
      "without parameters" in{
        val stack=new LCardStack()
        stack.getStackCard() should not be null
        stack.getStackCard().opened shouldBe false
      }
      "with Lists" in{
        val (a,b)=(List(CardBuilder().build(),CardBuilder().build()),List(CardBuilder().build(),CardBuilder().build()))
        val stack=new LCardStack(a,b)
        stack.getStackCard() should equal (a(0))
        stack.getTrashCard() should equal (b.last)
      }
    }
    "drawn from Stack" should{
      val stack=new LCardStack()
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
      val stack=new LCardStack().discard(CardBuilder().build())
      val sCard=stack.getStackCard()
      val tCard=stack.getTrashCard()
      "give Trashcard" in{
        val (tCard,cardStack)=(stack.getTrashCard(),stack.drawFromTrash())
        stack.getTrashCard() shouldEqual tCard
        cardStack.getTrashCard() should not be (stack.getTrashCard())
      }
    }
    "discarding" should{
      val stack=new LCardStack
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