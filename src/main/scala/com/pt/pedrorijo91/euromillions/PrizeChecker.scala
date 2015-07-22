package com.pt.pedrorijo91.euromillions

import java.util.Date

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

  val logger = LoggerFactory.getLogger(PrizeChecker.getClass)

  def main(args: Array[String]): Unit = {

    val conf: Config = ConfigFactory.load

    val ans: HttpResponse[String] = fetchDrawResult(readApiKey(conf))
    //logger.info("Answer: " + ans)

    val result: (mutable.SortedSet[Int], mutable.SortedSet[Int], Date, mutable.HashMap[(Int, Int), (Int, Float)]) = parseAns(ans)
    val prizes: mutable.HashMap[(Int, Int), (Int, Float)] = result._4

    checkTicketsPrize(conf, result, prizes)
  }

  def checkTicketsPrize(conf: Config, result: (mutable.SortedSet[Int], mutable.SortedSet[Int], Date, mutable.HashMap[(Int, Int), (Int, Float)]),
                        prizes: mutable.HashMap[(Int, Int), (Int, Float)]):
  Unit = {
    val tickets: List[(List[Integer], List[Integer])] = readTickets(conf)

    var totalPrize: Float = 0f

    tickets.foreach(ticket => {
      val ticketNumbers: List[Integer] = ticket._1
      val ticketStars: List[Integer] = ticket._2
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
        case Some(p) => {
          logger.info("You have won ! You and other " + p._1 + " players have won " + p._2 + " euros each")
          totalPrize += p._2
        }
        case None => logger.info("It seems that you have no prize with this ticket :(")
      }
    })

    if(tickets.size > 0) {
      logger.info("Total prize for draw " + printPrettyDate(result._3) + ": " + totalPrize + " euros.")
    }
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

  def fetchDrawResult(mashapeKey: String): HttpResponse[String] = {
    logger.info("Read mashape key from properties: " + mashapeKey)

    logger.info("Executing GET request to https://euromillions.p.mashape.com/ResultsService/FindLast")
    val request: HttpRequest = Http("https://euromillions.p.mashape.com/ResultsService/FindLast").header("X-Mashape-Key", mashapeKey)

    request.asString
  }

  def readApiKey(conf: Config): String = {
    conf.getString(ApiKeyConfigPath)
  }

  def readTickets(conf: Config): List[(List[Integer], List[Integer])] = {
    if (conf.hasPath(TicketsConfigPath)) {
      conf.getConfigList(TicketsConfigPath).toList.map(t => readTicket(t))
    }
    else {
      logger.info("No tickets read from configuration")
      Nil
    }
  }

  private[this] def readTicketNumbers(ticket: Config): List[Integer] = {
    ticket.getIntList(TicketNumbersConfigPath).toList
  }

  private[this] def readTicketStars(ticket: Config): List[Integer] = {
    ticket.getIntList(TicketStarsConfigPath).toList
  }

  private[this] def readTicket(ticketConfig: Config): (List[Integer], List[Integer]) = {
    val ticket: Config = ticketConfig.getConfig(TicketConfigPath)

    val numbers: List[Integer] = readTicketNumbers(ticket)
    val stars: List[Integer] = readTicketStars(ticket)

    (numbers, stars)
  }
}
