package com.agrovector.laboratory.photometer;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnRenderListener;
import com.itextpdf.text.BadElementException;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;


/**
 * Created by User on 2/28/2017.
 */

public class TabPdfFragment extends Fragment {

    private static int RESULT_LOAD_IMAGE = 22;
    final String COMPANY_INFO_PREFERENCES = "company_info_preferences";

    public Bitmap logo = null;


    private PDFView frame;

    private BaseFont roman;
    private Font bfRoman;
    private Font bfRomanBold;

    private File file;
    public String[] data = null;
    public String date = null;
    public String[] graphData = null;
    public Bitmap graph = null;
    public String companyInfoString = "";
    private InfoDialogFragment fragment;
    public Uri logoUri;

    public static void copyFile(File sourceFile, File destFile) throws IOException {
        if (!destFile.getParentFile().exists())
            destFile.getParentFile().mkdirs();

        if (!destFile.exists()) {
            destFile.createNewFile();
        }

        FileChannel source = null;
        FileChannel destination = null;

        try {
            source = new FileInputStream(sourceFile).getChannel();
            destination = new FileOutputStream(destFile).getChannel();
            destination.transferFrom(source, 0, source.size());
        } finally {
            if (source != null) {
                source.close();
            }
            if (destination != null) {
                destination.close();
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_print,container,false);
        frame = view.findViewById(R.id.container_print);
        file = new File(getActivity().getFilesDir()+"/temp.pdf");
        if(new File(getActivity().getFilesDir()+"/temp.png").exists()){
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            logo = BitmapFactory.decodeFile(getActivity().getFilesDir()+"/temp.png", options);
        }
        Button buttonSettings = view.findViewById(R.id.settingsButtonPdf);
        SharedPreferences spCompanyInfo = getActivity().getSharedPreferences(COMPANY_INFO_PREFERENCES, Context.MODE_PRIVATE);
        companyInfoString = spCompanyInfo.getString(COMPANY_INFO_PREFERENCES, "");
        buttonSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                @SuppressLint("CommitTransaction") FragmentTransaction ft = getFragmentManager().beginTransaction();
                Fragment prev = getFragmentManager().findFragmentByTag("dialog");
                if (prev != null) {
                    ft.remove(prev);
                }
                ft.addToBackStack(null);
                fragment = InfoDialogFragment.newInstance(TabPdfFragment.this, logoUri);
                fragment.show(ft,"dialog");
            }
        });

        try {
            roman = BaseFont.createFont("assets/roman.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        } catch (DocumentException | IOException e) {
            e.printStackTrace();
        }

        bfRoman = new Font(roman, 10);
        bfRomanBold = new Font(roman,10,Font.BOLD);

        return view;
    }




    public void update(String[] data, String date, String[] graphData, Bitmap graph){
        try {
            this.data = data;
            this.date = date;
            this.graphData = graphData;
            this.graph = graph;
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, new FileOutputStream(file));
            document.open();
            addMetaData(document,null);

            addPage(document, data,companyInfoString,date,graphData,graph,logo);
            document.close();
        } catch (DocumentException | FileNotFoundException e) {
            e.printStackTrace();
        }

        showPdf();
    }

    public void savePdf(File fileToSave){
        if(fileToSave!=null){
            try {
                copyFile(file,fileToSave);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void addPage(Document document, String[] data, String companyData, String date, String[] graphData, Bitmap graph, Bitmap logoBitmsp) {
        try{
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.pdf_default_image);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,stream);
            Image image = Image.getInstance(stream.toByteArray());
            image.scaleToFit(300,50);

            PdfPTable main = new PdfPTable(2);
            main.setWidthPercentage(100);
            PdfPCell cellMain = new PdfPCell();
            cellMain.setBorder(Rectangle.NO_BORDER);
            cellMain.addElement(image);
            main.addCell(cellMain);

            cellMain = new PdfPCell();
            cellMain.setBorder(Rectangle.NO_BORDER);
            if(logoBitmsp!=null){

                Bitmap imageWithBG = Bitmap.createBitmap(logoBitmsp.getWidth(), logoBitmsp.getHeight(),logoBitmsp.getConfig());
                imageWithBG.eraseColor(Color.WHITE);
                Canvas canvas = new Canvas(imageWithBG);
                canvas.drawBitmap(logoBitmsp, 0f, 0f, null);
                logoBitmsp.recycle();

                Image formatedImage = null;
                stream = new ByteArrayOutputStream();
                imageWithBG.compress(Bitmap.CompressFormat.JPEG,100,stream);
                try {
                    formatedImage= Image.getInstance(stream.toByteArray());
                } catch (BadElementException | IOException e) {
                    e.printStackTrace();
                }

                formatedImage.scaleToFit(125,50);
                formatedImage.setAlignment(Element.ALIGN_RIGHT);
                cellMain.addElement(formatedImage);
            }
            main.addCell(cellMain);

            cellMain = new PdfPCell();
            cellMain.setBorder(Rectangle.NO_BORDER);
            Paragraph mainCompanyDataParagraph = new Paragraph("Портативні лабораторії листової функціональної\nдіагностики та  аналізу грунту\nhttp://агровектор.укр\nтел.: +38(044) 331 21 50\ne-mail: agrooptimization@gmail.com\n",bfRoman);
            mainCompanyDataParagraph.setLeading(0, 1);
            cellMain.addElement(mainCompanyDataParagraph);
            main.addCell(cellMain);

            cellMain = new PdfPCell();
            cellMain.setBorder(Rectangle.NO_BORDER);
            if(companyData!=null){
                Paragraph companyDataParagraph = new Paragraph(companyData,bfRoman);
                companyDataParagraph.setLeading(0, 1);
                companyDataParagraph.setAlignment(Element.ALIGN_RIGHT);
                cellMain.addElement(companyDataParagraph);
            }
            main.addCell(cellMain);

            document.add(main);




            document.add(new Paragraph("\n",bfRoman));
            if(data == null){
                data = new String[21];
                for (int i = 0; i<data.length;i++)data[i]=" ";
            }
            PdfPTable info = new PdfPTable(new float[]{0.565f,1,0.49f,1});
            info.setWidthPercentage(100);
            for (int i = 0;i<4;i++){
                String str1 = "Название хозяйства:\nНомер поля:\nПлощадь поля:";
                String str2 = data[0]+'\n'+data[1]+'\n'+data[2];
                String str3 = "Культура:\nФаза розвития:\nПредшественник:";
                String str4 = data[3]+'\n'+data[5]+'\n'+data[4];
                Paragraph paragraph = null;
                if(i==0)paragraph = new Paragraph(str1,bfRomanBold);
                else if(i==1)paragraph = new Paragraph(str2,bfRoman);
                else if(i==2)paragraph = new Paragraph(str3,bfRomanBold);
                else if(i==3)paragraph = new Paragraph(str4,bfRoman);
                PdfPCell cell = new PdfPCell();
                cell.setBorder(Rectangle.NO_BORDER);
                cell.addElement(paragraph);
                info.addCell(cell);
            }
            document.add(info);

            Paragraph analise = new Paragraph("\nАнализ грунта",bfRomanBold);
            analise.setLeading(0,1);
            document.add(analise);
            Paragraph befourTable = new Paragraph(" ",new Font(Font.FontFamily.TIMES_ROMAN,4));
            document.add(befourTable);
            PdfPTable table = new PdfPTable(16);
            table.setWidthPercentage(100);
            String[] strings = {"PH","Гумус,\nмг/кг","N,\n  мг/кг","P,\nмг/кг","K,\nмг/кг","S,\nмг/кг","Ca,\nмг/кг","Mg,\nмг/кг","B,\nмг/кг","Cu,\nмг/кг","Zn,\nмг/кг","Mn,\nмг/кг","Fe,\nмг/кг","Mo,\nмг/кг","Co,\nмг/кг","J,\nмг/кг"};
            Font inCellTextFont = new Font(roman,9);
            for (int i = 0; i<32;i++){
                Paragraph paragraph;
                if(i<16)paragraph = new Paragraph(strings[i],inCellTextFont);
                else paragraph = new Paragraph(" ",inCellTextFont);
                paragraph.setLeading(0,1);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                PdfPCell cell = new PdfPCell();
                cell.setMinimumHeight(24);
                cell.addElement(paragraph);
                cell.addElement(new Paragraph(" ",new Font(roman,2)));
                cell.setHorizontalAlignment(Element.ALIGN_MIDDLE);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                table.addCell(cell);
            }
            document.add(table);
            PdfPTable fertilising = new PdfPTable(new float[]{2.73f,0.72f,1,0.52f,1,0.59f,1,0.22f,1});
            fertilising.setWidthPercentage(100);
            for (int i = 0; i<9;i++){
                Paragraph paragraph = null;
                if(i==0)paragraph = new Paragraph("Основное внесение удобрений\n\nПредпосевное внесение удобрений\nПрипосевное внесение удобрений",bfRomanBold);else
                if(i==1)paragraph = new Paragraph("Известь:\nN:\nN:\nN:",bfRoman);else
                if(i==2)paragraph = new Paragraph(data[10]+" кг/га\n"+data[6]+" кг/га\n"+data[13]+" кг/га\n"+data[17]+" кг/га\n",bfRoman);else
                if(i==3)paragraph = new Paragraph("Гипс:\nP2O5:\nP2O5:\nP2O5:",bfRoman);else
                if(i==4)paragraph = new Paragraph(data[11]+" кг/га\n"+data[7]+" кг/га\n"+data[14]+" кг/га\n"+data[18]+" кг/га\n",bfRoman);else
                if(i==5)paragraph = new Paragraph("Навоз:\nK2O:\nK2O:\nK2O:",bfRoman);else
                if(i==6)paragraph = new Paragraph(data[12]+" кг/га\n"+data[8]+" кг/га\n"+data[15]+" кг/га\n"+data[19]+" кг/га\n",bfRoman);else
                if(i==7)paragraph = new Paragraph("\nS:\nS:\nS:",bfRoman);else
                if(i==8)paragraph = new Paragraph("\n"+data[9]+" кг/га\n"+data[16]+" кг/га\n"+data[20]+" кг/га\n",bfRoman);
                if(i==1||i==3||i==5||i==7)paragraph.setAlignment(Element.ALIGN_RIGHT);
                PdfPCell cell = new PdfPCell();
                cell.setMinimumHeight(72);
                cell.addElement(paragraph);
                cell.setVerticalAlignment(Element.ALIGN_CENTER);
                cell.setBorder(Rectangle.NO_BORDER);
                fertilising.addCell(cell);
            }
            document.add(fertilising);
            document.add(new Paragraph("Подкормка",bfRomanBold));
            document.add(befourTable);
            String[] elements = {"Подкормка","N,\nкг/га","P,\nкг/га","K,\nкг/га","S,\nкг/га","Ca,\nкг/га","Mg,\nкг/га","B,\nг/га","Cu,\nг/га","Zn,\nг/га","Mn,\nг/га","Fe,\nг/га","Mo,\nг/га","Co,\nг/га","J,\nг/га"};
            PdfPTable feed = new PdfPTable(new float[]{2,1,1,1,1,1,1,1,1,1,1,1,1,1,1});
            feed.setWidthPercentage(100);
            for (int i = 0;i<75;i++){
                Paragraph paragraph;
                if(i<15)paragraph = new Paragraph(elements[i],inCellTextFont);
                else if(i==15)paragraph = new Paragraph("1",inCellTextFont);
                else if(i==30)paragraph = new Paragraph("2",inCellTextFont);
                else if(i==45)paragraph = new Paragraph("3",inCellTextFont);
                else if(i==60)paragraph = new Paragraph("4",inCellTextFont);
                else paragraph = new Paragraph(" ",inCellTextFont);
                paragraph.setLeading(0,1);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                PdfPCell cell = new PdfPCell();
                cell.setMinimumHeight(24);
                cell.addElement(paragraph);
                cell.addElement(new Paragraph(" ",new Font(roman,2)));
                cell.setHorizontalAlignment(Element.ALIGN_MIDDLE);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                feed.addCell(cell);
            }
            document.add(feed);
            if(data.length>21){
                Paragraph par = new Paragraph();
                par.add(new Chunk("Примечания: ",bfRomanBold));
                par.add(new Chunk(data[21]+'\n',bfRoman));
                document.add(par);
            }
            if(date!=null){
                Paragraph dateMeasurings = new Paragraph();
                dateMeasurings.add(new Chunk("Результат измерений от ", bfRomanBold));
                dateMeasurings.add(new Chunk(date, bfRoman));
                document.add(dateMeasurings);
            }

            if(graph!=null){
                ByteArrayOutputStream graphStream = new ByteArrayOutputStream();
                graph.compress(Bitmap.CompressFormat.JPEG,100,graphStream);
                Image graphImage = Image.getInstance(graphStream.toByteArray());
                graphImage.scaleToFit(523,152);
                document.add(graphImage);
            }
            if(graphData!=null){
                document.add(befourTable);
                PdfPTable graphDataTable = new PdfPTable(15);
                graphDataTable.setWidthPercentage(100);
                int k = 0;
                for (int i = 0;i<60;i++){
                    PdfPCell cell = new PdfPCell();
                    Paragraph paragraph = null;
                    if(i==0)paragraph = new Paragraph(" ",inCellTextFont);
                    else if (i>0&&i<15)paragraph = new Paragraph(elements[i],inCellTextFont);
                    else if (i==15)paragraph = new Paragraph("Изм.",inCellTextFont);
                    else if (i==30)paragraph= new Paragraph("%",inCellTextFont);
                    else if (i==45)paragraph = new Paragraph("ДВ",inCellTextFont);
                    else {
                        paragraph = new Paragraph(graphData[k]);
                    }
                    if((i>15&&i<30)||(i>30&&i<45)||(i>45&&i<60))k++;
                    paragraph.setLeading(0,1);
                    paragraph.setAlignment(Element.ALIGN_CENTER);
                    cell.setMinimumHeight(24);
                    cell.addElement(paragraph);
                    cell.addElement(new Paragraph(" ",new Font(roman,2)));
                    cell.setHorizontalAlignment(Element.ALIGN_MIDDLE);
                    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    graphDataTable.addCell(cell);
                }
                document.add(graphDataTable);
            }


        } catch (DocumentException | IOException e) {
            e.printStackTrace();
        }

    }

    private void addMetaData(Document document, String date) {
        document.addTitle("Анализ фотометра " + date);
    }


    public void showPdf(){
        frame.fromFile(file)
                .defaultPage(0)
                .enableSwipe(false)
                .enableDoubletap(false)
                .onRender(new OnRenderListener() {
                    @Override
                    public void onInitiallyRendered(int pages, float pageWidth, float pageHeight) {
                        frame.fitToWidth();
                    }
                })
                .load();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        fragment.onActivityResult(requestCode, resultCode, data);
    }

}