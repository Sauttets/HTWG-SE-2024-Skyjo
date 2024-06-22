package de.htwg.se.skyjo
package util

trait Command:
  def execute(): Memento
