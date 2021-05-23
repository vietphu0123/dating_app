package com.example.datingapp.Matches;

import com.example.datingapp.UserObject.UserObject;

import java.util.ArrayList;

public class MatchesObject {
    private  String userID, name, profileImageUrl, need, give,budget, lassMessage, lastTimeStamp, lastSeen, childId;
    private ArrayList<UserObject> userObjectArrayList = new ArrayList<>();

    public MatchesObject(String userID, String name, String profileImageUrl, String need, String give, String budget, String lassMessage, String lastTimeStamp, String lastSeen,String childId) {
        this.userID = userID;
        this.name = name;
        this.profileImageUrl = profileImageUrl;
        this.need = need;
        this.give = give;
        this.budget = budget;
        this.lassMessage = lassMessage;
        this.lastTimeStamp = lastTimeStamp;
        this.lastSeen = lastSeen;
        this.childId= childId;
        this.userObjectArrayList = userObjectArrayList;
    }



    public ArrayList<UserObject> getUserObjectArrayList () {
        return userObjectArrayList;
    }
    public void addUserToArrayList(UserObject mUser) {
        userObjectArrayList.add(mUser);
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public String getNeed() {
        return need;
    }

    public void setNeed(String need) {
        this.need = need;
    }

    public String getGive() {
        return give;
    }

    public void setGive(String give) {
        this.give = give;
    }

    public String getBudget() {
        return budget;
    }

    public void setBudget(String budget) {
        this.budget = budget;
    }

    public String getLassMessage() {
        return lassMessage;
    }

    public void setLassMessage(String lassMessage) {
        this.lassMessage = lassMessage;
    }

    public String getLastTimeStamp() {
        return lastTimeStamp;
    }

    public void setLastTimeStamp(String lastTimeStamp) {
        this.lastTimeStamp = lastTimeStamp;
    }

    public String getLastSeen() {
        return lastSeen;
    }

    public void setLastSeen(String lastSeen) {
        this.lastSeen = lastSeen;
    }

    public String getChildId() {
        return childId;
    }

    public void setChildId(String childId) {
        this.childId = childId;
    }

    public void setUserObjectArrayList(ArrayList<UserObject> userObjectArrayList) {
        this.userObjectArrayList = userObjectArrayList;
    }
}
