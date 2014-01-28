package com.kronosad.projects.kronoskonverse.common.objects;

import com.google.gson.Gson;
import com.kronosad.projects.kronoskonverse.common.interfaces.INetworkable;
import com.kronosad.projects.kronoskonverse.common.user.User;

import java.util.ArrayList;

/**
 * User: russjr08
 * Date: 1/27/14
 * Time: 10:49 PM
 */
public class Room implements INetworkable {

    private String name, topic;
    private ArrayList<User> users = new ArrayList<User>();
    private User adminUser;

    public Room(String name,  User adminUser) {
        this.name = name;
        this.adminUser = adminUser;
    }

    // Used for serialization. Don't use this constructor
    public Room(){};

    public String getName() {
        return name;
    }

    public ArrayList<User> getUsers() {
        return users;
    }

    public User getAdminUser() {
        return adminUser;
    }

    public void addUser(User user){
        this.users.add(user);
    }

    public void removeUser(String toRemove){
        for(User user : users){
            if(toRemove.equals(user.getUsername())){
                users.remove(user);
            }
        }
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    @Override
    public String toJSON() {
        return new Gson().toJson(this);
    }
}
