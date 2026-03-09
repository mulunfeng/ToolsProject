package com.nk.xml;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by zhangyuyang on 2017/2/21.
 */
@XmlRootElement
public class Test2 {
    @XmlAnyElement
    public List<JAXBElement> entries = new ArrayList<JAXBElement>();
    public Test2(Map<String, String> map) {
        for (Map.Entry<String, String> entry : map.entrySet()) {
            entries.add(new JAXBElement(new QName(entry.getKey()),
                    String.class, entry.getValue()));
        }
    }

    public Test2() {
    }

}
