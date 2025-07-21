package org.example.common.bean.clazz.loader;

public class Hello {

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "========这是Hello测试文件2号========";
    }

    public void add() {
        try {
            System.out.println("add 方法" + Class.forName("org.example.common.bean.clazz.loader.Hello").getClassLoader());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
