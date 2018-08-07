MATSim Usage Statistics Module
==============================

This project defines a module for the [MATSim Software](http://www.matsim.org)
that is able to collect usage statistics and send them to a remote server.

Design Decisions
----------------

The module is implemented in [Kotlin](https://kotlinlang.org/), 
instead of Java,
as it allows much more concise definition of data classes,
that this module uses heavily.

This is the MATSim side of the project.
The server side is implemented separately.
