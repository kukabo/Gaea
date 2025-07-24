package org.example.common.generics.type;

/**
 * 擦拭法
 * 编译器看到的代码如下
 * JVM看到的代码 T替换成Object
 * */
public class Pair<T> {
    private T first;
    private T last;
    public Pair(T first, T last) {
        this.first = first;
        this.last = last;
    }
    public T getFirst() {
        return first;
    }
    public T getLast() {
        return last;
    }

    public Pair<T> create1(T first, T last) {
        return new Pair<>(first, last);
    }


    /*
    * 1、泛型类型是在实例化对象时确定的，而静态方法是在类加载时就可以直接调用的，无需创建对象实例。所以静态方法中的返回值、参数等不能依赖泛型类型<T>，必须将静态方法的泛型类型和实例类型的泛型类型区分开。
    * 2、静态方法是与类本身相关联的，而不是与类的实例相关联的。因此，即使创建了一个类的实例，静态方法也无法访问实例的泛型类型。
    */
    public static <K> Pair<K> create4(K first, K last) {
        return new Pair<K>(first, last);
    }

    public static void main(String[] args) {

        Pair<String> zs = new Pair<>("zhang", "san");
        String first1 = zs.getFirst();
        String last1 = zs.getLast();
        Pair<String> zs1 = zs.create1("otherZhang", "otherSan");

        Pair<Integer> intType = new Pair<>(1, 2);
        Integer first2 = intType.getFirst();
        Integer last2 = intType.getLast();

        Person zl = new Person("zhao", "liu", 30);
        Person zlWife = new Person("li", "meng", 30);
        Pair<Person> zlPair = new Pair<>(zl, zlWife);
        Person first3 = zlPair.getFirst();
        Person last3 = zlPair.getLast();

        String wangWu = "ww";
        String zhouLiu = "zl";
        Pair<String> stringPair = Pair.<String>create4(wangWu, zhouLiu);

        Integer intType2 = 2;
        Integer intType3 = 3;
        Pair<Integer> integerPair = Pair.<Integer>create4(intType2, intType3);

    }
}

