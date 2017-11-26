package net.kurobako.pgmv

import scalafx.scene.control._
import scalafx.scene.layout.{StackPane, VBox}
import scalafxml.core.macros.sfxml
import scalafx.Includes._
import scalafx.scene.input.TransferMode
import better.files._

import scalafx.beans.property.ObjectProperty
import scalafx.scene.paint.Color


@sfxml
class MainController(root: VBox,
					 close: MenuItem,
					 stack: Button,
					 vhSplit: Button,
					 vSplit: Button,
					 files: ListView[String],
					 opacity: Slider,
					 colour: ColorPicker,
					 container: StackPane
					) {


	// dnd

	case class PgmLayer(pgm: Pgm)


	root.scene.onChange { (_, _, s) =>
		if (s != null) {
			s.onDragOver = event => {
				if (event.getGestureSource != s &&
					event.getDragboard.hasFiles)
					event.acceptTransferModes(TransferMode.CopyOrMove: _*)
				event.consume()
			}
			s.onDragDropped = event => {
				if (event.getDragboard.hasFiles) {
					println(event.getDragboard.getFiles)
					event.setDropCompleted(true)

					container.children = event.getDragboard.files
						.map {_.toScala}
						.map { f =>
							Pgm.read(f).fold(
							{ e => new Label(e.getMessage) },
							{ pgm => new PgmView(pgm, ObjectProperty(Color.Black)) }
							)
						}


				}
				event.consume()
			}
		}
	}


}
