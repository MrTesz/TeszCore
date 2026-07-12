# TeszCore
TeszCore is a utility project, created for easy SQL database management, logging and other utility features.

## Infos
- [Contributors](https://github.com/MrTesz/TeszCore/graphs/contributors?all=1)
- [Changelog](changelog/changelog.md)
- [GitHub](https://github.com/MrTesz/TeszCore)
- [JavaDocs](https://javadoc.io/doc/io.github.mrtesz/teszcore/latest/index.html)
- [License](https://github.com/MrTesz/TeszCore/blob/main/LICENSE)

## Maven
[sonatype.com](https://central.sonatype.com/artifact/io.github.mrtesz/teszcore/overview)

```xml
<!-- TeszCore -->                      
<dependency>
    <groupId>io.github.mrtesz</groupId>
    <artifactId>teszcore</artifactId>
    <version>{{ $themeConfig.version }}</version>
</dependency>
```

#### Dependencies: 
- [`io.github.mrtesz:ansi-impl`](https://github.com/MrTesz/ansi-impl) for parsing paragraph codes in logger messages to ansi colors
- `com.google.code.gson:gson` for [JSON](features/json)
- `org.yaml:snakeyaml` for [YAML management](features/yaml)
- `org.jetbrains:annotations` for transparent value annotation
- `org.apache.logging.log4j:log4j-core` for logging
- `com.zaxxer:HikariCP` for SQL connection pooling
- `org.mariadb.jdbc:mariadb-java-client` for MariaDB support
- `org.xerial:sqlite-jdbc` for Sqlite support
- `org.projectlombok:lombok` for allowing clean source code via generated methods
