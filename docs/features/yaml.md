# YAML
###### Last updated for version: 2.4.0

With the `YamlConfig` class you can create/load/manage YAML files

## Creation
When building a `YamlConfig` instance, the .yml file is by default created in the build process. (You can disable it by setting `autoload` to false)

Build a `YamlConfig` instance by building it with the `YamlConfig.Builder`:
```java
// Create the builder instance
YamlConfig.builder()
        .setFilePath("config/") // path, the .yml file will be created in
        .setFileName("my-config") // name the .yml file will be created with
        .setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK) // DumperOptions#defaultFlowStyle
        .setPrettyFlow(true) // DumperOptions#prettyFlow
        .build();
```

**Builder Values**

|         Value         |                                                 Default                                                 | Description                                                                |
|:---------------------:|:-------------------------------------------------------------------------------------------------------:|:---------------------------------------------------------------------------|
|      `filePath`       |                                                 `null`                                                  | The path, the .yml file will be created in (`null` is interpreted as `""`) |
|      `fileName`       |                                                    -                                                    | The name, the .yml file will be created with                               |
|      `autosave`       |                                                 `true`                                                  | Automatically save the config in `YamlConfig#set()`                        |
|    `dumperOptions`    |                                          `new DumperOptions()`                                          | DumperOptions, the .yml file will be created with                          |
|    `defaultStyle`     |                                    `DumperOptions.ScalarStyle.PLAIN`                                    | `defaultStyle` of the DumperOptions                                        |
|  `defaultFlowStyle`   |                                     `DumperOptions.FlowStyle.AUTO`                                      | `defaultFlowStyle` of the DumperOptions                                    |
|      `lineBreak`      |                                     `DumperOptions.LineBreak.UNIX`                                      | `lineBreak` of the DumperOptions                                           |
|  `nonPrintableStyle`  |                                `DumperOptions.NonPrintableStyle.BINARY`                                 | `nonPrintableStyle` of the DumperOptions                                   |
|   `anchorGenerator`   | <abbr title="org.yaml.snakeyaml.serializer.NumberAnchorGenerator">`new NumberAnchorGenerator(0)`</abbr> | `anchorGenerator` of the DumperOptions                                     |
|       `version`       |                                                 `null`                                                  | `version` of the DumperOptions                                             |
|      `timeZone`       |                                                 `null`                                                  | `timeZone` of the DumperOptions                                            |
|      `canonical`      |                                                 `false`                                                 | `canonical` of the DumperOptions                                           |
|    `allowUnicode`     |                                                 `true`                                                  | `allowUnicode` of the DumperOptions                                        |
|       `indent`        |                                                   `2`                                                   | `indent` of the DumperOptions                                              |
|   `indicatorIndent`   |                                                   `0`                                                   | `indicatorIndent` of the DumperOptions                                     |
| `indentWithIndicator` |                                                 `false`                                                 | `indentWithIndicator` of the DumperOptions                                 |
|        `width`        |                                                  `80`                                                   | `bestWidth` of the DumperOptions                                           |
|     `splitLines`      |                                                 `true`                                                  | `splitLines` of the DumperOptions                                          |
|    `explicitStart`    |                                                 `false`                                                 | `explicitStart` of the DumperOptions                                       |
|     `explicitEnd`     |                                                 `false`                                                 | `explicitEnd` of the DumperOptions                                         |
| `maxSimpleKeyLength`  |                                                  `128`                                                  | `maxSimpleKeyLength` of the DumperOptions                                  |
|   `processComments`   |                                                 `false`                                                 | `processComments` of the DumperOptions                                     |
|        `tags`         |                                                 `null`                                                  | `tags` of the DumperOptions                                                |
|     `prettyFlow`      |                                                 `false`                                                 | `prettyFlow` of the DumperOptions                                          |

## Saving

The data can be saved with `#save` or automatically after executing `#set`. <br>
The data is internally saved in a `Map<String, Object>` representing the `path` and the associated `value`. 
Although you can access this data with `#getRaw` and `#setData` it's highly recommended to use the getter methods: 
- `#get -> Object`
- `#getString -> String`
- `#getInt -> int`
- `#getLong -> long`
- `#getFloat -> float`
- `#getShort -> short`
- `#getDouble -> double`
- `#getBoolean -> boolean`
- `#getStringList -> List<String>`

the methods returning primitive data types, (which are returning `0` or `false` when not in the .yml file assigned), also have a `#get...OrNull` option, which returns the object type of the primitive and returns `null` when not assigned in the .yml.
(`#getStringList` has the same extra method, returning a new `ArrayList` when not assigned)

## Collect paths

You can use `#getPaths(String)` to retrieve a section of paths from a set parent:
```yaml
# config/saving.yml
save-method: mariadb
database:
    url: "jdbc:mariadb://localhost:3306/my-db"
    username: "my-user"
    password: "secure-password787"
```
```java
YamlConfig config = YamlConfig.builder()
        .setFilePath("config/")
        .setFileName("saving")
        .setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK)
        .setPrettyFlow(true)
        .build();

System.out.println(config.getPaths("database")); // Output: [database.url, database.username, database.password]
```

## Defaults

You can set default values in the config with either:<br>
`#addDefault(String path, Object value)` - Adds the provided value to the `defaults` map: If the path has no specified value, at the time you are executing `#get...` this value is provided<br>
`#setDefault(String path, Object value)` - Sets the provided value to the provided path if the .yml does not have a specified value at this path