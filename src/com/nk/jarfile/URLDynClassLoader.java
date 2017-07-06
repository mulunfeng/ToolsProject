package com.nk.jarfile;

import org.springframework.web.bind.annotation.RequestMapping;
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by zhangyuyang on 2017/6/22.
 * load a class file runtime
 */
public class URLDynClassLoader {
    public URLDynClassLoader(){
        try{
            List<Class> list = loadclasses( new File("D:\\class")
                    , new File("D:\\class\\com\\bjnk\\web\\controller") , "com.bjnk.web.controller." );
            if (list != null) {
                for (Class clzz : list) {
                    Method[] methods = clzz.getMethods();
                    for (Method method : methods) {
                        RequestMapping annotation = method.getAnnotation(RequestMapping.class);
                        if (annotation != null) {
                            Type cc = method.getGenericReturnType();
                            if (!(cc instanceof ParameterizedTypeImpl)) {
                                continue;
                            }
                            Type[] types = ((ParameterizedTypeImpl) cc).getActualTypeArguments();
                            if (types == null || types.length ==0 || types.length > 1)
                                throw new RuntimeException("返回值异常");
                            Type tt = types[0];
                            while (tt instanceof ParameterizedTypeImpl || !((Class) tt).getPackage().getName()
                                    .equals("com.bjnk.web.vo.entity")) {
                                tt = ((ParameterizedTypeImpl) tt).getActualTypeArguments()[0];
                            }
                            Set<Field> fields = getMethodFields((Class) tt);
                            for (Field field : fields){
                                System.out.print(field.getName() + " ");
                            }

                            System.out.println(clzz.getSimpleName()+" "+method.getName()+" "+annotation.value()[0]
                            +" "+ method.getReturnType().getSimpleName());
                        }
//                        System.out.println(method.getName());
                    }
                }
            }

        }
        catch(ClassNotFoundException e){
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
        catch( Exception f ){
            System.out.println(f.getMessage());
            System.out.println("error");
        }
    }

    private Set<Field> getMethodFields(Class clazz) {
        Field[] fields = clazz.getDeclaredFields();
        Set<Field> set = new HashSet<Field>();
        for (Field field : fields) {
            String fieldName = field.getName();
            String methodName = "set" + firstLetterToUpper(fieldName);
            try {
                //check if exits public set method
                clazz.getMethod(methodName, field.getType());
                set.add(field);
            } catch (NoSuchMethodException e) {
            }
        }
        return set;
    }

    public String firstLetterToUpper(String str){
        char[] array = str.toCharArray();
        array[0] -= 32;
        return String.valueOf(array);
    }

    private ArrayList<String> getClassNames( File folder ){
        File[] listOfFiles = folder.listFiles();
        ArrayList<String> fileNames = new ArrayList<String>() ;
        for (File file : listOfFiles) {
            if (file.isFile()) {
                if( accept(file.getName()) ){
                    fileNames.add(file.getName());
                }
            }
        }
        return fileNames ;
    }

    /**
     *
     * @param folder 包的根路径！！！
     * @param classFolder class 文件路径
     * @param packageName 完整包名+类名
     * @return
     * @throws MalformedURLException
     * @throws ClassNotFoundException
     */
    ArrayList<Class> loadclasses(File folder, File classFolder , String packageName ) throws MalformedURLException, ClassNotFoundException{

        URLClassLoader load = URLClassLoader.newInstance( new URL[] { folder.toURL() })  ;
        ArrayList<Class> data = new ArrayList<Class>();
        ArrayList<String> names = getClassNames(classFolder);

        for(int i=0 ; i<names.size() ; ++i){
            data.add(load.loadClass( fixName( packageName , names.get(i) ) ));
        }

        return data ;
    }

    private String fixName(String packageName, String className ) {
        className = className.replaceAll(".class", "");
        return packageName+className;
    }

    public boolean accept(String arg) {
        return arg.endsWith(".class");
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        new URLDynClassLoader();
    }
}
