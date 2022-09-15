# proM_plugin
A process mining plugin for proM to solve interoperability problems.

1. It takes an event log (XES file) and a petri net (PNML file) as inputs.
2. It mines a petri net from the event log (Process Model Discovery).
3. It checks the modeled and the observed behavior for conformance.
4. It returns an enhanced process model based on the one discovered from the event log.
