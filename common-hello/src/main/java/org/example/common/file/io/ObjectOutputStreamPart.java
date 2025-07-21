package org.example.common.file.io;

import org.junit.Test;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * 序列化对象
 * ObjectOutputStream 是包装流，用来序列化对象（值和类型的保存）
 */
public class ObjectOutputStreamPart {

    public static void main(String[] args) {

    }

    @Test
    public void test() throws IOException {

        //序列化后，保存的文件格式不是存文本，而是按照ObjectOutputStream的格式来保存
        String path = "src/main/resources/outputData.dat";

        ObjectOutputStream oos = new ObjectOutputStream(Files.newOutputStream(Paths.get(path)));

        //序列化对象，一定是 write 带类型的方法，否则保存的数据里没有类型
        oos.writeInt(1000);// 1000 自动装箱为 Integer 1000，并且 Integer implement Serializable
        oos.writeChar('a');//同上
        oos.writeBoolean(true);//同上
        oos.writeDouble(1.1);//同上
        oos.writeUTF("web Test");

        //此时会异常，因为 Dog没有implement Serializable
        /*Dog dog = new Dog("小日子", 1);
        oos.writeObject(dog);*/

        //Cat implement Serializable，成功
        oos.writeObject(new Cat("小日子", 1));

        oos.close();
        System.out.println("数据序列化完毕");
    }
}

class Dog {
    private String name;
    private Integer age;

    public Dog(String name, Integer age) {
        this.name = name;
        this.age = age;
    }
}

class Cat implements Serializable {
    private String name;
    private Integer age;

    public Cat(String name, Integer age) {
        this.name = name;
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return "Cat{" +
                "name='" + name + '\'' +
                ", age=" + age +
                '}';
    }
}
