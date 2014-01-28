name := "ist-app-market"

version := "1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  "org.fenixedu" % "fenixedu-sdk" % "1.0.1",
  "mysql" % "mysql-connector-java" % "5.1.18",
  javaJdbc,
  javaEbean,
  cache
)     

resolvers += "fenix-ashes-maven-repository" at "https://fenix-ashes.ist.utl.pt/nexus/content/groups/fenix-ashes-maven-repository"

play.Project.playJavaSettings
