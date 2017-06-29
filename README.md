# KernelHaven ![Build Status of KernelHaven Infrastructure](http://jenkins.sse.uni-hildesheim.de/buildStatus/icon?job=KernelHaven_Infrastructure)

KernelHaven offers a generic infrastructure for performing different analyses on product lines. This repository contains the main infrastructure, plug-ins are located in separate repositories.

## Download and Installation

* [KernelHaven infrastructure (without sources)](http://jenkins.sse.uni-hildesheim.de/job/KernelHaven_Infrastructure/lastSuccessfulBuild/artifact/build/jar/kernelhaven.jar)
* [KernelHaven infrastructure (sources included)](http://jenkins.sse.uni-hildesheim.de/job/KernelHaven_Infrastructure/lastSuccessfulBuild/artifact/build/jar/kernelhavenwithsource.jar)

## Documentation

* [The user documentation](https://github.com/KernelHaven/Documentation/raw/master/UserDocumentation/Arbeit.pdf) is the user manual
* [The developer documentation](https://github.com/KernelHaven/Documentation/raw/master/DeveloperDocumentation/Arbeit.pdf) describes how to write new plug-ins for the infrastructure

## Plug-ins

| Plug-in | Type | License | Download | Status |
|---------|------|---------|----------|--------|
|[KconfigReaderExtractor](https://github.com/KernelHaven/KconfigReaderExtractor)| **VariabilityModel-Extractor:** Kconfig | [GPLv3](http://www.gnu.org/licenses/gpl-3.0.html) | [JAR](http://jenkins.sse.uni-hildesheim.de/job/KernelHaven_KconfigReaderExtractor/lastSuccessfulBuild/artifact/build/jar/kconfigreaderextractor.jar), [JAR with sources](http://jenkins.sse.uni-hildesheim.de/job/KernelHaven_KconfigReaderExtractor/lastSuccessfulBuild/artifact/build/jar/kconfigreaderextractorwithsource.jar) | ![Build Status of KconfigReaderExtractor](http://jenkins.sse.uni-hildesheim.de/buildStatus/icon?job=KernelHaven_KconfigReaderExtractor) |
|[UndertakerExtractor](https://github.com/KernelHaven/UndertakerExtractor)| **CodeModel-Extractor:** `*.c, *.h, *.S` | [GPLv3](http://www.gnu.org/licenses/gpl-3.0.html) | [JAR](http://jenkins.sse.uni-hildesheim.de/job/KernelHaven_UndertakerExtractor/lastSuccessfulBuild/artifact/build/jar/undertakerextractor.jar), [JAR with sources](http://jenkins.sse.uni-hildesheim.de/job/KernelHaven_UndertakerExtractor/lastSuccessfulBuild/artifact/build/jar/undertakerextractorwithsource.jar) | ![Build Status of KernelHaven_UndertakerExtractor](http://jenkins.sse.uni-hildesheim.de/buildStatus/icon?job=KernelHaven_UndertakerExtractor) |
|[TypeChefExtractor](https://github.com/KernelHaven/TypeChefExtractor)| **CodeModel-Extractor:** `*.c, *.h, *.S` | [Apache License v2](http://www.apache.org/licenses/LICENSE-2.0.html) |  [JAR](http://jenkins.sse.uni-hildesheim.de/job/KernelHaven_TypeChefExtractor/lastSuccessfulBuild/artifact/build/jar/TypeChefExtractor.jar), [JAR with sources](http://jenkins.sse.uni-hildesheim.de/job/KernelHaven_TypeChefExtractor/lastSuccessfulBuild/artifact/build/jar/TypeChefExtractor_withsource.jar) | ![Build Status of TypeChefExtractor](http://jenkins.sse.uni-hildesheim.de/buildStatus/icon?job=KernelHaven_TypeChefExtractor) |
|[KbuildMinerExtractor](https://github.com/KernelHaven/KbuildMinerExtractor)| **BuildModel-Extractor:** Kbuild (`Kbuild*, Makefile*`)| [GPLv3](http://www.gnu.org/licenses/gpl-3.0.html) | [JAR](http://jenkins.sse.uni-hildesheim.de/job/KernelHaven_KbuildMinerExtractor/lastSuccessfulBuild/artifact/build/jar/kbuildminerextractor.jar), [JAR with sources](http://jenkins.sse.uni-hildesheim.de/job/KernelHaven_KbuildMinerExtractor/lastSuccessfulBuild/artifact/build/jar/kbuildminerextractorwithsource.jar) | ![Build Status of KbuildMinerExtractor](http://jenkins.sse.uni-hildesheim.de/buildStatus/icon?job=KernelHaven_KbuildMinerExtractor) |
|[CnfUtils](https://github.com/KernelHaven/CnfUtils)| Utilities for analysis plug-ins| [Apache License v2](http://www.apache.org/licenses/LICENSE-2.0.html) | [JAR](http://jenkins.sse.uni-hildesheim.de/job/KernelHaven_CnfUtils/lastSuccessfulBuild/artifact/build/jar/cnfutils.jar), [JAR with sources](http://jenkins.sse.uni-hildesheim.de/job/KernelHaven_CnfUtils/lastSuccessfulBuild/artifact/build/jar/cnfutilswithsource.jar) | ![Build Status of CnfUtils](http://jenkins.sse.uni-hildesheim.de/buildStatus/icon?job=KernelHaven_CnfUtils) |
|[UnDeadAnalyzer](https://github.com/KernelHaven/UnDeadAnalyzer)| **Analysis:** Detection of dead code and unused variables of the variability model | [Apache License v2](http://www.apache.org/licenses/LICENSE-2.0.html) | [JAR](http://jenkins.sse.uni-hildesheim.de/job/KernelHaven_UnDeadAnalyzer/lastSuccessfulBuild/artifact/build/jar/defaultanalyses.jar), [JAR with sources](http://jenkins.sse.uni-hildesheim.de/job/KernelHaven_UnDeadAnalyzer/lastSuccessfulBuild/artifact/build/jar/defaultanalyseswithsource.jar) | ![Build Status of UnDeadAnalyzer](http://jenkins.sse.uni-hildesheim.de/buildStatus/icon?job=KernelHaven_UnDeadAnalyzer) |
|[FeatureEffectAnalyzer](https://github.com/KernelHaven/FeatureEffectAnalysis)| **Analysis:** Detection of presence conditions and feature effects | [Apache License v2](http://www.apache.org/licenses/LICENSE-2.0.html) | [JAR](http://jenkins.sse.uni-hildesheim.de/view/KernelHaven/job/KernelHaven_FeatureEffectAnalysis/lastSuccessfulBuild/artifact/build/jar/FeatureEffectAnalysis.jar), [JAR with sources](http://jenkins.sse.uni-hildesheim.de/view/KernelHaven/job/KernelHaven_FeatureEffectAnalysis/lastSuccessfulBuild/artifact/build/jar/FeatureEffectAnalysis_withsource.jar) | ![Build Status of FeatureEffectAnalyzer](http://jenkins.sse.uni-hildesheim.de/buildStatus/icon?job=KernelHaven_FeatureEffectAnalysis) |

## Acknowledgments

This work is partially supported by the ITEA3 project [REVaMP<sup>2</sup>](https://itea3.org/project/revamp2.html), funded by the BMBF (German Ministry of Research and Education) under grant 01IS16042H.

We would like to thank the following persons, who contributed to the initial version of KernelHaven: [Moritz Flöter](http://www.moritzf.de/), Adam Krafczyk, Alice Schwarz, Kevin Stahr, Johannes Ude, Manuel Nedde, Malek Boukhari, and Marvin Forstreuter.
