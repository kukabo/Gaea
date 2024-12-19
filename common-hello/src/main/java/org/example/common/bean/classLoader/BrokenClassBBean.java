package org.example.common.bean.classLoader;

public class BrokenClassBBean {
    public void hello() {
        System.out.println("BrokenClassBBean: " + this.getClass().getClassLoader());
    }
}