package br.com.infoshop.model;

import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable {
    public String uid, name, username, pass, email, phone, address, isCreated, isNew;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String name, String username, String email, String pass, String address, String phone) {
        this.name = name;
        this.username = username;
        this.email = email;
        this.pass = pass;
        this.address = address;
        this.phone = phone;
        this.isCreated = "false";
        this.isNew = "false";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String isNew() {
        return isNew;
    }

    public void setNew(String aNew) {
        isNew = aNew;
    }

    public String isCreated() {
        return isCreated;
    }

    public void setCreated(String created) {
        isCreated = created;
    }

    /**
     * PARCELABLE
     **/

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(username);
        dest.writeString(email);
        dest.writeString(pass);
        dest.writeString(address);
        dest.writeString(phone);
        dest.writeString(isCreated);
        dest.writeString(isNew);
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[0];
        }
    };

    private User(Parcel in) {
        name = in.readString();
        username = in.readString();
        email = in.readString();
        pass = in.readString();
        address = in.readString();
        phone = in.readString();
        isCreated = in.readString();
        isNew = in.readString();
    }
}
