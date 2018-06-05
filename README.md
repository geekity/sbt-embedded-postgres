sbt-embedded-postgres
=====================

Support for running PostgreSQL for use in integration tests.

Installation
------------
Add the following to your `project/plugins.sbt` file:
```
resolvers += Resolver.bintrayRepo("geekity", "sbt-plugins")

addSbtPlugin("io.nhanzlikova.sbt" % "sbt-embedded-postgres" % "1.2.0")
```

Configuration
-------------
To use the embedded postgres server, just define a dependency on `startPostgres` or `postgresConnectionString`.
This will make sbt execute `startPostgres` before starting your process. For example:
```
enablePlugins(EmbeddedPostgresPlugin)
javaOptions += s"-DDATABASE_URL=${postgresConnectionString.value}"
postgresSilencer := true
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

The output from the embedded postgres can be suppressed by setting `postgresSilencer := true`.
