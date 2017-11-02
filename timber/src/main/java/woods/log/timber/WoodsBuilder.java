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
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;


/**
 * An builder to create environment for Wood
 */

public class WoodsBuilder {

    private Class<?>[] factoryArray = {defaultFactory.class};
    private ArrayList<Class<?>> allFactories = new ArrayList<Class<?>>();
    private ArrayList<Plant> allPlants = new ArrayList<Plant>();

    private EchoTree defaultTree = new EchoTree();

    private interface defaultFactory {
        EchoTree aPlant();
    }


    public WoodsBuilder() {
    }

    public WoodsBuilder addTreeFactory(@NonNull Class<?>... factories) {
        Collections.addAll(allFactories, factories);
        factoryArray = allFactories.toArray(new Class<?>[allFactories.size()]);
        return this;
    }

    public void build() {
        Timber.plant(defaultTree);

        if (allFactories.isEmpty()) {
            return;
        }

        Observable.fromArray(factoryArray)
                .flatMap(new Function<Class<?>, ObservableSource<Method>>() {
                    @Override
                    public ObservableSource<Method> apply(@NonNull Class<?> aClass) throws Exception {
                        return Observable.fromArray(aClass.getMethods());
                    }
                })
                .map(new Function<Method, Plant>() {
                    @Override
                    public Plant apply(@NonNull Method method) throws Exception {
                        return plantFromMethod(method);
                    }
                })
                .subscribe(new Observer<Plant>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull Plant plant) {
                        allPlants.add(plant);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        throw new AssertionError(e);
                    }

                    @Override
                    public void onComplete() {
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
                .map(new Function<String, Probe>() {
                    @Override
                    public Probe apply(@NonNull String pns) throws Exception {
                        return createProbe(pns);
                    }
                })
                .subscribe(new SingleObserver<Probe>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                    }

                    @Override
                    public void onSuccess(@NonNull Probe probe) {
                        for (Plant plant : allPlants) {
                            plantAPlant(plant, probe);
                        }
                        Timber.asTree().onplant(probe);
                        Timber.uproot(defaultTree);
                        Timber.v("Go Timber. Go! ");
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
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

    private Plant plantFromMethod(@NonNull Method method) {
        Class<?> plantClass = method.getReturnType();

        if (!Plant.class.isAssignableFrom(plantClass) || plantClass.isInterface() ||
                Modifier.isAbstract(plantClass.getModifiers())) {
            throw new AssertionError(plantClass.getName() + " type can not be instanced.", null);
        }

        Plant plant;
        try {
            plant = (Plant) plantClass.newInstance();
        } catch (InstantiationException e) {
            throw new AssertionError(plantClass.getName() + " missing default constructor?", e);
        } catch (IllegalAccessException e) {
            throw new AssertionError("Could not load class: " + plantClass.getName(), e);
        }

        Annotation[] annotations = method.getAnnotations();
        if (annotations.length > 1) {
            throw new AssertionError("One annotation for each method.", null);
        }
        Pin notes = method.getAnnotation(Pin.class);
        plant.pin(notes.value());

        return plant;
    }

    private Probe plantAPlant(@NonNull Plant plant, Probe probe) {
        plant.onplant(probe);
        Timber.plant(plant);

        return probe;
    }

    private Probe createProbe(@NonNull String pns) {
        if (pns.isEmpty()) {
            throw new AssertionError("Empty package name found.");
        }

        return new EnvironProbe(pns);
    }
}
