package com.example.elasticsearchdemo.engine.search;

import org.jboss.logging.Logger;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

/**
 * @Description: TODO
 * @author: jlsong E-mail: rainmap@163.com
 * @date: created in 下午5:16 19-7-1
 */
public class LogicExpressionUtils {

    private static final Logger logger = Logger.getLogger(LogicExpressionUtils.class);

    public static String suffixExpression(String expression){
        Queue<String> value = new LinkedList<>();
        Stack<Character> operator = new Stack<>();
        int len = expression.length();
        char currentChar;
        String operand;
        int m, n = -1;
        for (int i = 0; i < len; i++){
            currentChar = expression.charAt(i);
            if (isOperator(currentChar) || (i == len - 1)){
                if (i == len - 1 && (!isOperator(currentChar))){
                    m = i + 1;
                }else {
                    m = i;
                }
                for (int j = i - 1; j >= 0; j--){
                    if (isOperator(expression.charAt(j))){
                        n = j;
                        break;
                    }
                    n = j - 1;
                }
                if (m != n + 1){
                    operand = expression.substring(n + 1, m);
                    ((LinkedList<String>) value).add(operand);
                }
                if (i == 0 && (currentChar != '(') && (currentChar != '!')){
                    logger.error(String.format("Logic expression format error: %s", expression));
                    return  null;
                }else if (isOperator(currentChar)){
                    while (true){
                        if (currentChar == ')'){
                            while (operator.peek() != '('){
                                ((LinkedList<String>) value).add(operator.pop().toString());
                                if (operator.isEmpty()){
                                    logger.error(String.format("Logic expression format error: %s", expression));
                                    return null;
                                }
                            }
                            operator.pop();
                            break;
                        }else if (operator.isEmpty() || operator.peek() == '(' || currentChar == '('){
                            operator.push(currentChar);
                            break;
                        }else {
                            if (priorityCompare(currentChar, operator.peek()) == 1){
                                operator.push(currentChar);
                                break;
                            }else {
                                ((LinkedList<String>) value).add(operator.pop().toString());
                            }
                        }
                    }
                }
            }else {
                continue;
            }
        }

        while (!operator.isEmpty()) {
            if ((char) operator.peek() == '(') {
                logger.error(String.format("Logic expression format error: %s", expression));
                return null;
            }
            ((LinkedList<String>) value).add(operator.pop().toString());

        }
        return count_result(value);
    }


    private static int priorityCompare(char c1, char c2){
        switch (c1){
            case '&':
            case '|':
                return (c2 == '!' ? -1 : 0);
            case '!':
                return (c2 == '&' || c2 == '|' ? 1 : 0);
        }
        return 1;
    }


    private static boolean isOperator(char c){
        return (c == '!' || c == '&' || c == '|' || c == '(' || c == ')');
    }


    private static String count_result(Queue<String> value){
        Stack<String> result = new Stack<>();

        while (!value.isEmpty()){
            String operand = value.poll();
            boolean isOperator = false;
            if (1 == operand.length() && isOperator(operand.charAt(0))){
                isOperator = true;
            }
            if (!isOperator){
                result.push(operand);
            }else if ('!' == operand.charAt(0)){
                String str = result.pop();
                result.push(handleNotOperator(str));
            }else if ('&' == operand.charAt(0)){
                String operand1 = result.pop();
                String operand2 = result.pop();
                result.push(handleAndOperator(operand1, operand2));
            }else if ('|' == operand.charAt(0)){
                String operand1 = result.pop();
                String operand2 = result.pop();
                result.push(handleOrOperator(operand1, operand2));
            }

        }

        return result.peek();
    }


    private static String handleOrOperator(String operand1, String operand2){
        StringBuffer result = new StringBuffer();
        String operator1 = extractOperator(operand1);
        String operator2 = extractOperator(operand2);
        boolean complex = (null == operator1 || "|".equals(operator1)) && (null == operator2 || "|".equals(operator2));
        if (complex){
            result.append(operand2 + "|" + operand1);
            return result.toString();
        }else if (null == operator1 || "|".equals(operator1)){
            if (!complexExpression(operand1)){
                String[] values = operand2.split("&");
                for (int i = 0; i < values.length; i++){
                    String str = removeBracket(values[i]);
                    result.append("(" + str + "|" + operand1 + ")");
                    result.append("&");
                }
                result.deleteCharAt(result.length() - 1);
                return result.toString();
            }else {
                if (complexExpression(operand2)){
                    operand2 = trans(operand2, "&");
                    result.append(operand2 + "|" + operand1);
                }else {
                    result.append("(" + operand2 + ")" + "|" + operand1);
                }
                return result.toString();
            }
        }else if (null == operator2 || "|".equals(operator2)){
            if (!complexExpression(operand2)){
                String[] values = operand1.split("&");
                for (int i = 0; i < values.length; i++){
                    String str = removeBracket(values[i]);
                    result.append("(" + operand2 + "|" + str + ")");
                    result.append("&");
                }
                result.deleteCharAt(result.length() - 1);
                return result.toString();
            }else {
                if (complexExpression(operand1)){
                    operand1 = trans(operand1, "&");
                    result.append(operand2 + "|" + operand1);
                }else {
                    result.append(operand2 + "|" + "(" + operand1 + ")");
                }
                return  result.toString();
            }
        }else {
            if (!complexExpression(operand1) && !complexExpression(operand2)){
                String[] values1 = operand1.split("&");
                String[] values2 = operand2.split("&");
                for (int i = 0; i < values1.length; i++){
                    String str1 = values1[i];
                    str1 = removeBracket(str1);
                    for (int j = 0; j < values2.length; j++){
                        String str2 = removeBracket(values2[j]);
                        result.append(String.format("(%s|%s)", str2, str1));
                        result.append("&");
                    }
                }
                result.deleteCharAt(result.length() - 1);
                return result.toString();
            }else {
                if (complexExpression(operand1) && complexExpression(operand2)){
                    operand1 = trans(operand1, "&");
                    operand2 = trans(operand2, "&");
                    result.append(String.format("%s|%s", operand2, operand1));
                }else if (complexExpression(operand1)){
                    operand1 = trans(operand1, "&");
                    result.append(String.format("(%s)|%s", operand2, operand1));
                }else if (complexExpression(operand2)){
                    operand2 = trans(operand2, "&");
                    result.append(String.format("%s|(%s)", operand2, operand1));
                }else {
                    result.append(String.format("(%s)|(%s)", operand2, operand1));
                }
                return result.toString();
            }
        }

    }


    private static String handleAndOperator(String operand1, String operand2){
        StringBuffer result = new StringBuffer();
        String operator1 = extractOperator(operand1);
        String operator2 = extractOperator(operand2);
        boolean complex = (null == operator1 || "&".equals(operator1)) && (null == operator2 || "&".equals(operator2));
        if (complex){
            result.append(String.format("%s&%s", operand2, operand1));
            return result.toString();
        }else if (null == operator1 || "&".equals(operator1)){
            if (!complexExpression(operand1)){
                String[] values = operand2.split("\\|");
                for (int i = 0; i < values.length; i++){
                    String str = removeBracket(values[i]);
                    result.append(String.format("(%s&%s)", str, operand1));
                    result.append("|");
                }
                result.deleteCharAt(result.length() - 1);
                return result.toString();
            }else {
                if (complexExpression(operand2)){
                    operand2 = trans(operand2, "|");
                    result.append(String.format("%s&%s", operand2, operand1));
                }else {
                    result.append(String.format("(%s)&%s", operand2, operand1));
                }
                return result.toString();
            }
        }else if (null == operator2 || "&".equals(operator2)){
            if (!complexExpression(operand2)){
                String[] values = operand1.split("\\|");
                for (int i = 0; i < values.length; i++){
                    String str = removeBracket(values[i]);
                    result.append(String.format("(%s&%s)", operand2, str));
                    result.append("|");
                }
                result.deleteCharAt(result.length() - 1);
                return result.toString();
            }else {
                if (complexExpression(operand1)){
                    operand1 = trans(operand1, "|");
                    result.append(String.format("%s&%s", operand2, operand1));
                }else {
                    result.append(String.format("%s&(%s)", operand2, operand1));
                }
                return result.toString();
            }
        }else {
            if (!complexExpression(operand1) && !complexExpression(operand2)){
                String[] values1 = operand1.split("\\|");
                String[] values2 = operand2.split("\\|");
                for (int i = 0; i < values1.length; i++){
                    String str1 = values1[i];
                    str1 = removeBracket(str1);
                    for (int j = 0; j < values2.length; j++){
                        String str2 = removeBracket(values2[j]);
                        result.append(String.format("(%s&%s)", str2, str1));
                        result.append("|");
                    }
                }
                result.deleteCharAt(result.length() - 1);
                return result.toString();
            }else {
                if (complexExpression(operand1) && complexExpression(operand2)){
                    operand1 = trans(operand1, "|");
                    operand2 = trans(operand2, "|");
                    result.append(String.format("%s&%s", operand2, operand1));
                }else if (complexExpression(operand1)){
                    operand1 = trans(operand1, "|");
                    result.append(String.format("(%s)&%s", operand2, operand1));
                }else if (complexExpression(operand2)){
                    operand2 = trans(operand2, "|");
                    result.append(String.format("%s&(%s)", operand2, operand1));
                }else {
                    result.append(String.format("(%s)&(%s)", operand2, operand1));
                }
                return result.toString();
            }
        }

    }


    private static String handleNotOperator(String expression){
        expression = removeBracket(expression);
        String operator = extractOperator(expression);
        StringBuffer result = new StringBuffer();
        if (null == operator){
            if (notExpression(expression)){
                return expression.substring(1);
            }else {
                result.append("!");
                result.append(expression);
                return result.toString();
            }
        }else if ("&".equalsIgnoreCase(operator)){
            String[] subExpressions = expression.split("&");
            for (String subExpression : subExpressions){
                result.append(handleNotOperator(subExpression));
                result.append("|");

            }
            result.deleteCharAt(result.length() - 1);
        }else if ("|".equalsIgnoreCase(operator)){
            String[] subExpressions = expression.split("\\|");
            for (String subExpression : subExpressions){
                result.append(handleNotOperator(subExpression));
                result.append("&");
            }
            result.deleteCharAt(result.length() - 1);
        }
        return result.toString();
    }


    public static String extractOperator(String expression){
        if (expression.startsWith("(")){
            Character currentChar;
            int len = expression.length();
            int i = 0;
            for (i = 0; i < len; i++){
                currentChar = expression.charAt(i);
                if (')' == expression.charAt(i)){
                    break;
                }
            }
            if ((len - 1) == i){
                for (i = 1; i < len; i++){
                    currentChar = expression.charAt(i);
                    if (('&' == expression.charAt(i)) || (('|' == expression.charAt(i)))){
                        return currentChar.toString();
                    }
                }
                return null;
            }else {
                currentChar = expression.charAt(++i);
                return currentChar.toString();
            }
        }else {
            Character currentChar;
            int len = expression.length();
            int i = 0;
            for (i = 0; i < len; i++){
                currentChar = expression.charAt(i);
                if (('&' == expression.charAt(i)) || ('|' == expression.charAt(i))){
                    return currentChar.toString();
                }
            }
            return null;
        }
    }


    private static boolean notExpression(String expression){
        int len = expression.length();
        int i = 0;
        for (i = 0; i < len; i++){
            if ('!' == expression.charAt(i)){
                return true;
            }
        }
        return false;
    }


    private static String removeBracket(String expression){
        String result = "";
        if (expression.startsWith("(") && expression.endsWith(")")){
            result = expression.substring(1, expression.length() - 1);
        }else if (expression.startsWith("(")){
            result = expression.substring(1, expression.length());
        }else if (expression.endsWith(")")){
            result = expression.substring(0, expression.length() - 1);
        }else {
            result = expression;
        }
        return result;
    }


    private static boolean complexExpression(String expression){
        String operator = extractOperator(expression);
        if (null == operator){
            return false;
        }else if ("&".equals(operator)){
            String[] subExpressions = expression.split("&");
            String subOperator = extractOperator(removeBracket(subExpressions[0]));
            if ("|".equals(subOperator)){
                return true;
            }
        }else if ("|".equals(operator)){
            String[] subExpressions = expression.split("\\|");
            String subOperator = extractOperator(removeBracket(subExpressions[0]));
            if ("&".equals(subOperator)){
                return true;
            }

        }
        return false;
    }


    private static String trans(String operand, String operator){
        if (!complexExpression(operand)){
            return operator;
        }

        String[] values = null;
        String result = null;
        if ("&".equals(operator)){
            values = operand.split("&");
            result = values[0];
            for (int i = 1; i < values.length; i++){
                result = and(result, values[i]);
            }
        }else {
            values = operand.split("\\|");
            result = values[0];
            for (int i = 1; i < values.length; i++){
                result = or(result, values[i]);
            }
        }
        return result;
    }


    private static String and(String operand1, String operand2){
        StringBuffer result = new StringBuffer();
        String[] values1 = operand1.split("\\|");
        String[] values2 = operand2.split("\\|");
        for (int i = 0; i < values1.length; i++){
            String str1 = values1[i];
            str1 = removeBracket(str1);
            for (int j = 0; j < values2.length; j++){
                String str2 = removeBracket(values2[j]);
                result.append(String.format("(%s&%s)", str1, str2));
                result.append("|");
            }
        }

        result.deleteCharAt(result.length() - 1);
        return result.toString();
    }


    private static String or(String operand1, String operand2){
        StringBuffer result = new StringBuffer();
        String[] values1 = operand1.split("&");
        String[] values2 = operand2.split("&");
        for (int i = 0; i < values1.length; i++){
            String str1 = values1[i];
            str1 = removeBracket(str1);
            for (int j = 0; j < values2.length; j++){
                String str2 = removeBracket(values2[j]);
                result.append(String.format("(%s|%s)", str1, str2));
                result.append("&");
            }
        }
        result.deleteCharAt(result.length() - 1);
        return result.toString();
    }


    public static void main(String[] args){
        String express = LogicExpressionUtils.suffixExpression("((0|!1))&2&3&4");
        String operator = LogicExpressionUtils.extractOperator(express);
        System.out.println(express);;
        String[] exp = express.split(String.format("\\%s", operator));
        for (String s : exp){
            System.out.println(s + ":" + LogicExpressionUtils.extractOperator(s));
        }
    }
}
