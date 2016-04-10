package com.example.christopher.shopping_list_app;

import java.math.BigDecimal;

/**
 * Created by Christopher on 3/19/2016.
 */
public class StaticHelpers {
    public static float round(final float d, final int decimalPlace) {
        return BigDecimal.valueOf(d).setScale(decimalPlace, BigDecimal.ROUND_HALF_UP).floatValue();
    }
}
