package com.pdp.entity;

public class User implements UserActions{
    private int age;
    private String name;

    private User() {
    }

    private void setAge(int age){
        this.age=age;
    }

    public User(int age, String name) {
        this.age = age;
        this.name = name;
    }

    @Override
    public String toString() {
        return "User{" +
                "age=" + age +
                ", name='" + name + '\'' +
                '}';
    }
}
