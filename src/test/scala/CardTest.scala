import de.htwg.se.skyjo._
import org.scalatest._
import org.scalatest.wordspec.AnyWordSpec
import de.htwg.se.skyjo.model.Card
import org.scalatest.matchers.should._

class CardTest extends AnyWordSpec with Matchers{ 
  "A Card" when{
    "when created should have initial state" should{
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
    "default" should{ 
      "not be open"in{
      Card.getClass.getMethod("$lessinit$greater$default$2").invoke(Card) shouldEqual false
      }
    }
  }
}
  // "A PlayerTable" when {
  //  "not set to any value" should {
  //    val table= new PlayerTable()
  //    "have currentplayer 0" in {
  //     table.currentPlayer should equal(0)
  //    }
  //    "have players greater 0" in{
  //     table.playerCount should be > 0
  //    }
  //    "have a cardstack" in {
  //     table.cardstack should not be null
  //    }
  //    "Each player should have a same size field" in{
  //     val top=table.Tabletop
  //     top.length == table.playerCount
  //     top.forall(x =>x.rows.length==top(0).rows.length && x.rows(0).length== top(0).rows(0).length)
  //    }
  //    "getCurrentPlayerString should include current player" in{
  //     table.getCurrenPlayerString() should include ((table.currentPlayer+1).toString())
  //     table.getCurrenPlayerString().toUpperCase() should include ("PLAYER")
  //    }
  //  }
  // }
// }

