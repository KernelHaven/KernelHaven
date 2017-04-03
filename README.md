# KernelHaven
KernelHaven offers a generic infrastructure for performing different analyses on
product lines. This repository contains the main infrastructure, plug-ins are
located in separate repositories.

## Documentation
* [The user documentation](https://github.com/KernelHaven/UserDocumentation/raw/master/Arbeit.pdf) describes how to use KernelHaven
* [The developer documentation](https://github.com/KernelHaven/DeveloperDocumentation/raw/master/Arbeit.pdf) describes how to write new plug-ins for the infrastructure

## Plug-ins

| Plug-in | Type | License |
|---------|------|---------|
|[KconfigReaderExtractor](https://github.com/KernelHaven/KconfigReaderExtractor)| Kconfig Extractor | [GPLv3](http://www.gnu.org/licenses/gpl-3.0.html) |
|[UndertakerExtractor](https://github.com/KernelHaven/UndertakerExtractor)| Code Extractor (*.c, *.h, *.S)| [GPLv3](http://www.gnu.org/licenses/gpl-3.0.html) |
|[KbuildMinerExtractor](https://github.com/KernelHaven/KbuildMinerExtractor)| Kbuild Extractor (Kbuild*, Makefile*)| [GPLv3](http://www.gnu.org/licenses/gpl-3.0.html) |
