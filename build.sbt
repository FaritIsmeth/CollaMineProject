import play.Project._
import sbt._
import Keys._
import com.typesafe.config._

val appDependencies = Seq(
	"commons-io" % "commons-io" % "2.4"  //add this here
)

name := """hello-play-java"""

version := "1.0-SNAPSHOT"


libraryDependencies ++= Seq(
    "mysql" % "mysql-connector-java" % "5.1.18",
	"org.webjars" %% "webjars-play" % "2.2.0", 
	"org.webjars" % "bootstrap" % "2.3.1",
	"org.mindrot"  % "jbcrypt"   % "0.3m",
    javaCore,
    javaJdbc,
    javaEbean
    )

playJavaSettings

