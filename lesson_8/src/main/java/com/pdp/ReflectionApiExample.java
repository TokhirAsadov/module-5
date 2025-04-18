package com.pdp;

import com.pdp.entity.User;
import com.pdp.entity.Worker;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;

public class ReflectionApiExample {
    public static void main(String[] args) throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {

        Class<User> clazz = User.class;
        User user = new User(16, "Murodjon");



        //method(clazz, user);


        //fieldsSet(clazz, user);

        //fieldsGet(clazz, user);


        //userWork();

        //arraylistClazz();

    }

    private static void method(Class<User> clazz, User user) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        Method setAge = clazz.getDeclaredMethod("setAge", int.class);

        setAge.setAccessible(true);

        setAge.invoke(user,17);
        System.out.println(user);
    }

    private static void fieldsSet(Class<User> clazz, User user) throws NoSuchFieldException, IllegalAccessException {
        Field age = clazz.getDeclaredField("age");
        Field name = clazz.getDeclaredField("name");

        age.setAccessible(true);
        name.setAccessible(true);

        age.set(user,17);
        name.set(user,"Sadriddin");

        System.out.println("Age: "+age.get(user));
        System.out.println("Name: "+name.get(user));
        System.out.println("User: "+ user);
    }

    private static void fieldsGet(Class<User> clazz, User user) throws NoSuchFieldException, IllegalAccessException {
        Field age = clazz.getDeclaredField("age");
        Field name = clazz.getDeclaredField("name");

        age.setAccessible(true);
        name.setAccessible(true);

        System.out.println("Age: "+age.get(user));
        System.out.println("Name: "+name.get(user));
        System.out.println("User: "+ user);
    }

    private static void userWork() {
        Class<Worker> clazz = Worker.class;
        System.out.println("Class nomi: " + clazz.getName());
        System.out.println("Oddiy nomi: " + clazz.getSimpleName());
        System.out.println("Superklass: " + clazz.getSuperclass().getSimpleName());
        System.out.println("Interfeyslar: ");
        for (Class<?> iface : clazz.getInterfaces()) {
            System.out.println("- " + iface.getSimpleName());
        }
    }

    private static void arraylistClazz() throws ClassNotFoundException {
        Class<?> clazz = Class.forName("java.util.ArrayList");


        System.out.println(clazz.getGenericSuperclass());
        System.out.println("Class nomi: " + clazz.getName());
        System.out.println("Oddiy nomi: " + clazz.getSimpleName());
        System.out.println("Superklass: " + clazz.getSuperclass().getSimpleName());
        System.out.println("Interfeyslar: ");
        for (Class<?> iface : clazz.getInterfaces()) {
            System.out.println("- " + iface.getSimpleName());
        }
    }
}
