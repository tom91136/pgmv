package net.kurobako.pgmv


import scalafx.scene.{Group, Node}
import scalafx.scene.image.{Image, WritableImage}
import scalafx.scene.paint.Color
import scalafx.Includes._
import scalafx.scene.layout.{Pane, Region}
import scalafx.scene.shape.Rectangle


object PgmView {


	final val Scale = 30

	def apply(pgm: Pgm): Node = {
		val Pgm(_, (w, h), max, image) = pgm
		val pane = new Pane() {
			styleClass = Seq("image")
			minWidth = w * Scale
			minHeight = h * Scale
			maxWidth <== minWidth
			maxHeight <== minHeight
		}
		pane.children = for {
			(row, py) <- image.zipWithIndex
			(pixel, px) <- row.zipWithIndex
		} yield new Rectangle() {
			styleClass = Seq("pixel")
			x = px * Scale
			y = py * Scale
			width = Scale
			height = Scale
			fill = Color.Black.opacity(pixel.toDouble / max)
		}
		pane
	}
}
