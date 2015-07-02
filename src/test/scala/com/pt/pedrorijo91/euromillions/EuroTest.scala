package com.pt.pedrorijo91.euromillions

import org.scalatest._

import scala.collection.mutable.SortedSet

class EuroTest extends FunSuite  {

  /*
   test randomInt
   test randomNumber
   test randomStar
   test number of numbers
   test number of stars
   */

  test("generateTicket should return valid ticket") {
    val ticket: (SortedSet[Int], SortedSet[Int]) = Generator.generateTicket

    assert(ticket.productArity === 2)

    val numbers = ticket._1

    assert(numbers.size === NumberOfRegularNumbers)
    assert(numbers.forall(e => e > 0 && e < RegularNumbersMaxNumber))

    val stars = ticket._2

    assert(stars.size === NumberOfStars)
    assert(stars.forall(e => e > 0 && e < StarsMaxNumber))
  }


  test("test 1000 ticket calls") {
    1 to 1000 foreach(x => {
      val ticket: (SortedSet[Int], SortedSet[Int]) = Generator.generateTicket

      assert(ticket.productArity === 2)

      val numbers = ticket._1

      assert(numbers.size === NumberOfRegularNumbers)
      assert(numbers.forall(e => e > 0 && e < RegularNumbersMaxNumber))

      val stars = ticket._2

      assert(stars.size === NumberOfStars)
      assert(stars.forall(e => e > 0 && e < StarsMaxNumber))
    })
  }
}
