package uz.javatuz.functionalInterfaces;

import lombok.SneakyThrows;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Supplier;

public class SupplierExample {
    @SneakyThrows
    public static void main(String[] args) {
        Supplier<String> supplier = ()->{
            System.out.println("Hello from G52");
            return "xabar junatildi";
        };

//        System.out.println(supplier.get());
        System.out.println("over.............");


        //m1();

    }

    private static void m1() throws InterruptedException, ExecutionException {
        CompletableFuture<String> completableFuture = CompletableFuture.supplyAsync(new Supplier<String>() {
            @Override
            public String get() {
                return "Hello from CompletableFuture";
            }
        });

        System.out.println(completableFuture.get());
    }
}
