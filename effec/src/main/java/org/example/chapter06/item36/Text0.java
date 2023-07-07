package org.example.chapter06.item36;

public class Text0 {
    public static final int STYLE_BOLD = 1 << 0; // 1
    public static final int STYLE_ITALIC = 1 << 1; // 2
    public static final int STYLE_UNDERLINE = 1 << 2; // 4
    public static final int STYLE_STRIKETHROUGH = 1 << 3; // 8

    // 매개변수 styles는 0개 이상의 STYLE_ 상수를 비트별 OR한 값이다.
    public void applyStyles(int styles) {
        System.out.printf("Applying styles %d to text%n", styles);
    }

    // 사용 예
    public static void main(String[] args) {
        Text0 text = new Text0();
        text.applyStyles(STYLE_BOLD | STYLE_ITALIC);
    }
}
