package com.agrovector.laboratory.photometer;

import android.util.Log;

import java.util.Arrays;

public class Utils {
    public static int[] hexStringToByteArray(String s) {
        int len = s.length();
        int[] data = new int[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (int) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }

    public static String encodeHexString(int[] byteArray) {
        StringBuffer hexStringBuffer = new StringBuffer();
        hexStringBuffer.append('\t');
        for (int i = 0; i < byteArray.length; i++) {
            hexStringBuffer.append(byteToHex(byteArray[i])+" ");
        }
        return hexStringBuffer.toString();
    }

    public static String byteToHex(int num) {
        char[] hexDigits = new char[2];
        hexDigits[0] = Character.forDigit((num >> 4) & 0xF, 16);
        hexDigits[1] = Character.forDigit((num & 0xF), 16);
        return new String(hexDigits);
    }

    public static int[] rowToData(int[] rowDataArr){
        int i; /*adr = 388 + slotNumb * 86*/
        int[] arrBefore = new int[19], arrAfter = new int[19];
        int[] btArr = rowDataArr;//new byte[86];

        for (i = 0; i < 19; i++) {
            arrBefore[i] = (btArr[i * 2 + 1] << 8) + btArr[i * 2];
            arrAfter[i] = (btArr[i * 2 + 1 + 38] << 8) + btArr[i * 2 + 38];
        }
        int[] arrValues = new int[19];
        for (i = 0; i < 19; i++) {
            int value1;

            value1 = Math.abs(arrAfter[i] - arrBefore[i]);
            if(value1>256)value1-=256;
            arrValues[i] = value1;
        }

        Log.i("UT_RTD_BT_ARR", encodeHexString(btArr));
        Log.i("UT_RTD_ARR_AFT", Arrays.toString(arrAfter));
        Log.i("UT_RTD_ARR_BEF", Arrays.toString(arrBefore));
        Log.i("UT_RTD_ARR_RES", Arrays.toString(arrValues));
        return arrValues;
    }
}
