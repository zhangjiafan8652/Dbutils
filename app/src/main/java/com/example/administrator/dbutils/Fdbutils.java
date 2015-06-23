package com.example.administrator.dbutils;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Administrator on 2015/6/22.
 */
public class Fdbutils {

    public static Fdbutils mFdbutils;

    private void Fdbutils(){}

    public static FdbUtilsHelper fdbUtilsHelper;
    public static SQLiteDatabase db;

    public static  Fdbutils getmFdbutilsInstance(Context context){


        if (mFdbutils!=null){
            return mFdbutils;
        }else {
            fdbUtilsHelper= new FdbUtilsHelper(context);
            mFdbutils= new Fdbutils();
        }
        return  mFdbutils;

    }

    /**
     * 将对象存入数据库
     * @param mBean
     * @return
     */
    public boolean saveBean(FbaseBean mBean){
        setTable(mBean);
        //判断此表是否存在
        if (isExist(mBean)){

            //判断列是否有增加
            addExistclumd(mBean);

            //存入数据库
            return save(mBean);


        }else {

            //创建此表
            createTable(mBean);
            //保存此表
            saveBeantable(mBean);

            return save(mBean);

        }


    }

    /**
     * 将创建的table存入基类table
     * @param mBean
     */
    private boolean saveBeantable(FbaseBean mBean) {

        db = fdbUtilsHelper.getWritableDatabase();


        ContentValues values = new ContentValues();

        values.put("tablename", tablename + "");
        long id = db.insert(FdbUtilsHelper.ALLTABLE, null, values);

        db.close();
        if (id != -1) {

            return true;
        } else {
            return false;
        }

    }


    /**
     * 将bean存入数据库
     * @param mBean
     */
    private boolean save(FbaseBean mBean){

        db = fdbUtilsHelper.getWritableDatabase();
        Field[] fields = mBean.getClass().getFields();

        ContentValues values = new ContentValues();
        try {
            for (int i = 0; i < fields.length; i++) {
                // System.out.println(field[i].getName()+field[i].get(this));
                int lastIndexOf = fields[i].getType().getName().lastIndexOf(".");
                String objname = fields[i].getType().getName().substring(lastIndexOf + 1);
                Log.d("objname", objname);
                if (objname.equals("String")){
                    Log.d(fields[i].getName(), fields[i].get(mBean)+"");

                    values.put(fields[i].getName(), fields[i].get(mBean) + "");
                }else {
                    //利用base64算法.将不是String的对象转成String存到数据库中
                    String s = ObjectToString(fields[i].get(mBean));
                    values.put(fields[i].getName(), s);

                }

            }

        } catch (Exception e) {
            // TODO: handle exception
            throw new RuntimeException(e);
        }
        long id = db.insert(tablename, null, values);

        db.close();
        if (id != -1) {

            return true;
        } else {
            return false;
        }

    }

    public String getFieldClassname(Field field){
        int lastIndexOf = field.getType().getName().lastIndexOf(".");
        String fieldclassname = field.getType().getName().substring(lastIndexOf + 1);
        return fieldclassname;
    }


    public String getClassname(Class mclass){
        int lastIndexOf = mclass.getName().lastIndexOf(".");
        String classname = mclass.getName().substring(lastIndexOf + 1);
        return classname;
    }

    /**
     * 条件查询默认_id倒序
     * @param mclass
     * @param beanvalues
     * @param <E>
     * @return
     */
    public <E> List<E> query(Class<E> mclass,FdbutilesValues beanvalues){
       return query(mclass,beanvalues,null,0,0);
    }

    /**
     * 条件查询按照orderby排序
     * @param mclass
     * @param orderby
     * @param <E>
     * @return
     */
    public <E> List<E> query(Class<E> mclass,String orderby){
        return query(mclass,null,orderby,0,0);
    }

    /**
     * 条件查询
     * @param mclass
     * @param beanvalues
     * @param count 查询多少条
     * @param size  跳过多少条
     * @param <E>
     * @return
     */
    public <E> List<E> query(Class<E> mclass,FdbutilesValues beanvalues,int count,int size){
        return query(mclass,beanvalues,null,count,size);
    }


    /**
     * 条件查询
     * @param mclass
     * @param beanvalues
     * @param orderby
     * @param count  取多少行
     * @param offset   跳过多少行
     * @param <E>
     * @return
     */
    public <E> List<E> query(Class<E> mclass,FdbutilesValues beanvalues,String orderby,int count,int offset) {
        final Field[] fields = mclass.getFields();
        String[] fieldnames = new String[fields.length];

        for (int i = 0; i < fields.length; i++) {
            fieldnames[i] = fields[i].getName();
        }

        StringBuffer sb = new StringBuffer();
        sb.append("select * from " + getClassname(mclass)+" " );
        String[] values2=null;
        if (beanvalues!=null){
            sb.append(" where ");

            HashMap<String, String> hashMap = beanvalues.getWhereequalto();

            Iterator<String> iterator = hashMap.keySet().iterator();

            ArrayList<String> valuesarrayList = new ArrayList<String>();
            ArrayList<String> keyarrayList = new ArrayList<String>();
            int k = 0;

            while (iterator.hasNext()) {
                String key = iterator.next();
                String values = hashMap.get(key);

                if (k == 0) {
                    sb.append(key + "=? ");
                } else {
                    sb.append("and " + key + " =? ");
                }

                keyarrayList.add(key);
                valuesarrayList.add(values);
                k = k + 1;

            }

             values2 = new String[valuesarrayList.size()];
            for (int i = 0; i < values2.length; i++) {
                values2[i] = valuesarrayList.get(i);

            }
        }


        if (orderby!=null){
            sb.append(" order by "+orderby+" DESC ");
        }else {
            sb.append(" order by "+"_id "+"DESC ");
        }
        if(count!=0){
            sb.append("Limit  "+count+" Offset "+offset);
        }



        // String selection = sb.toString();

        db = fdbUtilsHelper.getWritableDatabase();

        String sql = sb.toString();
      //  System.out.println(sql);

        List<E> infos = new ArrayList<E>();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(sql, values2);
        } catch (Exception e) {
            e.printStackTrace();
            //cursor.close();
            return infos;

        }


        if (cursor.getCount() == 0) {
            cursor.close();
            // db.close();
            return infos;
        }

        while (cursor.moveToNext()) {
            try {
              //  System.out.println(this.getClass().getConstructors().length
                //        + "你懂的构造方法");
                // 获取构造方法创建对象
                E newInstance = (E) mclass.getConstructors()[0]
                        .newInstance();

                Method[] methods = mclass.getMethods();

                for (int i = 0; i < methods.length; i++) {
                    for (int j = 0; j < fields.length; j++) {
                        String methodname = methods[i].getName().toLowerCase();
                        String keyname = "set" + fieldnames[j].toLowerCase();
                        if (methodname.equals(keyname)) {
                            // System.out.println(values2[j]);
                            int columnIndex = cursor
                                    .getColumnIndex(fieldnames[j]);
                            String values = cursor.getString(columnIndex);


                            int lastIndexOf = fields[j].getType().getName().lastIndexOf(".");
                            String objname = fields[j].getType().getName().substring(lastIndexOf + 1);
                           // Log.d("objname", objname);
                            if (objname.equals("String")||objname.equals("Integer")){

                                methods[i].invoke(newInstance, values);

                            }else {



                                methods[i].invoke(newInstance, StringToObject(values));

                            }

                        }
                    }
                }
                infos.add(newInstance);

            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } finally {
                 //cursor.close();
                // db.close();
            }
        }
        cursor.close();
        db.close();
        return infos;

    }

    /**
     *
     *
     *查询所有条目
     * @param mclass
     *            哪个对象的数据库
     * @return
     */
    public <E> List<E> query(Class<E> mclass) {
        //setTable();
        final Field[] fields = mclass.getFields();
        String[] fieldnames = new String[fields.length];

        for (int i = 0; i < fields.length; i++) {
            fieldnames[i] = fields[i].getName();
        }

        StringBuffer sb = new StringBuffer();

        sb.append("select * from " + getClassname(mclass));
        // String selection = sb.toString();

        db = fdbUtilsHelper.getWritableDatabase();

        String sql = sb.toString();

        System.out.println(sql);
		/*
		 * System.out.println(tablename+selection+values2[0]); Cursor cursor =
		 * db.query(tablename, null, selection, values2, null, null, null,
		 * null);
		 */
        List<E> infos = new ArrayList<E>();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(sql, null);
        } catch (Exception e) {
            e.printStackTrace();
            return infos;
        }

        if (cursor.getCount() == 0) {
            cursor.close();
            // db.close();
            return infos;
        }

        while (cursor.moveToNext()) {
            try {
                System.out.println(mclass.getConstructors().length
                        + "你懂的构造方法");
                // 获取构造方法创建对象
                E newInstance = (E)mclass.getConstructors()[0]
                        .newInstance();

                Method[] methods = mclass.getMethods();

                for (int i = 0; i < methods.length; i++) {
                    for (int j = 0; j < fields.length; j++) {
                        String methodname = methods[i].getName().toLowerCase();
                        String keyname = "set" + fieldnames[j].toLowerCase();
                        if (methodname.equals(keyname)) {
                            // System.out.println(values2[j]);
                            int columnIndex = cursor
                                    .getColumnIndex(fieldnames[j]);
                            String values = cursor.getString(columnIndex);

                            int lastIndexOf = fields[j].getType().getName().lastIndexOf(".");
                            String objname = fields[j].getType().getName().substring(lastIndexOf + 1);
                            // Log.d("objname", objname);
                            if (objname.equals("String")||objname.equals("Integer")){

                                methods[i].invoke(newInstance, values);

                            }else {



                                methods[i].invoke(newInstance, StringToObject(values));

                            }


                        }
                    }
                }

                infos.add(newInstance);

            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return infos;
            } finally {
                // cursor.close();
                // db.close();
            }
        }
        cursor.close();
         db.close();
        return infos;
		/*
		 * Cursor cursor = db.query("Orderbean_db", null, "driver_id like ?",
		 * new String[] { driver_id }, null, null, null, "0,10");
		 */
    }



    /**
     * 传入参数 条件参数 删除对应的条目
     *
     * @param beanvalues
     * @return
     */
    public boolean deleData(Class mclass,FdbutilesValues beanvalues) {
        final Field[] fields = mclass.getFields();
        String[] fieldnames = new String[fields.length];

        for (int i = 0; i < fields.length; i++) {
            fieldnames[i] = fields[i].getName();
        }
        // update foods set name='CHOCOLATE BOBKA' where name='Chocolate Bobka';
        HashMap<String, String> hashMap = beanvalues.getWhereequalto();
        Iterator<String> iterator = hashMap.keySet().iterator();
        StringBuffer sb = new StringBuffer();
        ArrayList<String> valuesarrayList = new ArrayList<String>();
        ArrayList<String> keyarrayList = new ArrayList<>();
        int k = 0;
        // select * from mybean where name='张三' and age='5';
        sb.append(" delete from " + getClassname(mclass) + " where ");
        while (iterator.hasNext()) {
            String key = iterator.next();
            String values = hashMap.get(key);

            if (k == 0) {
                sb.append(key + "='" + values + "' ");
            } else {
                sb.append("and " + key + " ='" + values + "' ");
            }
            keyarrayList.add(key);
            valuesarrayList.add(values);
            k = k + 1;
        }
        String sql = sb.toString();
        //System.out.println(sql);
        db = fdbUtilsHelper.getWritableDatabase();
        try {
            db.execSQL(sql);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        db.close();
        return true;
    }


    /**
     * 传入参数 条件参数 更新对应的条目
     *
     * @param beanvalues
     * @return
     */
    public boolean updateData(Class mclass,FdbutilesValues beanvalues) {
        final Field[] fields = mclass.getFields();
        String[] fieldnames = new String[fields.length];

        for (int i = 0; i < fields.length; i++) {
            fieldnames[i] = fields[i].getName();
        }
        StringBuffer sb = new StringBuffer();
        // update foods set name='CHOCOLATE BOBKA' where name='Chocolate Bobka';
        // HashMap<String, String> equalmap = beanvalues.getWhereequalto();
        HashMap<String, String> setmap = beanvalues.getSetting();
        Iterator<String> iterator = setmap.keySet().iterator();
        int k = 0;
        // select * from mybean where name='张三' and age='5';
        // update foods set name='CHOCOLATE BOBKA' where name='Chocolate Bobka';
        sb.append(" update " + getClassname(mclass) + " set ");
        while (iterator.hasNext()) {
            String key = iterator.next();
            String values = setmap.get(key);
            if (k == 0) {
                sb.append(key + "='" + values + "' ");
            } else {
                sb.append("and " + key + " ='" + values + "' ");
            }
            k = k + 1;
        }
        // HashMap<String, String> setmap = beanvalues.getSetting();
        HashMap<String, String> equalmap = beanvalues.getWhereequalto();
        Iterator<String> iterator1 = equalmap.keySet().iterator();
        int y = 0;
        // select * from mybean where name='张三' and age='5';
        // update foods set name='CHOCOLATE BOBKA' where name='Chocolate Bobka';
        sb.append(" where ");
        while (iterator1.hasNext()) {
            String key = iterator1.next();
            String values = equalmap.get(key);
            if (y == 0) {
                sb.append(key + "='" + values + "' ");
            } else {
                sb.append("and " + key + " ='" + values + "' ");
            }
            // keyarrayList.add(key);
            // valuesarrayList.add(values);
            y = y + 1;
        }

        String sql = sb.toString();
        //System.out.println(sql);
        db = fdbUtilsHelper.getWritableDatabase();
        try {
            db.execSQL(sql);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }



    /**
     * 添加不存在的列
     */
    private void addExistclumd(FbaseBean mBean) {

        ArrayList<String> arrayList = new ArrayList<String>();
        String[] fieldnames = getBeanfield(mBean);
        db = fdbUtilsHelper.getWritableDatabase();
        for (int i = 0; i < fieldnames.length; i++) {
            if(!checkColumnExist1(db, tablename, fieldnames[i])){
                arrayList.add(fieldnames[i]);
                System.out.println("这是不存在的字段:"+fieldnames[i]);
            }
        }


        if(arrayList.size()!=0){
            String[] columnnames=new String[arrayList.size()];
            for (int i = 0; i < arrayList.size(); i++) {
                columnnames[i]=arrayList.get(i);
                //alter table mybean add sex1 varchar(20) ;
                StringBuffer sb=new StringBuffer();
                sb.append("alter table "+tablename+" add "+columnnames[i]+" varchar(20)");
                String sql=sb.toString();
                db.execSQL(sql);

            }

        }
        db.close();

    }


    /**
     * 方法1：检查某表列是否存在
     * @param db
     * @param tableName 表名
     * @param columnName 列名
     * @return
     */
    private boolean checkColumnExist1(SQLiteDatabase db, String tableName
            , String columnName) {
        boolean result = false ;
        Cursor cursor = null ;
        try{
            //查询一行
            cursor = db.rawQuery( "SELECT * FROM " + tableName + " LIMIT 0"
                    , null );
            result = cursor != null && cursor.getColumnIndex(columnName) != -1 ;
        }catch (Exception e){
            // Log.e(TAG,"checkColumnExists1..." + e.getMessage()) ;
        }finally{
            if(null != cursor && !cursor.isClosed()){
                cursor.close() ;
            }
        }

        return result ;
    }


    /**
     * 创建一个数据表
     * @param mBean
     */
    private void createTable(FbaseBean mBean) {

        Field[] fields = mBean.getClass().getFields();
        String[] beanfield = new String[fields.length];
        for (int i = 0; i < fields.length; i++) {
            beanfield[i] = fields[i].getName();
            Log.i("测试1",fields[i].getName());
        }

        StringBuffer sb = new StringBuffer();
        sb.append("create table ");
        sb.append(tablename);
        sb.append(" (_id integer primary key autoincrement");
        for (int i = 0; i < beanfield.length; i++) {
            if (getFieldClassname(fields[i]).equals("String")||getFieldClassname(fields[i]).equals("Integer"))
            {
                sb.append("," + beanfield[i] + " varchar(30)");
            }else {
                sb.append("," + beanfield[i] + " varchar(3000)");
            }


        }
        sb.append(")");
        db = fdbUtilsHelper.getWritableDatabase();
        Log.w("测试1",sb.toString());
        // System.out.println(sb.toString());
        db.execSQL(sb.toString());
        db.close();


    }

    /**
     * 获取bean的参数
     */
    private String[] getBeanfield(FbaseBean mbean){
        Field[] fields = mbean.getClass().getFields();
        String[] fieldnames = new String[fields.length];
        for (int i = 0; i < fields.length; i++) {
            fieldnames[i] = fields[i].getName();
        }
        return  fieldnames;
    }



    /**
     * 判断某javabean是否存在
     * @param mBean
     * @return
     */
    private boolean isExist(FbaseBean mBean){
        StringBuffer sb = new StringBuffer();
        sb.append("select * from " + FdbUtilsHelper.ALLTABLE + " where ");
        sb.append("tablename" + "=? ");
        String[] values2 = new String[1];
        for (int i = 0; i < values2.length; i++) {
            values2[i] = tablename;
        }
        String sql = sb.toString();
        db = fdbUtilsHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery(sql, values2);

        if (cursor.getCount() == 0) {
            cursor.close();
            db.close();
            return false;
        }
        return true;
    }

    String tablename;
    private void setTable(FbaseBean mBean) {
        tablename = mBean.getClass().getName();
        int lastIndexOf = tablename.lastIndexOf(".");
        tablename = tablename.substring(lastIndexOf + 1);
    }

    /**
     * 将对象转换成字符串
     * @param object
     * @param <T>
     * @return
     */
    public <T> String ObjectToString(T object){

        ByteArrayOutputStream baos = new ByteArrayOutputStream(3000);
        ObjectOutputStream oos = null;
        try {
            oos = new ObjectOutputStream(baos);
            oos.writeObject(object);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
        // 将Product对象放到OutputStream中
        // 将Product对象转换成byte数组，并将其进行base64编码
        String newWord = new String(myBase64.encode(baos.toByteArray()));
        return newWord;

    }

    /**
     * 将String 转成对象
     * @param k
     * @param <T>
     * @return
     */
    public <T> T StringToObject(String k){
        try {
            // 对Base64格式的字符串进行解码
            byte[] base64Bytes = myBase64.decode(k);
            ByteArrayInputStream bais = new ByteArrayInputStream(base64Bytes);
            ObjectInputStream ois = new ObjectInputStream(bais);
            // 从ObjectInputStream中读取Product对象
            T addWord = (T) ois.readObject();
            return addWord;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            return null;
        }
    }

}