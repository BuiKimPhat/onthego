package com.leobkdn.onthego.ui.modify_user.list;

public class Users_class {
    String name;
    String email;
    int stt;
    public Users_class(String name, String email, int stt){
        this.name= name;
        this.email=email;
        this.stt=stt;
    }
    public Users_class(Users_class a){
        this.name= a.name;
        this.email=a.email;
        this.stt=a.stt;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getStt() {
        return stt;
    }

    public void setStt(int stt) {
        this.stt = stt;
    }
}
