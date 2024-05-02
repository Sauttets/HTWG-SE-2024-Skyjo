package Model

import scala.util.Random
import scala.io.StdIn
import View.TUI.*
import scala.collection.immutable.HashMap

object MultiPlayerGame {
  import CardMatrix._

  def getRandomValue: Int = {
    val random = new Random
    random.nextInt(16) - 2
  }

  def playGame(player: Int, matrix: List[List[Int]]): List[List[Int]] = {
    printCurrentPlayer(player)
    printMatrix(matrix)
    printCardStack(getRandomValue, getRandomValue)
    val (row ,col)=cardPicker()
    val rand = getRandomValue
    val updatedMatrix = matrix.updated(row, matrix(row).updated(col,rand))
    printCardValue(row,col,rand)
    updatedMatrix
  }

  def main(args: Array[String]): Unit = {
    val (rows,cols,playerCount)=gameInitVals()

    val matrices = Array.fill(playerCount)(createMatrix(99, rows, cols))

    var currentPlayer = 1

    while (true) {
      val updatedMatrices = playGame(currentPlayer, matrices(currentPlayer - 1))
      matrices(currentPlayer - 1) = updatedMatrices
      currentPlayer = if (currentPlayer == playerCount) 1 else currentPlayer + 1
    }
  }
}