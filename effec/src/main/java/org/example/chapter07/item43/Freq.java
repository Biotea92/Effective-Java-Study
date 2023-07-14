package org.example.chapter07.item43;

import java.util.Map;
import java.util.TreeMap;

// map.merge를 이용해 구현한 빈도표 - 람다 방식과 메서드 참조 방식을 비교해보자. (259쪽)
public class Freq {
    public static void main(String[] args) {
        String[] args2 = {"a", "b", "c"};

        Map<String, Integer> frequencyTable = new TreeMap<>();
        frequencyTable.put("a", 1);
        frequencyTable.put("b", 2);

        for (String s : args2)
            frequencyTable.merge(s, 1, (count, incr) -> count + incr); // 람다
        System.out.println(frequencyTable);

        frequencyTable.clear();
        for (String s : args2)
            frequencyTable.merge(s, 1, Integer::sum); // 메서드 참조
        System.out.println(frequencyTable);
    }
}
