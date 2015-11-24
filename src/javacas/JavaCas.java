/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package javacas;

import java.util.Scanner;

/**
 * @author Rafi Ramadhan / 13512075
 * @author Hendro Triokta Brianto / 13512081
 */
public class JavaCas {
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        TweetMethod tweetMethod;
        String IP = "0.0.0.0";  // IP
        String keyspace = "";   // input your keyspace
        Boolean run = true;
        
        // Connect cluster in cassandra
        tweetMethod = new TweetMethod(IP, keyspace);
        
        Scanner in = new Scanner(System.in);
        
        while(run){
            String input = in.nextLine();
            String[] split = input.split(" ");
            
            switch(split[0]){
                case "/register":
                    if(tweetMethod.getUser().equals("unknown")){
                        tweetMethod.register(split[1], split[2]);
                    } else {
                        System.out.println("Log out first");
                    }
                    break;
                case "/login":
                    if(tweetMethod.getUser().equals("unknown")){
                        tweetMethod.login(split[1], split[2]);
                    } else {
                        System.out.println("You're logged in");
                        System.out.println("Log out first");
                    }
                    break;
                case "/follow":
                    if(!tweetMethod.getUser().equals("unknown")){
                        tweetMethod.follow(split[1]);
                    } else {
                        System.out.println("Not allowed");
                        System.out.println("Log in first");
                    }
                    break;
                case "/expand_tweet":
                    if(!tweetMethod.getUser().equals("unknown")){
                        tweetMethod.expand_tweet(split[1]);
                    } else {
                        System.out.println("Not allowed");
                        System.out.println("Log in first");
                    }
                    break;
                case "/timeline":
                    if(!tweetMethod.getUser().equals("unknown")){
                        tweetMethod.timeline();
                    } else {
                        System.out.println("Not allowed");
                        System.out.println("Log in first");
                    }
                    break;
                case "/logout":
                    if(!tweetMethod.getUser().equals("unknown")){
                        tweetMethod.logout();
                    } else {
                        System.out.println("Not allowed");
                        System.out.println("Log in first");
                    }
                    break;
                case "/exit":
                        run = false;
                    break;
                default:
                        tweetMethod.tweet(input);

            }
        }
        
        tweetMethod.close();
    }
    
}
