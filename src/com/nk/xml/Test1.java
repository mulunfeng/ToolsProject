package com.nk.xml;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import java.util.*;

/**
 * Created by zhangyuyang on 2017/2/21.
 */
@XmlRootElement(name="root")
@XmlSeeAlso({Test2.class})
public class Test1 {
    public List<Test2> result = new ArrayList<Test2>();

    public Test1() {    // JAXB required
    }


    public void setResult(List<Test2> result) {
        this.result = result;
    }

    public Test1(List<Map> list) {
        for (Map<String, String> map : list) {
            Test2 test2 = new Test2(map);
            result.add(test2);
        }
    }

    public static void main(String[] args) throws Exception {
        JAXBContext context = JAXBContext.newInstance(Test1.class);
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

        Map<String, String> map = new LinkedHashMap<String, String>();
        map.put("key1", "value1");
        map.put("key2", "value2");
        Map<String, String> map1 = new HashMap<String, String>();
        map1.put("key3", "value3");
        map1.put("key4", "value4");
        List<Map> list = new ArrayList();
        list.add(map);
        list.add(map1);

        Test1 mt = new Test1(list);

        marshaller.marshal(mt, System.out);
    }
}
