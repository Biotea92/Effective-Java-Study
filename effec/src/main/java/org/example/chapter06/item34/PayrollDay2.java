package org.example.chapter06.item34;

import java.util.function.BiFunction;

import static org.example.chapter06.item34.PayrollDay.PayType.WEEKDAY;
import static org.example.chapter06.item34.PayrollDay.PayType.WEEKEND;

public enum PayrollDay2 {
    MONDAY(WEEKDAY), TUESDAY(WEEKDAY), WEDNESDAY(WEEKDAY),
    THURSDAY(WEEKDAY), FRIDAY(WEEKDAY),
    SATURDAY(WEEKEND), SUNDAY(WEEKEND);

    private final PayrollDay.PayType payType;

    PayrollDay2(PayrollDay.PayType payType) {
        this.payType = payType;
    }

    public int pay(int minutesWorked, int payRate) {
        return payType.pay(minutesWorked, payRate);
    }

    private static final int MINS_PER_SHIFT = 8 * 60;

    enum PayType {
        WEEKDAY((minsWorked, payRate) -> minsWorked <= MINS_PER_SHIFT ? 0 : (minsWorked - MINS_PER_SHIFT) * payRate / 2),
        WEEKEND((minsWorked, payRate) -> minsWorked * payRate / 2);

        private final BiFunction<Integer, Integer, Integer> overtimeFunction;

        PayType(BiFunction<Integer, Integer, Integer> overtimeFunction) {
            this.overtimeFunction = overtimeFunction;
        }

        int overtimePay(int mins, int payRate) {
            return overtimeFunction.apply(mins, payRate);
        }

        int pay(int minsWorked, int payRate) {
            int basePay = minsWorked * payRate;
            return basePay + overtimePay(minsWorked, payRate);
        }
    }

    public static void main(String[] args) {
        for (PayrollDay2 day : values())
            System.out.printf("%-10s%d%n", day, day.pay(8 * 60, 1));
    }
}
