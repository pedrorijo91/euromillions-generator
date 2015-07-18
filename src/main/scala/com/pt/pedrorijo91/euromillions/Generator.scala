package com.pt.pedrorijo91.euromillions

import org.clapper.argot.{MultiValueOption, ArgotConverters, ArgotParser}
import ArgotConverters._

import scala.collection.mutable
import scala.collection.mutable.{ListBuffer, SortedSet}

import ch.qos.logback.classic.Logger
import org.slf4j.LoggerFactory

object Generator {

  val logger = LoggerFactory.getLogger(Generator.getClass).asInstanceOf[Logger]

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
      case Nil => logger.info("No pre-selected numbers as command line arguments")
      case numbers : List[Int] => {
        logger.info("Received pre-selected numbers as command line arguments: " + numbers.mkString(", "))
        preNumbers ++= numbers
      }
    }

    starsArgs.value match {
      case Nil => logger.info("No pre-selected stars as command line arguments")
      case stars : List[Int] => {
        logger.info("Received pre-selected stars as command line arguments: " + stars.mkString(", "))
        preStars ++= stars
      }
    }

    /*
     TODO
     should specify number of numbers to take
     should specify number of stars to take
     */

    val (numbers: SortedSet[Int], stars: SortedSet[Int]) = generateTicket(preNumbers.toList, preStars.toList)

    println("Numbers: " + numbers.mkString(", "))
    println("Stars: " + stars.mkString(", "))

  }

  def generateTicket(preNumbers : List[Int] = Nil, preStars : List[Int] = Nil): (mutable.SortedSet[Int], mutable.SortedSet[Int]) = {
    val numbers: mutable.SortedSet[Int] = mutable.SortedSet(preNumbers: _*)
    val stars: mutable.SortedSet[Int] = mutable.SortedSet(preStars: _*)

    while (numbers.size < NumberOfRegularNumbers) {
      val n = getRandomNumber
      logger.debug("Trying to add number '" + n + "' to existent numbers: " + numbers.mkString(", ") + ". Duplicate element? " + numbers.contains(n))
      numbers += n
    }

    while (stars.size < NumberOfStars) {
      val s = getRandomStar
      logger.debug("Trying to add star '" + s +  "' to existent numbers: " + stars.mkString(", ") + ". Duplicate element? " + stars.contains(s))
      stars += s
    }
    (numbers, stars)
  }

  private[this] def getRandomNumber : Int = getRandomInt(RegularNumbersMaxNumber)

  private[this] def getRandomStar : Int = getRandomInt(StarsMaxNumber)

  private[this] def getRandomInt(limit: Int): Int = scala.util.Random.nextInt(limit - 1) + 1 // because may return 0 and it is not valid

}
