package com.example.aaa.myapplication.utis;

import java.math.BigDecimal;
import java.text.DecimalFormat;

/**
 * Created by Administrator on 2017/8/24.
 */

public class MathUtils {
    public static double div(int a, int b) {
        BigDecimal b1 = new BigDecimal(a);
        BigDecimal b2 = new BigDecimal(b);
        BigDecimal divide = b1.divide(b2,2,BigDecimal.ROUND_HALF_UP);
        return divide.doubleValue();
    }
}
