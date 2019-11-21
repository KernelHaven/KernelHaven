# KernelHaven

![Build Status](https://jenkins-2.sse.uni-hildesheim.de/buildStatus/icon?job=KH_KernelHaven "Build Status")

KernelHaven offers a generic infrastructure for performing different analyses on product lines. This repository contains the main infrastructure, plugins are located in separate repositories.

## Downloads

### Core Infrastructure

This archive contains the core infrastructure only. Additional [plugins](#plugins) are needed for a useful execution.

* [KernelHaven infrastructure](https://jenkins-2.sse.uni-hildesheim.de/job/KH_KernelHaven/lastSuccessfulBuild/artifact/build/jar/KernelHaven.jar)

### Bundled Releases

These archives contain the infrastructure as well as all public [plugins](#plugins). These bundles contain more than needed to run most experiments.

* [KernelHaven plus all plugins (GPLv3 and Apache License 2.0)](https://jenkins-2.sse.uni-hildesheim.de/job/KH_Releases/lastSuccessfulBuild/artifact/build/KernelHaven_GPLv3.zip)
* [KernelHaven plus only plugins available under the Apache License 2.0](https://jenkins-2.sse.uni-hildesheim.de/job/KH_Releases/lastSuccessfulBuild/artifact/build/KernelHaven_Apache-2.0.zip)

## Plugins

The following tables lists commonly used plugins for KernelHaven. They mostly focus on analysing the Linux Kernel.

### Extractors

| Plugin | Description | License | Download | Status |
|--------|-------------|---------|----------|--------|
| [CodeBlockExtractor](https://github.com/KernelHaven/CodeBlockExtractor) | A code-model extractor that extracts `#ifdef` blocks | [Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0.html) | [JAR](https://jenkins-2.sse.uni-hildesheim.de/job/KH_CodeBlockExtractor/lastSuccessfulBuild/artifact/build/jar/CodeBlockExtractor.jar) | ![Build Status](https://jenkins-2.sse.uni-hildesheim.de/buildStatus/icon?job=KH_CodeBlockExtractor) |
| [KbuildMinerExtractor](https://github.com/KernelHaven/KbuildMinerExtractor) | A build-model extractor for the Linux Kernel | [GPLv3](https://www.gnu.org/licenses/gpl-3.0.html) | [JAR](https://jenkins-2.sse.uni-hildesheim.de/job/KH_KbuildMinerExtractor/lastSuccessfulBuild/artifact/build/jar/KbuildMinerExtractor.jar) | ![Build Status](https://jenkins-2.sse.uni-hildesheim.de/buildStatus/icon?job=KH_KbuildMinerExtractor) |
| [KconfigReaderExtractor](https://github.com/KernelHaven/KconfigReaderExtractor) | A variability-model extractor for the Linux Kernel | [GPLv3](https://www.gnu.org/licenses/gpl-3.0.html) | [JAR](https://jenkins-2.sse.uni-hildesheim.de/job/KH_KconfigReaderExtractor/lastSuccessfulBuild/artifact/build/jar/KconfigReaderExtractor.jar) | ![Build Status](https://jenkins-2.sse.uni-hildesheim.de/buildStatus/icon?job=KH_KconfigReaderExtractor) |
| [srcMLExtractor](https://github.com/KernelHaven/srcMLExtractor) | A code-model extractor that partially parses the C AST | [GPLv3](https://www.gnu.org/licenses/gpl-3.0.html) | [JAR](https://jenkins-2.sse.uni-hildesheim.de/job/KH_SrcMlExtractor/lastSuccessfulBuild/artifact/build/jar/SrcMLExtractor.jar) | ![Build Status](https://jenkins-2.sse.uni-hildesheim.de/buildStatus/icon?job=KH_SrcMlExtractor) |
| [TypeChefExtractor](https://github.com/KernelHaven/TypeChefExtractor) | An old code-model extractor that parses the C AST | [Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0.html) | [JAR](https://jenkins-2.sse.uni-hildesheim.de/job/KH_TypeChefExtractor/lastSuccessfulBuild/artifact/build/jar/TypeChefExtractor.jar) | ![Build Status](https://jenkins-2.sse.uni-hildesheim.de/buildStatus/icon?job=KH_TypeChefExtractor) |
| [UndertakerExtractor](https://github.com/KernelHaven/UndertakerExtractor) | An old code-model extractor that extracts `#ifdef` blocks | [GPLv3](https://www.gnu.org/licenses/gpl-3.0.html) | [JAR](https://jenkins-2.sse.uni-hildesheim.de/job/KH_UndertakerExtractor/lastSuccessfulBuild/artifact/build/jar/UndertakerExtractor.jar) | ![Build Status](https://jenkins-2.sse.uni-hildesheim.de/buildStatus/icon?job=KH_UndertakerExtractor) |

### Analysis

| Plugin | Description | License | Download | Status |
|--------|-------------|---------|----------|--------|
| [FeatureEffectAnalysis](https://github.com/KernelHaven/FeatureEffectAnalysis) | Detection of presence conditions and feature effects | [Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0.html) | [JAR](https://jenkins-2.sse.uni-hildesheim.de/job/KH_FeatureEffectAnalysis/lastSuccessfulBuild/artifact/build/jar/FeatureEffectAnalysis.jar) | ![Build Status](https://jenkins-2.sse.uni-hildesheim.de/buildStatus/icon?job=KH_FeatureEffectAnalysis) |
| [ConfigurationMismatchAnalysis](https://github.com/KernelHaven/ConfigurationMismatchAnalysis) | Detection of configuration mismatches | [Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0.html) | [JAR](https://jenkins-2.sse.uni-hildesheim.de/job/KH_ConfigurationMismatchAnalysis/lastSuccessfulBuild/artifact/build/jar/ConfigurationMismatchAnalysis.jar) | ![Build Status](https://jenkins-2.sse.uni-hildesheim.de/buildStatus/icon?job=KH_ConfigurationMismatchAnalysis) |
| [MetricHaven](https://github.com/KernelHaven/MetricHaven) | Metric infrastructure for SPL-Metrics | [Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0.html) | [JAR](https://jenkins-2.sse.uni-hildesheim.de/job/KH_MetricHaven/lastSuccessfulBuild/artifact/build/jar/MetricHaven.jar) | ![Build Status](https://jenkins-2.sse.uni-hildesheim.de/buildStatus/icon?job=KH_MetricHaven) |
| [UnDeadAnalyzer](https://github.com/KernelHaven/UnDeadAnalyzer) | Detection of dead code and unused variables of the variability model | [Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0.html) | [JAR](https://jenkins-2.sse.uni-hildesheim.de/job/KH_UnDeadAnalyzer/lastSuccessfulBuild/artifact/build/jar/UnDeadAnalyzer.jar) | ![Build Status](https://jenkins-2.sse.uni-hildesheim.de/buildStatus/icon?job=KH_UnDeadAnalyzer) |
| [EntityLocatorAnalysis](https://github.com/KernelHaven/EntityLocatorAnalysis) | Finds entities in different sources, e.g. for tracing | [Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0.html) | [JAR](https://jenkins-2.sse.uni-hildesheim.de/job/KH_EntityLocatorAnalysis/lastSuccessfulBuild/artifact/build/jar/EntityLocatorAnalysis.jar) | ![Build Status](https://jenkins-2.sse.uni-hildesheim.de/buildStatus/icon?job=KH_EntityLocatorAnalysis) |
| [PSS-MapperAnalysis](https://github.com/KernelHaven/ProblemSolutionSpaceMapperAnalysis) | Identification of relations between problem and solution space artifacts | [Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0.html) | [JAR](https://jenkins-2.sse.uni-hildesheim.de/job/KH_ProblemSolutionSpaceMapperAnalysis/lastSuccessfulBuild/artifact/build/jar/ProblemSolutionSpaceMapperAnalysis.jar) | ![Build Status](https://jenkins-2.sse.uni-hildesheim.de/buildStatus/icon?job=KH_ProblemSolutionSpaceMapperAnalysis) |
| [PSS-DivergenceDetectorAnalysis](https://github.com/KernelHaven/ProblemSolutionSpaceDivergenceDetectorAnalysis) | Identification of unintended divergences between problem and solution space artifacts | [Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0.html) | [JAR](https://jenkins-2.sse.uni-hildesheim.de/job/KH_ProblemSolutionSpaceDivergenceDetectorAnalysis/lastSuccessfulBuild/artifact/build/jar/ProblemSolutionSpaceDivergenceDetectorAnalysis.jar) | ![Build Status](https://jenkins-2.sse.uni-hildesheim.de/buildStatus/icon?job=KH_ProblemSolutionSpaceDivergenceDetectorAnalysis) |
| [PSS-DivergenceCorrectorAnalysis](https://github.com/KernelHaven/ProblemSolutionSpaceDivergenceCorrectorAnalysis) | Provision of (proposals for) corrections of unintended divergences between problem and solution space artifacts | [Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0.html) | [JAR](https://jenkins-2.sse.uni-hildesheim.de/job/KH_ProblemSolutionSpaceDivergenceCorrectorAnalysis/lastSuccessfulBuild/artifact/build/jar/ProblemSolutionSpaceDivergenceCorrectorAnalysis.jar) | ![Build Status](https://jenkins-2.sse.uni-hildesheim.de/buildStatus/icon?job=KH_ProblemSolutionSpaceDivergenceCorrectorAnalysis) |

### Utilities

| Plugin | Description | License | Download | Status |
|--------|-------------|---------|----------|--------|
| [CnfUtils](https://github.com/KernelHaven/CnfUtils) | SAT-Solver, CNF-Converter, Formula simplifiers, ... | [Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0.html) | [JAR](https://jenkins-2.sse.uni-hildesheim.de/job/KH_CnfUtils/lastSuccessfulBuild/artifact/build/jar/CnfUtils.jar) | ![Build Status](https://jenkins-2.sse.uni-hildesheim.de/buildStatus/icon?job=KH_CnfUtils) |
| [CppUtils](https://github.com/KernelHaven/CppUtils) | Utilities for parsing C-preprocessor statements (Experimental) | [Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0.html) | [JAR](https://jenkins-2.sse.uni-hildesheim.de/job/KH_CppUtils/lastSuccessfulBuild/artifact/build/jar/CppUtils.jar) | ![Build Status](https://jenkins-2.sse.uni-hildesheim.de/buildStatus/icon?job=KH_CppUtils) |
| [DBUtils](https://github.com/KernelHaven/DBUtils) | SQLite readers and writers for extractors and analysis plugins | [Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0.html) | [JAR](https://jenkins-2.sse.uni-hildesheim.de/job/KH_DBUtils/lastSuccessfulBuild/artifact/build/jar/DBUtils.jar) | ![Build Status](https://jenkins-2.sse.uni-hildesheim.de/buildStatus/icon?job=KH_DBUtils) |
| [IncrementalAnalysesInfrastructure](https://github.com/KernelHaven/IncrementalAnalysesInfrastructure) | Support for incremental anlyses | [Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0.html) | [JAR](https://jenkins-2.sse.uni-hildesheim.de/job/KH_IncrementalAnalysesInfrastructure/lastSuccessfulBuild/artifact/build/jar/IncrementalAnalysesInfrastructure.jar) | ![Build Status](https://jenkins-2.sse.uni-hildesheim.de/buildStatus/icon?job=KH_IncrementalAnalysesInfrastructure) |
| [IOUtils](https://github.com/KernelHaven/IOUtils) | Excel readers and writers for extractors and analysis plugins | [Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0.html) | [JAR](https://jenkins-2.sse.uni-hildesheim.de/job/KH_IOUtils/lastSuccessfulBuild/artifact/build/jar/IOUtils.jar) | ![Build Status](https://jenkins-2.sse.uni-hildesheim.de/buildStatus/icon?job=KH_IOUtils) |
| [NonBooleanUtils](https://github.com/KernelHaven/NonBooleanUtils) | Utilties for preparing source trees with non-boolean variables in C preprocessor analysis | [Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0.html) | [JAR](https://jenkins-2.sse.uni-hildesheim.de/job/KH_NonBooleanUtils/lastSuccessfulBuild/artifact/build/jar/NonBooleanUtils.jar) | ![Build Status](https://jenkins-2.sse.uni-hildesheim.de/buildStatus/icon?job=KH_NonBooleanUtils) |
| [BusybootPreparation](https://github.com/KernelHaven/BusybootPreparation) | Utilities for preparing Busybox and Coreboot source trees for KernelHaven | [Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0.html) | [JAR](https://jenkins-2.sse.uni-hildesheim.de/job/KH_BusybootPreparation/lastSuccessfulBuild/artifact/build/jar/BusybootPreparation.jar) | ![Build Status](https://jenkins-2.sse.uni-hildesheim.de/buildStatus/icon?job=KH_BusybootPreparation) |

### Dependencies

The following image visualizes the dependencies between the plugins (open image in a new browser tab to make plugin links clickable):

![Plugin Dependencies](misc/plugin_dependencies.svg "Plugin Dependencies")

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
analysis.class = net.ssehub.kernel_haven.undead_analyzer.DeadCodeAnalysis

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

See [`config_template.properties`](config_template.properties) for a full list of available configuration options for the infrastructure and common plugins.

## Further Documentation

Further documentation can be found in the [wiki on GitHub](https://github.com/KernelHaven/KernelHaven/wiki). A pdf version can be downloaded from [here](https://jenkins-2.sse.uni-hildesheim.de/job/KH_Documentation/lastSuccessfulBuild/artifact/Manual.pdf).

## Video Tutorials

#### Introduction and Experiments
[![KernelHaven - Introduction and Experiments](https://img.youtube.com/vi/xKde6tPY_jA/0.jpg "KernelHaven - Introduction and Experiments")](https://youtu.be/xKde6tPY_jA)

#### ICSE 2018 Demonstration
[![KernelHaven - ICSE 2018 Demonstration Video](https://img.youtube.com/vi/IbNc-H1NoZU/0.jpg "KernelHaven - ICSE 2018 Demonstration Video")](https://www.youtube.com/watch?v=IbNc-H1NoZU)

## License

The main infrastructure (this project) is licensed under the [Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0.html). Plugins may have different licenses.

## Acknowledgments

This work is partially supported by the ITEA3 project [REVaMP<sup>2</sup>](https://itea3.org/project/revamp2.html), funded by the BMBF (German Ministry of Research and Education) under grant 01IS16042H.

We would like to thank the following persons, who contributed to the initial version of KernelHaven: [Moritz Flöter](https://www.moritzf.de/), Adam Krafczyk, Alice Schwarz, Kevin Stahr, Johannes Ude, Manuel Nedde, Malek Boukhari, and Marvin Forstreuter.
