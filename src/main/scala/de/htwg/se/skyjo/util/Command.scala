package de.htwg.se.skyjo
package util

trait Command:
  def execute(): Unit
  def undo(): Unit
  def redo(): Unit