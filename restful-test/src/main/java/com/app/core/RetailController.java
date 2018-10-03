package com.app.core;

import org.json.JSONObject;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisConnectionException;
import java.util.*;

public class RetailController {

    public static int dbUser = 1;
    public static int dbProduct = 2;
    public static String redis_host = "redis";
    public static int redis_port = 6379;
    public static Jedis redis = null;

    static {
        connectToRedis(redis_host, redis_port);
    }

    public static Jedis connectToRedis(String host, int port) {
        redis = new Jedis(host, port);
        while (true) {
            try {
                redis.select(dbUser);
                Set<String> keys = redis.keys("*");
                System.out.println("user after keys");
                System.out.println(keys);

                redis.select(dbProduct);
                keys = redis.keys("*");
                System.out.println("product after keys");
                System.out.println(keys);
                break;
            } catch (JedisConnectionException e) {
                System.err.println("Waiting for redis retail controller");
            }
        }
        System.err.println("Connected to redis");
        return redis;
    }

    public RetailController() {}

    public static List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        redis.select(dbUser);

        Set<String> keys = redis.keys("*");
        for (String key : keys) {
            User user = getUser(key);
            users.add(user);
        }
        return users;
    }
    public static User getUser(String userid) {
        User user = new User();
        redis.select(dbUser);
        try {
            JSONObject json = new JSONObject(redis.get(userid));
            user.userId = json.getString("user_id");
            user.userName = json.getString("user_name");
            user.userCredit = json.getDouble("user_credit");
        } catch (Exception ex) {
            System.err.println("Error in getting user: " + userid);
        }
        return user;
    }
    public static String setUser(String user_id, String user_name, double user_credit) {
        String res = null;

        JSONObject json = new JSONObject();
        json.put("user_id", user_id );
        json.put("user_name", user_name);
        json.put("user_credit", user_credit );

        redis.select(dbUser);
        try {
            res = redis.set(user_id, json.toString());
        } catch (Exception ex) {
            System.err.println("Error in setting user: " + user_id);
        }
        return res;
    }

    public static List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();
        redis.select(dbProduct);

        Set<String> keys = redis.keys("*");
        for (String key : keys) {
            Product product = getProduct(key);
            products.add(product);
        }
        return products;
    }
    public static Product getProduct(String productid) {
        Product product = new Product();
        redis.select(dbProduct);

        try {
            JSONObject json = new JSONObject(redis.get(productid));
            product.productId = json.getString("product_id");
            product.productName = json.getString("product_name");
            product.productPrice = json.getDouble("product_price");
        } catch (Exception ex) {
            System.err.println("Error in getting product: " + productid);
        }
        return product;
    }
    public static String setProduct(String product_id, String product_name, double product_price) {
        String res = null;

        JSONObject json = new JSONObject();
        json.put("product_id", product_id );
        json.put("product_name", product_name);
        json.put("product_price", product_price );

        redis.select(dbProduct);
        try {
            res = redis.set(product_id, json.toString());
        } catch (Exception ex) {
            System.err.println("Error in setting product: " + product_id);
        }
        return res;
    }

    public static UserBuyProduct buyProduct(String user_id, String product_id) {
        // get user details, and get credit -->
        // if credit > prodict price, buy & reduce credit by price

        boolean canBuy = false;

        User user = getUser(user_id);
        Product product = getProduct(product_id);

        double user_credit = user.userCredit;
        String user_name = user.userName;
        double product_price = product.productPrice;
        if (user_credit >= product_price) {
            canBuy = true;
        }

        // user credit reduced
        if (canBuy) {
            setUser(user_id, user_name, (user_credit-product_price) );
        }

        // output json
        UserBuyProduct userBuyProduct = new UserBuyProduct();
        userBuyProduct.userId = user_id;
        userBuyProduct.userName = user_name;
        userBuyProduct.userCredit = user_credit;
        userBuyProduct.productPrice = product_price;
        userBuyProduct.canBuy = canBuy;
        userBuyProduct.newUserCredit = user_credit;

        if (canBuy) {
            userBuyProduct.newUserCredit = (user_credit-product_price);
        }
        return userBuyProduct;
    }

    public static void main(String[] args) {
        RetailController rc = new RetailController();
        String userid = "101";
        String productid = "203";

        System.out.println("\n\t Get One User");
        User q1 = rc.getUser(userid);
        System.out.println(q1);

//        System.out.println("\n\t Get All Users");
//        List<JSONObject> jsons = rc.getAllUsers();
//        for (JSONObject json : jsons) {
//            System.out.println(json);
//        }

        System.out.println("\n\t Get One Product");
        Product q2 = rc.getProduct(productid);
        System.out.println(q2);

//        System.out.println("\n\t Get All Products");
//        List<JSONObject> products = rc.getAllProducts();
//        for (JSONObject product : products) {
//            System.out.println(product);
//        }

        System.out.println("\n\t Can buy ??");
        UserBuyProduct res = rc.buyProduct(userid, productid);
        System.out.println(res);


    }

}
