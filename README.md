# Timber
An customization to Jake Wharton's excellent timber project. Flexible log and or log file tool for android.

# Basic Usage:
Create a Tree factory with annotations to control logging behaviors.
```
public interface Seed {
    @Pin(/*SpecString*/)
    Wood createTree();
}
```
Where SpecString is in JSon format and may looks like this:
```
{"Class":"SampleActivity", "Thread":"main", "Filters":["I", "W"], "Level":"E"}
```

And then build init Timber with builder()
```
Tree tree = new Wood();
Timber.builder()
        .addSeeds(Seed.class)
        .addTrees(tree)
        .build();
```

# Specs
Spec is the annotation in definition of Seed class, in which:
  "Class" field tell 'Wood' the logs from which class should be saved.
  "Thread" field tells 'Wood' the logs from which tread should be saved.
  "Filter" field tell 'Wood' to include certain levels in log files.
  "Level" field set the minimum level of logs that could go through the output filter.

All channels in Filter list or above the level will be let go inside the Wood class, but only those listed in filter list will be saved.

# Forest
There are 3 ways to add log engine:
```
// Implement Tree() interface and add to forest using Builder.addSeeds(), this is useful when there is specs annotation.
 Timber.builder().addSeeds(Seed.class)

// New instance of Tree Object and add it to forest(), using default specs for native implementation of the Tree(Wood)
 Timber.builder().addTrees(tree)

// Directly plant it to woods forest. The flexible way to add a tree on demand.
 Timber.plant()
```

# Cleanup
Do not forget to uproot() all trees when exit.
```
Timber.uprootall();
```