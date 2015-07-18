package com.pt.pedrorijo91.euromillions

import org.scalatest._

import scala.collection.mutable
import scala.collection.mutable.SortedSet

class EuroTest extends FunSuite {

  def validateTicket(ticket: (mutable.SortedSet[Int], mutable.SortedSet[Int])): Unit = {

    assert(ticket.productArity === 2)

    val numbers = ticket._1

    assert(numbers.size === NumberOfRegularNumbers)
    assert(numbers.forall(e => e > 0 && e < RegularNumbersMaxNumber))

    val stars = ticket._2

    assert(stars.size === NumberOfStars)
    assert(stars.forall(e => e > 0 && e < StarsMaxNumber))
  }

  def containsPreSelected(lst: mutable.SortedSet[Int], preSelected: List[Int]): Boolean = {
    preSelected.forall(elem => lst.contains(elem))
  }

  test("generateTicket should return valid ticket") {
    validateTicket(Generator.generateTicket())
  }

  test("generateTicket with pre-selected numbers should return ticket containing those numbers") {
    val preNumbers = 10 :: 20 :: 30 :: 40 :: Nil

    val ticket: (SortedSet[Int], SortedSet[Int]) = Generator.generateTicket(preNumbers)

    validateTicket(ticket)

    val numbers = ticket._1
    assert(containsPreSelected(numbers, preNumbers))

  }

  test("generateTicket with pre-selected stars should return ticket containing those stars") {
    val preStars: List[Int] = 5 :: Nil

    val ticket: (SortedSet[Int], SortedSet[Int]) = Generator.generateTicket(preStars = preStars)

    validateTicket(ticket)

    val stars = ticket._2
    assert(containsPreSelected(stars, preStars))

  }

  test("generateTicket with pre-selected numbers and stars should return ticket containing those numbers and stars stars") {
    val preNumbers = 10 :: 20 :: 30 :: 40 :: Nil
    val preStars: List[Int] = 5 :: Nil

    val ticket: (SortedSet[Int], SortedSet[Int]) = Generator.generateTicket(preNumbers, preStars)

    validateTicket(ticket)

    val numbers = ticket._1
    assert(containsPreSelected(numbers, preNumbers))

    val stars = ticket._2
    assert(containsPreSelected(stars, preStars))
  }


  test("generateTicket should limit if too many pre-selected numbers") {
    val preNumbers: List[Int] = List(1, 2, 3, 4, 5, 6)
    val expectedResult: List[Int] = List(1, 2, 3, 4, 5)

    assert(Generator.filterNumbers(preNumbers) == expectedResult)
  }

  test("generateTicket should limit if too many pre-selected stars") {
    val preNumbers: List[Int] = List(1, 2, 3)
    val expectedResult: List[Int] = List(1, 2)

    assert(Generator.filterStars(preNumbers) == expectedResult)
  }

  test("generateTicket should filter invalid pre-selected numbers") {
    val preNumbers: List[Int] = List(1, 200, 3, 400, 5, 600)
    val expectedResult: List[Int] = List(1, 3, 5)

    assert(Generator.filterNumbers(preNumbers) == expectedResult)
  }

  test("generateTicket should filter invalid pre-selected stars") {
    val preNumbers: List[Int] = List(100, 2, 300)
    val expectedResult: List[Int] = List(2)

    assert(Generator.filterStars(preNumbers) == expectedResult)
  }

  test("test 1000 ticket generator calls") {
    1 to 1000 foreach (_ => validateTicket(Generator.generateTicket()))
  }

  test("sorted numbers") {
    val (numbers: SortedSet[Int], _) = Generator.generateTicket()

    val sorted = numbers.foldLeft(true, None: Option[Int]) { (acc, item) =>
      (acc._1 && acc._2.map(_ <= item).getOrElse(true), Some(item))
    }._1

    assert(sorted)
  }

  test("sorted stars") {
    val (_, stars: SortedSet[Int]) = Generator.generateTicket()

    stars.foldLeft(true, None: Option[Int]) { (acc, item) =>
      (acc._1 && acc._2.map(_ <= item).getOrElse(true), Some(item))
    }._1
  }
}
