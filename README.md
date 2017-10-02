sbt-embedded-postgres
=====================

Support for running PostgreSQL for use in integration tests.

Installation
------------
Add the following to your `project/plugins.sbt` file:
```
resolvers += Resolver.url("io.nhanzlikova.sbt", url("https://dl.bintray.com/geekity/sbt-plugins/"))(Resolver.ivyStylePatterns)

addSbtPlugin("io.nhanzlikova.sbt" % "sbt-embedded-postgres" % "1.1.0")
```

Configuration
-------------
The following represents an example configuration in `build.sbt` to use [sbt-embedded-postgres](https://github.com/geekity/sbt-embedded-postgres)

To use the postgresql in your project have your tests depend on starting the PostgreSQL server.
```
lazy val root = (project in file(".")).enablePlugins(EmbeddedPostgresPlugin)

testOptions in Test += postgresTestCleanup.value // clean up postgresql after tests

test in Test := (test in Test).dependsOn(startPostgres)

testOnly in Test := (testOnly in Test).dependsOn(startPostgres)
```

Configuration options (in `build.sbt`) and their defaults
```
postgresPort := 25432
postgresDatabase := "database",
postgresUsername := "admin"
postgresPassword := "admin"
postgresVersion := PRODUCTION // IVersion from ru.yandex.qatools.embed.postgresql.distribution.Version.Main
```

If you want to run your build on a CI server, it is advised to let sbt chose a port at random. For this use case is an utility function defined.    
For example: `postgresPort := EmbeddedPostgresPlugin.getFreePort(25432 to 25532)`.  

The default connection string is `jdbc:postgresql://localhost:25432/database`. It is accessible in sbt with the setting key `postgresConnectionString`. 

The output from the embedded postgres can be suppressed by setting `postgresSilencer := true`.
