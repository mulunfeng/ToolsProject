package com.nk;

import com.nk.redis.IRedisClient;
import com.nk.redis.RedisClientFactory;
import com.nk.util.SystemClock;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static java.lang.Thread.sleep;

/**
 * Created by zhangyuyang1 on 2016/10/31.
 */
public class Test {
    @org.junit.Test
    public void testRandomString(){
//        System.out.println(RandomString(5));
        Random random = new Random();
        Long total = 0L;
        for (int i=0;i<10000000;i++) {
            if (random.nextInt(10) == 10) {
                System.out.println("123");
            }
        }
    }

    /** 产生一个随机的字符串*/
    public static String RandomString(int length) {
        String str = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int num = random.nextInt(36);
            buf.append(str.charAt(num));
        }
        return buf.toString();
    }


    @org.junit.Test
    public void testFinal(){
        final StringBuilder sb = new StringBuilder("123");
        sb.append("改了");
        System.out.println(sb.toString());
    }

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
    @org.junit.Test
    public void compareData(){
        String[] str = new String[]{
                "13660274929570",
                "13631129309155",
                "13637505474125"
        };

        String[] target = new String[]{
                "13660275226868",
                "13660414100084",
                "13660718842238"
        };
        for (String ss:str) {
            boolean flag = false;
            for (String tar : target) {
                if (ss.equals(tar)){
                    flag = true;
                    break;
                }
            }
            if (!flag)
                System.out.println(ss);
        }
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
    public void testThreadRedis() throws Exception {
        IRedisClient redisClient= RedisClientFactory.createRedisClient("127.0.0.1",6379);

        for (int i=0; i< 100000 ;i++) {
            new Thread(new JedisRun(String.valueOf(i),"hello",redisClient)).start();
        }
    }

    class JedisRun implements Runnable{
        private String key;
        private String value;
        private IRedisClient redisClient;

        public JedisRun(String key, String value, IRedisClient redisClient) {
            this.key = key;
            this.value = value;
            this.redisClient = redisClient;
        }

        @Override
        public void run() {
            redisClient.set(key, value);
        }
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

    @org.junit.Test
    public void testEncode(){
        System.out.println(System.getProperty("file.encoding"));
    }

    @org.junit.Test
    public void testHash(){
        Set<Person> set = new HashSet<Person>();
        Person person = new Person();
        person.setName("123");
        person.setSex("男");
        Person person1 = new Person();
        person1.setName("123");
        person1.setSex("女");
        set.add(person);
        set.add(person1);
        System.out.println(set.size());
    }

    @org.junit.Test
    public void testAA(){
        System.out.println(StringUtils.equals(null, "false"));
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


class Person {
    private String name;
    private String sex;

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
    public int hashCode() {
        System.out.println("调用hashCode");
        return name.length();
    }

    @Override
    public boolean equals(Object obj) {
        System.out.println("调用equals");
        return this.name.equals(((Person)obj).getName());
    }

}
