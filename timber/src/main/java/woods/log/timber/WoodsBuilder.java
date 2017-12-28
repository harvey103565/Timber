package woods.log.timber;

import android.support.annotation.NonNull;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Function;
import io.reactivex.observables.ConnectableObservable;


/**
 * An builder to create environment for Tree
 */

public class WoodsBuilder {

    private ArrayList<Class<?>> Factories = new ArrayList<Class<?>>();

    public WoodsBuilder() {
    }

    public WoodsBuilder addTreeFactory(@NonNull Class<?>... factories) {
        Collections.addAll(Factories, factories);
        return this;
    }

    public void build() {
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

        Observable<Tree> treeObservable = methodObservable
                .map(new Function<Method, Tree>() {
                    @Override
                    public Tree apply(@NonNull Method method) throws Exception {
                        return treeFromMethod(method);
                    }
                });

        Observable<Spec> tipObservable = methodObservable
                .map(new Function<Method, Spec>() {
                    @Override
                    public Spec apply(@NonNull Method method) throws Exception {
                        return tipFromMethod(method);
                    }
                });

        Observable
                .zip(treeObservable, tipObservable, new BiFunction<Tree, Spec, Tree>() {
                    @Override
                    public Tree apply(Tree tree, Spec spec) throws Exception {
                        tree.pin(spec);
                        return tree;
                    }
                })
                .subscribe(new Observer<Tree>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(Tree tree) {
                        Timber.plant(tree);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.e(e, "Error getting package name.");
                    }

                    @Override
                    public void onComplete() {
                        Timber.i("Timber build complete.");
                    }
                });

        methodObservable.connect();
    }

    private Tree treeFromMethod(@NonNull Method method) {
        Class<?> treeClass = method.getReturnType();

        if (!Tree.class.isAssignableFrom(treeClass) || treeClass.isInterface() ||
                Modifier.isAbstract(treeClass.getModifiers())) {
            throw new AssertionError(treeClass.getName() + " type can not be instanced.", null);
        }

        Tree tree;
        try {
            tree = (Tree) treeClass.newInstance();
        } catch (InstantiationException e) {
            throw new AssertionError(treeClass.getName() + " missing default constructor?", e);
        } catch (IllegalAccessException e) {
            throw new AssertionError("Could not load class: " + treeClass.getName(), e);
        }

        return tree;
    }

    private Spec tipFromMethod(@NonNull Method method) {
        Annotation[] annotations = method.getAnnotations();
        if (annotations.length > 1) {
            throw new AssertionError("One annotation for each method.", null);
        }
        Pin pin = method.getAnnotation(Pin.class);

        return Tools.parseTipString(pin.value());
    }
}
