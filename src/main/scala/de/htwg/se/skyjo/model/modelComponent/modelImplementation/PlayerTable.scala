package de.htwg.se.skyjo.model.modelComponent.modelImplementation
import de.htwg.se.skyjo.util.CardStackStrategy

import de.htwg.se.skyjo.model.modelComponent._

case class PlayerTable(Tabletop: List[PlayerMatrix], cardstack: CardStackStrategy, playerCount: Int, currentPlayer: Int) extends ModelInterface:
    def this(playerCount: Int = 2, width: Int = 4, height: Int = 4,lcard:Boolean=true) = {
        this(List.tabulate(playerCount) { _ =>
            new PlayerMatrix(height, width)
            }, new LCardStack(), playerCount, 0)
    }
    def setCardStackStrategy(strat:CardStackStrategy)= copy(Tabletop,strat,playerCount,currentPlayer)

    private def padValue(card: CardInterface): String = {
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
            | ┌${padValue(cardstack.getStackCard())}│  ${padValue(cardstack.getTrashCard())}│
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
        copy(Tabletop, cardstack = cardstack.openStackTop(), playerCount, currentPlayer)
    }

    def drawFromTrash() = {
        copy(Tabletop, cardstack = cardstack, playerCount, currentPlayer)
    }

    def getScores()={
        Tabletop.map(matrix=>matrix.getScore()).zipWithIndex
    }

    def swapCard(player: Int, row:Int,col:Int,card:CardInterface) = {
        val tupel=Tabletop(player).changeCard(row, col, card)
        (copy(Tabletop.updated(player, tupel(0)), cardstack, playerCount, currentPlayer),tupel(1))
    }

    def flipCard(player: Int, row:Int,col:Int)={
        copy(Tabletop.updated(player, Tabletop(player).flipCard(row,col)))
    }

    def updateCardstack(card: CardInterface, drwawFromStack: Boolean) = {
        if drwawFromStack then copy(Tabletop, cardstack.discard(card).removeStackTop(), playerCount, currentPlayer)
        else copy(Tabletop, cardstack.removeTrashTop().discard(card), playerCount, currentPlayer)
    }

    def nextPlayer()={
        copy(Tabletop,cardstack,playerCount,(currentPlayer+1)%playerCount)
    }
    def getStackCard()=cardstack.getStackCard()
    def getTrashCard()=cardstack.getTrashCard()

    def gameEnd()=Tabletop.exists(_.checkFinished())

    def reset()=new PlayerTable(playerCount,Tabletop(0).rows(0).size,Tabletop(0).rows.size,true)