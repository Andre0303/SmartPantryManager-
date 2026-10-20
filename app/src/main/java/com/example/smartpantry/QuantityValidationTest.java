
package com.example.smartpantry;

public class QuantityValidationTest {

    public static boolean isValidQuantity(double quantity) {
        return Double.isFinite(quantity) && quantity > 0;
    }

    public static void main(String[] args) {
        System.out.println("-2 valid: " + isValidQuantity(-2));
        System.out.println("0 valid: " + isValidQuantity(0));
        System.out.println("2.5 valid: " + isValidQuantity(2.5));
    }
}
