package com.example.administrator.dbutils;

/**
 * Created by Administrator on 2015/6/23.
 */
public class Ceshibean extends FbaseBean  {

    public String name;

    public String sex;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    @Override
    public String toString() {
        return "Ceshibean{" +
                "name='" + name + '\'' +
                ", sex='" + sex + '\'' +
                '}';
    }
}
