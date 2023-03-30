# Interoperability Solver

  Interoperability Solver is a process mining plugin for ProM that helps solve interoperability problems. The plugin enables users to verify the conformity between an event log and its associated process model, repair the model based on the result of the alignment, and annotate the activities of the repaired model according to the content and type of messages associated with the events.

  This plugin aims to address the problem of interoperability by implementing a process mining solution that helps to remedy conflicts between processes. It allows users to import PNML files (process models) and XES files (event logs), and then perform syntactic analysis to extract a Petri net and a Log object, respectively. Users can also choose the alignment algorithm they wish to apply, as well as its parameters.

  Once the alignment is performed, the plugin checks if the event log is conformant to the process model according to the chosen algorithm. If not, the process model is repaired based on the results of the alignment. Finally, the plugin annotates the repaired model according to the types and contents of the messages associated with the events.

  This plugin is integrated into the ProM framework, providing users with a seamless and user-friendly experience. To get started with Interoperability Solver, simply import your PNML and XES files, choose your alignment algorithm, and let the plugin do the rest!

### Installation
To install Interoperability Solver, follow these steps:

- Download and install the ProM framework.
- Clone or download the Interoperability Solver repository.
- Extract the files to the ProM plugins folder.
- Launch ProM and select Interoperability Solver from the list of available plugins.


The required packages for Interoperability Solver to function properly are taken from the ProM repo: https://svn.win.tue.nl/repos/prom/Packages/
