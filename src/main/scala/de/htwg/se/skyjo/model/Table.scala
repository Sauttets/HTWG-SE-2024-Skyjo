package de.htwg.se.skyjo.model

case class Table(Tabletop: List[PlayerMatrix], cardstack: Cardstack, playerCount: Int, currentPlayer: Int):
    def this(playerCount: Int = 2, width: Int = 4, height: Int = 4) = {
        this(List.tabulate(playerCount) { _ =>
            new PlayerMatrix(width, height)
            }, new Cardstack(), playerCount, 0)
    }

    def padValue(cardValue: Int): String = cardValue match {
        case v if v == 99 => "│ xx "
        case v if v >= 0 && v < 10 => "│ 0" + v + " "
        case v => "│ " + v.toString + " "
    }

    def getCardStackString(): String = {
        val str =
        s"""\nCurrent card stack:
            |  ┌────┐  ┌────┐
            | ┌┤ xx │  ${padValue(cardstack.trashCard.value)}│
            | │└───┬┘  └────┘
            | └────┘""".stripMargin
        str
    }


    def getPlayerMatricesString(): String = {
        val colors = Array("\u001B[31m", "\u001B[32m", "\u001B[35m", "\u001B[36m", "\u001B[33m")
        val str = Tabletop.zipWithIndex.map { case (playerMatrix, index) =>
            val color = colors(index % colors.length) 
            s"""${color}\nPlayer ${index}:
                |${"+----" * playerMatrix.rows.head.length}+
                |${playerMatrix.rows.map { row =>
                row.map { card =>
                    if (card.opened) padValue(card.value) else padValue(99)
                }.mkString("") + ("│\n" + "+----" * (row.length))
            }.mkString("+\n")}""".stripMargin
        }.mkString("+\n")
        str + "+\u001B[0m" // Reset color
    }