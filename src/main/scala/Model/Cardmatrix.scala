package Model

import Control.MultiPlayerGame.currentPlayer


case class Card(value: Int, open: Boolean = false)

class CardMatrix (matrice:List[List [Int]],players:Int,currPlayer:Int){
  def this(rows: Int, cols: Int,players:Int,currPlayer:Int)={
    this(List.fill(rows)(List.fill(cols)(99)),players,currPlayer)
  }
  
  val cardValues=List(5,10,15,10,10,10,10,10,10,10,10,10,10,10,10)
  val cardList=cardValues.zipWithIndex.map((value,ind)=>List.tabulate(value)(x=>Card(ind-2))).flatten
  print(cardList)
  def getRndmCards():List[Card]=
    scala.util.Random.shuffle(cardList)
  val cardMap=getRndmCards().zipWithIndex.map((value,prob)=>value->prob).toMap

  def getVals():(Int,Int,Int,Int)=(rows,cols,players,currPlayer)
  val matrix= matrice
  val rows=matrix.length
  val cols=matrix(0).length
}

