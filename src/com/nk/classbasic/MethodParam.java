package com.nk.classbasic;

import javassist.*;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.LocalVariableAttribute;
import javassist.bytecode.MethodInfo;

import java.lang.reflect.Method;

/**
 * java低版本中获取参数名称
 * java8中可以使用getParameters()来获取Parameter数组
 * Created by zhangyuyang1 on 2016/5/31.
 */
public class MethodParam {
    public static void main(String[] args) throws NotFoundException {
        Person pv = new Person();
        Method[] methods = pv.getClass().getDeclaredMethods();
        if(methods == null)
            return;
        for(Method method : methods){
            method.getParameterTypes();
            ClassPool classPool = ClassPool.getDefault();
            CtClass ctClass = classPool.get(pv.getClass().getName());
            CtMethod ctMethod = ctClass.getDeclaredMethod(method.getName());
            MethodInfo methodInfo = ctMethod.getMethodInfo();
            CodeAttribute codeAttribute = methodInfo.getCodeAttribute();
            LocalVariableAttribute localVariableAttribute = (LocalVariableAttribute) codeAttribute.getAttribute(LocalVariableAttribute.tag);
            if(localVariableAttribute != null){
                String[] paramNames = new String[ctMethod.getParameterTypes().length];
                int pos = Modifier.isStatic(ctMethod.getModifiers()) ? 0 : 1;
                for (int i = 0; i < paramNames.length; i++) {
                    paramNames[i] = localVariableAttribute.variableName(i + pos);
                }
                System.out.println("方法名:"+method.getName()+":");
                if(paramNames.length == 0)
                    continue;
                for (String param:paramNames) {
                    System.out.println("参数名:"+param);
                }
            }
        }
    }
}

//Test class
class Person{
    Person(){}
    private String name;
    private Integer age;
    private Integer sex;
    private String email;
    private String address;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Integer getSex() {
        return sex;
    }

    public void setSex(Integer sex) {
        this.sex = sex;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
