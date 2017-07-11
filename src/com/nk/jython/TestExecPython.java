package com.nk.jython;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhangyuyang on 2017/7/11.
 */
public enum TestExecPython {
    INSTANCE;

    public void test()
    {
        String scriptFile = this.getClass().getResource("").getPath()+"test.py";
        Map<String,String> properties = new HashMap<String,String>();
        properties.put("userName", "zyy");

        ExecPython.INSTANCE.execute(scriptFile, properties);
    }
}
