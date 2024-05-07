package de.htwg.se.skyjo
package aview

import model.Move
import controller.TableController

import scala.io.StdIn.readLine
import util.Observer

class TUI(controller: TableController) extends Observer:
  controller.add(this)
  def run =
    println(controller)
    getInputAndPrintLoop()


  override def update = println(controller)  

  def getInputAndPrintLoop(): Unit =
    print("Enter your command: (q)uit, (stack), (trash) ")
    drawInput(readLine) match
      case None       =>
      case Some(draw) => {
        if draw then controller.drawFromStack()
        println("Please enter your move: (swapped, row, col)")
        moveInput(readLine, draw) match
          case None       =>
          case Some(move) => controller.doMove(move)
      }
    getInputAndPrintLoop()
    

def drawInput(input: String): Option[Boolean] =
    input.toUpperCase() match
      case "Q" => sys.exit(0)
      case "STACK" => Some(true)
      case "TRASH" => Some(false)
      case "S" => Some(true)
      case "T" => Some(false)

def moveInput(input: String, draw: Boolean): Option[Move] =
    val Input = input.toUpperCase()
     Input match
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