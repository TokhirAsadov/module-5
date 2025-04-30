package com.pdp.database;

import com.pdp.entity.History;
import com.pdp.entity.Product;
import com.pdp.entity.User;

import java.util.ArrayList;
import java.util.List;

public interface DB {
    List<User> users = new ArrayList<>();
    List<Product> products = new ArrayList<>();
    List<History> histories = new ArrayList<>();
}
