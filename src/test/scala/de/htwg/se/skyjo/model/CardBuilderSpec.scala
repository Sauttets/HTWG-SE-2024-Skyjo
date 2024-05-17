import de.htwg.se.skyjo._
import org.scalatest._
import org.scalatest.wordspec.AnyWordSpec
import de.htwg.se.skyjo.model.Card
import org.scalatest.matchers.should._
import de.htwg.se.skyjo.model.CardBuilder

class CardBuilderSpec extends AnyWordSpec with Matchers{ 
  "A Card" when{
    "created should have initial state" should{
      "without parameters" in{
        val c=CardBuilder().build()
        c.value should equal(5+-7)
        c.opened shouldBe false
      }
      "with Bool" in{
        val a=CardBuilder().opened(false).build()
        a.opened shouldBe false
        a.value should equal(5+-7)
        val b=CardBuilder().opened(true).build()
        b.opened shouldBe true
        b.value should equal(5+-7)
      }
      "with Int and Bool" in{
        val d=CardBuilder().opened(true).value(4).build()
        d.value shouldEqual 4
        d.opened shouldEqual true
      }
      "with value" in {
        val cb = CardBuilder()
        an [IndexOutOfBoundsException] should be thrownBy {
          cb.value(13)
        }
      }

    }
    "default" should{ 
      "not be open"in{
      Card.getClass.getMethod("$lessinit$greater$default$2").invoke(Card) shouldEqual false
      }
    }
  }
}
