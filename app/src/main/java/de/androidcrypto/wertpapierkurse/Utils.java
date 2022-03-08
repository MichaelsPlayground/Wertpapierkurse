package de.androidcrypto.wertpapierkurse;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Utils {

    static public float dateToUnixDate(String date) {
        float unixDate = 0f;
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date dateUnix = null;
        try {
            dateUnix = dateFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long unixTime = (long) dateUnix.getTime() / 1000;
        unixDate = Float.valueOf(unixTime);
        return unixDate;
    }

}
