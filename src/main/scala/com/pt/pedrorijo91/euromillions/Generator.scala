package com.pt.pedrorijo91.euromillions

import scala.collection.mutable.SortedSet


object Generator {
  def main(args: Array[String]): Unit = {

    val numbers : SortedSet[Int] = SortedSet.empty
    val stars : SortedSet[Int] = SortedSet.empty

  /*
   TODO
   should accept numbers from CLI
   should accept stars from CLI

   should specify number of numbers to take
   should specify number of stars to take

   should check duplicates on numbers
   should check duplicates on starts

   test randomInt
   test randomNumber
   test randomStar
   test number of numbers
   test number of stars
   */


    while(numbers.size < NumberOfRegularNumbers) {
      numbers += getRandomNumber
    }

    while(stars.size < NumberOfStars) {
      stars += getRandomStar
    }

    println("Numbers: " + numbers.mkString(", "))
    println("Stars: " + stars.mkString(", "))

  }

  private[this] def getRandomNumber : Int = getRandomInt(RegularNumbersMaxNumber)

  private[this] def getRandomStar : Int = getRandomInt(StarsMaxNumber)

  private[this] def getRandomInt(limit: Int): Int = scala.util.Random.nextInt(limit - 1) + 1 // because may return 0 and it is not valid

}
