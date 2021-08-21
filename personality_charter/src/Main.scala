import java.io.File
import java.nio.file.{Files, Paths}
import java.util

import scala.jdk.CollectionConverters._

object Main {
  def main(args: Array[String]): Unit = {
    println("Hello world")

    val f = new File("../Personality Study 1 data dump BASED IN ITALY.rtf")
    println(f.getAbsolutePath)
    println(f.exists())

    val lines = Files.readAllLines(Paths.get(f.toURI)).asScala
      .filter(_.nonEmpty)
      .filterNot(_.contains("-------------------------------------"))

    println(s"header line:${lines.head}")
    val headers = parseCols(lines.head)
    println(s"headers: $headers")

    val colToHeader = lines.map(line => {
      val cols = parseCols(line)
      headers.zip(cols)
    }).toSeq
    val players = parsePlayers(colToHeader)
    println(s"players: $players")
  }

  def parseCols(line: String): Seq[String] = line.trim
    .split("\\|")
    .map(_.trim)
    .filter(_.nonEmpty)

  def parsePlayers(lines: Seq[String]): List[Player] = {
//    val fields
//    return Player(
//      ???
//    ) + parsePlayers(lines.tail)
  return ???
  }

  case class Player(id: String, name: String)

}