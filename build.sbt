
lazy val root = (project in file(".")).
  settings(
    name := "MSL Scraper",
    version := "1.0",
    scalaVersion := "2.12.3",
    mainClass in Compile := Some("com.stilbruch.mslscraper.Scraper")
  )

// https://mvnrepository.com/artifact/org.jsoup/jsoup
libraryDependencies += "org.jsoup" % "jsoup" % "1.8.3"
libraryDependencies += "me.tongfei" % "progressbar" % "0.5.5"
