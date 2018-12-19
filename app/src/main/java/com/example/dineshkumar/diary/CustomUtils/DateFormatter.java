package com.example.dineshkumar.diary.CustomUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Dinesh Kumar on 2/26/2018.
 */

public class DateFormatter {

    public static String setDateFormat(Date date)
    {
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
        String dateFormat = format.format(date);
        return dateFormat;
    }
}
