package com.pt.pedrorijo91.euromillions

import java.util
import java.util.Date

import com.typesafe.config.{Config, ConfigFactory}
import org.scalatest.FunSuite

import scala.collection.mutable
import scalaj.http.HttpResponse

/**
 * Created by pedrorijo on 27/07/15.
 */
class PrizeCheckerTest extends FunSuite {

  test("parsing json response should produce expected numbers, stars, date, and prizes") {
    val body: String =
      """
        | {
        |   "Date":"\/Date(1437696000000+0000)\/",
        |   "Jackpot":40000000.00,
        |   "NextJackpot":15000000.00,
        |   "Num1":2,
        |   "Num2":9,
        |   "Num3":21,
        |   "Num4":35,
        |   "Num5":46,
        |   "PrizeCombinations":[
        |      {
        |         "Numbers":5,
        |         "Prize":40468528.00,
        |         "Stars":2,
        |         "Winners":1
        |      },
        |      {
        |         "Numbers":5,
        |         "Prize":182139.77,
        |         "Stars":1,
        |         "Winners":8
        |      },
        |      {
        |         "Numbers":5,
        |         "Prize":34693.29,
        |         "Stars":0,
        |         "Winners":14
        |      },
        |      {
        |         "Numbers":4,
        |         "Prize":5059.44,
        |         "Stars":2,
        |         "Winners":48
        |      },
        |      {
        |         "Numbers":4,
        |         "Prize":215.51,
        |         "Stars":1,
        |         "Winners":986
        |      },
        |      {
        |         "Numbers":4,
        |         "Prize":100.00,
        |         "Stars":0,
        |         "Winners":2125
        |      },
        |      {
        |         "Numbers":3,
        |         "Prize":64.26,
        |         "Stars":2,
        |         "Winners":2362
        |      },
        |      {
        |         "Numbers":2,
        |         "Prize":21.84,
        |         "Stars":2,
        |         "Winners":31970
        |      },
        |      {
        |         "Numbers":3,
        |         "Prize":15.62,
        |         "Stars":1,
        |         "Winners":42752
        |      },
        |      {
        |         "Numbers":3,
        |         "Prize":12.64,
        |         "Stars":0,
        |         "Winners":88891
        |      },
        |      {
        |         "Numbers":1,
        |         "Prize":11.97,
        |         "Stars":2,
        |         "Winners":164891
        |      },
        |      {
        |         "Numbers":2,
        |         "Prize":8.66,
        |         "Stars":1,
        |         "Winners":616908
        |      },
        |      {
        |         "Numbers":2,
        |         "Prize":4.19,
        |         "Stars":0,
        |         "Winners":1304557
        |      }
        |   ],
        |   "RaffleNumber":0,
        |   "Star1":2,
        |   "Star2":11
        |}
      """.stripMargin

    val ans: HttpResponse[String] = HttpResponse.apply(body, 0, Map[String, String]())

    val parsedAns: (mutable.SortedSet[Int], mutable.SortedSet[Int], Date, mutable.HashMap[(Int, Int), (Int, Float)]) = PrizeChecker.parseAns(ans)

    val parsedNumbers = parsedAns._1
    val parsedStars = parsedAns._2
    val parsedDate = parsedAns._3
    val parsedPrizes = parsedAns._4

    assert(parsedNumbers == mutable.SortedSet(2, 9, 21, 35, 46))

    assert(parsedStars == mutable.SortedSet(2, 11))

    assert(parsedDate == new Date(1437696000000L))

    assert(parsedPrizes.size == 13)
    assert(parsedPrizes.contains((5, 2)) && parsedPrizes.get((5, 2)).isDefined && parsedPrizes.get((5, 2)) == Some((1, 40468528.00f)))
    assert(parsedPrizes.contains((5, 1)) && parsedPrizes.get((5, 1)).isDefined && parsedPrizes.get((5, 1)) == Some((8, 182139.77f)))
    assert(parsedPrizes.contains((5, 0)) && parsedPrizes.get((5, 0)).isDefined && parsedPrizes.get((5, 0)) == Some((14, 34693.29f)))
    assert(parsedPrizes.contains((4, 2)) && parsedPrizes.get((4, 2)).isDefined && parsedPrizes.get((4, 2)) == Some((48, 5059.44f)))
    assert(parsedPrizes.contains((4, 1)) && parsedPrizes.get((4, 1)).isDefined && parsedPrizes.get((4, 1)) == Some((986, 215.51f)))
    assert(parsedPrizes.contains((4, 0)) && parsedPrizes.get((4, 0)).isDefined && parsedPrizes.get((4, 0)) == Some((2125, 100.00f)))
    assert(parsedPrizes.contains((3, 2)) && parsedPrizes.get((3, 2)).isDefined && parsedPrizes.get((3, 2)) == Some((2362, 64.26f)))
    assert(parsedPrizes.contains((3, 1)) && parsedPrizes.get((3, 1)).isDefined && parsedPrizes.get((3, 1)) == Some((42752, 15.62f)))
    assert(parsedPrizes.contains((3, 0)) && parsedPrizes.get((3, 0)).isDefined && parsedPrizes.get((3, 0)) == Some((88891, 12.64f)))
    assert(parsedPrizes.contains((2, 2)) && parsedPrizes.get((2, 2)).isDefined && parsedPrizes.get((2, 2)) == Some((31970, 21.84f)))
    assert(parsedPrizes.contains((2, 1)) && parsedPrizes.get((2, 1)).isDefined && parsedPrizes.get((2, 1)) == Some((616908, 8.66f)))
    assert(parsedPrizes.contains((2, 0)) && parsedPrizes.get((2, 0)).isDefined && parsedPrizes.get((2, 0)) == Some((1304557, 4.19f)))
    assert(parsedPrizes.contains((1, 2)) && parsedPrizes.get((1, 2)).isDefined && parsedPrizes.get((1, 2)) == Some((164891, 11.97f)))
  }

  test("checking provided ticket against draw result should return correct number of matches") {

    val result: (mutable.SortedSet[Int], mutable.SortedSet[Int], Date, mutable.HashMap[(Int, Int), (Int, Float)]) = (mutable.SortedSet(1, 2, 3, 4, 5),
      mutable.SortedSet(6, 7),
      new Date(0L), mutable.HashMap[(Int, Int), (Int, Float)]())

    val ticketNumbers: List[Integer] = List(4, 5, 6, 7, 8)
    val ticketStars: List[Integer] = List(5, 6)

    val matches: (mutable.SortedSet[Int], mutable.SortedSet[Int]) = PrizeChecker.checkMatches(result, ticketNumbers, ticketStars)

    println(matches)

    assert(matches._1.size == 2)
    assert(matches._1.contains(4) && matches._1.contains(5))

    assert(matches._2.size == 1)
    assert(matches._2.contains(6))
  }

  test("date conversion from a string to Date object") {
    val dateString: String = "/Date(1437436800000+0000)/"

    val expectedDate: Date = new Date(0L)
    expectedDate.setDate(21)
    expectedDate.setMonth(6)
    expectedDate.setYear(2015 - 1900)
    expectedDate.setHours(1)
    expectedDate.setMinutes(0)
    expectedDate.setSeconds(0)

    val date: Date = PrizeChecker.extractDate(dateString)

    assert(date == expectedDate)
  }

  test("read API key should return provided key in configuration file") {
    val config: Config = ConfigFactory.load("test")

    val apiKey: String = PrizeChecker.readApiKey(config)

    val apiTestkey: String = "apiTestKey"
    assert(apiKey == apiTestkey)
  }

  test("read tickets should return expected tickets") {
    val config: Config = ConfigFactory.load("test")

    val tickets: List[(List[Integer], List[Integer])] = PrizeChecker.readTickets(config)

    assert(tickets.size == 2)
    assert(tickets.contains((List(3,4,5,6,7),List(1,2))) && tickets.contains((List(8,9,10,11,12),List(3,4))))
  }

}
