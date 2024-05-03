object TUI {
    def padValue(value: Int): String =
        if (value >= 0 && value < 10) "0" + value
        else if (value == 99) "xx"
        else value.toString

    def formatRow(row: List[Int]): String = {
        val formattedRow = row.map { elem => padValue(elem)}
        "│ " + formattedRow.mkString(" │ ") + " │"
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
