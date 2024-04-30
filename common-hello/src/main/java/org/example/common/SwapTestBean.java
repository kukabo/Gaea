package org.example.common;

import lombok.Data;

@Data
public class SwapTestBean {
    public static int a = 10;
    public static String s = "hello";

    private String name1;
    private String name2;


    public void swapString(String name1, String name2) {
        String tem = name1;
        name1 = name2;
        name2 = tem;
        System.out.println(name1 +"----"+name2);
    }

    public void swapInteger(Integer age1, Integer age2) {
        Integer tem = age1;
        age1 = age2;
        age2 = tem;
        System.out.println(age1 +"----"+ age2);
    }

    public void hi() {
        System.out.println("hi from SwapTestBean");
    }
}
