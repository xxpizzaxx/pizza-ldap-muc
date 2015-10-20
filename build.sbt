name := "pizza-ldap-muc"

version := "1.0"

scalaVersion := "2.11.7"

resolvers += "andimiller-bintray" at "http://dl.bintray.com/andimiller/maven/"
resolvers += "Local Maven Repository" at "file:///"+Path.userHome+"/.m2/repository"



unmanagedJars in Compile ++= {
  val base = baseDirectory.value
  val baseDirectories = (base / "libs")
  val customJars = (baseDirectories ** "*.jar")
  customJars.classpath
}

libraryDependencies += "org.igniterealtime.whack" % "core" % "2.0.0"
libraryDependencies += "moe.pizza" % "pizza-auth" % "1.0-SNAPSHOT" exclude("moe.pizza", "eveapi")
// libraryDependencies += "moe.pizza" % "eveapi" % "0.7"