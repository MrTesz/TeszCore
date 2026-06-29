# Bugs

This is an overview of known bugs and their affected and fixing versions

> [!NOTE]
> Please use the [latest version](#latest) to prevent bugs.

## YamlConfig*1 - YamlConfigs not initialized defaults map

**Fix:** [2.0.6](version/2.0.6)<br>
**Affected Versions:** [2.0.0](version/2.0.0), [2.0.1](version/2.0.1), [2.0.2](version/2.0.2), [2.0.3](version/2.0.3), [2.0.4](version/2.0.4), [2.0.5](version/2.0.5)

**Effect:**<br>
`YamlConfig#addDefault(...)`,`YamlConfig#setDefaults(...)`and not found value returns in get methods throws a `NullPointerException`.

**Reason:**<br>
The `defaults` map, used for handling affected methods, is not initialized while creating the `YamlConfig` instance.

## TeszCoreApi*1 - Exception on initialization call

**Fix:** [2.0.5](version/2.0.5)<br>
**Affected Versions:** [2.0.0](version/2.0.0), [2.0.1](version/2.0.1), [2.0.2](version/2.0.2), [2.0.3](version/2.0.3), [2.0.4](version/2.0.4)

**Effect:**
`TeszCoreApi#initialize` always throws a `NullPointerException` on call.

**Reason:**
`TeszCoreApi#initialize` calls `TeszCoreApi#getInstance`. This always results in a `NullPointerException` 
because of a `Conditions.checkNonNull` check, checking the current API instance