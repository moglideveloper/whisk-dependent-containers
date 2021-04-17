name := "dependent-containers"

libraryDependencies ++= Seq(
  "com.whisk" %% "docker-testkit-impl-docker-java" % "0.9.9" excludeAll ( ExclusionRule("com.github.docker-java") ),
  "com.whisk" %% "docker-testkit-impl-spotify" % "0.9.9",
  "javax.activation" % "activation" % "1.1.1",
  "com.github.docker-java" % "docker-java" % "3.2.7"
)
