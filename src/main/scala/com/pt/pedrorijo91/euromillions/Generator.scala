package com.pt.pedrorijo91.euromillions

import org.clapper.argot.{MultiValueOption, ArgotConverters, ArgotParser}
import ArgotConverters._

import scala.collection.mutable
import scala.collection.mutable.{ListBuffer, SortedSet}


object Generator {


  val parser = new ArgotParser("Euromillions")

  val numbersArgs: MultiValueOption[Int] = parser.multiOption[Int](List("n", "number"), "numbers",
    "Pre-selected numbers")

  val preNumbers: collection.mutable.ListBuffer[Int] = ListBuffer.empty

  val starsArgs: MultiValueOption[Int] = parser.multiOption[Int](List("s", "star"), "stars",
    "Pre-selected stars")

  val preStars: collection.mutable.ListBuffer[Int] = ListBuffer.empty

  def main(args: Array[String]): Unit = {

    parser.parse(args)

    numbersArgs.value match {
      case Nil => println("NONE numbers")
      case numbers@_ => preNumbers ++= numbers
    }

    starsArgs.value match {
      case Nil => println("NONE stars")
      case stars@_ => preStars ++= stars
    }

    /*
     TODO

     should specify number of numbers to take
     should specify number of stars to take

     log
     */

    val (numbers: SortedSet[Int], stars: SortedSet[Int]) = generateTicket(preNumbers.toList, preStars.toList)

    println("Numbers: " + numbers.mkString(", "))
    println("Stars: " + stars.mkString(", "))

  }

  def generateTicket(preNumbers : List[Int] = Nil, preStars : List[Int] = Nil): (mutable.SortedSet[Int], mutable.SortedSet[Int]) = {
    val numbers: mutable.SortedSet[Int] = mutable.SortedSet(preNumbers: _*)
    val stars: mutable.SortedSet[Int] = mutable.SortedSet(preStars: _*)

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
