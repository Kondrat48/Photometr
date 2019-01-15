import com.agrovector.laboratory.photometer.Utils;

import junit.framework.TestCase;

import java.util.Arrays;

public class DataTest extends TestCase {

    public void testData () {
        String value = (
                "f9 01 f9 01 f9 01 f8 01 f8 01 f8 01 f8 01 f8 01 f8 01 f8 01 f8 01 f7 01 f7 01 f7 01 f7 01 f7 01 f7 01 f7 01 f7 01 f8 01 f9 01 f9 01 f9 01 f9 01 f9 01 f9 01 f9 01 f8 01 f8 01 f8 01 f8 01 f7 01 f7 01 f8 01 f7 01 f7 01 f7 01 f8 01 34 09 12 13 07 18 00 05 05 78"
        ).replaceAll(" ", "");
//        System.out.println(value);
        byte[] validByteData = Utils.hexStringToByteArray(value);


        int i; /*adr = 388 + slotNumb * 86*/
        long[] arrBefore = new long[19], arrAfter = new long[19];
        byte[] btArr = validByteData;//new byte[86];

        for (i = 0; i < 19; i++) {
            arrBefore[i] = (btArr[i * 2 + 1] << 8) + btArr[i * 2];
            arrAfter[i] = (btArr[i * 2 + 1 + 38] << 8) + btArr[i * 2 + 38];
        }
        long[] arrValues = new long[19];
        for (i = 0; i < 19; i++) {
            long value1;
            if (arrAfter[i] > arrBefore[i]) {
                value1 = arrAfter[i] - arrBefore[i];
                if (value1 > 256) value1 -= 256;
                arrValues[i] = value1;
            } else arrValues[i] = 0;
        }
        System.out.println(value);
        System.out.println(Arrays.toString(btArr));
        System.out.println(Arrays.toString(arrAfter));
        System.out.println(Arrays.toString(arrBefore));
        System.out.println(Arrays.toString(arrValues));
//        if(
//                arrValues
//                )
        assertTrue(true);
    }


}
