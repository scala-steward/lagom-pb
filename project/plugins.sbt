addSbtPlugin("com.lightbend.lagom" % "lagom-sbt-plugin" % "1.6.2")
addSbtPlugin("com.dwijnand" % "sbt-dynver" % "4.1.0")
addSbtPlugin("com.typesafe.sbt" % "sbt-native-packager" % "1.7.3")
addSbtPlugin("org.scalameta" % "sbt-scalafmt" % "2.4.0")
addSbtPlugin("org.scoverage" % "sbt-scoverage" % "1.6.1")
addSbtPlugin("com.beautiful-scala" % "sbt-scalastyle" % "1.4.0")
addSbtPlugin("ch.epfl.scala" % "sbt-scalafix" % "0.9.17")
addSbtPlugin("au.com.onegeek" %% "sbt-dotenv" % "2.1.190")
addSbtPlugin("com.lightbend.akka.grpc" %% "sbt-akka-grpc" % "1.0.0")
resolvers += Resolver.bintrayRepo("playframework", "maven")
libraryDependencies += "com.lightbend.play" %% "play-grpc-generators" % "0.8.2"
addSbtPlugin("org.jmotor.sbt" % "sbt-dependency-updates" % "1.2.2")
addSbtPlugin("com.typesafe.sbt" % "sbt-site" % "1.4.0")
addSbtPlugin("com.geirsson" % "sbt-ci-release" % "1.5.3")
addSbtPlugin("net.virtual-void" % "sbt-dependency-graph" % "0.10.0-RC1")
