package github.hmasum52.campusdeal.model;

import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class User{
    private String uid;

    private String name;

    private String email;

    private String phone;
    private String address;
    private String profileImageUrl;

    public User() {
    }

    public User(String id, String name, String email, String phone, String address, String profileImageUrl) {
        this.uid = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.profileImageUrl = profileImageUrl;
    }


    public User(FirebaseUser user) {
        this.uid = user.getUid();
        this.name = user.getDisplayName();
        this.email = user.getEmail();
        // add photo url if available
        if(user.getPhotoUrl() != null){
            this.profileImageUrl = user.getPhotoUrl().toString();
        }
    }


    public String getUid() {
        return uid;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }


    public String getPhone() {
        return phone;
    }

    public String getAddress() {
        return address;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    @Override
    public String toString() {
        return "User{" +
                "uid='" + uid + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", address='" + address + '\'' +
                ", profileImageUrl='" + profileImageUrl + '\'' +
                '}';
    }
}
