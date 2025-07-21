package org.example.common.bean.clazz.loader;

public class BrokenClassABean {

    public void hello() {
        System.out.println("BrokenClassABean: " + this.getClass().getClassLoader());
        BrokenClassBBean testB = new BrokenClassBBean();
        testB.hello();
    }

}