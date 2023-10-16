package com.docirs.ambicioso.io.beans;

import java.io.Serializable;

/**
 * Created by luiseliberal on 08/12/16.
 */
public class FacebookBean implements Serializable {

    private String id;
    private String name;
    private String gender;
    private String birthday;
    private String email;
    private LocationFaceBean location;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public LocationFaceBean getLocation() {
        return location;
    }

    public void setLocation(LocationFaceBean location) {
        this.location = location;
    }
}
