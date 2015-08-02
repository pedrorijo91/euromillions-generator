package com.pt.pedrorijo91

import java.text.SimpleDateFormat
import java.util.Date

/**
 * Created by pedrorijo on 02/07/15.
 */

package object euromillions {

  private[euromillions] val RegularNumbersMaxNumber: Int = 50
  private[euromillions] val StarsMaxNumber: Int = 11

  private[euromillions] val NumberOfRegularNumbers: Int = 5
  private[euromillions] val NumberOfStars: Int = 2

  private[euromillions] val ApiKeyConfigPath = "euromillions.results.api.key"
  private[euromillions] val TicketsConfigPath = "euromillions.tickets"
  private[euromillions] val TicketConfigPath = "ticket"
  private[euromillions] val TicketNumbersConfigPath = "numbers"
  private[euromillions] val TicketStarsConfigPath = "stars"

  private[euromillions] def printPrettyDate(date: Date): String = {
    val dateFormat = new SimpleDateFormat("dd/MM/yyyy")
    dateFormat.format(date);
  }
}
