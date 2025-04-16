package com.pdp;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.stream.Stream;

public class App {
    public static void main( String[] args ) {

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(User.class, new UserSerializer())
                .setPrettyPrinting()
                .create();

        User user = User.builder()
                .name("Murodjon")
                .age(16)
                .build();

        String json = gson.toJson(user);
        System.out.println(json);


        //gsonBuilder();


        //fromJsonAndToJson();


        //m1();
    }

    private static void gsonBuilder() {
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                //.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .setVersion(2.0)
//                .excludeFieldsWithoutExposeAnnotation()
                .excludeFieldsWithModifiers(Modifier.VOLATILE,Modifier.TRANSIENT,Modifier.STATIC)
                .create();

        Post post = gson.fromJson("""
                 {
                    "userId": 1,
                    "id": 2,
                    "title": "qui est esse",
                    "body": "est rerum tempore vitae"
                  }
                """, Post.class);
        System.out.println(post);

        Post post1 = new Post("G52 salom!", 1L, "Salom olib keldik", 201L);
        String json = gson.toJson(post1);
        System.out.println(json);

//                .setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE)
//                .serializeNulls()
    }

    private static void fromJsonAndToJson() {
        Gson gson = new Gson();
        Post post = gson.fromJson("""
                 {
                    "userId": 1,
                    "id": 2,
                    "title": "qui est esse",
                    "body": "est rerum tempore vitae"
                  }
                """, Post.class);
        System.out.println(post);

        Post post1 = new Post("G52 salom!", 1L, "Salom olib keldik", 201L);
        String json = gson.toJson(post1);
        System.out.println(json);
    }

    private static void m1() throws IOException, InterruptedException {
        HttpClient httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .connectTimeout(Duration.of(3, ChronoUnit.SECONDS))
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build();

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create("https://jsonplaceholder.typicode.com/posts"))
                .GET()
                .build();

        HttpResponse<Stream<String>> send = httpClient.send(
                httpRequest,
                HttpResponse.BodyHandlers.ofLines()
        );

        send.body().forEach(e->{
            try {
                Thread.sleep(100);
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }
            System.out.println(e);
        });
    }
}
