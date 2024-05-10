package de.htwg.se.skyjo.model

case class PlayerTable(Tabletop: List[PlayerMatrix], cardstack: Cardstack, playerCount: Int, currentPlayer: Int):
    def this(playerCount: Int = 2, width: Int = 4, height: Int = 4) = {
        this(List.tabulate(playerCount) { _ =>
            new PlayerMatrix(width, height)
            }, new Cardstack(), playerCount, 0)
    }

    def padValue(card: Card): String = {
        if (card.opened) {
            if (card.value >= 0 && card.value < 10) {
                "│ 0" + card.value + " "
            } else {
                "│ " + card.value.toString + " "
            }

        } else {
            "│ xx "
        }
    }   
        
    def getCardStackString(): String = {
        val str =
        s"""\nCurrent card stack:
            |  ┌────┐  ┌────┐
            | ┌${padValue(cardstack.stackCard)}│  ${padValue(cardstack.trashCard)}│
            | │└───┬┘  └────┘
            | └────┘""".stripMargin
        str
    }


    def getPlayerMatricesString(): String = {
        val colors = Array("\u001B[31m", "\u001B[32m", "\u001B[35m", "\u001B[36m", "\u001B[33m")
        val str = Tabletop.zipWithIndex.map { case (playerMatrix, index) =>
            val color = colors(index % colors.length) 
            s"""${color}\nPlayer ${(index+1)}:
                |${"+----" * playerMatrix.rows.head.length}+
                |${playerMatrix.rows.map { row =>
                row.map { card =>
                    padValue(card)
                }.mkString("") + ("│\n" + "+----" * (row.length))
            }.mkString("+\n")}""".stripMargin
        }.mkString("+\n")
        str + "+\u001B[0m" // Reset color
    }

    def getCurrenPlayerString()=getPlayerString(currentPlayer)

    def getPlayerString(player:Int):String={
        val colors = Array("\u001B[31m", "\u001B[32m", "\u001B[35m", "\u001B[36m", "\u001B[33m")
        colors(player%colors.length)+"Player "+(player+1)+"\u001B[0m"
    }
    
    def getTableString(): String = {
        getPlayerMatricesString() + getCardStackString()
    }

    def drawFromStack() = {
        copy(Tabletop, cardstack = cardstack.drawFromStack(), playerCount, currentPlayer)
    }

    def getScores()={
        Tabletop.map(matrix=>matrix.getScore()).zipWithIndex
    }

    def updateMatrix(player: Int, playerMatrix: PlayerMatrix) = {
        copy(Tabletop.updated(player, playerMatrix), cardstack, playerCount, currentPlayer)
    }

    def updateCardstack(card: Card, drwawFromStack: Boolean) = {
        if drwawFromStack then copy(Tabletop, cardstack.discard(card).newStackCard(), playerCount, currentPlayer)
        else copy(Tabletop, cardstack.discard(card), playerCount, currentPlayer)
    }
