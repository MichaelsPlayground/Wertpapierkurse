package de.androidcrypto.wertpapierkurse;

import android.text.InputFilter;
import android.text.Spanned;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

// limits the number of digits before and after the decimal separator in an EditTextField
// usage: editText.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(5,2)});
public class DecimalDigitsInputFilter implements InputFilter {
// source: https://stackoverflow.com/a/8272212/8166854 answered Nov 25, 2011 at 17:04 Asaf Pinhassi
    Pattern mPattern;
    public DecimalDigitsInputFilter(int digitsBeforeZero,int digitsAfterZero) {
        mPattern=Pattern.compile("[0-9]{0," + (digitsBeforeZero-1) + "}+((\\.[0-9]{0," + (digitsAfterZero-1) + "})?)||(\\.)?");
    }
    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        Matcher matcher=mPattern.matcher(dest);
        if(!matcher.matches())
            return "";
        return null;
    }
}
