---

title: Database Managing
nav_fold: false
---

# Database Managing
###### Last updated for version: 2.3.1

Database managing is a big part of this project and, by the way, the purpose this project was created for (initially as "DBUtils").<br>
This project provides managing of MariaDB and Sqlite databases natively, but you can also create a custom DBManager if you want to. (See [Create DBManager](#create-a-dbmanager))

---
## Create a Connection

To create a connection between your project and your database, you must have these values ready to provide:<br>
### MariaDB:
**Required:**
- URL to the database (`jdbc:mariadb://<host>:<port>/<database>`)
- The name of the user, you want to connect with
- The password for this user

**Optional:** (Nullable)
- The name of your project (will be shown in debug messages when provided)

```java
YamlConfig credentialsConfig = // Your YamlConfig creation
MariaDBManager db = TeszCorePaperApi.getInstance().createMariaDBManager(
        "My-Project", // Project Name
        true, // throws a NullPointerException when "url", "user" or "password" is null or blank
        "users", // Name of this database (iditentification)
        credentialsConfig.getString("mariadb.url"), // url
        credentialsConfig.getString("mariadb.username"), // username
        credentialsConfig.getString("mariadb.password")); // password
```

### Sqlite:
**Required:**
- The name of the .db file

**Optional:** (Nullable)
- The name of your project (will be shown in debug messages when provided)
- The path to the .db file (`null` = `""`)

```java
YamlConfig dbConfig = // Your YamlConfig creation
SqliteManager db = TeszCorePaperApi.getInstance().createMariaDBManager(
        "My-Project", // Project Name
        credentialsConfig.getString("db.path"), // path
        credentialsConfig.getString("db.filename")); // name
```

After creation, you have to use `#connect` to connect to the database.
Other Methods for managing Connections:
- `#disconnect` - disconnects the current session
- `#checkConnection` - checks, if the current connection is still online, if not: Tries to connect again, if still not connected: Send an error message with reason "Cant connect!"
- `#getConnection` - runs `#checkConnection` and returns a `java.sql.Connection` if connected
- `#close` and `#isClosed` - methods to handle locking of a DBManager; if closed, the DBManager throws an `IllegalStateException` every time a method of the manager, wich requires a connection, is called.

---
## Possibilities

With an `Async-/MariaDBManager` or `Async-/SqliteManager` you can practically do anything what SQL has to offer. 
Each method in these managers, either has its own SQL query building logic (e.g. `#insertInto`) or allows you to provide your own query (e.g. `#executeSelect`).
If you use the second type of methods, there is always an option to write the values in the query as `?` and pass a `List<Object>`, which then sets these values using a `PreparedStatement`.

When using a normal `...Manager` you are executing your queries on the same thread, the methods are executed in.<br>
When you have to execute expensive executions, it's sometimes better to execute these methods asynchronously, you can do this by using an `Async...Manager`, which can be created using `DBManager#async`. 
This method creates a new `Async...Manager` with the same credentials and connections as before.<br>
Async DBMangers are executing queries with a `CompletableFuture` so you can also provide an action, when the execution is run.

> To see through all method signatures and what you can do with specific database managers, view the [javadocs](https://javadoc.io/static/io.github.mrtesz/teszcore/2.3.1+javadoc/io/github/mrtesz/teszcore/db/manager/package-summary.html)

---
## Create a custom DBManager

When you have a database with a different system then MariaDB or Sqlite you can create your own database manager by implementing: 
- `DBManager` - Bare minimum: name, lock, connect/disconnect/check-/getConnection, #sync, #async
- `SyncDBManager` - All methods like `#createOrAlter`, `#executeSql`, `#deleteFrom` etc. returning `int` or other objects directly
- `AsyncDBManager` - All methods like `#createOrAlter`, `#executeSql`, `#deleteFrom` etc. returning `CompletableFuture` objects