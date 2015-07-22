package com.pt.pedrorijo91.euromillions

import java.util
import java.util.Date

import ch.qos.logback.classic.Logger
import com.google.gson.{JsonArray, JsonObject, JsonParser}
import com.typesafe.config.{Config, ConfigFactory}
import org.slf4j.LoggerFactory

import scala.collection.JavaConversions._
import scala.collection.mutable
import scalaj.http.{Http, HttpRequest, HttpResponse}

/**
 * Created by pedrorijo on 22/07/15.
 */
object PrizeChecker {

  val logger = LoggerFactory.getLogger(PrizeChecker.getClass).asInstanceOf[Logger]

  def main(args: Array[String]): Unit = {

    val conf: Config = ConfigFactory.load

    val ans: HttpResponse[String] = fetchDrawResult(conf)
    //logger.info("Answer: " + ans)

    val result: (mutable.SortedSet[Int], mutable.SortedSet[Int], Date, mutable.HashMap[(Int, Int), (Int, Float)]) = parseAns(ans)
    val prizes: mutable.HashMap[(Int, Int), (Int, Float)] = result._4

    val tickets: util.List[_ <: Config] = conf.getConfigList("euromillions.tickets")

    tickets.foreach(ticket => {
      val ticketNumbers: List[Integer] = ticket.getConfig("ticket").getIntList("numbers").toList
      val ticketStars: List[Integer] = ticket.getConfig("ticket").getIntList("stars").toList
      logger.info("Ticket read from properties. Numbers: " + ticketNumbers.mkString(", ") + " and Stars: " + ticketStars.mkString(", "))

      if (ticketNumbers.size != NumberOfRegularNumbers) {
        logger.warn("Ticket contains the wrong number of Regular Numbers")
      }
      if (ticketNumbers.toList.exists(n => n <= 0 || n > RegularNumbersMaxNumber)) {
        logger.warn("Ticket contains the number(s) out of valid bounds: [1-" + RegularNumbersMaxNumber + "]")
      }

      if (ticketStars.size != NumberOfStars) {
        logger.warn("Ticket contains the wrong number of Stars")
      }
      if (ticketStars.toList.exists(n => n <= 0 || n > StarsMaxNumber)) {
        logger.warn("Ticket contains the star(s) out of valid bounds: [1-" + StarsMaxNumber + "]")
      }

      val matches = checkMatches(result, ticketNumbers, ticketStars)
      logger.info("Ticket hit " + matches._1.size + " (" + matches._1.mkString(", ") + ")" + " numbers and " + matches._2.size + " (" +
        matches._2.mkString(", ") + ")" + " stars")

      val prize: Option[(Int, Float)] = prizes.get(matches._1.size, matches._2.size)

      prize match {
        case Some(p) => logger.info("You have won ! You and other " + p._1 + " players have won " + p._2 + " euros each")
        case None => logger.info("It seems that you have no prize with this ticket :(")
      }
    })
  }

  def checkMatches(result: (mutable.SortedSet[Int], mutable.SortedSet[Int], Date, mutable.HashMap[(Int, Int), (Int, Float)]),
                   ticketNumbers: List[Integer], ticketStars: List[Integer]): (mutable.SortedSet[Int], mutable.SortedSet[Int]) = {
    (result._1.filter(n => ticketNumbers.contains(n)), result._2.filter(s => ticketStars.contains(s)))
  }

  def extractDate(jsonDate: String): Date = {
    // Date format: /Date(1437436800000+0000)/

    val r = "\\d+\\+\\d+".r

    r findFirstIn jsonDate match {
      case Some(d) => {
        val milliseconds: Long = d.split("\\+")(0).toLong
        new Date(milliseconds)
      }
      case _ => {
        logger.info("Invalid date (" + jsonDate + ")")
        new Date(0)
      }
    }
  }

  def parseAns(ans: HttpResponse[String]): (mutable.SortedSet[Int], mutable.SortedSet[Int], Date, mutable.HashMap[(Int, Int), (Int, Float)]) = {
    val body: String = ans.body //TODO check error
    //logger.info("Body: " + body)

    val parser: JsonParser = new JsonParser()
    val jsonObj: JsonObject = parser.parse(body).getAsJsonObject

    val jsonDate: String = jsonObj.get("Date").getAsString
    val date = extractDate(jsonDate)

    val numbers: mutable.SortedSet[Int] = mutable.SortedSet(jsonObj.get("Num1").getAsInt, jsonObj.get("Num2").getAsInt, jsonObj.get("Num3").getAsInt,
      jsonObj.get("Num4").getAsInt, jsonObj.get("Num5").getAsInt)
    val stars: mutable.SortedSet[Int] = mutable.SortedSet(jsonObj.get("Star1").getAsInt, jsonObj.get("Star2").getAsInt)

    logger.info("Draw result for day " + printPrettyDate(date) + ": Numbers = " + numbers.mkString(", ") + " and " +
      "Stars = " + stars.mkString(", "))

    val prizeCombinationsJson: JsonArray = jsonObj.get("PrizeCombinations").getAsJsonArray

    val prizes = new mutable.HashMap[(Int, Int), (Int, Float)]()

    prizeCombinationsJson.foreach(comb => {
      val combJObject: JsonObject = comb.getAsJsonObject
      val nNumbers: Int = combJObject.get("Numbers").getAsInt
      val nStars: Int = combJObject.get("Stars").getAsInt
      val nWinners: Int = combJObject.get("Winners").getAsInt
      val prize: Float = combJObject.get("Prize").getAsFloat

      logger.info("For " + nNumbers + " right numbers and " + nStars + " right stars there were " + nWinners + " " +
        "winners, each with a prize of " + prize + " euros")

      prizes += ((nNumbers, nStars) ->(nWinners, prize))
    })

    (numbers, stars, date, prizes)
  }

  def fetchDrawResult(conf: Config): HttpResponse[String] = {
    val mashapeKey: String = conf.getString("euromillions.results.api.key")
    logger.info("Read mashape key from properties: " + mashapeKey)

    logger.info("Executing GET request to https://euromillions.p.mashape.com/ResultsService/FindLast")
    val request: HttpRequest = Http("https://euromillions.p.mashape.com/ResultsService/FindLast").header("X-Mashape-Key", mashapeKey)

    request.asString
  }
}
