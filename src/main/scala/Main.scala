import scala.util.Random
import TUI.*
class Card(value:Int,open:Boolean=false){
  def isOpen():Boolean=open
}
object CardMatrix {

  val rows = 3
  val cols = 4
  val playerCount = 2
  val leaderboard = Array.fill[Int](playerCount)(0)
  val initialValue = 99
  val cardValues=List(5,10,15,10,10,10,10,10,10,10,10,10,10,10,10)
  val cardList=cardValues.zipWithIndex.map((value,ind)=>List.tabulate(value)(x=>Card(ind))).flatten
  
  def getRndmCards():List[Card]=
    scala.util.Random.shuffle(cardList)

  def createRow(initialValue: Int, size: Int): List[Int] =
    List.fill(size)(initialValue)

  def createMatrix(initialValue: Int, rows: Int, cols: Int): List[List[Int]] =
    List.fill(rows)(createRow(initialValue, cols))

}

object MultiPlayerGame {
  import CardMatrix._

  // Function to create random value between -2 and 13
  def getRandomValue: Int = {
    val random = new Random
    random.nextInt(16) - 2  
  }

  def playGame(player: Int, matrix: List[List[Int]]): List[List[Int]] = {
    println(s"\nPlayer $player's turn:")
    printMatrix(matrix)
    printCardStack(getRandomValue, getRandomValue)
    matrix // Return the matrix unchanged
  }

  @main def Main(): Unit = {
    val matrices = Array.fill(playerCount)(createMatrix(initialValue, rows, cols))

    matrices.indices.foreach { i =>
      println(s"\nPlayer ${i + 1}'s turn:")
      printMatrix(matrices(i))
      printCardStack(99, 99)
    }
  }
}
