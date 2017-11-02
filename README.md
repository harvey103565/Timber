# Timber
Customized version derived from JakeWharton's timber project. Flexible logging tool for android, writing message and errors into files.

# Basic Usage:
Create a Tree factory with annotations to control logging behaviors.
```
public interface TreeFactory {
    @Pin("{\"Class\":\"SampleActivity\", \"Filters\":[\"V\",\"I\",\"W\",\"E\",\"A\"]}")
    EchoTree createDebugTree();

    @Pin("{\"Level\":\"W\", \"Thread\":\"main\"}")
    MemoTree createMemoTree();

    @Pin("{\"Package\":\"woods.log.sample\"}")
    CatcherTree createCatcherTree();
}
```

And then build init Timber with builder()
```
Timber.builder()
        .addTreeFactory(TreeFactory.class)
        .build();
```

Do not forget to uproot() all trees when exit.
```
Timber.uprootAll();
```