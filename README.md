# KernelHaven ![Build Status of KernelHaven Infrastructure](http://jenkins.sse.uni-hildesheim.de/buildStatus/icon?job=KernelHaven_Infrastructure)
KernelHaven offers a generic infrastructure for performing different analyses on
product lines. This repository contains the main infrastructure, plug-ins are
located in separate repositories.

## Download and Installation 
* [KernelHaven infrastructure (without sources)](http://jenkins.sse.uni-hildesheim.de/job/KernelHaven_Infrastructure/lastSuccessfulBuild/artifact/build/jar/kernelhaven.jar)
* [KernelHaven infrastructure (sources included)](http://jenkins.sse.uni-hildesheim.de/job/KernelHaven_Infrastructure/lastSuccessfulBuild/artifact/build/jar/kernelhavenwithsource.jar)

## Documentation
* [The user documentation](https://github.com/KernelHaven/UserDocumentation/raw/master/Arbeit.pdf) describes how to use KernelHaven
* [The developer documentation](https://github.com/KernelHaven/DeveloperDocumentation/raw/master/Arbeit.pdf) describes how to write new plug-ins for the infrastructure

## Plug-ins

| Plug-in | Type | License | Download | Status |
|---------|------|---------|----------|--------|
|[KconfigReaderExtractor](https://github.com/KernelHaven/KconfigReaderExtractor)| **VariabilityModel-Extractor:** Kconfig | [GPLv3](http://www.gnu.org/licenses/gpl-3.0.html) | [JAR](http://jenkins.sse.uni-hildesheim.de/view/KernelHaven/job/KernelHaven_KconfigReaderExtractor/lastSuccessfulBuild/artifact/build/jar/kconfigreaderextractor.jar), [JAR with sources](http://jenkins.sse.uni-hildesheim.de/view/KernelHaven/job/KernelHaven_KconfigReaderExtractor/lastSuccessfulBuild/artifact/build/jar/kconfigreaderextractorwithsource.jar) | ![Build Status of KconfigReaderExtractor](http://jenkins.sse.uni-hildesheim.de/buildStatus/icon?job=KernelHaven_KconfigReaderExtractor) |
|[UndertakerExtractor](https://github.com/KernelHaven/UndertakerExtractor)| **CodeModel-Extractor:** `*.c, *.h, *.S` | [GPLv3](http://www.gnu.org/licenses/gpl-3.0.html) |
|[KbuildMinerExtractor](https://github.com/KernelHaven/KbuildMinerExtractor)| **BuildModel-Extractor:** Kbuild (`Kbuild*, Makefile*`)| [GPLv3](http://www.gnu.org/licenses/gpl-3.0.html) |
|[CnfUtils](https://github.com/KernelHaven/CnfUtils)| Utilities for analysis plug-ins| [Apache License v2](http://www.apache.org/licenses/LICENSE-2.0.html) |
|[UnDeadAnalyzer](https://github.com/KernelHaven/UnDeadAnalyzer)| **Analyzer:** Detection of dead code and unused variables of the variability model | [Apache License v2](http://www.apache.org/licenses/LICENSE-2.0.html) |
