//import com.agrovector.laboratory.photometer.Utils;
//
//import junit.framework.TestCase;
//
//import java.util.Arrays;
//
//public class DataTest extends TestCase {
//
//    public void testData () {
//        String value = (
////                "f9 01 f9 01 f9 01 f8 01 f8 01 f8 01 f8 01 f8 01 f8 01 f8 01 f8 01 f7 01 f7 01 f7 01 f7 01 f7 01 f7 01 f7 01 f7 01 f8 01 f9 01 f9 01 f9 01 f9 01 f9 01 f9 01 f9 01 f8 01 f8 01 f8 01 f8 01 f7 01 f7 01 f8 01 f7 01 f7 01 f7 01 f8 01 34 09 12 13 07 18 00 05 05 78"
//                " 08 02 0F 02 06 02 0D 02 01 02 01 02 03 02 06 02 " +
//                        " FD 01 01 02 FB 01 FA 01 FB 01 06 02 F5 01 FC 01 " +
//                        " F8 01 FC 01 E7 01 04 02 0B 02 02 02 04 02 FF 01 " +
//                        " FE 01 00 02 04 02 FB 01 00 02 FB 01 F9 01 FA 01" +
//                        " 03 02 F4 01 FB 01 F9 01 F9 01 E9 01 44 29 17 16 " +
//                        " 10 18 00 0F 0A 71 04 02 F3 01 07 02 04 02 00 02 " +
//                        " 04 02 04 02 05 02 00 02 02 02 01 02 00 02 FC 01 " +
//                        " FC 01 FB 01 FE 01 FF 01 FB 01 F9 01 08 02 F8 01"
////                        "02 02 FE 01 FA 01 F9 01 FC 01 F5 01 FA 01 F8 01" +
////                        " F7 01 F4 01 F6 01 F3 01 F4 01 F4 01 F5 01 EF 01" +
////                        " EF 01 F4 01 F5 01 0A 02 06 02 02 02 00 02 03 02" +
////                        " FB 01 00 02 FF 01 FC 01 FA 01 FC 01 FA 01 FA 01" +
////                        " F9 01 FB 01 F6 01 F5 01 FA 01 F9 01 18 47 11 10" +
////                        " 04 19 00 0F 0A 6E FF FF FF FF FF FF FF FF FF FF" +
////                        " FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF" +
////                        " FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF "
//        ).replaceAll("[ \n]", "");
////        System.out.println(value);
//        int[] validByteData = Utils.hexStringToByteArray(value);
//
//
//
//        int i; /*adr = 388 + slotNumb * 86*/
//        int[] arrBefore = new int[19], arrAfter = new int[19];
//        int[] btArr = validByteData;//new byte[86];
//
//        for (i = 0; i < 19; i++) {
//            arrBefore[i] = (btArr[i * 2 + 1] << 8) + btArr[i * 2];
//            arrAfter[i] = (btArr[i * 2 + 1 + 38] << 8) + btArr[i * 2 + 38];
//        }
//        int[] arrValues = new int[19];
//        //int[] arrValues = Utils.rowToData(validByteData);
//        for (i = 0; i < 19; i++) {
//            int value1;
//            if (arrAfter[i] > arrBefore[i]) {
//                value1 = arrAfter[i] - arrBefore[i];
//                arrValues[i] = value1;
//            } else arrValues[i] = 0;
//        }
//        System.out.println(value);
//        System.out.println(Arrays.toString(btArr));
//        System.out.println(Arrays.toString(arrAfter));
//        System.out.println(Arrays.toString(arrBefore));
//        System.out.println(Arrays.toString(arrValues));
////        if(
////                arrValues
////                )
//        assertTrue(true);
//    }
//
//
//}
