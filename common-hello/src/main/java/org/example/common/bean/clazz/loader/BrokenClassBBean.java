package org.example.common.bean.clazz.loader;

public class BrokenClassBBean {
    public void hello() {
        System.out.println("BrokenClassBBean: " + this.getClass().getClassLoader());
    }
}