package com.example.artem.photometr;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by artem on 10.07.2017.
 */

public class PdfMetodes {

    public Boolean write(String fname, String fcontent) {
        try {
            String fpath = "/storage/emulated/0/" + fname + ".pdf";
            File file = new File(fpath);

            if(!file.exists()){
                file.createNewFile();
            }
            Font bfBold12 = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD, new BaseColor(0, 0, 0));
            Font bf12 = new Font(Font.FontFamily.TIMES_ROMAN, 12);

            Document document = new Document();

            PdfWriter.getInstance(document, new FileOutputStream(file.getAbsoluteFile()));
            document.open();


            document.close();

            return true;

        } catch (IOException e){
            e.printStackTrace();
            return false;
        }catch (DocumentException e) {
            e.printStackTrace();
            return false;
        }
    }
}
