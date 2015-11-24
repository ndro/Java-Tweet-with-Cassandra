/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package javacas;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.utils.UUIDs;
import java.nio.ByteBuffer;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @author Rafi Ramadhan / 13512075
 * @author Hendro Triokta Brianto / 13512081
 */
public class TweetMethod {
    
    private Cluster cluster;
    private Session session;
    private static String user = "unknown";
    public static List<String> listUser = new ArrayList<>();
    public static List<String> listFollower = new ArrayList<>();
    public static Map<UUID, String> listTweet = new HashMap<>();
    
    public TweetMethod(String IP, String keyspace){
        cluster = Cluster.builder().addContactPoint(IP).build();
        session = cluster.connect(keyspace);
        
        ResultSet results = session.execute("SELECT * FROM users");
        for (Row row : results) {
            listUser.add(row.getString("username"));
        }
        
        ResultSet results3 = session.execute("SELECT * FROM tweets");
        for (Row row : results3) {
            listTweet.put(row.getUUID("tweet_id"),row.getString("body"));
        }
    }
    
    public void register(String username, String password){
        if(!listUser.contains(username)){   // belum ada
            session.execute("INSERT INTO users (username, password)"
                        + "VALUES ('"+ username +"', '"+ password +"')");
            System.out.println("Succes");
            System.out.println("Your username : " + username);
            
            listUser.add(username);
        }else{  // sudah ada
            System.out.println("Error");
            System.out.println("This " + username + " is already exist");
        }
        
    }
    
    public void login(String username, String password){
        if(listUser.contains(username)){
            user = username;
            System.out.println("Login success");
            
            ResultSet results2 = session.execute("SELECT * FROM followers WHERE follower = '"+ user +"'");
            for (Row row : results2) {
                listFollower.add(row.getString("username"));
            }
        } else {
            System.out.println("Login error");
        }
            
    }
    
    public void expand_tweet(String username){
        if(listUser.contains(username)){
            ResultSet results = session.execute("SELECT * FROM userline WHERE username = '"+ username +"'");
            for (Row row : results) {
                if(listTweet.containsKey(row.getUUID("tweet_id"))){
                    System.out.println(listTweet.get(row.getUUID("tweet_id")));
                }
            }
        } else {
            System.out.println("Username doesn't exist");
        }
    }
    
    public void timeline(){
        
        ResultSet results = session.execute("SELECT * FROM timeline WHERE username = '"+ user +"'");
        for (Row row : results) {
            if(listTweet.containsKey(row.getUUID("tweet_id"))){
                System.out.println(listTweet.get(row.getUUID("tweet_id")));
            }
        }
    }
    
    public void tweet(String text){
        UUID uuid = UUIDs.random();
        UUID timeuuid = UUIDs.timeBased();
        
        session.execute("INSERT INTO tweets (tweet_id, username, body)"
                        + "VALUES ("+ uuid +", '"+ user + "', '"+ text +"')");
        session.execute("INSERT INTO userline (username, time, tweet_id)"
                        + "VALUES ('"+ user +"', "+ timeuuid + ", "+ uuid +")");
        session.execute("INSERT INTO timeline (username, time, tweet_id)"
                        + "VALUES ('"+ user +"', "+ timeuuid + ", "+ uuid +")");
        
        for (String friend : listFollower){
            session.execute("INSERT INTO timeline (username, time, tweet_id)"
                        + "VALUES ('"+ friend +"', "+ timeuuid + ", "+ uuid +")");
        }
        
    }
    
    public void follow(String friend){
        Date date = new Date();
        //System.out.println(date.getTime());   // get millisecond
        //System.out.println(new Timestamp(date.getTime()));    // get calendar
        
        if(listUser.contains(friend)){
            if(!listFollower.contains(friend)){
                session.execute("INSERT INTO friends (username, friend, since)"
                        + "VALUES ('"+ user +"', '"+ friend + "', "+ date.getTime() +")");
                session.execute("INSERT INTO followers (username, follower, since)"
                        + "VALUES ('"+ friend +"', '"+ user + "', "+ date.getTime() +")");
                
                listFollower.add(friend);
                System.out.println("Follow success");
            } else {
                System.out.println("You're already follow");
            }
        } else {
            System.out.println("Username doesn't exist");
        }
        
    }
    
    public void logout(){
        System.out.println("Logout success !");
        System.out.println("bye");
        
        user = "unknown";
    }
    
    public void close(){
        cluster.close();
    }
    
    public String getUser(){
        return user;
    }
    
}
