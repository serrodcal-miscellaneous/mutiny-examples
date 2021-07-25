package com.serrodcal;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import java.util.stream.IntStream;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.subscription.Cancellable;

public class Main {

    public static void main(String[] args) {

        //Exercise 1: hello Mutiny!
        helloMutiny();

        //Exercise 2: Mutiny uses a builder API
        builderAPI();

        //Excercise 3: The Uni type
        uniType();

        //Excercise 4: Subscribing to an Uni
        subscribingUni();

        //Excercise 5: Supplying an Uni
        supplyingUni();

        //Excercise 6: Failing Unis
        failingUnis();

        //Excercise 7: Operation to not produce a result
        voidUni();

        //Excercise 8: The Multy type
        multiType();

        //Excercise 9: Supplying a Multi
        supplyingMulti();

        //Excercise 10: Empty Multis
        emptyMulti();

        //Excercise 11: Managing null
        uniNull();

        //Excercise 12: From multi to uni of list
        multiToUniOfList();

        //Excercise 13: Recovering from fail
        recoveringFromFail();

        //Excercise 14: Chain an uni with another one
        chainUnis();

        //Excercise 15: Combine several unis (paralelly)
        combineUnis();

        //Excercise 16: Combine several unis (paralelly) with a failure
        combineUnisWithFailure();

    }

    private static void helloMutiny() {
        Uni.createFrom().item("hello")
            .onItem().transform(item -> item + " mutiny")
            .onItem().transform(String::toUpperCase)
            .subscribe().with(
                item -> System.out.println(">> " + item)
            );
    }

    private static void builderAPI() {
        Uni<String> uni1 = Uni.createFrom().item("hello");
        Uni<String> uni2 = uni1.onItem().transform(item -> item + " mutiny");
        Uni<String> uni3 = uni2.onItem().transform(String::toUpperCase);

        uni3.subscribe().with(item -> System.out.println(">> " + item));

        //Note that the previous program is not equivalent to the code below
        Uni<String> uni = Uni.createFrom().item("hello");

        uni.onItem().transform(item -> item + " mutiny");
        uni.onItem().transform(String::toUpperCase);

        uni.subscribe().with(item -> System.out.println(">> " + item));
    }

    private static void uniType() {
        //A Uni<T> is a specialized stream that emits only an item or a failure.
        Uni.createFrom().item(1)
            .onItem().transform(i -> "hello-" + i)
            .subscribe().with(System.out::println);
    }

    private static void subscribingUni() {
        // Remember: if you don’t subscribe, nothing is going to happen. 
        // What’s more, the pipeline is materialized for each subscription.
        Cancellable cancellable = Uni.createFrom().item("subscribed")
                                    .subscribe().with(
                                        item -> System.out.println(item),
                                        failure -> System.out.println("Failed with " + failure)
                                    );
        // Note the returned Cancellable: this object allows canceling the operation if need be.
    }

    private static void supplyingUni() {
        //The Supplier is called for every subscriber. So, each of them will get a different value.
        AtomicInteger counter = new AtomicInteger();
        Uni<Integer> uni = Uni.createFrom().item(() -> counter.getAndIncrement());
        
        uni.subscribe().with(item -> System.out.println("Subscriber " + item));
        uni.subscribe().with(item -> System.out.println("Subscriber " + item));
    }

    private static void failingUnis() {
        // Pass an exception directly:
        Uni<Integer> failed1 = Uni.createFrom().failure(new Exception("directly boom"));

        // Pass a supplier called for every subscriber:
        Uni<Integer> failed2 = Uni.createFrom().failure(() -> new Exception("supplier boom"));

        failed1.subscribe().with(
            item -> System.out.println(item),
            failure -> System.out.println(failure.getMessage())
        );

        failed2.subscribe().with(
            item -> System.out.println(item),
            failure -> System.out.println(failure.getMessage())
        );

    }

    private static void voidUni() {
        Uni<Void> uni = Uni.createFrom().nullItem();
    }

    private static void multiType() {
        Multi.createFrom().items(1, 2, 3, 4, 5)
            .onItem().transform(i -> i * 2)
            .select().first(3)
            .onFailure().recoverWithItem(0)
            .subscribe().with(System.out::println);
    }

    private static void supplyingMulti() {
        AtomicInteger counter = new AtomicInteger();
        Multi<Integer> multi = Multi.createFrom().items(() -> IntStream.range(counter.getAndIncrement(), counter.get() * 2).boxed());
        multi.subscribe().with(item -> System.out.println(item));
        multi.subscribe().with(item -> System.out.println(item));
    }

    private static void emptyMulti() {
        Multi<String> multi = Multi.createFrom().empty();
    }

    private static Uni<String> findNameById(int id) {
        if (id == 1)
            return Uni.createFrom().nullItem();
        else if (id > 1)
            return Uni.createFrom().failure(new RuntimeException("Name does not exist"));
        return Uni.createFrom().item("name");
    }

    private static void uniNull() {
        //Same code, but different behavior

        // Get string
        findNameById(0)
            .onItem().ifNull().continueWith("Name is null")
            .onItem().ifNotNull().transform(String::toUpperCase)
            .subscribe().with(
                item -> System.out.println(item),
                failure -> System.out.println(failure.getMessage())
        );

        //Get null
        findNameById(1)
                .onItem().ifNull().continueWith("Name is null")
                .onItem().ifNotNull().transform(String::toUpperCase)
                .subscribe().with(
                item -> System.out.println(item),
                failure -> System.out.println(failure.getMessage())
        );

        // Get failure
        findNameById(2)
                .onItem().ifNull().continueWith("Name is null")
                .onItem().ifNotNull().transform(String::toUpperCase)
                .subscribe().with(
                item -> System.out.println(item),
                failure -> System.out.println(failure.getMessage())
        );
    }

    private static void multiToUniOfList() {
        Multi<Integer> items = Multi.createFrom().items(1, 2, 3);

        items.collect().asList()
                .subscribe().with(
                        item -> {
                            for(Integer i: item)
                                System.out.println(">> " + i);
                        }
                );
    }

    private static void recoveringFromFail() {
        Uni.createFrom().failure(new RuntimeException("Some error"))
                .onFailure().recoverWithItem("hello")
                .subscribe().with(System.out::println);
    }

    private static Uni<String> invokeRemoteGreetingService(String name) {
        return Uni.createFrom().item("hello " + name);
    }

    private static void chainUnis() {
        Uni<String> uni = Uni.createFrom().item("serrodcal");

        uni
            .onItem().transformToUni(name -> invokeRemoteGreetingService(name)) //Calling a remote service asynchronously
            .subscribe().with(System.out::println);

        uni
            .chain(name -> invokeRemoteGreetingService(name))
            .subscribe().with(System.out::println);
    }

    private static Uni<Integer> doSomething(Integer i) {
        return Uni.createFrom().item(i);
    }

    private static void combineUnis() {
        List<Uni<?>> unis = new ArrayList<>();

        for (int i = 0; i < 10; i++){
            Uni<Integer> uni = doSomething(i);
            unis.add(uni);
        }

        Uni<Integer> uni = Uni.combine().all().unis(unis)
            .combinedWith(results -> {
                int acc = 0;
                for (Object result: results)
                    if(result instanceof Integer)
                        acc += (Integer) result;
                return acc;
            }).onFailure().recoverWithUni(failure -> {
                return Uni.createFrom().item(-1);
            });

        uni.subscribe().with(System.out::println);

    }

    private static Uni<Integer> doSomethingWrong(Integer i) {
        if (i == 2)
            return Uni.createFrom().failure(new RuntimeException("error"));
        return Uni.createFrom().item(i);
    }

    private static void combineUnisWithFailure() {
        List<Uni<?>> unis = new ArrayList<>();

        for (int i = 0; i < 10; i++){
            Uni<Integer> uni = doSomethingWrong(i);
            unis.add(uni);
        }

        Uni<Integer> uni = Uni.combine().all().unis(unis)
                .combinedWith(results -> {
                    int acc = 0;
                    for (Object result: results)
                        if(result instanceof Integer)
                            acc += (Integer) result;
                    return acc;
                }).onFailure().recoverWithUni(failure -> {
                    return Uni.createFrom().item(-1);
                });

        uni.subscribe().with(System.out::println);

    }

}
