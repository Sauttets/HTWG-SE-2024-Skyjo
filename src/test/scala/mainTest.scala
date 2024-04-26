import org.scalatest.funsuite.AnyFunSuite

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

class MultiPlayerGameTest extends AnyFunSuite {

  test("getRandomValue should return a value between -2 and 13") {
    val randomValue = MultiPlayerGame.getRandomValue
    assert(randomValue >= -2 && randomValue <= 13)
  }

  // test("playGame should not modify the matrix") {
  //   val initialMatrix = List(
  //     List(1, 2, 3, 4),
  //     List(5, 6, 7, 8),
  //     List(9, 10, 11, 12)
  //   )
  //   val newMatrix = MultiPlayerGame.playGame(1,initialMatrix)
  //   assert(initialMatrix == newMatrix)
  // }
}

