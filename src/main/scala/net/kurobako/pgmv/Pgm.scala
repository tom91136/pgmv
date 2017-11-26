package net.kurobako.pgmv

import java.io.{BufferedInputStream, BufferedReader, DataInputStream}
import java.nio.charset.StandardCharsets

import better.files.File
import net.kurobako.pgmv.Pgm.{Image, Version}

import scala.io.Source
import scala.util.{Failure, Success, Try}


case class Pgm(version: Version,
			   dimension: (Int, Int),
			   max: Int,
			   image: Image) {

	import better.files.Dsl.SymbolicOperations

	def write(file: File): Try[File] = Try {
		if (!file.isEmpty) throw new IllegalArgumentException(s"File $file not empty")
		file.createIfNotExists()
		file << version.toString
		file << s"${dimension._1} ${dimension._2}"
		file << max.toString
		file.appendByteArray((for {
			r <- image
			c <- r
		} yield c.toByte).toArray)
		file
	}

	override def toString: String = {
		val scales = image.map { r =>
			r.map { v => v.toInt.toString.padTo(4, ' ') }.mkString
		}.mkString("\n")
		s"""$version
		   |${dimension.toString()}
		   |$max
		   |$scales""".stripMargin
	}
}

object Pgm {

	trait Version
	case object P5 extends Version

	type Image = IndexedSeq[IndexedSeq[Char]]


	def read(file: File): Try[Pgm] = {

		val is = new DataInputStream(file.newInputStream)
		val reader = Source.fromInputStream(is, StandardCharsets.ISO_8859_1.name).bufferedReader()

		def fail(reason: String) = Failure(new IllegalArgumentException(reason))

		val l1 = reader.readLine()
		val l2 = reader.readLine()
		val l3 = reader.readLine()
		if (Seq(l1, l2, l3).contains(null)) throw new IllegalArgumentException(s"EOL for $file")

		for {
			version <- l1.trim match {
				case "P5" => Success(P5: Version)
				case v@_  => fail(s"bad version $v")
			}
			dimension@(w, h) <- l2
				.trim.split(" ").toList match {
				case f :: s :: Nil => for {
					w <- Try {f.toInt}
					h <- Try {s.toInt}
				} yield (w, h)
				case v@_           => fail(s"bad dimension $v")
			}
			max <- Try {l3.trim.toInt}
			image <- Try {
				val is = new DataInputStream(file.newInputStream)
				is.skipBytes(Seq(l1, l2, l3).map {_.length + 1}.sum)
				Vector.fill(h) {Vector.fill(w) {is.readUnsignedByte().toChar}}
			}
		} yield Pgm(version, dimension, max, image)
	}


}
