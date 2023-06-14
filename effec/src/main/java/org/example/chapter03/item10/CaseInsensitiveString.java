package org.example.chapter03.item10;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

// 코드 10-1 잘못된 코드 - 대칭성 위배! (54-55쪽)
public final class CaseInsensitiveString {
    private final String s;

    public CaseInsensitiveString(String s) {
        this.s = Objects.requireNonNull(s);
    }

    // 대칭성 위배!
//    @Override public boolean equals(Object o) {
//        if (o instanceof CaseInsensitiveString)
//            return s.equalsIgnoreCase(((CaseInsensitiveString) o).s);
//        if (o instanceof String)  // 한 방향으로만 작동한다!
//            return s.equalsIgnoreCase((String) o);
//        return false;
//    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CaseInsensitiveString that = (CaseInsensitiveString) o;
        System.out.println("s = " + s);
        System.out.println("that.s = " + that.s);
        System.out.println("s.equals(that.s) = " + s.equals(that.s));
        return Objects.equals(s, that.s);
    }

    @Override
    public int hashCode() {
        return Objects.hash(s);
    }

    // 문제 시연 (55쪽)
    public static void main(String[] args) {
        CaseInsensitiveString cis = new CaseInsensitiveString("Polish");
        String s = "polish";

        List<CaseInsensitiveString> list = new ArrayList<>();
        list.add(cis);

        System.out.println("cis.equals(s) = " + cis.equals(s));
        System.out.println("s.equals(cis) = " + s.equals(cis));

        System.out.println("cis.equals(new CaseInsensitiveString(\"Polish\")) = " + cis.equals(new CaseInsensitiveString("Polish")));

        System.out.println("list.contains(s) = " + list.contains(s));
    }

    // 수정한 equals 메서드 (56쪽)
//    @Override public boolean equals(Object o) {
//        return o instanceof CaseInsensitiveString && ((CaseInsensitiveString) o).s.equalsIgnoreCase(s);
//    }
}
