package de.htwg.se.skyjo
package util

trait Observer:
  def update: Unit

trait Observable:
  var subscribers: Vector[Observer] = Vector()
  def add(s: Observer) = subscribers = subscribers :+ s
  def remove(s: Observer) = subscribers = subscribers.filterNot(o => o == s)
  def notifyObservers = subscribers.foreach(o => o.update)
