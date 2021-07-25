package com.serrodcal;

import java.util.concurrent.atomic.AtomicInteger;

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

        //Excercise 4: Subscribing to a Uni
        subscribingUni();

        //Excercise 5: Supplying a Uni
        supplyingUni();

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

}
