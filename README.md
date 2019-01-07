# Nexus Jenkins libraries

A repo containing Jenkins libraries to compare current build dependencies to whats actually in Nexus.

## Getting Started

clone the repository and set it up as a shared libraries under the current Jenkins master.
https://jenkins.io/doc/book/pipeline/shared-libraries/

### Prerequisites

What things you need to install the software and how to install them

```
* A Jenkins Master
* Nexus 3.1+
```

### To do
```
* Set up a Nexus web request library so we conform to dry
* Finish Compare to Nexus using the beta api once Nexus has been upgraded
* Ask Moulali to set up these as jenkins shared libraries if possible.
* If the above cannot be done nexusdep.groovy can run as one long groovy step.
```
