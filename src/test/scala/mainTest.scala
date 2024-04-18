import org.scalatest.funsuite.AnyFunSuite
import CardMatrix.printMatrix

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

  test("padValue should pad single-digit numbers with a leading zero") {
    assert(CardMatrix.padValue(5) == "05")
  }

  test("padValue should return 'xx' for 99") {
    assert(CardMatrix.padValue(99) == "xx")
  }

  test("formatRow should format a row properly") {
    val row = List(1, 2, 3, 4)
    val formattedRow = CardMatrix.formatRow(row)
    assert(formattedRow == "│ 01 │ 02 │ 03 │ 04 │")
  }
  test("printMatrix should print corners edges"){
    val matrix = List(List(99,99),List(99,99))
    val stream = new java.io.ByteArrayOutputStream()
    Console.withOut(stream) {
      printMatrix(matrix)
    }
    val output=stream.toString()
    print(output)
    assert(output.startsWith("┌────┬────┐\n"))
    val line2=output.substring(12,24)
    assert(line2.startsWith("│ xx │ xx │\n"))
    val line3=output.substring(24,36)
    assert(line3.startsWith("├────┼────┤\n"))
    val line4=output.substring(36,48)
    assert(line4.startsWith("│ xx │ xx │\n"))
    val line5=output.substring(48,output.length()-1)
    assert(line5.startsWith("└────┴────┘"))
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

