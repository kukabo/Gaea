package org.example.common.file.io;

import org.junit.Test;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * 恢复 ObjectOutputStreamPart 序列化的内容 outputData.dat
 * */
public class ObjectInputStreamPart {
    public static void main(String[] args) {

    }

    @Test
    public void test() throws IOException, ClassNotFoundException {
        //反序列化时顺序一定要和序列化顺序一样
        String path = "src/main/resources/outputData.dat";
        ObjectInputStream ois = new ObjectInputStream(Files.newInputStream(Paths.get(path)));

        //反序列化对象
        int i = ois.readInt();
        System.out.println(i);
        char c = ois.readChar();
        System.out.println(c);
        boolean b = ois.readBoolean();
        System.out.println(b);
        double d = ois.readDouble();
        System.out.println(d);
        String s = ois.readUTF();
        System.out.println(s);
        //cat的编译类型是 Object
        Object cat = ois.readObject();
        System.out.println("运行类型是" + cat.getClass());
        System.out.println("cat信息 " + cat);

        //向下转型，为了能调用 cat 方法
        Cat cat1 = (Cat) cat;
        System.out.println(cat1.getName());

        //关闭流
        ois.close();
    }
}
