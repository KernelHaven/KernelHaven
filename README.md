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
<table style="width:100%">
  <tr>
    <th>Plug-in</th>
    <th>Type</th>
    <th>License</th>
    <th>Download</th>
    <th>Status</th>
  </tr>
  <!-- KconfigReaderExtractor -->
  <tr>
    <td><a href="https://github.com/KernelHaven/KconfigReaderExtractor">KconfigReaderExtractor</a></td>
    <td><b>VariabilityModel-Extractor:</b> Kconfig</td>
    <td><a href="http://www.gnu.org/licenses/gpl-3.0.html">GPLv3</a></td>
    <td><ul>
      <li><a href="http://jenkins.sse.uni-hildesheim.de/job/KernelHaven_KconfigReaderExtractor/lastSuccessfulBuild/artifact/build/jar/KconfigReaderExtractor.jar">JAR</a></li>
      <li><a href="http://jenkins.sse.uni-hildesheim.de/job/KernelHaven_KconfigReaderExtractor/lastSuccessfulBuild/artifact/build/jar/KconfigReaderExtractor_withsource.jar">JAR with sources</a></li>
    </ul></td>
    <td><img src="http://jenkins.sse.uni-hildesheim.de/buildStatus/icon?job=KernelHaven_KconfigReaderExtractor" alt="Build Status of KconfigReaderExtractor"></td>
  </tr>
  <!-- UndertakerExtractor -->
  <tr>
    <td><a href="https://github.com/KernelHaven/UndertakerExtractor">UndertakerExtractor</a></td>
    <td><b>CodeModel-Extractor:</b> <code>*.c, *.h, *.S</code></td>
    <td><a href="http://www.gnu.org/licenses/gpl-3.0.html">GPLv3</a></td>
    <td><ul>
      <li><a href="http://jenkins.sse.uni-hildesheim.de/job/KernelHaven_UndertakerExtractor/lastSuccessfulBuild/artifact/build/jar/UndertakerExtractor.jar">JAR</a></li>
      <li><a href="http://jenkins.sse.uni-hildesheim.de/job/KernelHaven_UndertakerExtractor/lastSuccessfulBuild/artifact/build/jar/UndertakerExtractor_withsource.jar">JAR with sources</a></li>
    </ul></td>
    <td><img src="http://jenkins.sse.uni-hildesheim.de/buildStatus/icon?job=KernelHaven_UndertakerExtractor" alt="Build Status of UndertakerExtractor"></td>
  </tr>
  <!-- TypeChefExtractor -->
  <tr>
    <td><a href="https://github.com/KernelHaven/TypeChefExtractor">TypeChefExtractor</a></td>
    <td><b>CodeModel-Extractor:</b> <code>*.c, *.h, *.S</code></td>
    <td><a href="http://www.apache.org/licenses/LICENSE-2.0.html">Apache License v2</a></td>
    <td><ul>
      <li><a href="http://jenkins.sse.uni-hildesheim.de/job/KernelHaven_TypeChefExtractor/lastSuccessfulBuild/artifact/build/jar/TypeChefExtractor.jar">JAR</a></li>
      <li><a href="http://jenkins.sse.uni-hildesheim.de/job/KernelHaven_TypeChefExtractor/lastSuccessfulBuild/artifact/build/jar/TypeChefExtractor_withsource.jar">JAR with sources</a></li>
    </ul></td>
    <td><img src="http://jenkins.sse.uni-hildesheim.de/buildStatus/icon?job=KernelHaven_TypeChefExtractor" alt="Build Status of TypeChefExtractor"></td>
  </tr>
  <!-- srcMLExtractor -->
  <tr>
    <td><a href="https://github.com/KernelHaven/srcMLExtractor">srcMLExtractor</a></td>
    <td><b>CodeModel-Extractor:</b> <code>*.c</code></td>
    <td><a href="http://www.gnu.org/licenses/gpl-3.0.html">GPLv3</a></td>
    <td><ul>
      <li><a href="http://jenkins.sse.uni-hildesheim.de/job/KernelHaven_SrcMlExtractor/lastSuccessfulBuild/artifact/build/jar/SrcMLExtractor.jar">JAR</a></li>
      <li><a href="http://jenkins.sse.uni-hildesheim.de/job/KernelHaven_SrcMlExtractor/lastSuccessfulBuild/artifact/build/jar/SrcMLExtractor_withsource.jar">JAR with sources</a></li>
    </ul></td>
    <td><img src="http://jenkins.sse.uni-hildesheim.de/buildStatus/icon?job=KernelHaven_SrcMlExtractor" alt="Build Status of srcMLExtractor"></td>
  </tr>
  <!-- KbuildMinerExtractor -->
  <tr>
    <td><a href="https://github.com/KernelHaven/KbuildMinerExtractor">KbuildMinerExtractor</a></td>
    <td><b>BuildModel-Extractor:</b> <code>Kbuild*, Makefile*</code></td>
    <td><a href="http://www.gnu.org/licenses/gpl-3.0.html">GPLv3</a></td>
    <td><ul>
      <li><a href="http://jenkins.sse.uni-hildesheim.de/job/KernelHaven_KbuildMinerExtractor/lastSuccessfulBuild/artifact/build/jar/KbuildminerExtractor.jar">JAR</a></li>
      <li><a href="http://jenkins.sse.uni-hildesheim.de/job/KernelHaven_KbuildMinerExtractor/lastSuccessfulBuild/artifact/build/jar/KbuildminerExtractor_withsource.jar">JAR with sources</a></li>
    </ul></td>
    <td><img src="http://jenkins.sse.uni-hildesheim.de/buildStatus/icon?job=KernelHaven_KbuildMinerExtractor" alt="Build Status of KbuildMinerExtractor"></td>
  </tr>
  <!-- CnfUtils -->
  <tr>
    <td><a href="https://github.com/KernelHaven/CnfUtils">CnfUtils</a></td>
    <td><b>Utilities:</b> SAT-Solver, CNF-Converter, Formula simplifiers, ...</td>
    <td><a href="http://www.apache.org/licenses/LICENSE-2.0.html">Apache License v2</a></td>
    <td><ul>
      <li><a href="http://jenkins.sse.uni-hildesheim.de/job/KernelHaven_CnfUtils/lastSuccessfulBuild/artifact/build/jar/CnfUtils.jar">JAR</a></li>
      <li><a href="http://jenkins.sse.uni-hildesheim.de/job/KernelHaven_CnfUtils/lastSuccessfulBuild/artifact/build/jar/CnfUtils_withsource.jar">JAR with sources</a></li>
    </ul></td>
    <td><img src="http://jenkins.sse.uni-hildesheim.de/buildStatus/icon?job=KernelHaven_CnfUtils" alt="Build Status of CnfUtils"></td>
  </tr>
  <!-- IOUtils -->
  <tr>
    <td><a href="https://github.com/KernelHaven/IOUtils">IOUtils</a></td>
    <td><b>Utilities:</b> Excel-readers and writers for extractors and analysis plug-ins</td>
    <td><a href="http://www.apache.org/licenses/LICENSE-2.0.html">Apache License v2</a></td>
    <td><ul>
      <li><a href="http://jenkins.sse.uni-hildesheim.de/job/KernelHaven_IOUtils/lastSuccessfulBuild/artifact/build/jar/IOUtils.jar">JAR</a></li>
      <li><a href="http://jenkins.sse.uni-hildesheim.de/job/KernelHaven_IOUtils/lastSuccessfulBuild/artifact/build/jar/IOUtils_withsource.jar">JAR with sources</a></li>
    </ul></td>
    <td><img src="http://jenkins.sse.uni-hildesheim.de/buildStatus/icon?job=KernelHaven_IOUtils" alt="Build Status of IOUtils"></td>
  </tr>
  <!-- NonBooleanUtils -->
  <tr>
    <td><a href="https://github.com/KernelHaven/NonBooleanUtils">NonBooleanUtils</a></td>
    <td><b>Utilities:</b> Utilities to handle models, which are not pure Boolean (Pseudo-SAT)</td>
    <td><a href="http://www.apache.org/licenses/LICENSE-2.0.html">Apache License v2</a></td>
    <td><ul>
      <li><a href="http://jenkins.sse.uni-hildesheim.de/job/KernelHaven_NonBooleanUtils/lastSuccessfulBuild/artifact/build/jar/NonBooleanUtils.jar">JAR</a></li>
      <li><a href="http://jenkins.sse.uni-hildesheim.de/job/KernelHaven_NonBooleanUtils/lastSuccessfulBuild/artifact/build/jar/NonBooleanUtils_withsource.jar">JAR with sources</a></li>
    </ul></td>
    <td><img src="http://jenkins.sse.uni-hildesheim.de/buildStatus/icon?job=KernelHaven_NonBooleanUtils" alt="Build Status of NonBooleanUtils"></td>
  </tr>
  <!-- UnDeadAnalyzer -->
  <tr>
    <td><a href="https://github.com/KernelHaven/UnDeadAnalyzer">UnDeadAnalyzer</a></td>
    <td><b>Analysis:</b> Detection of dead code and unused variables of the variability model</td>
    <td><a href="http://www.apache.org/licenses/LICENSE-2.0.html">Apache License v2</a></td>
    <td><ul>
      <li><a href="http://jenkins.sse.uni-hildesheim.de/job/KernelHaven_UnDeadAnalyzer/lastSuccessfulBuild/artifact/build/jar/defaultanalyses.jar">JAR</a></li>
      <li><a href="http://jenkins.sse.uni-hildesheim.de/job/KernelHaven_UnDeadAnalyzer/lastSuccessfulBuild/artifact/build/jar/defaultanalyses_withsource.jar">JAR with sources</a></li>
    </ul></td>
    <td><img src="http://jenkins.sse.uni-hildesheim.de/buildStatus/icon?job=KernelHaven_UnDeadAnalyzer" alt="Build Status of UnDeadAnalyzer"></td>
  </tr>
  <!-- FeatureEffectAnalyzer -->
  <tr>
    <td><a href="https://github.com/KernelHaven/FeatureEffectAnalysis">FeatureEffectAnalyzer</a></td>
    <td><b>Analysis:</b> Detection of presence conditions and feature effects</td>
    <td><a href="http://www.apache.org/licenses/LICENSE-2.0.html">Apache License v2</a></td>
    <td><ul>
      <li><a href="http://jenkins.sse.uni-hildesheim.de/view/KernelHaven/job/KernelHaven_FeatureEffectAnalysis/lastSuccessfulBuild/artifact/build/jar/FeatureEffectAnalysis.jar">JAR</a></li>
      <li><a href="http://jenkins.sse.uni-hildesheim.de/view/KernelHaven/job/KernelHaven_FeatureEffectAnalysis/lastSuccessfulBuild/artifact/build/jar/FeatureEffectAnalysis_withsource.jar">JAR with sources</a></li>
    </ul></td>
    <td><img src="http://jenkins.sse.uni-hildesheim.de/buildStatus/icon?job=KernelHaven_FeatureEffectAnalysis" alt="Build Status of FeatureEffectAnalyzer"></td>
  </tr>
  <!-- MetricHaven -->
  <tr>
    <td><a href="https://github.com/KernelHaven/MetricHaven">MetricHaven</a></td>
    <td><b>Analysis:</b> Metric Infrastructure for SPL-Metrics</td>
    <td><a href="http://www.apache.org/licenses/LICENSE-2.0.html">Apache License v2</a></td>
    <td><ul>
      <li><a href="http://jenkins.sse.uni-hildesheim.de/job/KernelHaven_MetricHaven/lastSuccessfulBuild/artifact/build/jar/MetricHaven.jar">JAR</a></li>
      <li><a href="http://jenkins.sse.uni-hildesheim.de/job/KernelHaven_MetricHaven/lastSuccessfulBuild/artifact/build/jar/MetricHaven_withsource.jar">JAR with sources</a></li>
    </ul></td>
    <td><img src="http://jenkins.sse.uni-hildesheim.de/buildStatus/icon?job=KernelHaven_MetricHaven" alt="Build Status of MetricHaven"></td>
  </tr>
  <!-- Configuration Mismatches -->
  <tr>
    <td><a href="https://github.com/KernelHaven/ConfigurationMismatchAnalysis">Configuration Mismatches</a></td>
    <td><b>Analysis:</b> Detection of Configuration Mismatches</td>
    <td><a href="http://www.apache.org/licenses/LICENSE-2.0.html">Apache License v2</a></td>
    <td><ul>
      <li><a href="">JAR</a></li>
      <li><a href="">JAR with sources</a></li>
    </ul></td>
    <td><img src="http://jenkins.sse.uni-hildesheim.de/buildStatus/icon?job=KernelHaven_Configuration_Mismatches" alt="Build Status of Configuration Mismatches"></td>
  </tr>
</table>

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

See `config_template.properties` for a full list of configuration options for the infrastructure. Note that plugins
also can have their own configuration options. 

## Further Documentation

* [The user documentation](https://github.com/KernelHaven/Documentation/raw/master/UserDocumentation/KernelHaven%20User%20Documentation.pdf), for users who want to run analyzes with the infrastructure.
* [The developer documentation](https://github.com/KernelHaven/Documentation/raw/master/DeveloperDocumentation/Arbeit.pdf), for developers who want to write new extractors and analyzes.

## Video Tutorial ##
[![KernelHaven - ICSE 2018 Demonstration Video](https://img.youtube.com/vi/IbNc-H1NoZU/0.jpg)](https://www.youtube.com/watch?v=IbNc-H1NoZU)

## Acknowledgments

This work is partially supported by the ITEA3 project [REVaMP<sup>2</sup>](https://itea3.org/project/revamp2.html), funded by the BMBF (German Ministry of Research and Education) under grant 01IS16042H.

We would like to thank the following persons, who contributed to the initial version of KernelHaven: [Moritz Flöter](http://www.moritzf.de/), Adam Krafczyk, Alice Schwarz, Kevin Stahr, Johannes Ude, Manuel Nedde, Malek Boukhari, and Marvin Forstreuter.
