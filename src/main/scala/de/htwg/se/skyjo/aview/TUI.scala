package de.htwg.se.skyjo
package aview

import model.Move
import controller.TableController

import scala.io.StdIn.readLine
import util.Observer
import java.lang.ModuleLayer.Controller

class TUI(controller: TableController) extends Observer:
  controller.add(this)
  def run =
    println("\u001B[34m"+"Welcome to Skyjo\u001B[0m")
    println(controller)
    getInputAndPrintLoop()
    lastRound()

  override def update = {
    println(controller)
  }

  def getInputAndPrintLoop(): Unit =
    inputAndPrint()
    if(controller.gameEnd())
      println(controller.table.getPlayerString(controller.table.currentPlayer-1)+ " ENDED THE GAME: one more round to go")
      return
    getInputAndPrintLoop()

  def lastRound():Unit=
    for (x<-1 to controller.table.playerCount-1)
      inputAndPrint()
    val scores=controller.table.getScores()
    scores.foreach((score,player)=>println(controller.table.getPlayerString(player)+" : "+score))
    val winner=scores.minBy((score,player)=>score)
    println(controller.table.getPlayerString(winner(1))+" WON WITH ONLY "+winner(0)+" POINTS")

  def inputAndPrint():Unit=
    println(controller.table.getCurrenPlayerString()+"'s turn\nEnter your command: (q)uit, (stack), (trash) ")
    drawInput(readLine) match
      case None       =>
      case Some(draw) => {
        if draw then  
          controller.drawFromStack()
          println(controller.table.getCurrenPlayerString())
        println("Please enter your move: (swapped: S | T , row, col)")
        moveInput(readLine, draw) match
          case None       =>
          case Some(move) => controller.doMove(move)
      }
    

  def drawInput(input: String): Option[Boolean] =
    input.toUpperCase().replaceAll("\\s+","") match
      case "Q" => sys.exit(0)
      case "STACK" => Some(true)
      case "TRASH" => Some(false)
      case "S" => Some(true)
      case "T" => Some(false)

  def moveInput(input: String, draw: Boolean): Option[Move] =
    val Input = (" "+input).toUpperCase().replaceAll("\\s+"," ") 
     Input.replaceAll(" ","") match
      case "Q" => sys.exit(0)
      case _ => Some(Move(
        draw,
        Input.split(" ")(1) match
            case "S" => true
            case "T" => false
            case "D" => false
            case "SWAP" => true
            case "DISCARD" => false,
        Input.split(" ")(2).toInt,
        Input.split(" ")(3).toInt
        ))