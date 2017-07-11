package com.nk.jython;

import org.python.util.PythonInterpreter;

import java.util.Map;
import java.util.Properties;

/**
 * Created by zhangyuyang on 2017/7/11.
 */
public enum ExecPython {
    INSTANCE;

    public void execute(String scriptFile, Map<String,String> properties)
    {
        Properties props = new Properties();
        props.put("python.home","path to the Lib folder(C:\\Users\\zhangyuyang\\AppData\\Local\\Programs\\Python\\Python36-32\\Lib)");
        props.put("python.console.encoding", "UTF-8");
        props.put("python.security.respectJavaAccessibility", "false");
        props.put("python.import.site","false");
        Properties preprops = System.getProperties();
        PythonInterpreter.initialize(preprops, props, new String[0]);
        //获取python解释器
        final PythonInterpreter inter = JythonEnvironment.getInstance().getPythonInterpreter();

        //设置python属性,python脚本中可以使用
        for (Map.Entry<String,String> entry : properties.entrySet())
        {
            inter.set(entry.getKey(), entry.getValue());
        }

        try
        {
            //通过python解释器调用python脚本
            inter.execfile(scriptFile);
        }
        catch (Exception e)
        {
            System.out.println("ExecPython encounter exception:" + e);
        }
    }
}
