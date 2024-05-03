import scala.util.Random
import scala.io.StdIn
import TUI.*

class Card(value: Int, open: Boolean = false) {
  def isOpen(): Boolean = open
}

object CardMatrix {
  def createRow(initialValue: Int, size: Int): List[Int] =
    List.fill(size)(initialValue)

  def createMatrix(initialValue: Int, rows: Int, cols: Int): List[List[Int]] =
    List.fill(rows)(createRow(initialValue, cols))
}

object MultiPlayerGame {
  import CardMatrix._

  def getRandomValue: Int = {
    val random = new Random
    random.nextInt(16) - 2
  }

  def playGame(player: Int, matrix: List[List[Int]]): List[List[Int]] = {
    println(s"\nPlayer $player's turn:")
    printMatrix(matrix)
    printCardStack(getRandomValue, getRandomValue)
    print("Enter Row: ")
    val row = StdIn.readInt() - 1
    print("Enter Column: ")
    val col = StdIn.readInt() - 1
    val rand = getRandomValue
    val updatedMatrix = matrix.updated(row, matrix(row).updated(col, rand))
    println(s"Card at row $row and column $col has value $rand")
    updatedMatrix
  }

  def main(args: Array[String]): Unit = {
    println("Enter number of rows:")
    val rows = StdIn.readInt()
    println("Enter number of columns:")
    val cols = StdIn.readInt()
    println("Enter number of players:")
    val playerCount = StdIn.readInt()

    val matrices = Array.fill(playerCount)(createMatrix(99, rows, cols))

    var currentPlayer = 1

    while (true) {
      val updatedMatrices = playGame(currentPlayer, matrices(currentPlayer - 1))
      matrices(currentPlayer - 1) = updatedMatrices
      currentPlayer = if (currentPlayer == playerCount) 1 else currentPlayer + 1
    }
  }
}