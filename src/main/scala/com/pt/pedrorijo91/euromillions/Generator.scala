package com.pt.pedrorijo91.euromillions

import scala.collection.mutable
import scala.collection.mutable.SortedSet


object Generator {
  def main(args: Array[String]): Unit = {

  /*
   TODO
   should accept numbers from CLI
   should accept stars from CLI

   should specify number of numbers to take
   should specify number of stars to take
   */

    val (numbers: SortedSet[Int], stars: SortedSet[Int]) = generateTicket

    println("Numbers: " + numbers.mkString(", "))
    println("Stars: " + stars.mkString(", "))

  }

  def generateTicket: (mutable.SortedSet[Int], mutable.SortedSet[Int]) = {
    val numbers: mutable.SortedSet[Int] = SortedSet.empty
    val stars: mutable.SortedSet[Int] = SortedSet.empty

    while (numbers.size < NumberOfRegularNumbers) {
      numbers += getRandomNumber
    }

    while (stars.size < NumberOfStars) {
      stars += getRandomStar
    }
    (numbers, stars)
  }

  private[this] def getRandomNumber : Int = getRandomInt(RegularNumbersMaxNumber)

  private[this] def getRandomStar : Int = getRandomInt(StarsMaxNumber)

  private[this] def getRandomInt(limit: Int): Int = scala.util.Random.nextInt(limit - 1) + 1 // because may return 0 and it is not valid

}
