package com.pt.pedrorijo91.euromillions

import ch.qos.logback.classic.Logger
import org.clapper.argot.ArgotConverters._
import org.clapper.argot.ArgotParser
import org.slf4j.LoggerFactory


/**
 * Created by pedrorijo on 26/07/15.
 */
object Application {

  val logger = LoggerFactory.getLogger(Application.getClass).asInstanceOf[Logger]


  def main(args: Array[String]): Unit = {

    val parser = new ArgotParser("Euromillions")
    val checkPrize = parser.flag[Boolean](List("p", "prize"), "Check draw prize.")

    parser.parse(args)

    checkPrize.value match {
      case None => Generator.main(args)
      case Some(x) => PrizeChecker.main(args)
    }

  }

}
