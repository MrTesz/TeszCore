---
parent: TeszCore
title: Bugs
---

## An overview of known bugs, affected and fixing versions

> Please use the [latest version](..#latest) to prevent bugs.

---

### YamlConfig*1 - YamlConfigs not initialized defaults map

##### Fix: [2.0.6](../version/2.0.6)

##### Affected Versions: [2.0.0](../version/2.0.0), [2.0.1](../version/2.0.1), [2.0.2](../version/2.0.2), [2.0.3](../version/2.0.3), [2.0.4](../version/2.0.4), [2.0.5](../version/2.0.5)

##### Affect:
[addDefault(...)](../../Files/config/YamlConfig#adddefault),
[setDefaults(...)](../../Files/config/YamlConfig#setdefaults) and 
not found value returns in [get methods](../../Files/config/YamlConfig#yaml-methods)
throws a `NullPointerException`.

##### Reason: 
The `defaults` map, used for handling affected methods, is not initialized while creating the 
[YamlConfig](../../Files/config/YamlConfig) instance.

---

### TeszCoreApi*1 - Exception on initialization call

##### Fix: [2.0.5](../version/2.0.5)

##### Affected Versions: [2.0.0](../version/2.0.0), [2.0.1](../version/2.0.1), [2.0.2](../version/2.0.2), [2.0.3](../version/2.0.3), [2.0.4](../version/2.0.4)

##### Affect:
[TeszCoreApi#initialize](../../Files/api/TeszCoreApi#initialize) always throws a `NullPointerException` on call.

##### Reason:
[TeszCoreApi#initialize](../../Files/api/TeszCoreApi#initialize) calls 
[TeszCoreApi#getInstance](../../Files/api/TeszCoreApi#getinstance); this always results in a `NullPointerException` 
because of a [Conditions.checkNonNull](../../Files/util/Conditions#checknonnull) check, checking the current API instance