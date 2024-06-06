package de.htwg.se.skyjo
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should._
import aview._
import controller._
import model._

class TuiSpec extends AnyWordSpec with Matchers {
  "A TUI" when{
    var table = new PlayerTable()
    var tableController = new TableController(table)
    var tui = new TUI(tableController)
    "update called" should{
      "print tableString"in{
        val stream = new java.io.ByteArrayOutputStream()
        Console.withOut(stream) {
          tui.update
        }
        stream.toString() shouldBe tableController.table.getTableString()+"\n"
      }
      "print on notified" in{
        val stream = new java.io.ByteArrayOutputStream()
        Console.withOut(stream) {
          tableController.notifyObservers
        }
        stream.toString() shouldBe tableController.table.getTableString()+"\n"
      }
      "not print once removed" in{
        val stream = new java.io.ByteArrayOutputStream()
        Console.withOut(stream) {
          tableController.remove(tui)
          tableController.notifyObservers
          tableController.add(tui)
        }
        stream.toString() shouldBe ""
      }
    }
    "moveInput called" should{
      "return true or false accordingly" in{
        tui.moveInput(" s 1  2",true).contains(Move(true,true,1,2)) shouldBe true
        tui.moveInput("SwAp 1  2 egoeog",false).contains(Move(false,true,1,2)) shouldBe true
        tui.moveInput(" S 1 2",true).contains(Move(true,true,1,2)) shouldBe true
        tui.moveInput(" d 1 2  ",true).contains(Move(true,false,1,2)) shouldBe true
        tui.moveInput("diSCaRd 1 2",true).contains(Move(true,false,1,2)) shouldBe true
        tui.moveInput(" T 1 2",false).contains(Move(false,false,1,2)) shouldBe true
      }
    } 
  }
}