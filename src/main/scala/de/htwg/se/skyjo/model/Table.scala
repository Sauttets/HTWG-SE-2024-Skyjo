package de.htwg.se.skyjo.model

case class Table(Tabletop: List[PlayerMatrix], cardstack: Cardstack, playerCount: Int, currentPlayer: Int):
    def this(playerCount: Int = 2, width: Int = 4, height: Int = 4) = {
        this(List.tabulate(playerCount) {PlayerMatrix(width, height)}, Cardstack(), playerCount, 0)
    } 

    def padValue(cardValue: Int): String =
        if (cardValue >= 0 && cardValue < 10) "0" + cardValue
        else cardValue.toString

    def getCardStackString(): String = {
        val str = "Current card stack:\n" +
        "  ┌────┐  ┌────┐\n" +
        s" ┌┤ xx │  │ ${padValue(cardstack.trashCard.value)} │\n" +
        " │└───┬┘  └────┘\n" +
        " └────┘\n"
        str
    }
