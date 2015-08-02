package com.pt.pedrorijo91.euromillions

import org.clapper.argot.ArgotConverters._
import org.clapper.argot.{ArgotParser, MultiValueOption}
import org.slf4j.LoggerFactory

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

object Generator {

  val logger = LoggerFactory.getLogger(Generator.getClass)

  val parser = new ArgotParser("Euromillions")

  val numbersArgs: MultiValueOption[Int] = parser.multiOption[Int](List("n", "number"), "numbers",
    "Pre-selected numbers")

  val preNumbers: collection.mutable.ListBuffer[Int] = ListBuffer.empty

  val starsArgs: MultiValueOption[Int] = parser.multiOption[Int](List("s", "star"), "stars",
    "Pre-selected stars")

  val preStars: collection.mutable.ListBuffer[Int] = ListBuffer.empty

  def main(args: Array[String]): Unit = {

    val processedArgs = processArgs(parser, args)
    val argNumbers = processedArgs._1
    val argStars = processedArgs._2

    preNumbers ++= argNumbers
    preStars ++= argStars

    val ticket = generateTicket(preNumbers.toList, preStars.toList)
    val numbers = ticket._1
    val stars = ticket._2

    println("Numbers: " + numbers.mkString(", "))
    println("Stars: " + stars.mkString(", "))
  }

  def processArgs(parser: ArgotParser, args: Array[String]): (Seq[Int], Seq[Int]) = {

    parser.parse(args)

    (processArgNumbers, processArgStars)
  }

  def processArgNumbers: Seq[Int] = {
    numbersArgs.value match {
      case Nil => {
        logger.info("No pre-selected numbers as command line arguments")
        Nil
      }
      case numbers: List[Int] => {
        logger.info("Received pre-selected numbers as command line arguments: " + numbers.mkString(", "))
        filterNumbers(numbers)
      }
    }
  }

  def processArgStars: Seq[Int] = {
    starsArgs.value match {
      case Nil => {
        logger.info("No pre-selected stars as command line arguments")
        Nil
      }
      case stars: List[Int] => {
        logger.info("Received pre-selected stars as command line arguments: " + stars.mkString(", "))
        filterStars(stars)
      }
    }
  }

  def filterNumbers(numbers: List[Int]): List[Int] = {
    filter(numbers, 0, RegularNumbersMaxNumber, NumberOfRegularNumbers)
  }

  def filterStars(stars: List[Int]): List[Int] = {
    filter(stars, 0, StarsMaxNumber, NumberOfStars)
  }

  def filter(list: List[Int], lowerBound: Int, upperBound: Int, numberOfElements: Int): List[Int] = {
    list.filter(elem => elem > lowerBound && elem <= upperBound).take(numberOfElements)
  }

  def generateTicket(preNumbers: List[Int] = Nil, preStars: List[Int] = Nil): (mutable.SortedSet[Int], mutable.SortedSet[Int]) = {
    val numbers: mutable.SortedSet[Int] = mutable.SortedSet(preNumbers: _*)
    val stars: mutable.SortedSet[Int] = mutable.SortedSet(preStars: _*)


    while (numbers.size < NumberOfRegularNumbers) {
      //TODO: replace while by recursion
      val n = genRandomNumber
      logger.debug("Trying to add number '" + n + "' to existent numbers: " + numbers.mkString(", ") + ". Duplicate element? " + numbers.contains(n))
      numbers += n
    }

    while (stars.size < NumberOfStars) {
      //TODO: replace while by recursion
      val s = genRandomStar
      logger.debug("Trying to add star '" + s + "' to existent stars: " + stars.mkString(", ") + ". Duplicate element? " + stars.contains(s))
      stars += s
    }
    (numbers, stars)
  }

  private[this] def genRandomNumber: Int = genRandomInt(RegularNumbersMaxNumber)

  private[this] def genRandomStar: Int = genRandomInt(StarsMaxNumber)

  private[this] def genRandomInt(limit: Int): Int = scala.util.Random.nextInt(limit - 1) + 1 // because may return 0 and it is not valid

}
