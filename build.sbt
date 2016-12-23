name := "Typewriter"

version := "1.0"

scalaVersion := "2.11.0"

libraryDependencies += "org.planet42" %% "laika-core" % "0.6.0"
libraryDependencies += "net.jcazevedo" %% "moultingyaml" % "0.3.1"
libraryDependencies += "com.github.nscala-time" %% "nscala-time" % "2.14.0"
libraryDependencies ++= {
  val akkaVersion = "2.4.11"
  Seq(
    "com.typesafe.akka" %% "akka-actor"      % akkaVersion,
    "com.typesafe.akka" %% "akka-http-spray-json-experimental"  % akkaVersion,
    "com.typesafe.akka" %% "akka-slf4j"      % akkaVersion,
    "ch.qos.logback"    %  "logback-classic" % "1.1.3",
    "com.typesafe.akka" %% "akka-testkit"    % akkaVersion   % "test",
    "org.scalatest"     %% "scalatest"       % "2.2.0"       % "test"
  )
}

libraryDependencies += "de.zalando" %% "beard" % "0.2.0"

resolvers ++= Seq(
  "zalando-maven" at "https://dl.bintray.com/zalando/maven"
)
