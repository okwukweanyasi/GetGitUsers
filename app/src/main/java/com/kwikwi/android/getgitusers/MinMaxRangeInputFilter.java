package com.kwikwi.android.getgitusers;

import android.content.Context;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.Spanned;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by KWIKWI on 9/22/2017.
 */

public class MinMaxRangeInputFilter implements InputFilter {
    private int minValue;
    private int maxValue;
    private Context context;

    public MinMaxRangeInputFilter(Context context, int minVal, int maxVal) {
        this.minValue = minVal;
        this.maxValue = maxVal;
        this.context = context;
    }

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dStart, int dEnd) {
        try {
            // Remove the string out of destination that is to be replaced
            String newVal = dest.toString().substring(0, dStart) + dest.toString().substring(dEnd, dest.toString().length());
            newVal = newVal.substring(0, dStart) + source.toString() + newVal.substring(dStart, newVal.length());
            int input = Integer.parseInt(newVal);
            Log.d("here: ", "min max 1");

            if (isInRange(minValue, maxValue, input)) {
                Log.d("here: ", "min max 2");
                return null;
            }
        } catch (NumberFormatException e) {
           Toast.makeText(context, "Wrong input format.\n Only numbers " + minValue + " to " + maxValue + " can be entered.", Toast.LENGTH_SHORT).show();
            Log.d("here: ", "min max 3");
            e.printStackTrace();
        }
        return "";
    }

    private boolean isInRange(int a, int b, int c) {
        Log.d("min max: ", "a: " + a + " b: " + b + " c: " + c);
        return b > a ? c >= a && c <= b : c >= b && c <= a;
    }
}
