---
parent: Projects
title: TeszCore
nav_fold: false
---

# TeszCore
## Utility project for easy SQL Database usage, logging and easy development

## Infos:
Developers: [Mr_Tesz](https://github.com/MrTesz)<br>
GitHub: [[Github]](https://github.com/MrTesz/TeszCore)<br>
Javadocs: [[JavaDoc.io]](https://javadoc.io/doc/io.github.mrtesz/teszcore/latest/index.html)
License: [[License]](LICENSE)<br>
Changelog: [[Changelog]](Changelog)

## Maven:
[central.sonatype.com](https://central.sonatype.com/artifact/io.github.mrtesz/teszcore/overview)

```xml
<!-- TeszCore -->                      
<dependency>
    <groupId>io.github.mrtesz</groupId>
    <artifactId>teszcore</artifactId>
    <version><!-- replace with latest version --></version>
</dependency>
```

#### Dependencies: 
- [io.github.mrtesz:ansi-impl](https://github.com/MrTesz/ansi-impl) for parsing paragraph codes in logger messages to ansi colors
- org.yaml:snakeyaml for [YamlConfig](YAML)
- org.jetbrains:annotations for transparent value annotation
- org.apache.logging.log4j:log4j-core for logging
- com.zaxxer:HikariCP for easy SQL connection pooling
- org.mariadb.jdbc:mariadb-java-client for MariaDB support
- org.xerial:sqlite-jdbc for Sqlite support
- org.projectlombok:lombok for allowing clean source code via generated methods
