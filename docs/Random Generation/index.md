---

title: Random Generation
nav_fold: false
---

# Random Generation
###### Last updated for version: 2.3.1

In TeszCore there is one class, which provides methods for:
- Rolling a boolean, from a given percentage value (true if that percentage is hit) - `Chance#roll(int)`, `Chance#roll(int, Random)`, `Chance#roll(double)`, `Chance#roll(double, Random)`
- Rolling a value of given values with set percentage for specific values - `Chance#roll(Collection<? extends WeightedEntry<T>>)`, `Chance#roll(Collection<? extends WeightedEntry<T>>, Random)`

In all methods for random generation there is one method which uses the default random `new Random()` and one, where you can provide the random, the value should be rolled with.

## Rolling values

When rolling values with a collection of `WeightedEntry`s it's good to know, that the percentage values you provide in the `WeightedEntry`s don't have to add up to 100,
because the method handles the normalization for you.