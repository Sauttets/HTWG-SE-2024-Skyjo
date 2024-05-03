package View

import Util._
import scala.io.StdIn
import Control._

object TUI extends Observer{

    MultiPlayerGame.add(this)
    override def update: Unit ={
        val (r,c,p,cp)=MultiPlayerGame.getVals()
        printCurrentPlayer(cp)
        printMatrix(MultiPlayerGame.getMatrix(MultiPlayerGame.currentPlayer))
        printCardStack(MultiPlayerGame.getRandomValue, MultiPlayerGame.getRandomValue)
    }

    def gameInitVals():(Int,Int,Int)={
        println("Enter number of rows:")
        val rows = StdIn.readInt()
        println("Enter number of columns:")
        val cols = StdIn.readInt()
        println("Enter number of players:")
        val playerCount = StdIn.readInt()
        MultiPlayerGame.setVals(rows,cols,playerCount)
        (rows,cols,playerCount)
    }
    def padValue(value: Int): String =
        if (value >= 0 && value < 10) "0" + value
        else if (value == 99) "xx"
        else value.toString

    def formatRow(row: List[Int]): String = {
        val formattedRow = row.map { elem => padValue(elem)}
        "│ " + formattedRow.mkString(" │ ") + " │"
    }
    def printCurrentPlayer(player: Int): Unit=
        println(s"\nPlayer $player's turn:")
    
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
    def cardPicker():(Int,Int)={
        print("Enter Row: ")
        val row = StdIn.readInt() - 1
        print("Enter Column: ")
        val col = StdIn.readInt() - 1
        (row,col)
    }
    def printCardValue(row:Int,col:Int,rand:Int):Unit=
        println(s"Card at row $row and column $col has value $rand")
    
    def main(args: Array[String]): Unit = {
    val (rows,cols,playerCount)=TUI.gameInitVals()

    val (r,c,players,cp)=MultiPlayerGame.getVals()
    val matrices = Array.fill(players)(MultiPlayerGame.getMatrix(0))

    var currentPlayer = 1

    while (true) {
      MultiPlayerGame.playGame(currentPlayer-1, matrices(currentPlayer-1))
      val (row ,col)=TUI.cardPicker()
      val card=MultiPlayerGame.getRandomValue
      TUI.printCardValue(row,col,card)
      val updatedMatrices = MultiPlayerGame.updatedMatrix(currentPlayer,matrices(currentPlayer-1),row,col,card)
      matrices(currentPlayer - 1) = updatedMatrices
      currentPlayer =(currentPlayer+1)%players+1
    }
  }
}