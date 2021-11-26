package com.group70.mobileoffloading.utils;

import android.util.Log;
import android.widget.Toast;

import com.group70.mobileoffloading.data.Slave;

import java.util.Arrays;

public final class AppUtils {
    public static void multiplication(Slave slave) {
        if (slave.connected) {
            int[][] m1 = slave.m1;
            int[][] m2 = slave.m2;
            Log.d("hello", String.valueOf(m1[0][0]));
            int[][] result = new int[m1.length][m1[0].length];
            int z = 0;
            for (int i = 0; i < m1.length; i++) {
                int[] dummy = new int[m1.length];
                int y = 0;
                for (int j = 0; j < m1.length; j++) {
                    int sum = 0;
                    for (int k = 0; k < m1[0].length; k++) {
                        sum += m1[i][k] * m2[j][k];
                    }
                    dummy[y++] = sum;
                }
                result[z++] = dummy;
            }

            slave.result = result;
        }

    }
}
