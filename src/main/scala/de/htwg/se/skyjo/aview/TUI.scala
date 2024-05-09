package de.htwg.se.skyjo
package aview

import model.Move
import controller.TableController

import scala.io.StdIn.readLine
import util.Observer

class TUI(controller: TableController) extends Observer:
  controller.add(this)
  def run =
    println("\u001B[34m"+"Welcome to Skyjo\u001B[0m")
    println(controller)
    getInputAndPrintLoop()


  override def update = {
    println(controller)
  }

  def getInputAndPrintLoop(): Unit =
    print(controller.table.getCurrenPlayerString()+"\nEnter your command: (q)uit, (stack), (trash) ")
    drawInput(readLine) match
      case None       =>
      case Some(draw) => {
        if draw then controller.drawFromStack()
        println(controller.table.getCurrenPlayerString()+"\nPlease enter your move: (swapped, row, col)")
        moveInput(readLine, draw) match
          case None       =>
          case Some(move) => controller.doMove(move)
      }
    if(controller.gameEnd())
      println(controller.table.getCurrenPlayerString()+ " ENDED THE GAME")
    getInputAndPrintLoop()
    

def drawInput(input: String): Option[Boolean] =
    input.toUpperCase().replaceAll(" ","") match
      case "Q" => sys.exit(0)
      case "STACK" => Some(true)
      case "TRASH" => Some(false)
      case "S" => Some(true)
      case "T" => Some(false)

def moveInput(input: String, draw: Boolean): Option[Move] =
    val Input = input.toUpperCase()
     Input.replaceAll(" ","") match
      case "Q" => sys.exit(0)
      case _ => Some(Move(
        draw,
        Input.split(" ")(0) match
            case "S" => true
            case "D" => false
            case "SWAP" => true
            case "DISCARD" => false,
        Input.split(" ")(1).toInt,
        Input.split(" ")(2).toInt
        ))