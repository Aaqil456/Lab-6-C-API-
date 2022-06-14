package com.example.lab_6_api_a182209;

public class Member {
    String matric;
    String name;
    String clubID;
    String status;
    String id;


    public Member(String matric, String name, String clubID, String status, String id) {
        this.matric = matric;
        this.name = name;
        this.clubID = clubID;
        this.status = status;
        this.id = id;

    }

    public String getMatric() {
        return matric;
    }

    public void setMatric(String matric) {
        this.matric = matric;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getClubID() {
        return clubID;
    }

    public void setClubID(String clubID) {
        this.clubID = clubID;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


}
