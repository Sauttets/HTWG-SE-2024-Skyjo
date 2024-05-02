import org.scalatest.funsuite.AnyFunSuite
import View.TUI._
import java.util.Random

class TUITests extends AnyFunSuite{
  test("padValue should pad single-digit numbers with a leading zero") {
    assert(padValue(5) == "05")
  }
  test("padValue should return 'xx' for 99") {
    assert(padValue(99) == "xx")
  }
  test("formatRow should format a row properly") {
    val row = List(1, 2, 3, 4)
    val formattedRow = formatRow(row)
    assert(formattedRow == "│ 01 │ 02 │ 03 │ 04 │")
  }
  test("printCurrentPlayer should print thegiven player with some text"){
    val random = new Random
    val player=random.nextInt(100)
    val stream = new java.io.ByteArrayOutputStream()
    Console.withOut(stream) {
      printCurrentPlayer(player)
    }
    val output=stream.toString()
    assert(output.split('\n').size==2)
    assert(output==s"\nPlayer $player's turn:\n")
  }
  test("printMatrix should print corners edges"){
    val matrix = List(List(99,99),List(99,99))
    val stream = new java.io.ByteArrayOutputStream()
    Console.withOut(stream) {
      printMatrix(matrix)
    }
    val output=stream.toString().split('\n')
    assert(output(0)=="┌────┬────┐")
    assert(output(1)=="│ xx │ xx │")
    assert(output(2)=="├────┼────┤")
    assert(output(3)=="│ xx │ xx │")
    assert(output(4)=="└────┴────┘")
    assert(output.length==5)
  }
  test("printCardstack should have a certain format"){
    val stream = new java.io.ByteArrayOutputStream()
    Console.withOut(stream) {
      printCardStack()
    }
    val output=stream.toString().split('\n')
    assert(output(0)=="Current card stack:")
    assert(output(1)=="  ┌────┐  ┌────┐")
    assert(output(2)==" ┌┤ xx │  │ xx │")
    assert(output(3)==" │└───┬┘  └────┘")
    assert(output(4)==" └────┘")
    assert(output.length==5)
  }
}
