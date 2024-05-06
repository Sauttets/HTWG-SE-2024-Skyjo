package de.htwg.se.skyjo.model

case class Table(Tabletop: List[PlayerMatrix], cardstack: Cardstack, playerCount: Int, currentPlayer: Int):
    def this(playerCount: Int = 2, width: Int = 4, height: Int = 4) = {
        this(List.tabulate(playerCount) { _ =>
            new PlayerMatrix(width, height)
            }, new Cardstack(), playerCount, 0)
    }

    def padValue(cardValue: Int): String = cardValue match {
        case v if v >= 0 && v < 10 => "0" + v
        case v => v.toString
    }

    def getCardStackString(): String = {
        val str =
        s"""Current card stack:
            |  ┌────┐  ┌────┐
            | ┌┤ xx │  │ ${padValue(cardstack.trashCard.value)} │
            | │└───┬┘  └────┘
            | └────┘""".stripMargin
        str
    }


    def getPlayerMatricesString(): String = {
        val str = Tabletop.zipWithIndex.map { case (playerMatrix, index) =>
            s"""Player ${index + 1}:
                |${playerMatrix.rows.map { row =>
                row.map { card =>
                    if (card.opened) padValue(card.value) else "xx"
                }.mkString(" ")
            }.mkString("\n")}""".stripMargin
        }.mkString("\n")
        str
    }