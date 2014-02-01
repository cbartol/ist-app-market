name := "ist-app-market"

version := "1.2.0-SNAPSHOT"

libraryDependencies ++= Seq(
  "org.fenixedu" % "fenixedu-sdk-core" % "2.0.0",
  "mysql" % "mysql-connector-java" % "5.1.18",
  "com.sun.jersey" % "jersey-core" % "1.17.1",
  javaJdbc,
  javaEbean,
  cache
)     

resolvers += "fenix-ashes-maven-repository" at "https://fenix-ashes.ist.utl.pt/nexus/content/groups/fenix-ashes-maven-repository"

play.Project.playJavaSettings
