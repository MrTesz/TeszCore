# Logging
###### Last updated for version: 2.4.1

## Creation
You can create a `TeszCoreLogger` with `TeszCoreAPI#getLogger` this method requires a `DebugLevel` representing the importance of this log message; the lower the level, the more important the message is.

## Format
The logger message is formatted as `<projectName if provided>[<DebugLevel's intValue>] <Message>`

## Logger Methods
You have different options to log a message:
- `TeszCoreLogger#log`: Automatically chose the log type according to the provided `DebugLevel`
- `TeszCoreLogger#debug`: Logs the message at debug level
- `TeszCoreLogger#info`: Logs the message at info level
- `TeszCoreLogger#warning`: Logs the message at warn level
- `TeszCoreLogger#error`: Logs the message at error level

Exceptions can be logged with `TeszCoreLogger#throwing` or `TeszCoreLogger#printStackTrace` (same logic)<br>
These methods are logging:
- The class of the provided exception
- The error message
- The part of the code, the error occurred on
- The cause
- The StackTrace