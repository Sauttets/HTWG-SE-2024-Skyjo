import org.scalatest.funsuite.AnyFunSuite
import TUI._

class CardMatrixTest extends AnyFunSuite {

  test("createRow should create a row with the specified initial value") {
    val row = CardMatrix.createRow(99, 4)
    assert(row.length == 4)
    assert(row.forall(_ == 99))
  }

  test("createMatrix should create a matrix with the specified initial value") {
    val matrix = CardMatrix.createMatrix(99, 3, 4)
    assert(matrix.length == 3)
    assert(matrix.forall(_.length == 4))
    assert(matrix.flatten.forall(_ == 99))
  }
}
class TUITests extends AnyFunSuite{
  test("padValue should pad single-digit numbers with a leading zero") {
    assert(TUI.padValue(5) == "05")
  }

  test("padValue should return 'xx' for 99") {
    assert(TUI.padValue(99) == "xx")
  }
  test("formatRow should format a row properly") {
    val row = List(1, 2, 3, 4)
    val formattedRow = TUI.formatRow(row)
    assert(formattedRow == "│ 01 │ 02 │ 03 │ 04 │")
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

class MultiPlayerGameTest extends AnyFunSuite {

  test("getRandomValue should return a value between -2 and 13") {
    val randomValue = MultiPlayerGame.getRandomValue
    assert(randomValue >= -2 && randomValue <= 13)
  }

  test("playGame should not modify the matrix") {
    val initialMatrix = List(
      List(1, 2, 3, 4),
      List(5, 6, 7, 8),
      List(9, 10, 11, 12)
    )
    val newMatrix = MultiPlayerGame.playGame(1, initialMatrix)
    assert(initialMatrix == newMatrix)
  }
}

