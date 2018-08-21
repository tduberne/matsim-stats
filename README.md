MATSim Usage Statistics Module
==============================

This project defines a module for the [MATSim Software](http://www.matsim.org)
that is able to collect usage statistics and send them to a remote server.

Adding the Module as a Dependency
---------------------------------

The module is distributed using jitpack. For maven, add the following snippets:

```xml
<repositories>
	<!-- other repositories -->

	<repository>
		<!-- for matsim stats -->
		<id>jitpack.io</id>
		<url>httpttps://jitpack.io</url>
	</repository>
</repositories>
								
```

```xml
	<dependencies>
		<!-- other dependencies -->

		<dependency>
			<groupId>com.github.tduberne</groupId>
			<artifactId>matsim-stats</artifactId>
			<version>[version number]</version>
		</dependency>
	</dependencies>
```

where `[version number]` is any commit number or tags
(see  on this repository.


How to Use
----------

The module provides one Guice module and one config group to configure what and how
data is transmitted. To enable the module in you simulations, you will need to add
the two following lines to your "run script":

```java
public static void main(String... args) {
	String configFile = args[0];

	// line 1: add the config group.
	Config config = ConfigUtils.loadConfig(configFile, new UsageStatsConfigGroup() );

	Controler controler = new Controler(config);

	// line 2: add the Guice module that will enable stats collection and push
	controler.addOverridingModule(new UsageStatsModule());

	// here you go!
	controler.run();
}
```

Design Decisions
----------------

The module is implemented in [Kotlin](https://kotlinlang.org/), 
instead of Java,
as it allows much more concise definition of data classes,
that this module uses heavily.

This is the MATSim side of the project.
The server side is implemented separately.
