package View
import scala.io.StdIn

object TUI {
    def gameInitVals():(Int,Int,Int)={
        println("Enter number of rows:")
        val rows = StdIn.readInt()
        println("Enter number of columns:")
        val cols = StdIn.readInt()
        println("Enter number of players:")
        val playerCount = StdIn.readInt()
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
}
