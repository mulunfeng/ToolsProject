package com.nk;

import com.nk.util.SystemClock;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;

import static java.lang.Thread.sleep;

/**
 * Created by zhangyuyang1 on 2016/10/31.
 */
public class Test {
    @org.junit.Test
    public void testSystemClock(){
        System.out.println(SystemClock.getInstance().now());
        System.out.println(SystemClock.getInstance().now());
        try {
            sleep(100000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @org.junit.Test
    public void testSystemTime(){
        long begin = System.currentTimeMillis();
        for (int i=0;i<100000;i++) {
            System.currentTimeMillis();
        }
        long end = System.currentTimeMillis();
        System.out.println(end - begin);
    }

    @org.junit.Test
    public void testCount(){
        Integer a = 12;
        Integer b = 12;
        System.out.println(b==a);
        Integer i = 1000;
        Integer m = 1000;
        System.out.println(i==m);
    }

    @org.junit.Test
    public void testScheduledExecutorService(){
        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        //定时执行
        service.scheduleAtFixedRate(new Print(1),100, 100, TimeUnit.MILLISECONDS);
        sleeps(2000);
    }

    private void sleeps(int i) {
        try {
            Thread.sleep(i);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @org.junit.Test
    public void testExecutorService(){
        ExecutorService service = Executors.newFixedThreadPool(2);
        service.execute(new Print(2));
        service.execute(new Print(3));
        //线程池的两个线程运行完才能执行这个线程
        service.execute(new Print(4));
        sleeps(20000);
    }

    @org.junit.Test
    public void testScheduledFuture(){

    }

    @org.junit.Test
    public void testList(){
        List list = new ArrayList(10);
        list.add(1);
        list.add(2);
        list.add(3);
        list.add(4);
        list.add(5);
        list.subList(2, 3).clear();
        list.clear();
        for (int i=0;i<list.size();i++){
            System.out.println(i+"-------"+list.get(i));
        }
    }

    @org.junit.Test
    public void testSort(){
        List<Student> list = new ArrayList<Student>();
        list.add(new Student("1", "mulunfeng", 24));
        list.add(new Student("2", "mulunfeng", 26));

        Collections.sort(list);   //排序

        Student student = new Student("2", "mulunfeng", 26);

        //检索student在list中的位置
        int index1 = list.indexOf(student);//indexOf调用equals比较，比较的是name
        int index2 = Collections.binarySearch(list, student);//binarySearch调用compareTo比较，比较的是age

        System.out.println("index1 = " + index1);
        System.out.println("index2 = " + index2);
    }
}

class Print implements Runnable {

    int i =0 ;

    public Print(int i) {
        this.i = i;
    }

    @Override
    public void run() {
        System.out.println(i);
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

class Student implements Comparable<Student>{
    private String id;
    private String name;
    private int age;

    public Student(String id,String name,int age){
        this.id = id;
        this.name = name;
        this.age = age;
    }

    public boolean equals(Object obj){
        if(obj == null){
            return false;
        }

        if(this == obj){
            return true;
        }

        if(obj.getClass() != this.getClass()){
            return false;
        }

        Student student = (Student)obj;
        if(!student.getName().equals(getName())){
            return false;
        }

        return true;
    }

    public int compareTo(Student student) {
        return this.age - student.age;
    }

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

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}

