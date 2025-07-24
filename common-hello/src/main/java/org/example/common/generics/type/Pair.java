package org.example.common.generics.type;

/**
 * ���÷�
 * �����������Ĵ�������
 * JVM�����Ĵ��� T�滻��Object
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
    * 1��������������ʵ��������ʱȷ���ģ�����̬�������������ʱ�Ϳ���ֱ�ӵ��õģ����贴������ʵ�������Ծ�̬�����еķ���ֵ�������Ȳ���������������<T>�����뽫��̬�����ķ������ͺ�ʵ�����͵ķ����������ֿ���
    * 2����̬���������౾��������ģ������������ʵ��������ġ���ˣ���ʹ������һ�����ʵ������̬����Ҳ�޷�����ʵ���ķ������͡�
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

