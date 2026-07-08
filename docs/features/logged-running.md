# Logged Running
###### Last updated for version: 2.4.0

With the classes `Initializer` and `Runner` you can execute Constructors, Runnables, Suppliers with logging 
at the start and the end.

## Usage

You can use these classes as a utility class with the static methods 
[`Initializer#initialize`](https://javadoc.io/static/io.github.mrtesz/teszcore/2.4.0/io/github/mrtesz/teszcore/logged/Initializer.html#initialize(java.lang.Class,java.lang.String,io.github.mrtesz.teszcore.logger.TeszCoreLogger,java.lang.Object...)), 
[`Runner#executeSupply`](https://javadoc.io/static/io.github.mrtesz/teszcore/2.4.0/io/github/mrtesz/teszcore/logged/Runner.html#executeSupply(java.util.function.Supplier,java.lang.String,java.lang.String,io.github.mrtesz.teszcore.logger.TeszCoreLogger)) and
[`Runner#executeRunnable`](https://javadoc.io/static/io.github.mrtesz/teszcore/2.4.0/io/github/mrtesz/teszcore/logged/Runner.html#executeRunnable(java.lang.Runnable,java.lang.String,java.lang.String,io.github.mrtesz.teszcore.logger.TeszCoreLogger)).
This is useful if you only want to use this logging only once or a few times. 

When you want to use wrapping logging more times for one project, you may consider to initialize an instance of the class you want to use by providing the values used for every method (`usingProject` and optional `logger`) and afterward use the methods 
[`Initializer#init`](https://javadoc.io/static/io.github.mrtesz/teszcore/2.4.0/io/github/mrtesz/teszcore/logged/Initializer.html#init(java.lang.Class,java.lang.Object...)), 
[`Runner#get`](https://javadoc.io/static/io.github.mrtesz/teszcore/2.4.0/io/github/mrtesz/teszcore/logged/Runner.html#get(java.util.function.Supplier,java.lang.String)) and 
[`Runner#run`](https://javadoc.io/static/io.github.mrtesz/teszcore/2.4.0/io/github/mrtesz/teszcore/logged/Runner.html#run(java.lang.Runnable)).
This has the benefit, that you can use this class with only supplying the values to wrap (e.g. `Runnable`) and have the not changing values already set.