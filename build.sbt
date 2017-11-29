
lazy val `pgmv` = (project in file(".")).
	settings(
		addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full),

		inThisBuild(List(
			organization := "net.kurobako",
			scalaVersion := "2.12.3",
			version := "0.1.0-SNAPSHOT"
		)),
		name := "pgmv",
		mainClass in Compile := Some("net.kurobako.pgmv.Main"),
		resolvers ++= Seq(Resolver.jcenterRepo),
		libraryDependencies ++=
		Seq(
			"com.github.pathikrit" %% "better-files" % "3.2.0",
			"com.google.guava" % "guava" % "23.4-jre",
			"ch.qos.logback" % "logback-classic" % "1.2.3",
			"com.typesafe.scala-logging" %% "scala-logging" % "3.7.2",
			"org.controlsfx" % "controlsfx" % "8.40.12",
			"org.scalafx" %% "scalafx" % "8.0.102-R11",
			"org.scalafx" %% "scalafxml-core-sfx8" % "0.4",
			"org.scalafx" %% "scalafxml-macwire-sfx8" % "0.4",
			"org.fxmisc.easybind" % "easybind" % "1.0.3",
			"net.kurobako.gesturefx" % "gesturefx" % "0.2.0",
		)
	)
