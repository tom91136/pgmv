package net.kurobako.pgmv

import java.util.Collections
import javafx.collections.ObservableList
import javafx.scene.control

import scalafx.scene.control._
import scalafx.scene.layout.{HBox, StackPane, VBox}
import scalafxml.core.macros.sfxml
import scalafx.Includes._
import scalafx.scene.input.TransferMode
import better.files._
import com.google.common.io.Resources
import com.typesafe.scalalogging.LazyLogging
import net.kurobako.gesturefx.GesturePane
import net.kurobako.gesturefx.GesturePane.FitMode
import net.kurobako.pgmv.Main.{logger, stage}

import scalafx.beans.property.ObjectProperty
import scalafx.scene.{Node, Scene}
import scalafx.scene.image.ImageView
import scalafx.scene.paint.Color
import net.kurobako.pgmv.RichScalaFX._

import scala.collection.JavaConverters._
import scalafx.application.JFXApp.PrimaryStage
import scalafx.application.{JFXApp, Platform}
import scalafx.collections.ObservableBuffer
import scalafx.geometry.Orientation
import scalafx.stage.Stage
import scalafxml.core.{DependenciesByType, FXMLView}

case class Layer(name: String, view: Node)


sealed trait ViewOption
case object Single extends ViewOption
case object Stacked extends ViewOption
case object VSplit extends ViewOption
case object HSplit extends ViewOption

@sfxml
class MainController(root: VBox,
					 create: MenuItem,
					 close: MenuItem,
					 single: ToggleButton,
					 stacked: ToggleButton,
					 hSplit: ToggleButton,
					 vSplit: ToggleButton,
					 layers: ListView[Layer],
					 opacity: Slider,
					 colour: ColorPicker,
					 viewport: StackPane
					) extends LazyLogging {

	val viewOption: ObjectProperty[ViewOption] = ObjectProperty(Single)

	create.onAction = { _ =>
		new Stage() {
			title = "PGMV"
			scene = new Scene(FXMLView(
				Resources.getResource("Main.fxml"),
				new DependenciesByType(Map.empty)))
		}.show()
	}


	def invalidateViewport(): Unit = Platform.runLater {

		def selectedLayersOrAll(): ObservableList[Layer] = {
			val selected = layers.getSelectionModel.getSelectedItems
			if (selected.isEmpty) layers.getItems
			else selected
		}

		viewport.children.clear()
		viewOption.value match {
			case Stacked             =>
				viewport.children = selectedLayersOrAll().map {_.view}
			case d@(HSplit | VSplit) =>
				val sp = new SplitPane() {
					orientation = d match {
						case HSplit => Orientation.Horizontal
						case _      => Orientation.Vertical
					}
				}
				sp.items ++= selectedLayersOrAll().map {_.view.delegate}
				viewport.children = sp
			case w@_                 => // single
				println(s"Selected for $w => ${selectedLayersOrAll()}")
				viewport.children = selectedLayersOrAll().head.view
		}
	}


	layers.getSelectionModel.setSelectionMode(SelectionMode.Multiple)
	layers.getSelectionModel.selectedItems.onChange {invalidateViewport()}
	layers.getItems.onChange {invalidateViewport()}
	viewOption.onChange {invalidateViewport()}


	single.onAction = { _ => viewOption.value = Single }
	stacked.onAction = { _ => viewOption.value = Stacked }
	hSplit.onAction = { _ => viewOption.value = HSplit }
	vSplit.onAction = { _ => viewOption.value = VSplit }


	layers.cellFactory = { l =>
		new ListCell[Layer]() {
			item.onChangeOption { o =>
				graphic = o match {
					case Some(Layer(name, _)) =>
						new HBox(
							new Button("▲") {
								onAction = { _ => Collections.rotate(l.getItems, -1) }
							},
							new Button("▼") {
								onAction = { _ => Collections.rotate(l.getItems, 1) }
							},
							new Button("x") {
								onAction = { _ => l.getItems.remove(index.value) }
							},
							new Label(name))
					case None                 => null
				}
			}
		}
	}


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

					layers.getItems ++= event.getDragboard.files
						.map {_.toScala}
						.par
						.map { f =>
							Pgm.read(f).fold({ e =>
								Layer(s"!${f.name}", new Label(e.getMessage))
							},
							{ pgm =>
								val pane = new GesturePane(PgmView(pgm))
								pane.setMinScale(0.0000001f)
								pane.setMaxScale(1000000f)
								pane.setFitMode(FitMode.CENTER)
								Layer(f.name, pane)
							})
						}.toList
					layers.getSelectionModel.selectAll()
				}
				event.consume()
			}
		}
	}


}
