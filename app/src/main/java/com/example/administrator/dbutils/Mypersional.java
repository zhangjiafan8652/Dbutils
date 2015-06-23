package com.example.administrator.dbutils;

/**
 * Created by zhangjiafan on 2015/6/23.
 */
public class Mypersional extends FbaseBean     {

    public String name;
    public String age;
    public String money;
    public String date;

    public Ceshibean ceshibean;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "Mypersional{" +
                "name='" + name + '\'' +
                ", age='" + age + '\'' +
                ", money='" + money + '\'' +
                ", date='" + date + '\'' +
                '}';
    }

    public Ceshibean getCeshibean() {
        return ceshibean;
    }

    public void setCeshibean(Ceshibean ceshibean) {
        this.ceshibean = ceshibean;
    }
}
