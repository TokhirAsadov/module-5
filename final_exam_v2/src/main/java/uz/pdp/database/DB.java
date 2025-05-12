package uz.pdp.database;

import uz.pdp.entity.Homework;
import uz.pdp.entity.User;

import java.util.ArrayList;
import java.util.List;

public interface DB {
    List<User> users = new ArrayList<>();
    List<Homework> homeworks = new ArrayList<>();
}
