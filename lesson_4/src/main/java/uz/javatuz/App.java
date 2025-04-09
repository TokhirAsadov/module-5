package uz.javatuz;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class App {

    private static Integer count = 0;
    private Integer age = 15;

    public Integer getAge(){
        return age;
    }

    public void setAge(Integer age){
        this.age = age;
    }

    public static void main(String[] args) {
        //extracted();




    }

    private static void extracted() {
        App app = new App();

        List<String> list = Arrays.asList("Java", "Python", "JavaScript");
        int i = 0;

//            LambdaTest lambdaTest = (c)-> {
//                System.out.println("Hello," + app.getAge());
//            };
        app.setAge(42);
        System.out.println(app.getAge());
    }

    public static void test(String name) {
        System.out.println("Hello, "+name);
    }
}
