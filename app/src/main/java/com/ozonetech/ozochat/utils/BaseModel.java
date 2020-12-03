package com.ozonetech.ozochat.utils;

import java.util.List;
import java.util.Locale;

public class BaseModel  {

    public boolean isValidString (String data) {
        return data != null && !data.trim().isEmpty();
    }

    public String getValidString (String data) {
        return data == null ? "" : data;
    }

    public boolean isValidList (List list) {
        return list != null && list.size() > 0;
    }

    public boolean isValidObject (Object object) {
        return object != null;
    }

    public String getValidDecimalFormat (String value) {
        if (!isValidString(value)) {
            return "0.00";
        }
        double netValue = Double.parseDouble(value);
        return getValidDecimalFormat(netValue);
    }

    public String getValidDecimalFormat (double value) {
        return String.format(Locale.ENGLISH, "%.2f", value);
    }
}
