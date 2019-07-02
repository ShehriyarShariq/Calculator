package com.company;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

class Calculator {

    String expression; // Expression string
    double result = 0; // Expression result

    // Errors
    boolean isMathError = false;

    public Calculator(String expression){
        this.expression = expression;
        initialize(); // Solve the expression
    }

    void initialize(){
        this.expression = removeSpaces(this.expression);

        if(checklogarithmicErrors(this.expression)){ // Check if logarithmic
            isMathError = true;
        } else {
            // Replace all trig and log functions with their respective values
            this.expression = replaceAllFunctionsAndConstants(this.expression);

            // Fixes all unary minuses in the express and replaces them with a defined constant 'n'
            this.expression = fixUnaryMinus(this.expression);

            // Adds brackets to the expression to define the order of operation
            this.expression = fixExpression(this.expression);

            // Position of all the brackets
            int[][] bracketsPos = bracketsPos(this.expression);

            // Highest order bracket
            int maxOrder = getHighestOrder(bracketsPos);

            // Stores the brackets of the highest order
            ArrayList<HashMap<String, String>> ordered = new ArrayList<>();

            int currentHighestRemainingOrder = getHighestRemainingOrder(bracketsPos, maxOrder + 1); // Get highest remaining order
            for (int i = 0; i < bracketsPos.length; i++) {
                if (bracketsPos[i][2] == currentHighestRemainingOrder) {
                    HashMap<String, String> details = new HashMap<>();
                    String curExp = this.expression.substring(bracketsPos[i][0] + 1, bracketsPos[i][1]);
                    details.put("exp", curExp); // Enclosed bracket expression
                    details.put("startInd", String.valueOf(bracketsPos[i][0])); // Bracket open index
                    details.put("endInd", String.valueOf(bracketsPos[i][1])); // bracket close index

                    ordered.add(details);
                }
            }

            while (ordered.size() != 0) {
                HashMap<String, String> exp = ordered.get(0); // First bracket in the ordered list
                String val = String.valueOf(evaluateExp(exp.get("exp"))); // Solved expression

                // Update string expression
                this.expression = replaceInRange(this.expression, Integer.parseInt(exp.get("startInd")), Integer.parseInt(exp.get("endInd")), val);
                this.expression = fixUnaryMinus(this.expression);

                // Updates brackets list
                bracketsPos = bracketsPos(this.expression);

                ordered.clear();

                currentHighestRemainingOrder = getHighestRemainingOrder(bracketsPos, maxOrder + 1);  // Get highest remaining order
                for (int i = 0; i < bracketsPos.length; i++) {
                    if (bracketsPos[i][2] == currentHighestRemainingOrder) {
                        HashMap<String, String> details = new HashMap<>();
                        String curExp = this.expression.substring(bracketsPos[i][0] + 1, bracketsPos[i][1]);
                        details.put("exp", curExp); // Enclosed bracket expression
                        details.put("startInd", String.valueOf(bracketsPos[i][0])); // Bracket open index
                        details.put("endInd", String.valueOf(bracketsPos[i][1])); // bracket close index

                        ordered.add(details);
                    }
                }
            }

            // final result
            result = evaluateExp(this.expression);
        }
    }

    // Adds and replaces a particular range from a string
    // Takes a string which is to be changed, a starting and ending index in that string and a string with which it has be to replaced in the range
    static String replaceInRange(String string, int startIndex, int endIndex, String replaceWith){
        String finalString = "";
        for(int i = 0; i < string.length(); i++){
            if((i < startIndex) || (i > endIndex)){
                finalString += string.charAt(i);
            } else if(i == startIndex){ // if within replacing range
                finalString += replaceWith;
            }
        }
        return finalString;
    }

    // Get brackets of the highest order
    int getHighestOrder(int[][] bracketPos){
        int highestOrder = 1;
        for(int i = 0; i < bracketPos.length; i++){
            if(bracketPos[i][2] > highestOrder){
                highestOrder = bracketPos[i][2];
            }
        }
        return highestOrder;
    }

    // Get the brackets of the highest order relative to the last searched order
    int getHighestRemainingOrder(int[][] bracketPos, int lastOrder){
        int highestOrder = 1;
        for(int i = 0; i < bracketPos.length; i++){
            if((bracketPos[i][2] > highestOrder) && (bracketPos[i][2] < lastOrder)){
                highestOrder = bracketPos[i][2];
            }
        }
        return highestOrder;
    }

    // Get the number of brackets in the expression
    int numOfBrackets(String expression){
        int numOfBrackets = 0;
        for(int i = 0; i < expression.length(); i++){
            if(expression.charAt(i) == '('){
                numOfBrackets++;
            }
        }
        return numOfBrackets;
    }

    // Get the last available index in the int 2D array to add a new element to
    int getLastAvailableIndex(int[][] bracketsPos){
        int lastAvailableIndex = 0;
        for(int i = 0; i < bracketsPos.length; i++){
            if((bracketsPos[i][0] == -1) || (bracketsPos[i][1] == -1)){
                lastAvailableIndex = i;
                break;
            }
        }
        return lastAvailableIndex;
    }

    // Method overloading
    // Get the last available index in the String array to add a new element to
    int getLastAvailableIndex(String[] array){
        int lastAvailableIndex = 0;
        for(int i = 0; i < array.length; i++){
            if(array[i] == null){
                lastAvailableIndex = i;
                break;
            }
        }
        return lastAvailableIndex;
    }

    // Add to a 2d array
    void addToArray(int[][] bracketsPos, int[] values){
        int lastAvailableIndex = getLastAvailableIndex(bracketsPos);

        for(int i = 0; i < bracketsPos[lastAvailableIndex].length; i++){
            bracketsPos[lastAvailableIndex][i] = values[i];
        }
    }

    // Add to a 1d array
    void addAllToArray(String[] target, String[] source){
        int lastAvailableIndex = getLastAvailableIndex(target);

        for(int i = 0; i < source.length; i++){
            target[lastAvailableIndex + i] = source[i];
        }
    }

    // Get indexes of all brackets
    int[][] bracketsPos(String expression){
        int[][] bracketsPos = new int[numOfBrackets(expression)][3]; // a 2d array with 3 cols and rows equal to the number of opening brackets

        // Initialize the array with -1
        for(int i = 0; i < bracketsPos.length; i++){
            for(int j = 0; j < bracketsPos[i].length; j++){
                bracketsPos[i][j] = -1;
            }
        }

        for(int i = 0; i < expression.length(); i++){
            if(expression.charAt(i) == '('){ // If an opening bracket
                int[] bracketDetails = new int[3]; // bracket row values
                bracketDetails[0] = i; // start index

                int ignores = 0;
                for(int j = i + 1; j < expression.length(); j++){
                    if(expression.charAt(j) == '('){
                        ignores++;
                    } else if(expression.charAt(j) == ')') {
                        if(ignores == 0){
                            bracketDetails[1] = j; // end index
                            break;
                        } else {
                            ignores--;
                        }
                    }
                }

                int order = 1;
                for(int j = getLastAvailableIndex(bracketsPos) - 1; j >= 0; j--){
                    if((bracketDetails[0] > bracketsPos[j][0]) && (bracketDetails[1] < bracketsPos[j][1])){
                        order++;
                    }

                }

                bracketDetails[2] = order; // bracket precedence order

                addToArray(bracketsPos, bracketDetails);
            }
        }
        return bracketsPos;
    }

    // Evaluate a operand1 binaryOperator operand2 type of expression
    double evaluateExp(String expression){
        String[] allowedOperators = {"+", "-", "*", "/"};

        int operatorCounter = 0; // Store the number of operators in the expression
        for(int i = 0; i < expression.length(); i++){
            if(contains(allowedOperators, String.valueOf(expression.charAt(i)))){
                operatorCounter++;
            }
        }

        if(operatorCounter == 0){ // If no operators
            return Double.parseDouble(expression.replace('n', '-'));
        }

        double result = 0;
        char[] expressionArr = expression.toCharArray();
        for(int i = 0; i < expressionArr.length; i++){
            if(contains(allowedOperators, String.valueOf(expressionArr[i]))){
                String operandOne = "", operandTwo = "";
                for(int j = i + 1; j < expressionArr.length; j++){ // Right side of operator
                    if(!contains(allowedOperators, String.valueOf(expressionArr[j]))){
                        operandTwo += expressionArr[j];
                    } else {
                        break;
                    }
                }
                for(int k = i - 1; k >= 0; k--){ // Left side of operator
                    if(!contains(allowedOperators, String.valueOf(expressionArr[k]))){
                        operandOne += expressionArr[k];
                    } else {
                        break;
                    }
                }
                operandOne = reverseStr(operandOne); // reverse the left operand

                operandOne = operandOne.replace('n', '-');
                operandTwo = operandTwo.replace('n', '-');

                result += getExpResult(Double.parseDouble(operandOne), Double.parseDouble(operandTwo), String.valueOf(expressionArr[i]));
            }
        }

        return result;
    }

    // Reverse a string
    String reverseStr(String string){
        String revStr = "";
        for(int i = (string.length() - 1); i >= 0; i--){
            revStr += string.charAt(i);
        }

        return revStr;
    }

    // Linear search for a value inside another array
    boolean contains(String[] array, String find){
        for(String elem : array){
            if(elem.equals(find)){
                return true;
            }
        }

        return false;
    }

    // Remove all white spaces in a string
    String removeSpaces(String string){
        String newString = "";
        for(int i = 0; i < string.length(); i++){
            if(string.charAt(i) != ' '){
                newString += string.charAt(i);
            }
        }

        return newString;
    }

    // Check brackets for syntax error
    boolean checkSyntax(String expression){
        int openBracketCount = 0, closedBracketCount = 0;
        for(int i = 0; i < expression.length(); i++){
            if(expression.charAt(i) == '('){
                openBracketCount++;
            } else if(expression.charAt(i) == ')'){
                closedBracketCount++;
            }
        }

        if(openBracketCount != closedBracketCount){
            return false;
        }


        return true;
    }

    // Solve for basic operations
    double getExpResult(double op1, double op2, String operand){
        switch (operand){
            case "+":
                return MathOps.add(op1, op2);
            case "-":
                return MathOps.sub(op1, op2);
            case "*":
                return MathOps.mult(op1, op2);
            case "/":
                return MathOps.div(op1, op2);
        }

        return 0;
    }

    // Solve for the functions
    double getExpResult(double val, String function){
        switch (function){
            case "sin":
                return MathOps.roundToNDecimalPoint(MathOps.sinInDeg(val).toPlainString(), 6);
            case "arcsin":
                return MathOps.roundToNDecimalPoint(MathOps.arcsinInDeg(val).toPlainString(), 6);
            case "cos":
                return MathOps.roundToNDecimalPoint(MathOps.cosInDeg(val).toPlainString(), 6);
            case "arccos":
                return MathOps.roundToNDecimalPoint(MathOps.arccosInDeg(val).toPlainString(), 6);
            case "tan":
                return MathOps.roundToNDecimalPoint(MathOps.tanInDeg(val).toPlainString(), 6);
            case "arctan":
                return MathOps.roundToNDecimalPoint(MathOps.arctanInDeg(val).toPlainString(), 6);
            case "log":
                return MathOps.roundToNDecimalPoint(MathOps.logBase10(val).toPlainString(), 6);
            case "ln":
                return MathOps.roundToNDecimalPoint(MathOps.logBaseE(val).toPlainString(), 6);
        }

        return 0;
    }

    // Add brackets in the expression to define precendence
    String fixExpression(String expression){
        String[] allowedOperators = {"/", "*", "+", "-"};

        for(String operator : allowedOperators){
            int operatorCount = getOperatorCount(expression, operator);
            ArrayList<Integer> visitedIndexes = new ArrayList<>();
            while(operatorCount > 0){
                int relativeBracketOrderRight = 0, relativeBracketOrderLeft = 0;
                int rightEndIndex, leftEndIndex;

                int operatorIndex = expression.indexOf(operator, visitedIndexes.size() == 0 ? 0 : visitedIndexes.get(visitedIndexes.size() - 1) + 2);
                visitedIndexes.add(operatorIndex);

                rightEndIndex = operatorIndex;
                leftEndIndex = operatorIndex;

                for(int j = (operatorIndex + 1); j < expression.length(); j++){
                    if(String.valueOf(expression.charAt(j)).equals("(")){
                        relativeBracketOrderRight++;
                    } else if (String.valueOf(expression.charAt(j)).equals(")") && (relativeBracketOrderRight > 0)) {
                        relativeBracketOrderRight--;
                    }

                    if(contains(allowedOperators, String.valueOf(expression.charAt(j))) && (relativeBracketOrderRight == 0)){
                        break;
                    }

                    rightEndIndex++;
                }

                for(int k = (operatorIndex - 1); k >= 0; k--){
                    if(String.valueOf(expression.charAt(k)).equals(")")){
                        relativeBracketOrderLeft++;
                    } else if (String.valueOf(expression.charAt(k)).equals("(")) {
                        relativeBracketOrderLeft--;
                    }

                    if(contains(allowedOperators, String.valueOf(expression.charAt(k))) && (relativeBracketOrderLeft == 0)){
                        break;
                    }

                    leftEndIndex--;
                }

                expression = expression.substring(0, leftEndIndex) + "(" + expression.substring(leftEndIndex, rightEndIndex + 1) + ")" + expression.substring(rightEndIndex + 1, expression.length());
                operatorCount--;
            }
        }

        return expression;
    }

    // Count of an operator in the expression
    int getOperatorCount(String expression, String operator){
        int count = 0;
        for(char elem : expression.toCharArray()){
            if(String.valueOf(elem).equals(operator)){
                count++;
            }
        }
        return count;
    }

    // Replace all functions with their specific value in the expression
    String replaceAllFunctionsAndConstants(String expression){
        String[] allowedTrigFunctions = {"sin", "cos", "tan", "arcsin", "arccos", "arctan"};
        String[] allowedConstants = {"pi", "e"};
        String[] allowedLogarithmicFunc = {"log", "ln"};
        String[] allFunctionsAndConstants = new String[allowedTrigFunctions.length + allowedLogarithmicFunc.length + allowedConstants.length];

        addAllToArray(allFunctionsAndConstants, allowedTrigFunctions);
        addAllToArray(allFunctionsAndConstants, allowedLogarithmicFunc);
        addAllToArray(allFunctionsAndConstants, allowedConstants);

        for(String function : allFunctionsAndConstants){
            int indexOfFunc = expression.indexOf(function);
            while(indexOfFunc != -1){
                int endIndex = indexOfFunc + function.length() - 1;
                double val;

                for(int i = (indexOfFunc + function.length()); i < expression.length(); i++){
                    if((expression.charAt(i) >= '0') && (expression.charAt(i) <= '9')){
                        endIndex++;
                    } else {
                        break;
                    }
                }

                String valStr = expression.substring(indexOfFunc + function.length(), endIndex + 1);
                val = Double.parseDouble(valStr);
                expression = expression.substring(0, indexOfFunc) + getExpResult(val, function) + expression.substring(endIndex + 1);

                indexOfFunc = expression.indexOf(function, indexOfFunc + 1);
            }
        }

        return expression;
    }

    // Replace all unary minues with n
    String fixUnaryMinus(String expression){
        String[] allowedOperators = {"/", "*", "+", "-"};
        String newStr = "";
        for(int i = 0; i < expression.length(); i++){
            if(expression.charAt(i) == '-'){
                if(i != 0){
                    for(int j = i - 1; j >= 0; j--){
                        if(contains(allowedOperators, String.valueOf(expression.charAt(j))) || !((expression.charAt(j) >= '0') && (expression.charAt(j) <= '9'))){
                            newStr += "n";
                            break;
                        } else if(expression.charAt(j) == ')'){
                            newStr += expression.charAt(i);
                            break;
                        } else if((expression.charAt(j) >= '0') && (expression.charAt(j) <= '9')){
                            newStr += expression.charAt(i);
                            break;
                        }
                    }
                } else {
                    newStr += "n";
                }
            } else {
                newStr += expression.charAt(i);
            }
        }
        return newStr;
    }

    // Check log errors
    boolean checklogarithmicErrors(String expression){
        String[] allowedLogarithmicFunc = {"log", "ln"};

        for(String function : allowedLogarithmicFunc){
            int indexOfFunc = expression.indexOf(function);
            while(indexOfFunc != -1){
                int endIndex = indexOfFunc + function.length() - 1;
                double val = 0;

                for(int i = (indexOfFunc + function.length()); i < expression.length(); i++){
                    if((expression.charAt(i) >= '0') && (expression.charAt(i) <= '9')){
                        endIndex++;
                    } else {
                        break;
                    }
                }

                String valStr = expression.substring(indexOfFunc + function.length(), endIndex + 1);
                val = Double.parseDouble(valStr);

                indexOfFunc = expression.indexOf(function, indexOfFunc + 1);

                if(val == 0){
                    return true;
                }
            }
        }

        return false;
    }

    public double getResult(){
        return this.result;
    }

    public boolean getIsMathError(){
        return this.isMathError;
    }

    public void setExpression(String expression){
        this.expression = expression;
        initialize();
    }
}