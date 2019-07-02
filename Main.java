package com.company;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);

        System.out.print("Enter in an expression [Enter to q to exit]: ");
        String userExpressionInp = input.nextLine(); // Expression input

        Calculator calculator = new Calculator(userExpressionInp);

        while(!userExpressionInp.equals("q")){ // q to exit
            calculator.setExpression(userExpressionInp);
            boolean isMathError = calculator.getIsMathError();

            if(isMathError){ // Math error
                System.out.print("Math Error!");
            } else {
                System.out.print(userExpressionInp + " = " + calculator.getResult());
            }

            System.out.print("\n\nEnter in an expression [Enter to q to exit]: ");
            userExpressionInp = input.nextLine();
        }

        System.out.println("Calculator closed...!");

        Math math = (Math) new Object();
        math.

    }


}
