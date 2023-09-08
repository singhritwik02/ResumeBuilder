package com.ritwik.resumebuilder.datamodel;

import android.os.Parcel;


public class PersonalInfo {
    private String name;
    private String jobTitle;
    private String addressLine1;
    private String addressLine2;
    private String phone;
    private String email;
    private String introLine;
    private String profileImageLink = "";
    private String isFresher; // (YES/NO)

    public String getProfileImageLink() {
        return profileImageLink;
    }

    public void setProfileImageLink(String profileImageLink) {
        this.profileImageLink = profileImageLink;
    }

    public String getIsFresher() {
        return isFresher;
    }

    public void setIsFresher(String isFresher) {
        this.isFresher = isFresher;
    }

    public String getIntroLine() {
        return introLine;
    }

    public void setIntroLine(String introLine) {
        this.introLine = introLine;
    }

    public PersonalInfo() {
        this("", "", "", "", "", "");
    }

    public String isFresher() {
        return isFresher;
    }

    public void setFresher(String fresher) {
        isFresher = fresher;
    }

    public PersonalInfo(String name, String jobTitle, String addressLine1, String addressLine2, String phone, String email) {
        this.name = name;
        this.jobTitle = jobTitle;
        this.addressLine1 = addressLine1;
        this.addressLine2 = addressLine2;
        this.phone = phone;
        this.email = email;
    }

    protected PersonalInfo(Parcel in) {
        name = in.readString();
        jobTitle = in.readString();
        addressLine1 = in.readString();
        addressLine2 = in.readString();
        phone = in.readString();
        email = in.readString();
    }



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getAddressLine1() {
        return addressLine1;
    }

    public void setAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
    }

    public String getAddressLine2() {
        return addressLine2;
    }

    public void setAddressLine2(String addressLine2) {
        this.addressLine2 = addressLine2;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
