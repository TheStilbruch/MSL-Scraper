package com.stilbruch.mslscraper

import com.stilbruch.mslscraper.Scraper.Server
import me.tongfei.progressbar.{ProgressBar, ProgressBarStyle}
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

import scala.collection.mutable

class WebsiteScraper(maxIndex: Int, urlGen: (Int => String), pageParser: (Document => Set[Server])) {

  var index = 0
  val servers = mutable.Set.empty[Server]
  val progressBar: ProgressBar = new ProgressBar("Downloading", maxIndex, 500, System.out, ProgressBarStyle.ASCII)

  def getIndex = synchronized {

    index += 1
    index
  }

  def start(numThreads: Int,
            perServer: (Server => Unit) = (server => Unit),
            onComplete: (mutable.Set[Server] => Unit) = (servers => Unit)): Unit = {

    progressBar.start()

    for (j <- 0 to numThreads) {

      val thread = new Thread {
        override def run(): Unit = {

          var i = getIndex

          while (i < maxIndex) {

            val doc = Jsoup.connect(urlGen.apply(getIndex))
              .userAgent("Mozilla")
              .get

            val additions = pageParser.apply(doc)
            additions.foreach(perServer)
            servers ++= additions

            progressBar.step()
            i = getIndex
          }
        }
      }

      thread.start()
      thread.join()
    }
    progressBar.stop()

    onComplete.apply(servers)
  }
}
