# KernelHaven ![Build Status of KernelHaven Infrastructure](http://jenkins.sse.uni-hildesheim.de/buildStatus/icon?job=KernelHaven_Infrastructure)

KernelHaven offers a generic infrastructure for performing different analyses on product lines. This repository contains the main infrastructure, plug-ins are located in separate repositories.

## Downloads

### Core Infrastructure
These archives contain the core infrastructure only and require additional [plug-ins](https://github.com/KernelHaven/KernelHaven#plug-ins). 
* [KernelHaven infrastructure (without sources)](http://jenkins.sse.uni-hildesheim.de/job/KernelHaven_Infrastructure/lastSuccessfulBuild/artifact/build/jar/KernelHaven.jar)
* [KernelHaven infrastructure (sources included)](http://jenkins.sse.uni-hildesheim.de/job/KernelHaven_Infrastructure/lastSuccessfulBuild/artifact/build/jar/KernelHaven_withsource.jar)

### Bundled Releases
These archives contain the infrastructure as well as all public [plug-ins](https://github.com/KernelHaven/KernelHaven#plug-ins) sorted by license. These bundles contain everything needed to run most experiments. However, they probably contain more than actually needed.
* [KernelHaven (with sources) Apache License v2](http://jenkins.sse.uni-hildesheim.de/view/KernelHaven/job/KernelHaven_Public_Releases/lastSuccessfulBuild/artifact/build/KernelHaven_Apache-2.0_withsource.zip)
* [KernelHaven (with sources) GPLv3](http://jenkins.sse.uni-hildesheim.de/view/KernelHaven/job/KernelHaven_Public_Releases/lastSuccessfulBuild/artifact/build/KernelHaven_GPLv3_withsource.zip)

## Plug-ins

The following table lists commonly used plug-ins for KernelHaven. They mostly focus on analysing the Linux Kernel.

| Plug-in | Type | License | Download | Status |
|---------|------|---------|----------|--------|
|[KconfigReaderExtractor](https://github.com/KernelHaven/KconfigReaderExtractor)| **VariabilityModel-Extractor:** Kconfig | [GPLv3](http://www.gnu.org/licenses/gpl-3.0.html) | [JAR](http://jenkins.sse.uni-hildesheim.de/job/KernelHaven_KconfigReaderExtractor/lastSuccessfulBuild/artifact/build/jar/KconfigReaderExtractor.jar), [JAR with sources](http://jenkins.sse.uni-hildesheim.de/job/KernelHaven_KconfigReaderExtractor/lastSuccessfulBuild/artifact/build/jar/KconfigReaderExtractor_withsource.jar) | ![Build Status of KconfigReaderExtractor](http://jenkins.sse.uni-hildesheim.de/buildStatus/icon?job=KernelHaven_KconfigReaderExtractor) |
|[UndertakerExtractor](https://github.com/KernelHaven/UndertakerExtractor)| **CodeModel-Extractor:** `*.c, *.h, *.S` | [GPLv3](http://www.gnu.org/licenses/gpl-3.0.html) | [JAR](http://jenkins.sse.uni-hildesheim.de/job/KernelHaven_UndertakerExtractor/lastSuccessfulBuild/artifact/build/jar/UndertakerExtractor.jar), [JAR with sources](http://jenkins.sse.uni-hildesheim.de/job/KernelHaven_UndertakerExtractor/lastSuccessfulBuild/artifact/build/jar/UndertakerExtractor_withsource.jar) | ![Build Status of KernelHaven_UndertakerExtractor](http://jenkins.sse.uni-hildesheim.de/buildStatus/icon?job=KernelHaven_UndertakerExtractor) |
|[TypeChefExtractor](https://github.com/KernelHaven/TypeChefExtractor)| **CodeModel-Extractor:** `*.c, *.h, *.S` | [Apache License v2](http://www.apache.org/licenses/LICENSE-2.0.html) |  [JAR](http://jenkins.sse.uni-hildesheim.de/job/KernelHaven_TypeChefExtractor/lastSuccessfulBuild/artifact/build/jar/TypeChefExtractor.jar), [JAR with sources](http://jenkins.sse.uni-hildesheim.de/job/KernelHaven_TypeChefExtractor/lastSuccessfulBuild/artifact/build/jar/TypeChefExtractor_withsource.jar) | ![Build Status of TypeChefExtractor](http://jenkins.sse.uni-hildesheim.de/buildStatus/icon?job=KernelHaven_TypeChefExtractor) |
|[KbuildMinerExtractor](https://github.com/KernelHaven/KbuildMinerExtractor)| **BuildModel-Extractor:** Kbuild (`Kbuild*, Makefile*`)| [GPLv3](http://www.gnu.org/licenses/gpl-3.0.html) | [JAR](http://jenkins.sse.uni-hildesheim.de/job/KernelHaven_KbuildMinerExtractor/lastSuccessfulBuild/artifact/build/jar/KbuildminerExtractor.jar), [JAR with sources](http://jenkins.sse.uni-hildesheim.de/job/KernelHaven_KbuildMinerExtractor/lastSuccessfulBuild/artifact/build/jar/KbuildminerExtractor_withsource.jar) | ![Build Status of KbuildMinerExtractor](http://jenkins.sse.uni-hildesheim.de/buildStatus/icon?job=KernelHaven_KbuildMinerExtractor) |
|[CnfUtils](https://github.com/KernelHaven/CnfUtils)| Utilities for analysis plug-ins| [Apache License v2](http://www.apache.org/licenses/LICENSE-2.0.html) | [JAR](http://jenkins.sse.uni-hildesheim.de/job/KernelHaven_CnfUtils/lastSuccessfulBuild/artifact/build/jar/CnfUtils.jar), [JAR with sources](http://jenkins.sse.uni-hildesheim.de/job/KernelHaven_CnfUtils/lastSuccessfulBuild/artifact/build/jar/CnfUtils_withsource.jar) | ![Build Status of CnfUtils](http://jenkins.sse.uni-hildesheim.de/buildStatus/icon?job=KernelHaven_CnfUtils) |
|[IOUtils](https://github.com/KernelHaven/IOUtils)| Readers and Writers for extractors and analysis plug-ins| [Apache License v2](http://www.apache.org/licenses/LICENSE-2.0.html) | [JAR](http://jenkins.sse.uni-hildesheim.de/job/KernelHaven_IOUtils/lastSuccessfulBuild/artifact/build/jar/IOUtils.jar), [JAR with sources](http://jenkins.sse.uni-hildesheim.de/job/KernelHaven_IOUtils/lastSuccessfulBuild/artifact/build/jar/IOUtils_withsource.jar) | ![Build Status of IOUtils](http://jenkins.sse.uni-hildesheim.de/buildStatus/icon?job=KernelHaven_IOUtils) |
|[UnDeadAnalyzer](https://github.com/KernelHaven/UnDeadAnalyzer)| **Analysis:** Detection of dead code and unused variables of the variability model | [Apache License v2](http://www.apache.org/licenses/LICENSE-2.0.html) | [JAR](http://jenkins.sse.uni-hildesheim.de/job/KernelHaven_UnDeadAnalyzer/lastSuccessfulBuild/artifact/build/jar/defaultanalyses.jar), [JAR with sources](http://jenkins.sse.uni-hildesheim.de/job/KernelHaven_UnDeadAnalyzer/lastSuccessfulBuild/artifact/build/jar/defaultanalyses_withsource.jar) | ![Build Status of UnDeadAnalyzer](http://jenkins.sse.uni-hildesheim.de/buildStatus/icon?job=KernelHaven_UnDeadAnalyzer) |
|[FeatureEffectAnalyzer](https://github.com/KernelHaven/FeatureEffectAnalysis)| **Analysis:** Detection of presence conditions and feature effects | [Apache License v2](http://www.apache.org/licenses/LICENSE-2.0.html) | [JAR](http://jenkins.sse.uni-hildesheim.de/view/KernelHaven/job/KernelHaven_FeatureEffectAnalysis/lastSuccessfulBuild/artifact/build/jar/FeatureEffectAnalysis.jar), [JAR with sources](http://jenkins.sse.uni-hildesheim.de/view/KernelHaven/job/KernelHaven_FeatureEffectAnalysis/lastSuccessfulBuild/artifact/build/jar/FeatureEffectAnalysis_withsource.jar) | ![Build Status of FeatureEffectAnalyzer](http://jenkins.sse.uni-hildesheim.de/buildStatus/icon?job=KernelHaven_FeatureEffectAnalysis) |
|[MetricHaven](https://github.com/KernelHaven/MetricHaven)| **Analysis:** Metric Infrastructure for SPL-Metrics | [Apache License v2](http://www.apache.org/licenses/LICENSE-2.0.html) | [JAR](http://jenkins.sse.uni-hildesheim.de/job/KernelHaven_MetricHaven/lastSuccessfulBuild/artifact/build/jar/MetricHaven.jar), [JAR with sources](http://jenkins.sse.uni-hildesheim.de/job/KernelHaven_MetricHaven/lastSuccessfulBuild/artifact/build/jar/MetricHaven_withsource.jar) | ![Build Status of MetricHaven](http://jenkins.sse.uni-hildesheim.de/buildStatus/icon?job=KernelHaven_MetricHaven) |

## Setup

Although KernelHaven can be configured to use different paths, the usual setup looks like this:

```
kernel_haven/
├── cache/
│   └── ...
├── log/
│   └── ...
├── output/
│   └── ...
├── plugins/
│   ├── cnfutils.jar
│   ├── kbuildminerextractor.jar
│   ├── kconfigreaderextractor.jar
│   ├── undeadanalyzer.jar
│   └── undertakerextractor.jar
├── res/
│   └── ...
├── kernel_haven.jar
└── dead_code.properties
```

A configuration to execute a dead code analysis on Linux with this setup looks like this:

```Properties
# Linux Source Tree
source_tree = /path/to/linux-4.4
arch = x86

# Analysis
analysis.class = net.ssehub.kernel_haven.default_analyses.DeadCodeAnalysis

# Code Extractor
code.provider.cache.read = true
code.provider.cache.write = true
code.extractor.class = net.ssehub.kernel_haven.undertaker.UndertakerExtractor
code.extractor.threads = 4

# Build Extractor
build.provider.cache.read = true
build.provider.cache.write = true
build.extractor.class = net.ssehub.kernel_haven.kbuildminer.KbuildMinerExtractor

# Variability Extractor
variability.provider.cache.read = true
variability.provider.cache.write = true
variability.extractor.class = net.ssehub.kernel_haven.kconfigreader.KconfigReaderExtractor

# Logging
log.console = true
log.file = true

# Directories
archive.dir = .
cache_dir = cache/
log.dir = log/
output_dir = output/
plugins_dir = plugins/
resource_dir = res/
```

See `config_template.properties` for a full list of configuration options for the infrastructure. Note that plugins
also can have their own configuration options. 

## Further Documentation

* [The user documentation](https://github.com/KernelHaven/Documentation/raw/master/UserDocumentation/KernelHaven%20User%20Documentation.pdf), for users who want to run analyzes with the infrastructure.
* [The developer documentation](https://github.com/KernelHaven/Documentation/raw/master/DeveloperDocumentation/Arbeit.pdf), for developers who want to write new extractors and analyzes.

## Acknowledgments

This work is partially supported by the ITEA3 project [REVaMP<sup>2</sup>](https://itea3.org/project/revamp2.html), funded by the BMBF (German Ministry of Research and Education) under grant 01IS16042H.

We would like to thank the following persons, who contributed to the initial version of KernelHaven: [Moritz Flöter](http://www.moritzf.de/), Adam Krafczyk, Alice Schwarz, Kevin Stahr, Johannes Ude, Manuel Nedde, Malek Boukhari, and Marvin Forstreuter.
