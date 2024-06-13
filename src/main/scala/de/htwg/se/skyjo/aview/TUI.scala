package de.htwg.se.skyjo
package aview

import controller.controllerComponent.ControllerInterface

import scala.util.{Try, Success, Failure}
import scala.io.StdIn.readLine
import util.Observer
import util.Move

class TUI(controller: ControllerInterface) extends Observer:
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
      println(controller.getPlayerString(controller.getCurrenPlayer()-1)+ " ENDED THE GAME: one more round to go")
      return
    getInputAndPrintLoop()

  def lastRound():Unit=
    for (x<-1 to controller.getPLayerCount()-1)
      inputAndPrint()
    val scores=controller.getScores()
    scores.foreach((score,player)=>println(controller.getPlayerString(player)+" : "+score))
    val winner=scores.minBy((score,player)=>score)
    println(controller.getPlayerString(winner(1))+" WON WITH ONLY "+winner(0)+" POINTS")

  def inputAndPrint():Unit=
    println(controller.getCurrenPlayerString()+"'s turn\nEnter your command: (q)uit, (stack), (trash), (u)ndo, (r)edo ")
    drawInput() match
      case None  =>
      case Some(command) => command match
        case "drawStack" =>
          controller.drawFromStack()
          println(controller.getCurrenPlayerString())
          println("Please enter your move: (swapped, row, col)")
          moveInput(readLine, true) match
            case None       =>
            case Some(move) => controller.doMove(move)
        case "drawTrash" =>
          println("Please enter your move: (swapped, row, col)")
          moveInput(readLine, false) match
            case None       =>
            case Some(move) => controller.doMove(move)
        case "undo" => controller.undo()
        case "redo" => controller.redo()

  def drawInput(): Option[String] =
    while(true){
      readLine.toUpperCase().replaceAll("\\s+","") match
        case "Q" => sys.exit(0)
        case "STACK" => return Some("drawStack")
        case "TRASH" => return Some("drawTrash")
        case "S" => return Some("drawStack")
        case "T" => return Some("drawTrash")
        case "U" => return Some("undo")
        case "R" => return Some("redo")
        case _=> print("wrong input \n")
    }
    None

  def moveInput(input: String, draw: Boolean): Option[Move] = {
    val Input = (" " + input).toUpperCase().replaceAll("\\s+", " ")

    if (Input.replaceAll(" ", "") == "Q") {
      sys.exit(0)
    }

    val parts = Input.split(" ")

    if (parts.length < 4) {
      None // Not enough parts to parse
    } else {
      Try {
        val swapped = parts(1) match {
          case "S" => true
          case "T" => false
          case "D" => false
          case "SWAP" => true
          case "DISCARD" => false
          case _ => throw new IllegalArgumentException("Invalid action")
        }

        val row = parts(2).toInt
        val col = parts(3).toInt

        Move(draw, swapped, row, col)
      } match {
        case Success(move) => Some(move)
        case Failure(_) => None
      }
    }
  }