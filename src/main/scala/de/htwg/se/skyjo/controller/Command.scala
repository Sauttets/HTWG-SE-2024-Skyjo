package de.htwg.se.skyjo.controller

trait Command {
  def execute(): Unit
}
