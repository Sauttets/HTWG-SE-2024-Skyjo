import scala.io.StdIn.readLine
import scala.util.Random

object CardMatrix {

  val rows = 3
  val cols = 4
  val playerCount = 2
  val leaderboard = Array.fill[Int](playerCount)(0)
  val initialValue = 99

  def createRow(initialValue: Int, size: Int): List[Int] =
    List.fill(size)(initialValue)

  def createMatrix(initialValue: Int, rows: Int, cols: Int): List[List[Int]] =
    List.fill(rows)(createRow(initialValue, cols))

  def padValue(value: Int): String =
    if (value >= 0 && value < 10) "0" + value
    else if (value == 99) "xx"
    else value.toString


  def formatRow(row: List[Int]): String = {
    val formattedRow = row.map { elem => padValue(elem)}
    "│ " + formattedRow.mkString(" │ ") + " │"
  }

  def manipulateMatrix(matrix: List[List[Int]], rowIndex: Int, colIndex: Int, newValue: Int): List[List[Int]] =
    matrix.zipWithIndex.map { case (row, r) =>
      row.zipWithIndex.map { case (cell, c) =>
        if (r == rowIndex && c == colIndex) newValue
        else cell
      }
    }

  def printMatrix(matrix: List[List[Int]]): Unit = {
    println("┌" + "────┬" * (matrix.head.length - 1) + "────┐")
    matrix.zipWithIndex.foreach { case (row, index) =>
      if (index == matrix.length - 1) {
        println(formatRow(row))
        println("└" + "────┴" * (matrix.head.length - 1) + "────┘")
      } else {
        println(formatRow(row))
        println("├" + "────┼" * (matrix.head.length - 1) + "────┤")
      }
    }
  }


  def printCardStack(stack:Int = 99, thrown:Int = 99): Unit = {
      println("Current card stack:")
      println("  ┌────┐  ┌────┐" )
      println(s" ┌┤ ${padValue(stack)} │  │ ${padValue(thrown)} │")
      println(" │└───┬┘  └────┘")
      println(" └────┘")
  }

}
object MultiPlayerGame {
  import CardMatrix._

  //function to create random value between -2 and 13
  def getRandomValue: Int = {
    val random = new Random
    random.nextInt(16) - 2  
  }

  def playGame(player: Int, matrix: List[List[Int]]): List[List[Int]] = {
    println(s"\nPlayer $player's turn:")
    printMatrix(matrix)
    printCardStack(getRandomValue, getRandomValue)
    println("Enter row and column (separated by a space):")
    // read from terminal
    val input = readLine()
    val inputValues = input.split(" ").map(_.toInt)
    if (inputValues.length == 2) {
      val Array(row, col) = inputValues
      if (row >= 0 && row < rows && col >= 0 && col < cols) {
        // fill selected filed with random value 
        val value = getRandomValue
        val manipulatedMatrix = manipulateMatrix(matrix, row, col, value)
        println(s"\nPlayer $player's move:")
        printMatrix(manipulatedMatrix)
        manipulatedMatrix
      } else {
        println("Invalid input. Row and column must be within the matrix dimensions.")
        matrix
      }
    } else {
      println("Invalid input. Please enter row and column.")
      matrix
    }
  }

  @main def Main(): Unit = {
    val matrices = Array.fill(playerCount)(createMatrix(initialValue, rows, cols))

    // Endlessly continue the game
    var currentMatrices = matrices
    var currentPlayer = 1
    while (true) {
      currentMatrices(currentPlayer - 1) = playGame(currentPlayer, currentMatrices(currentPlayer - 1))
      currentPlayer = if (currentPlayer < playerCount) currentPlayer + 1 else 1
    }
  }
}
