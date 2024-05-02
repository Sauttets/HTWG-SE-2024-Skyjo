package Model

case class Card(value: Int, open: Boolean = false)

object CardMatrix {
  val cardValues=List(5,10,15,10,10,10,10,10,10,10,10,10,10,10,10)
  val cardList=cardValues.zipWithIndex.map((value,ind)=>List.tabulate(value-2)(x=>Card(ind))).flatten

  def getRndmCards():List[Card]=
    scala.util.Random.shuffle(cardList)
  val cardMap=getRndmCards().zipWithIndex.map((value,prob)=>value->prob).toMap
  def createRow(initialValue:Int,size: Int): List[Int] ={
    List.fill(size)(initialValue)
  }

  def createMatrix(initialValue: Int, rows: Int, cols: Int): List[List[Int]] =
    List.fill(rows)(createRow(initialValue, cols))
}

