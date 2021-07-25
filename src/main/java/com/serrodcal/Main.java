package com.serrodcal;

import io.smallrye.mutiny.Uni;

public class Main {

    public static void main(String[] args) {

        //Exercise 1: hello Mutiny!
        helloMutiny();
    }

    private static void helloMutiny() {
        Uni.createFrom().item("hello")
            .onItem().transform(item -> item + " mutiny")
            .onItem().transform(String::toUpperCase)
            .subscribe().with(
                item -> System.out.println(">> " + item)
            );
    }

}
