package org.example.chapter03.item10;

public class Car {

    private String brand;
    private String model;

    public Car(String brand, String model) {
        this.brand = brand;
        this.model = model;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Car)
            return brand.equals(((Car) obj).brand) && model.equals(((Car) obj).model);
        if (obj instanceof String)
            return this.equals(obj);
        return false;
    }

    public static void main(String[] args) {
        Car car1 = new Car("현대", "소나타");
        Car car2 = new Car("현대", "소나타");

        System.out.println(car1);
        System.out.println(car2);
        System.out.println(car1.equals(car2));
        System.out.println(car2.equals(car1));
    }
}
