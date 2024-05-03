package de.htwg.se.skyjo.model


case class PlayerMatrix(rows: Vector[Vector[Card]]):
  def this(height: Int, widht: Int) = {
    this(Vector.tabulate(height, widht) {
        (row, col) => Card()
      }
    )
  } 
  val size: Int = rows.size
  def getCard(row: Int, col: Int): Card = rows(row)(col)
  def getRow(row: Int): Vector[Card] = rows(row)  

  def flipCard (row: Int, col: Int): Matrix[Card] = {
    //val selectedCard = getCard(row, col)
    val newCard = getCard(row, col).openCard()
    copy(rows.updated(row, selectedRow.updated(col, newCard)))
  }

  //def flipCard (row: Int, col: Int): Matrix[Card] = copy(rows.updated(row, selectedRow.updated(col, getCard(row, col).copy(opened = true))))

  def changeCard(row: Int, col: Int, card: Card): Matrix[Card] = copy(rows.updated(row, rows(row).updated(col, card)))
