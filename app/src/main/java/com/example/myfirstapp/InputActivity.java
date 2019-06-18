package com.example.myfirstapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class InputActivity extends AppCompatActivity {
    // add path as part of key for uniqueness
    public static final String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";
    private static final String LEFT_PAREN = "(";
    private static final String RIGHT_PAREN = ")";
    public static final List<String> OPERATORS =
            Arrays.asList(new String[]{LEFT_PAREN, "x", "/", "+", "-", RIGHT_PAREN});

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input);
    }

    private float calculate(List<String> parsedExpr)
            throws IllegalArgumentException, NullPointerException {
        /* Calculates result of expr and returns as float.
        *  parsedExpr: List of strings, not necessarily a valid expression
        *  return: float or exception for empty or non-parsable expressions */
        if (parsedExpr.isEmpty() || parsedExpr.get(0).equals(""))
            throw new NullPointerException(); // calculator should stay clear if empty
        else if (parsedExpr.size() == 1)
            return Float.parseFloat(parsedExpr.get(0));

        List<Float> numStack = new ArrayList<>();
        List<String> opStack = new ArrayList<>();
        List<String> partialExpr; // use for parentheticals
        while (!parsedExpr.isEmpty()) {
            /* TODO: Refactor to combine with repeated code into determineNextNum subroutine. */
            float firstNum;
            if (numStack.isEmpty()) {
                String firstItem = parsedExpr.get(0);
                parsedExpr.remove(0);
                if (firstItem.equals(LEFT_PAREN)) {
                    int lastRightParenIndex = parsedExpr.lastIndexOf(RIGHT_PAREN);
                    if (lastRightParenIndex == -1)
                        throw new IllegalArgumentException("Mismatched parens");

                    partialExpr = new ArrayList<>(parsedExpr.subList(0, lastRightParenIndex));
                    parsedExpr = new ArrayList<>(parsedExpr.subList(
                            lastRightParenIndex + 1, parsedExpr.size()));
                    firstNum = calculate(partialExpr);
                } else {
                    firstNum = Float.parseFloat(firstItem);
                }
                numStack.add(0, firstNum);
            }
            if (parsedExpr.isEmpty())
                break;

            String newOperator = parsedExpr.get(0);
            float newOperand;
            parsedExpr.remove(0);

            if (parsedExpr.isEmpty())
                // can't end on operator
                throw new NumberFormatException();
            String newOperandStr = parsedExpr.get(0);
            parsedExpr.remove(0);
            if (newOperandStr.equals(LEFT_PAREN)) {
                int lastRightParenIndex = parsedExpr.lastIndexOf(RIGHT_PAREN);
                if (lastRightParenIndex == -1)
                    throw new IllegalArgumentException("Mismatched parens");

                partialExpr = new ArrayList<>(parsedExpr.subList(0, lastRightParenIndex));
                parsedExpr = new ArrayList<>(parsedExpr.subList(
                        lastRightParenIndex + 1, parsedExpr.size()));
                newOperand = calculate(partialExpr);
            } else {
                newOperand = Float.parseFloat(newOperandStr);
            }
            numStack.add(0, newOperand);
            opStack.add(0, newOperator);
            String currentOperator = newOperator;
            while (parsedExpr.isEmpty() ||
                    OPERATORS.indexOf(currentOperator) < OPERATORS.indexOf(parsedExpr.get(0))) {
                // evaluate
                float currentOperand1 = numStack.get(0);
                numStack.remove(0);
                float currentOperand2 = numStack.get(0);
                numStack.remove(0);

                Float result = executeOperation(
                        currentOperand2, currentOperand1, currentOperator);
                if (result.isNaN())
                    throw new NumberFormatException("nan");
                numStack.add(0, result);
                opStack.remove(0);
                if (opStack.isEmpty())
                    break;
                currentOperator = opStack.get(0);
            }
        }

        assert numStack.size() == 1 && opStack.isEmpty();
        return numStack.get(0);
    }

    private float executeOperation(float operand1, float operand2, String operator) {
        switch (operator) {
            case "+":
                return operand1 + operand2;
            case "x":
                return operand1 * operand2;
            case "-":
                return operand1 - operand2;
            case "/":
                return operand1 / operand2;
            default:
                return Float.NaN;
        }
    }

    public void sendMessage(View view) throws IllegalArgumentException {
        EditText editText = (EditText) findViewById(R.id.editText);
        String expr = editText.getText().toString();
        ArrayList<String> parsedExpr = new ArrayList<>(Arrays.asList(expr.split("\\s")));
        String text;
        try {
            text = Float.toString(calculate(parsedExpr));
        } catch (IllegalArgumentException e) {
            /* TODO: Create an Exception class inheriting from IllegalArgumentException. */
            if (e.getMessage() == null)
                text = "error";
            else if (e.getMessage().startsWith("For"))
                text = "invalid expression";
            else
                text = e.getMessage();

        } catch (NullPointerException e){
            text = "";
        }

        editText.setText(text);
    }

    public void buttonInput(View view) {
        Button button = (Button) view;
        String buttonText = button.getText().toString();
        EditText editText = (EditText) findViewById(R.id.editText);
        String existingText = editText.getText().toString();
        String addOnText;
        if (buttonText.equals("(")) {
            addOnText = String.format("%s ", buttonText);
        } else if (buttonText.equals(")")) {
            addOnText = String.format(" %s", buttonText);
        } else if (OPERATORS.contains(buttonText)) {
            addOnText = String.format(" %s ", buttonText);
        } else {
            addOnText = buttonText;
        }
        editText.setText(existingText + addOnText);
    }

    public void buttonClear(View view) {
        EditText editText = (EditText) findViewById(R.id.editText);
        editText.setText("");
    }
}

