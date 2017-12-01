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

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.observables.ConnectableObservable;
import io.reactivex.schedulers.Schedulers;


/**
 * An builder to create environment for Tree
 */

public class WoodsBuilder {

    private ArrayList<Class<?>> Factories = new ArrayList<Class<?>>();

    private EchoTree defEchoTree = new EchoTree();


    public WoodsBuilder() {
    }

    public WoodsBuilder addTreeFactory(@NonNull Class<?>... factories) {
        Collections.addAll(Factories, factories);
        return this;
    }

    public void build() {
        Timber.plant(defEchoTree);

        if (Factories.isEmpty()) {
            return;
        }

        ConnectableObservable<Method> methodObservable =
                Observable.fromArray(Factories.toArray(new Class<?>[Factories.size()]))
                .flatMap(new Function<Class<?>, ObservableSource<Method>>() {
                    @Override
                    public ObservableSource<Method> apply(@NonNull Class<?> aClass) throws Exception {
                        return Observable.fromArray(aClass.getMethods());
                    }
                })
                .publish();

        methodObservable
                .map(new Function<Method, Tree>() {
                    @Override
                    public Tree apply(@NonNull Method method) throws Exception {
                        return plantFromMethod(method);
                    }
                });
//                .subscribe(new Observer<Tree>() {
//                    @Override
//                    public void onSubscribe(@NonNull Disposable d) {
//                        Timber.supervise();
//                    }
//
//                    @Override
//                    public void onNext(@NonNull Tree tree) {
//                        Timber.plant(tree);
//                    }
//
//                    @Override
//                    public void onError(@NonNull Throwable e) {
//                        Timber.wtf(e, "Error creating tree object.");
//                    }
//
//                    @Override
//                    public void onComplete() {
//                        Timber.uproot(defEchoTree);
//                        Timber.v("Go Timber. Go! ");
//                    }
//                });

        methodObservable
                .map(new Function<Method, Tip>() {
                    @Override
                    public Tip apply(@NonNull Method method) throws Exception {
                        return tipFromMethod(method);
                    }
                });


        Single.just(Tools.getHostProcessId())
                .subscribeOn(Schedulers.newThread())
                .map(new Function<Integer, String>() {
                    @Override
                    public String apply(@NonNull Integer id) throws Exception {
                        return matchPackageName(id);
                    }
                })
                .subscribe(new SingleObserver<String>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                    }

                    @Override
                    public void onSuccess(@NonNull String procname) {
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Timber.wtf(e, "Error getting package name.");
                    }
                });
    }

    private String matchPackageName(@NonNull Integer id) throws Exception {
        final Pattern pattern =
                Pattern.compile("\\b([a-z][a-z0-9_]*(?:\\.[a-z0-9_]+)+[0-9a-z_])\\b");

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

    private Tree plantFromMethod(@NonNull Method method) {
        Class<?> plantClass = method.getReturnType();

        if (!Tree.class.isAssignableFrom(plantClass) || plantClass.isInterface() ||
                Modifier.isAbstract(plantClass.getModifiers())) {
            throw new AssertionError(plantClass.getName() + " type can not be instanced.", null);
        }

        Tree tree;
        try {
            tree = (Tree) plantClass.newInstance();
        } catch (InstantiationException e) {
            throw new AssertionError(plantClass.getName() + " missing default constructor?", e);
        } catch (IllegalAccessException e) {
            throw new AssertionError("Could not load class: " + plantClass.getName(), e);
        }

        tree.pin(notes.value());

        return tree;
    }

    private Tip tipFromMethod(@NonNull Method method) {
        Annotation[] annotations = method.getAnnotations();
        if (annotations.length > 1) {
            throw new AssertionError("One annotation for each method.", null);
        }
        Pin notes = method.getAnnotation(Pin.class);

        return Tools.parseTipString(notes.value());
    }
}
