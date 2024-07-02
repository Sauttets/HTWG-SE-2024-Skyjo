import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.se.skyjo.model.modelComponent.modelImplementation.{Card, CardBuilder}

class CardBuilderSpec extends AnyWordSpec with Matchers {
  "A CardBuilder" when {
    "new" should {
      "create a card with default values" in {
        val card = CardBuilder().build()
        card.value should (be >= -2 and be <= 12)
        card.opened shouldBe false
      }
      
      "create a card with a specific value" in {
        val card = CardBuilder().value(5).build()
        card.value shouldBe 5
        card.opened shouldBe false
      }
      
      "throw an exception for an out of range value" in {
        an[IndexOutOfBoundsException] should be thrownBy CardBuilder().value(13).build()
        an[IndexOutOfBoundsException] should be thrownBy CardBuilder().value(-3).build()
      }
      
      "create a card with a specific open state" in {
        val card = CardBuilder().opened(true).build()
        card.opened shouldBe true
      }
      
      "create a card with a specific value and open state" in {
        val card = CardBuilder().value(3).opened(true).build()
        card.value shouldBe 3
        card.opened shouldBe true
      }
    }
  }
}
