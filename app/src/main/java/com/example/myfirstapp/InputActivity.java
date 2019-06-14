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
    public static final List<String> OPERATORS =
            Arrays.asList(new String[]{"x", "/", "+", "-"});

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input);
    }

    private String calculate(String expr) {
        /* Calculates result of expr and returns as String.
        *  expr: String of characters, not necessarily a valid expression
        *  return: String of result or "error". */
        ArrayList<String> parsedExpr = new ArrayList<>(Arrays.asList(expr.split("\\s")));
        if (parsedExpr.isEmpty() || parsedExpr.size() == 1) {
            return expr;
        }
        List<Float> numStack = new ArrayList<>();
        List<String> opStack = new ArrayList<>();
        while (!parsedExpr.isEmpty()) {
            try {
                if (numStack.isEmpty()) {
                    float firstNum = Float.parseFloat(parsedExpr.get(0));
                    parsedExpr.remove(0);
                    numStack.add(0, firstNum);
                }
                if (parsedExpr.size() < 1)
                    return "error";
                String newOperator = parsedExpr.get(0);
                parsedExpr.remove(0);
                float newOperand = Float.parseFloat(parsedExpr.get(0));
                parsedExpr.remove(0);

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
                        return "error";
                    numStack.add(0, result);
                    opStack.remove(0);
                    if (opStack.isEmpty())
                        break;
                    currentOperator = opStack.get(0);
                }
            } catch (NumberFormatException e) {
                return "error";
            }
        }
        assert numStack.size() == 1 && opStack.isEmpty();
        return Float.toString(numStack.get(0));
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

    public void sendMessage(View view) {
        EditText editText = (EditText) findViewById(R.id.editText);
        String message = calculate(editText.getText().toString());
        editText.setText(message);
    }

    public void buttonInput(View view) {
        Button button = (Button)view;
        String buttonText = button.getText().toString();
        EditText editText = (EditText) findViewById(R.id.editText);
        String existingText = editText.getText().toString();
        String addOnText;
        if (OPERATORS.contains(buttonText)) {
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

