package com.pt.pedrorijo91.euromillions

import org.scalatest._

import scala.collection.mutable.SortedSet

class EuroTest extends FunSuite  {

  test("generateTicket should return valid ticket") {
    val ticket: (SortedSet[Int], SortedSet[Int]) = Generator.generateTicket()

    assert(ticket.productArity === 2)

    val numbers = ticket._1

    assert(numbers.size === NumberOfRegularNumbers)
    assert(numbers.forall(e => e > 0 && e < RegularNumbersMaxNumber))

    val stars = ticket._2

    assert(stars.size === NumberOfStars)
    assert(stars.forall(e => e > 0 && e < StarsMaxNumber))
  }

  test("generateTicket with pre-selected numbers should return ticket containing those numbers") {
    fail("Not implemented yet")
  }

  test("generateTicket with pre-selected stars should return ticket containing those stars") {
    fail("Not implemented yet")
  }

  test("generateTicket with pre-selected numbers and stars should return ticket containing those numbers and stars stars") {
    fail("Not implemented yet")
  }

  test("generateTicket should fail if too much pre-selected numbers") {
    fail("Not implemented yet")
  }

  test("generateTicket should fail if too much pre-selected stars") {
    fail("Not implemented yet")
  }

  test("test 1000 ticket generator calls") {
    1 to 1000 foreach(_ => {
      val ticket: (SortedSet[Int], SortedSet[Int]) = Generator.generateTicket()

      assert(ticket.productArity === 2)

      val numbers = ticket._1

      assert(numbers.size === NumberOfRegularNumbers)
      assert(numbers.forall(e => e > 0 && e < RegularNumbersMaxNumber))

      val stars = ticket._2

      assert(stars.size === NumberOfStars)
      assert(stars.forall(e => e > 0 && e < StarsMaxNumber))
    })
  }

  test("sorted numbers") {
    val (numbers: SortedSet[Int],_) = Generator.generateTicket()

    numbers.foldLeft (true, None:Option[Int]) {(acc,item) =>
      (acc._1 && acc._2.map(_ <= item).getOrElse(true), Some(item))}._1
  }

  test("sorted stars") {
    val (_, stars: SortedSet[Int]) = Generator.generateTicket()

    stars.foldLeft (true, None:Option[Int]) {(acc,item) =>
      (acc._1 && acc._2.map(_ <= item).getOrElse(true), Some(item))}._1
  }
}
