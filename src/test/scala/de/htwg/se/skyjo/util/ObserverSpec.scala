import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.se.skyjo.util.{Observer, Observable}

class TestObserver extends Observer {
  var updated = false
  override def update: Unit = updated = true
}

class ObservableSpec extends AnyWordSpec with Matchers {
  "An Observable" when {
    "notifying observers" should {
      val observable = new Observable {}
      val observer = new TestObserver
      observable.add(observer)

      "notify all subscribed observers" in {
        observable.notifyObservers
        observer.updated shouldBe true
      }

      "allow observers to unsubscribe" in {
        observer.updated = false
        observable.remove(observer)
        observable.notifyObservers
        observer.updated shouldBe false
      }
    }
  }
}
