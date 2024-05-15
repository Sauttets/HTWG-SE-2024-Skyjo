package de.htwg.se.skyjo.model


case class PlayerMatrix(rows: Vector[Vector[Card]]):
  def this(height: Int, width: Int) = {
    this({
      val matrix = Array.ofDim[Card](height, width)
      for (i <- 0 until height) {
        for (j <- 0 until width) {
          matrix(i)(j) = CardBuilder().build()
        }
      }
      matrix.map(_.toVector).toVector
    })
  }
  val size: Int = rows.size
  def getCard(row: Int, col: Int): Card = rows(row)(col)
  def getRow(row: Int): Vector[Card] = rows(row)  

  def flipCard (row: Int, col: Int): PlayerMatrix = {
    //val selectedCard = getCard(row, col)
    val newCard = CardBuilder().value(getCard(row, col).value).opened(true).build()
    copy(rows.updated(row, rows(row).updated(col, newCard)))
  }


  def changeCard(row: Int, col: Int, card: Card): (PlayerMatrix, Card) = {
    val oldCard = getCard(row, col)
    (copy(rows.updated(row, rows(row).updated(col, card))), oldCard)
  }

  def checkFinished()={
    rows.forall(_.forall(_.opened))
  }
  
  def getScore()={
    rows.map(row=>row.map(card=>card.value).sum).sum
  }
