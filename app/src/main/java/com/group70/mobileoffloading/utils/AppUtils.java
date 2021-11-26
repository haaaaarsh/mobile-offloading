package com.group70.mobileoffloading.utils;

import android.content.Context;
import android.location.Location;
import android.util.Log;
import android.widget.Toast;

import com.group70.mobileoffloading.data.Slave;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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

    public static double getDistance(double lat1, double lon1, double lat2, double lon2, String s_name) {
        if (lat1 == 0 || lon1 == 0 || lat2 == 0 || lon2 == 0) {
            return 0;
        }
        Location startPoint = new Location("locationA");
        startPoint.setLatitude(lat1);
        startPoint.setLongitude(lon1);

        Location endPoint = new Location("locationA");
        endPoint.setLatitude(lat2);
        endPoint.setLongitude(lon2);

//        Toast.makeText(MainActivity.this, "Distance From Slave("+s_name+"): "+String.valueOf(startPoint.distanceTo(endPoint)), Toast.LENGTH_LONG).show();

        return startPoint.distanceTo(endPoint);
    }

    public static File writeToFile(Context context, Slave s) {
        //external file write
        File path = context.getFilesDir();
        File folder = new File(path.getAbsolutePath() + "/text/");

        if (!folder.exists()) {
            folder.mkdir();
        }
        File file = new File(folder, "slave_stats.txt");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {

            FileOutputStream fileout = new FileOutputStream(file, true);
            fileout.write(s.getAllVariables().getBytes());
            fileout.write("\n".getBytes());
            fileout.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return file;
    }

}
