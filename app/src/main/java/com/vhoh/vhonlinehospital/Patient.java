package com.vhoh.vhonlinehospital;

public class Patient {
    private String Name;
    private String Age;
    private String Email;
    private String Contact;
    private String Address;
    private String Description;
    private String ImageUrl;

    public Patient() {

    }

    public Patient(String name, String age, String email, String phone, String location, String description) {
        Name = name;
        Age = age;
        Email = email;
        Contact = phone;
        Address = location;
        Description = description;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getAge() {
        return Age;
    }

    public void setAge(String age) {
        Age = age;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getContact() {
        return Contact;
    }

    public void setContact(String phone) {
        Contact = phone;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String location) {
        Address = location;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getImageUrl() {
        return ImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        ImageUrl = imageUrl;
    }
}
