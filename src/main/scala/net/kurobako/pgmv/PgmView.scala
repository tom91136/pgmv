package net.kurobako.pgmv

import scalafx.beans.property.ObjectProperty
import scalafx.beans.value.ObservableValue
import scalafx.scene.layout.Pane
import scalafx.scene.paint.{Color, Paint}
import scalafx.scene.shape.Rectangle
import scalafx.Includes._

class PgmView(pgm: Pgm, paint: ObjectProperty[Color]) extends Pane {


	val pxs = for {
		(row, py) <- pgm.image.zipWithIndex
		(pixel, px) <- row.zipWithIndex
	} yield new Rectangle() {
		x = px
		y = py
		opacity = pixel.toDouble / pgm.max
		width = 1
		height = 1
		fill <== paint
	}

	children = pxs


}
