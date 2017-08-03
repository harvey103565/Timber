package woods.log.timber;

import android.support.annotation.NonNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;


/**
 * An builder to create environment for Woods
 */

public class WoodsBuilder {

    private final Single<String> buildObservable;

    private Class<?>[] creatorClasses = null;

    private Prober defaultProber = null;

    private ArrayList<Tree> forest = new ArrayList<Tree>();

    private static class PackageMatcher implements Function<Integer, String> {
        private final Pattern pattern =
                Pattern.compile("\\b([a-z][a-z0-9_]*(?:\\.[a-z0-9_]+)+[0-9a-z_])\\b");

        @Override
        public String apply(@NonNull Integer id) throws Exception {
            String ps = "ps " + id;
            try {
                Process process = Runtime.getRuntime().exec(ps);
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(process.getInputStream()), 1024);

                reader.readLine();
                String line = reader.readLine();

                if (line == null || line.isEmpty()) {
                    Timber.e("Can not invoke ps call, using pid instead.");
                    return ps;
                }
                Matcher matcher = pattern.matcher(line);
                if (!matcher.find()) {
                    Timber.e("No package name found, using pid instead.");
                    return ps;
                }
                ps = matcher.group(1);
            } catch (IOException e) {
                throw new AssertionError("Fail to get input stream from ps call.");
            }
            return ps;
        }
    }

    private class BuilderObserver implements SingleObserver<String> {

        @Override
        public void onSubscribe(@NonNull Disposable d) {

        }

        @Override
        public void onSuccess(@NonNull String packageName) {
            defaultProber = new DefaultProber(packageName);

            if (creatorClasses != null) {
                for (Class<?> creator : creatorClasses) {
                    Method[] methods = creator.getDeclaredMethods();
                    for (Method method : methods) {
                        buildMethod(method, forest);
                    }
                }

                if (forest.size() > 0) {
                    Timber.plant(forest.toArray(new Tree[forest.size()]));
                }
            }

            Timber.asTree().prober(defaultProber);
        }

        @Override
        public void onError(@NonNull Throwable e) {
            Timber.wtf(e, "Fail parsing package name by pid.");
        }
    }

    public WoodsBuilder() {
        buildObservable = Single.just(Tools.getHostProcessId())
                .map(new PackageMatcher())
                .subscribeOn(Schedulers.newThread());
    }


    public WoodsBuilder addTreeFactory(@NonNull Class<?>... creators) {
        creatorClasses = creators;
        return this;
    }

    public void build() {
        buildObservable.subscribe(new BuilderObserver());
    }


    private void buildMethod(@NonNull Method method, @NonNull ArrayList<Tree> forest) {
        Class<?> t = method.getReturnType();

        if (Tree.class.isAssignableFrom(t)) {
            if (t.equals(Tree.class)) {
                Tree[] trees = fromAnnotation(method.getAnnotations());
                Collections.addAll(forest, trees);
            } else if (t.isInterface() || Modifier.isAbstract(t.getModifiers())) {
                throw new AssertionError("Creator return type can not be instanced.", null);
            } else {
                Custom custom = null;
                if (method.isAnnotationPresent(Custom.class)) {
                    custom = method.getAnnotation(Custom.class);
                }

                Tree tree = fromLocalClass(t, custom);
                forest.add(tree);
            }
        } else {
            throw new AssertionError("Creator must return Tree implementation.", null);
        }
    }

    private Tree[] fromAnnotation(Annotation[] annotations) {
        Tree[] trees = new Tree[annotations.length];

        for (int i = 0, n = annotations.length; i < n; i = i + 1) {
            String json;
            if (annotations[i] instanceof Echo) {
                trees[i] = new DebugTree();
                Echo echo = (Echo) annotations[i];
                json = echo.value();
            } else if (annotations[i] instanceof Paper) {
                trees[i] = new MemoTree();
                Paper paper = (Paper) annotations[i];
                json = paper.value();
            } else if (annotations[i] instanceof Catcher) {
                trees[i] = new CatcherTree();
                Catcher catcher = (Catcher) annotations[i];
                json = catcher.value();
            } else {
                throw new AssertionError("Specify a Tree implementation for annotation: "
                        + annotations[i].getClass().getName(), null);
            }

            trees[i].policy(Tools.parsePolicyString(json));
        }

        return trees;
    }

    private Tree fromLocalClass(@NonNull Class<?> t, Custom custom) {
        try {
            Tree tree = (Tree) t.newInstance();

            if (custom != null) {
                String json = custom.value();
                tree.policy(Tools.parsePolicyString(json));
            }

            return tree;
        } catch (InstantiationException e) {
            throw new AssertionError(t.getName() + " missing default constructor?", e);
        } catch (IllegalAccessException e) {
            throw new AssertionError("Could not load class: " + t.getName(), e);
        }
    }
}
