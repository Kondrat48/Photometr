package com.example.artem.photometr;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.text.Editable;

import java.io.File;

/**
 * Created by User on 2/28/2017.
 */

public class TabDocumentSettingsFragment extends Fragment implements View.OnClickListener {
    private EditText
            inputAgricultureName, inputFieldNumber, inputCrop, inputFieldArea, inputPredecessor, textNote,
            cellAnalysisPh, cellAnalysisGumus, cellAnalysisN, cellAnalysisP, cellAnalysisK, cellAnalysisS, cellAanalysisCa, cellAnalysisMg, cellAnalysisB, cellAnalysisCu, cellAnalysisZn, cellAnalysisMn, cellAnalysisFe, cellAnalysisMo, cellAnalysisJ,

            inputElementNitrogen, inputElementPhosphorusPentoxide, inputElementPotassiumOxide, inputElementSulfur, inputElementChalk, inputElementGypsum, inputElementManure,
            inputElementNitrogen2, inputElementPhosphorusPentoxide2, inputElementPotassiumOxide2, inputElementSulfur2,
            inputElementNitrogen3, inputElementPhosphorusPentoxide3, inputElementPotassiumOxide3, inputElementSulfur3,

            cellTopDressin1N, cellTop_dressin1P, cellTop_dressin1K, cellTop_dressin1S, cellTopDressin1Ca, cellTopDressin1Mg, cellTopDressin1B, cellTopDressin1Cu, cellTopDressin1Zn, cellTopDressin1Mn, cellTopDressin1Fe, cellTopDressin1Mo, cellTopDressin1J,
            cellTopDressin2N, cellTop_dressin2P, cellTop_dressin2K, cellTop_dressin2S, cellTopDressin2Ca, cellTopDressin2Mg, cellTopDressin2B, cellTopDressin2Cu, cellTopDressin2Zn, cellTopDressin2Mn, cellTopDressin2Fe, cellTopDressin2Mo, cellTopDressin2J,
            cellTopDressin3N, cellTop_dressin3P, cellTop_dressin3K, cellTop_dressin3S, cellTopDressin3Ca, cellTopDressin3Mg, cellTopDressin3B, cellTopDressin3Cu, cellTopDressin3Zn, cellTopDressin3Mn, cellTopDressin3Fe, cellTopDressin3Mo, cellTopDressin3J,
            cellTopDressin4N, cellTop_dressin4P, cellTop_dressin4K, cellTop_dressin4S, cellTopDressin4Ca, cellTopDressin4Mg, cellTopDressin4B, cellTopDressin4Cu, cellTopDressin4Zn, cellTopDressin4Mn, cellTopDressin4Fe, cellTopDressin4Mo, cellTopDressin4J,

            logoPath, dbPath;

    private EditText editText[]={
            inputAgricultureName, inputFieldNumber, inputCrop, inputFieldArea, inputPredecessor, textNote,
            cellAnalysisPh, cellAnalysisGumus, cellAnalysisN, cellAnalysisP, cellAnalysisK, cellAnalysisS, cellAanalysisCa, cellAnalysisMg, cellAnalysisB, cellAnalysisCu, cellAnalysisZn, cellAnalysisMn, cellAnalysisFe, cellAnalysisMo, cellAnalysisJ,

            inputElementNitrogen, inputElementPhosphorusPentoxide, inputElementPotassiumOxide, inputElementSulfur, inputElementChalk, inputElementGypsum, inputElementManure,
            inputElementNitrogen2, inputElementPhosphorusPentoxide2, inputElementPotassiumOxide2, inputElementSulfur2,
            inputElementNitrogen3, inputElementPhosphorusPentoxide3, inputElementPotassiumOxide3, inputElementSulfur3,

            cellTopDressin1N, cellTop_dressin1P, cellTop_dressin1K, cellTop_dressin1S, cellTopDressin1Ca, cellTopDressin1Mg, cellTopDressin1B, cellTopDressin1Cu, cellTopDressin1Zn, cellTopDressin1Mn, cellTopDressin1Fe, cellTopDressin1Mo, cellTopDressin1J,
            cellTopDressin2N, cellTop_dressin2P, cellTop_dressin2K, cellTop_dressin2S, cellTopDressin2Ca, cellTopDressin2Mg, cellTopDressin2B, cellTopDressin2Cu, cellTopDressin2Zn, cellTopDressin2Mn, cellTopDressin2Fe, cellTopDressin2Mo, cellTopDressin2J,
            cellTopDressin3N, cellTop_dressin3P, cellTop_dressin3K, cellTop_dressin3S, cellTopDressin3Ca, cellTopDressin3Mg, cellTopDressin3B, cellTopDressin3Cu, cellTopDressin3Zn, cellTopDressin3Mn, cellTopDressin3Fe, cellTopDressin3Mo, cellTopDressin3J,
            cellTopDressin4N, cellTop_dressin4P, cellTop_dressin4K, cellTop_dressin4S, cellTopDressin4Ca, cellTopDressin4Mg, cellTopDressin4B, cellTopDressin4Cu, cellTopDressin4Zn, cellTopDressin4Mn, cellTopDressin4Fe, cellTopDressin4Mo, cellTopDressin4J};

    private static final int editTextIds[]={
            R.id.input_farm_name, R.id.input_field_number, R.id.input_crop, R.id.input_field_area, R.id.input_predecessor, R.id.text_note,
            R.id.cell_analysis_ph, R.id.cell_analysis_gumus, R.id.cell_analysis_n, R.id.cell_analysis_p, R.id.cell_analysis_k, R.id.cell_analysis_s, R.id.cell_analysis_ca, R.id.cell_analysis_mg, R.id.cell_analysis_b, R.id.cell_analysis_cu, R.id.cell_analysis_zn, R.id.cell_analysis_mn, R.id.cell_analysis_fe, R.id.cell_analysis_mo, R.id.cell_analysis_j,

            R.id.input_element_nitrogen, R.id.input_element_phosphorus_pentoxide, R.id.input_element_potassium_oxide, R.id.input_element_sulfur, R.id.input_element_chalk, R.id.input_element_gypsum, R.id.input_element_manure,
            R.id.input_element_nitrogen2, R.id.input_element_phosphorus_pentoxide2, R.id.input_element_potassium_oxide2, R.id.input_element_sulfur2,
            R.id.input_element_nitrogen3, R.id.input_element_phosphorus_pentoxide3, R.id.input_element_potassium_oxide3, R.id.input_element_sulfur3,

            R.id.cell_top_dressin1_n, R.id.cell_top_dressin1_p, R.id.cell_top_dressin1_k, R.id.cell_top_dressin1_s, R.id.cell_top_dressin1_ca, R.id.cell_top_dressin1_mg, R.id.cell_top_dressin1_b, R.id.cell_top_dressin1_cu, R.id.cell_top_dressin1_zn, R.id.cell_top_dressin1_mn, R.id.cell_top_dressin1_fe, R.id.cell_top_dressin1_mo,R.id.cell_top_dressin1_j,
            R.id.cell_top_dressin2_n, R.id.cell_top_dressin2_p, R.id.cell_top_dressin2_k, R.id.cell_top_dressin2_s, R.id.cell_top_dressin2_ca, R.id.cell_top_dressin1_mg, R.id.cell_top_dressin2_b, R.id.cell_top_dressin2_cu, R.id.cell_top_dressin2_zn, R.id.cell_top_dressin2_mn, R.id.cell_top_dressin2_fe, R.id.cell_top_dressin2_mo,R.id.cell_top_dressin2_j,
            R.id.cell_top_dressin3_n, R.id.cell_top_dressin3_p, R.id.cell_top_dressin3_k, R.id.cell_top_dressin3_s, R.id.cell_top_dressin3_ca, R.id.cell_top_dressin1_mg, R.id.cell_top_dressin3_b, R.id.cell_top_dressin3_cu, R.id.cell_top_dressin3_zn, R.id.cell_top_dressin3_mn, R.id.cell_top_dressin3_fe, R.id.cell_top_dressin3_mo,R.id.cell_top_dressin3_j,
            R.id.cell_top_dressin4_n, R.id.cell_top_dressin4_p, R.id.cell_top_dressin4_k, R.id.cell_top_dressin4_s, R.id.cell_top_dressin4_ca, R.id.cell_top_dressin1_mg, R.id.cell_top_dressin4_b, R.id.cell_top_dressin4_cu, R.id.cell_top_dressin4_zn, R.id.cell_top_dressin4_mn, R.id.cell_top_dressin4_fe, R.id.cell_top_dressin4_mo,R.id.cell_top_dressin4_j};


    private Button btnCompanyInfoSettings, btnGraphSettings;

    public static TabDocumentSettingsFragment newInstance(int page, String title) {
        TabDocumentSettingsFragment tabDocumentSettingsFragment = new TabDocumentSettingsFragment();
        Bundle args = new Bundle();
        tabDocumentSettingsFragment.setArguments(args);
        return tabDocumentSettingsFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_document_settings,container,false);
        btnCompanyInfoSettings = (Button) view.findViewById(R.id.button_company_info_settings);
        btnCompanyInfoSettings.setOnClickListener(this);
        btnGraphSettings=(Button) view.findViewById(R.id.button_graph_settings);
        btnGraphSettings.setOnClickListener(this);

        for(int i = 0; i<=editText.length-1;i++){
            editText[i] = (EditText) view.findViewById(editTextIds[i]);
            editText[i].addTextChangedListener(editTextWatcher);}

        return view;
    }

    @Override
    public void onClick(View view){
        FragmentManager manager = getFragmentManager();
        switch (view.getId()){
            case R.id.button_company_info_settings:
                DialogCompanyInformationFragment dialog = new DialogCompanyInformationFragment();
                dialog.show(manager,"");
                break;
            case R.id.button_graph_settings:
                DialogGraphSettingsFragment dialog1 = new DialogGraphSettingsFragment();
                dialog1.show(manager,"");
                break;
        }
    }


    private final TextWatcher editTextWatcher = new TextWatcher() {
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        public void onTextChanged(CharSequence s, int start, int before, int count) {}

        public void afterTextChanged(Editable s) {

        }
    };

    public void update(File file){

    }

}