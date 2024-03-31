package org.example.common.fileIO;

import org.junit.Test;
import java.io.File;
import java.io.IOException;

/*
* 创建文件
*
* */
public class FileCreate {

    @Test
    //方式 1， new File(String pathName)
    public void create01() {
        //创建的文件路径和名称
        String pathName = "src/main/resources/test01.txt";
        //创建文件对象
        File file = new File(pathName);
        try {
            file.createNewFile();//这里才是把文件对象写入磁盘
            System.out.println("File created");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    //方式 2， new File(String parent, String child)，父目录+子路径
    public void create02() {
        //创建的文件路径和名称
        String parent = "src/main/resources";
        String child = "test02.txt";
        File file = new File(parent, child);
        try {
            file.createNewFile();
            System.out.println("File created");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    //方式 3， new File(File parent, String child)，父目录文件+子路径
    public void create03() {
        //创建的文件路径和名称
        File parent = new File("src/main/resources");
        String child = "test03.txt";
        File file = new File(parent, child);
        try {
            file.createNewFile();
            System.out.println("File created");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void info() {
        //创建文件对象
        File file = new File("src/main/resources/test01.txt");//目录+文件名
        //调用相应方法获取文件信息
        System.out.println(file.getName());
        System.out.println(file.exists());
        System.out.println(file.getParentFile());
        System.out.println(file.getAbsolutePath());
        System.out.println(file.getAbsolutePath());
        System.out.println(file.isDirectory());
        System.out.println(file.isFile());
        System.out.println(file.canRead());
        System.out.println(file.canWrite());
        System.out.println(file.canExecute());
        System.out.println("文件大小(字节)" + file.length());
        System.out.println(file.lastModified());
        System.out.println(file.delete());//删除文件

        File file1 = new File("src/main/txtPac"); //目录
        if (file1.mkdirs()) {
            System.out.println("文件夹创建成功");
        }
    }

}
