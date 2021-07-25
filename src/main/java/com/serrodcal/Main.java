package com.serrodcal;

import io.smallrye.mutiny.Uni;

public class Main {

    public static void main(String[] args) {

        //Exercise 1: hello Mutiny!
        helloMutiny();

        //Exercise 2: Mutiny uses a builder API
        builderAPI();

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

}
