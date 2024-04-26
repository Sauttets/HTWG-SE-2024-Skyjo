import scala.util.Random
import scala.io.StdIn
import TUI.*
import scala.collection.immutable.HashMap

case class Card(value: Int, open: Boolean = false)

object CardMatrix {
  val cardValues=List(5,10,15,10,10,10,10,10,10,10,10,10,10,10,10)
  val cardList=cardValues.zipWithIndex.map((value,ind)=>List.tabulate(value-2)(x=>Card(ind))).flatten

  def getRndmCards():List[Card]=
    scala.util.Random.shuffle(cardList)
  val cardMap=getRndmCards().zipWithIndex.map((value,prob)=>value->prob).toMap
  def createRow(initialValue:Int,size: Int): List[Int] ={
    List.fill(size)(initialValue)
  }

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
    val (rows,cols,playerCount)=TUI.gameInitVals()

    val matrices = Array.fill(playerCount)(createMatrix(99, rows, cols))

    var currentPlayer = 1

    while (true) {
      val updatedMatrices = playGame(currentPlayer, matrices(currentPlayer - 1))
      matrices(currentPlayer - 1) = updatedMatrices
      currentPlayer = if (currentPlayer == playerCount) 1 else currentPlayer + 1
    }
  }
}