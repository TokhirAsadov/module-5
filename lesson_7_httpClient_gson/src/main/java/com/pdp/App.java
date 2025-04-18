package com.pdp;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Stream;

public class App {
    public static void main(String[] args) throws IOException, InterruptedException {

        Gson gson = new Gson();

        HttpClient httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .connectTimeout(Duration.of(3, ChronoUnit.SECONDS))
                .build();

        Post post = new Post("hello",  "from g52", 1L);

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create("https://jsonplaceholder.typicode.com/posts/7"))
                .DELETE()
                .build();

        HttpResponse<String> send = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        System.out.println(send);
        System.out.println(send.body());
        //postRequest(gson, post, httpClient);
// 2xx - 200 - 299
        //
        //getObjectListFromJson();

    }

    private static void postRequest(Gson gson, Post post, HttpClient httpClient) throws IOException, InterruptedException {
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create("https://jsonplaceholder.typicode.com/posts"))
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(post)))
                .header("Content-Type","application/json")
                .build();

        HttpResponse<String> send = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        System.out.println(send);
        System.out.println(send.body());
    }

    private static void getObjectListFromJson() throws IOException, InterruptedException {
        Gson gson = new Gson();
        String json = getJson();

        List<Post> posts = gson.fromJson(json, new TypeToken<List<Post>>() {
        });

        for (Post post : posts) {
            System.out.println(post);
        }
    }

    private static void m3() {
        String json = """
                 {
                    "userId": 1,
                    "id": 2,
                    "title": "qui est esse",
                    "body": "est rerum tempore vitae"
                  }
                """;

        Gson gson = new Gson();
        Post post = gson.fromJson(json, Post.class);
        System.out.println("JSON: "+json);
        System.out.println("Object: "+post);
    }

    private static String getJson() throws IOException, InterruptedException {
        HttpClient httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .connectTimeout(Duration.of(3, ChronoUnit.SECONDS))
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build();

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create("https://jsonplaceholder.typicode.com/posts"))
                .GET()
                .build();

        HttpResponse<String> send1 = httpClient.send(
                httpRequest,
                HttpResponse.BodyHandlers.ofString()
        );
        return send1.body();
    }

    private static void gsonBuilder() {
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                //.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .setVersion(2.0)
//                .excludeFieldsWithoutExposeAnnotation()
                .excludeFieldsWithModifiers(Modifier.VOLATILE, Modifier.TRANSIENT, Modifier.STATIC)
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

        send.body().forEach(e -> {
            try {
                Thread.sleep(100);
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }
            System.out.println(e);
        });
    }
}
