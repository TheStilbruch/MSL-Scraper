package com.stilbruch.mslscraper

import java.io.{File, FileWriter}
import java.util.Scanner

case class Server(name: String, ip: String, port: Int, players: Int, online: Boolean)

object Scraper {

  def main(args: Array[String]): Unit = {

    val mcsScraper = new WebsiteScraper(
      2179,
      index => s"http://minecraftservers.org/index/$index",
      page => {
        var servers: Set[Server] = Set()

        val table = page.select("table[class=serverlist]").first()
        val serverRows = table.select("tr")

        serverRows.forEach(row => {

          val cols = row.select("td")

          if (cols.size() == 5) {

            val ip = cols.get(2).text().replace(" copy copied", "").split(":")

            val server = Server(
              cols.get(1).text(),
              ip(0),
              if (ip.length == 2) ip(1).toInt else 0,
              cols.get(3).text().split("/")(0).toInt,
              if (cols.get(4).text().equals("offline")) false else true)

            servers += server

            //println(server.ip)
          }
        })
        servers
      }
    )

    val scraperMap = Map(
      1 -> mcsScraper
    )

    //////////
    // Input
    /////////
    val in = new Scanner(System.in)
    var choice = 0

    while (choice == 0) {
      println()
      println(" __  __  ___  __      ___   __  ___    __   ___  ___  ___  ")
      println("(  \\/  )/ __)(  )    / __) / _)(  ,)  (  ) (  ,\\(  _)(  ,) ")
      println(")    ( \\__ \\ )(__   \\__ \\( (_  )  \\  /__\\  ) _/ ) _) )  \\ ")
      println("(_/\\/\\_)(___/(____)  (___/ \\__)(_)\\_)(_)(_)(_)  (___)(_)\\_)")
      println("Please choose a server list to download from")
      println()
      println("1) MineCraftServers.org")
      println()
      println("0) Quit")

      val input = in.nextInt()

      if (input == 0) System.exit(0)
      else if (scraperMap.contains(input)) choice = input
      else println("Invalid input!")


      val csvFile = new File("servers.csv")
      if (!csvFile.exists()) csvFile.createNewFile()

      val writer = new FileWriter(csvFile)

      writer.append("name,ip,port,players,online\n")

      scraperMap(choice).start(16,
        onComplete = servers => {
          servers.foreach(server => {
            writer.append(s"${server.name},${server.ip},${server.port},${server.players},${server.online}\n")
          })
        }
      )

      writer.flush()
      writer.close()
    }
  }
}