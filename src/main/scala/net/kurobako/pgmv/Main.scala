package net.kurobako.pgmv

import com.google.common.io.Resources
import com.typesafe.scalalogging.LazyLogging

import scala.reflect.runtime.universe.typeOf
import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.scene.Scene
import scalafxml.core.{DependenciesByType, FXMLView}
import scalafx.Includes._


object Main extends JFXApp with LazyLogging {


	val root = FXMLView(
		Resources.getResource("Main.fxml"),
		new DependenciesByType(Map(
			typeOf[PrimaryStage] -> stage)))


	stage = new JFXApp.PrimaryStage()
	stage.title = "PGMV"
	stage.scene = new Scene(root)
	logger.info("Scene ready")


}
