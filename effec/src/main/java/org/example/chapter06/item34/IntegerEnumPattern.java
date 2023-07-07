package org.example.chapter06.item34;

public class IntegerEnumPattern {

    public enum Apple { FUJI, PIPPIN, GRANNY_SMITH }
    public enum Orange { NAVEL, TEMPLE, BLOOD }

    public static final int APPLE_FUJI = 0;
    public static final int APPLE_PIPPIN = 1;
    public static final int APPLE_GRANNY_SMITH = 2;

    public static final int ORANGE_NAVEL = 0;
    public static final int ORANGE_TEMPLE = 1;
    public static final int ORANGE_BLOOD = 2;

    public static void main(String[] args) {
        int i = (APPLE_FUJI - ORANGE_TEMPLE) / APPLE_PIPPIN;
        System.out.println(i);
    }
}
