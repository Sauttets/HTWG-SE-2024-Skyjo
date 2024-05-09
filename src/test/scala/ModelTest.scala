import de.htwg.se.skyjo._
import org.scalatest._
import org.scalatest.wordspec.AnyWordSpec
import de.htwg.se.skyjo.controller.TableController
import de.htwg.se.skyjo.model._
import org.scalatest.matchers.should._

class ModelSpec extends AnyWordSpec with Matchers{ 
  "A Card" when{
    "initializing" should{
      "without parameters" in{
        val c=new Card
        c.value should equal(5+-7)
        c.opened shouldBe false
      }
      "with Bool" in{
        val a=new Card(false)
        a.opened shouldBe false
        a.value should equal(5+-7)
        val b=new Card(true)
        b.opened shouldBe true
        b.value should equal(5+-7)
      }
      "with Int and Bool" in{
        val d=Card(4,true)
        d.value shouldEqual 4
        d.opened shouldEqual true
      }
    }
  }
  "A CardStack" when{
    "initializing" should{
      "without parameters" in{
        val stack=new Cardstack
        stack.stackCard should not be null
        stack.trashCard should not be null
      }
      "with Cards" in{
        val (a,b)=(new Card,new Card)
        val stack=new Cardstack(a,b)
        stack.stackCard should equal (a)
        stack.trashCard should equal (b)
      }
    }
    "initialized" should{
      val stack=new Cardstack
      val sCard=stack.stackCard
      val tCard=stack.trashCard
      "draw from stack" in{
        sCard.opened shouldBe false
        stack.drawFromStack()
        stack.trashCard shouldBe tCard
        stack.stackCard.value shouldBe sCard.value
        stack.stackCard.opened shouldBe sCard.opened
      }
      "discard" in{
        val dCard=new Card
        val dStack=stack.discard(dCard)
        dStack.trashCard.value shouldBe dCard.value
        dStack.trashCard.opened shouldBe true
      }
      "drawfromTrash" in{
        val (tdCard,cardStack)=stack.drawFromTrash()
        stack.trashCard shouldBe tdCard
        cardStack.trashCard should not be (stack.trashCard)
      }
      "newStackCard" in{
        val nsCard=stack.newStackCard().stackCard
        nsCard should not be stack.stackCard
      }
    }
  }
  "A PlayerTable" when {
   "not set to any value" should {
     val table= new PlayerTable()
     "have currentplayer 0" in {
      table.currentPlayer should equal(0)
     }
     "have players greater 0" in{
      table.playerCount should be > 0
     }
     "have a cardstack" in {
      table.cardstack should not be null
     }
     "Each player should have a same size field" in{
      val top=table.Tabletop
      top.length == table.playerCount
      top.forall(x =>x.rows.length==top(0).rows.length && x.rows(0).length== top(0).rows(0).length)
     }
     "getCurrentPlayerString should include current player" in{
      table.getCurrenPlayerString() should include ((table.currentPlayer+1).toString())
      table.getCurrenPlayerString().toUpperCase() should include ("PLAYER")
     }
   }
  }
}

