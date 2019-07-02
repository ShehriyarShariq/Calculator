package com.company;

import java.math.BigDecimal;

public class MathOps {

    // Private constructor to not allow for an object to be made
    private MathOps(){}

    // Addition method
    public static double add(double op1, double op2){
        return op1 + op2;
    }

    // Subtraction method
    public static double sub(double op1, double op2){
        return op1 - op2;
    }

    // Multiplication method
    public static double mult(double op1, double op2){
        return op1 * op2;
    }

    // Division method
    public static double div(double op1, double op2){
        return op1 / op2;
    }

    // Trignometric sin function
    public static BigDecimal sinInDeg(double angleInDeg){
        return new BigDecimal(String.valueOf(Math.sin(Math.toRadians(angleInDeg))));
    }

    // Trignometric inverse sin function
    public static BigDecimal arcsinInDeg(double val){
        return new BigDecimal(String.valueOf(Math.asin(val)));
    }

    // Trignometric cos function
    public static BigDecimal cosInDeg(double angleInDeg){
        return new BigDecimal(String.valueOf(Math.cos(Math.toRadians(angleInDeg))));
    }

    // Trignometric inverse cos function
    public static BigDecimal arccosInDeg(double val){
        return new BigDecimal(String.valueOf(Math.acos(val)));
    }

    // Trignometric tan function
    public static BigDecimal tanInDeg(double angleInDeg){
        return new BigDecimal(String.valueOf(Math.tan(Math.toRadians(angleInDeg))));
    }

    // Trignometric inverse tan function
    public static BigDecimal arctanInDeg(double val){
        return new BigDecimal(String.valueOf(Math.atan(val)));
    }

    // Logarithm base 10
    public static BigDecimal logBase10(double val){
        return new BigDecimal(String.valueOf(Math.log10(val)));
    }

    // Natural Logarithm
    public static BigDecimal logBaseE(double val){
        return new BigDecimal(String.valueOf(Math.log(val)));
    }

    // Truncate to n decimal places
    public static double roundToNDecimalPoint(String val, int roundTo){
        if((val.length() - val.indexOf('.') - 1) > roundTo){
            val = val.substring(0,  val.indexOf('.') + roundTo + 1);
        }
        return Double.parseDouble(val);
    }

}