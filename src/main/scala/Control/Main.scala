package Control

import Util._
import Model._
import scala.util.Random
import scala.io.StdIn
import scala.collection.immutable.HashMap

object MultiPlayerGame extends Observable{
  var currentPlayer=0
  var mat:Vector[CardMatrix]=Vector()
  def setVals(rows:Int,cols:Int,players:Int):Unit={
    for (i<-1 to players)
      mat:+=CardMatrix(rows,cols,i,players)
  }
  def getVals():(Int,Int,Int,Int)=mat(currentPlayer).getVals()
  def getMatrix(player:Int):List[List[Int]]=mat(player).matrix
  def getRandomValue: Int = {
    val random = new Random
    random.nextInt(16) - 2
  }

  def playGame(player: Int, matrix: List[List[Int]]):Unit ={
    currentPlayer=player
    notifyObservers
  }
  def updatedMatrix(currplayer:Int,matrix: List[List[Int]],row:Int,col:Int,card:Int): List[List[Int]] = {
    //mat(currentPlayer)=CardMatrix(matrix.updated(row, matrix(row).updated(col,card)),2,currplayer)
    matrix
  }
}