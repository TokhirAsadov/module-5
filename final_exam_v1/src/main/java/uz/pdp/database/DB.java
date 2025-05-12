package uz.pdp.database;

import uz.pdp.entity.Book;
import uz.pdp.entity.Order;
import uz.pdp.entity.User;

import java.util.ArrayList;
import java.util.List;

public interface DB {
    List<User> users = new ArrayList<>();
    List<Book> books = new ArrayList<>();
    List<Order> orders = new ArrayList<>();
}
