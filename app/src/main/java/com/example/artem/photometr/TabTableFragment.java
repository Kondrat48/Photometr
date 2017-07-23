package com.example.artem.photometr;

import android.icu.text.DecimalFormat;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by artem on 06.07.2017.
 */

public class TabTableFragment  extends Fragment{

    String[] array;

    private static final int idMeasuring[]={R.id.cell_changes_k1,R.id.cell_changes_n,R.id.cell_changes_p,R.id.cell_changes_ks,R.id.cell_changes_kcl,R.id.cell_changes_k2,R.id.cell_changes_ca,R.id.cell_changes_mg,R.id.cell_changes_b,R.id.cell_changes_cu,R.id.cell_changes_k3,R.id.cell_changes_zn,R.id.cell_changes_mn,R.id.cell_changes_fe,R.id.cell_changes_k4,R.id.cell_changes_mo,R.id.cell_changes_co,R.id.cell_changes_j,R.id.cell_changes_k5};
    private static final int idHundPerc[]={R.id.cell_100_procent_k1,R.id.cell_100_procent_n,R.id.cell_100_procent_p,R.id.cell_100_procent_ks,R.id.cell_100_procent_kcl,R.id.cell_100_procent_k2,R.id.cell_100_procent_ca,R.id.cell_100_procent_mg,R.id.cell_100_procent_b,R.id.cell_100_procent_cu,R.id.cell_100_procent_k3,R.id.cell_100_procent_zn,R.id.cell_100_procent_mn,R.id.cell_100_procent_fe,R.id.cell_100_procent_k4,R.id.cell_100_procent_mo,R.id.cell_100_procent_co,R.id.cell_100_procent_j,R.id.cell_100_procent_k5};
    private static final int idPercent[]={R.id.cell_procent_k1,R.id.cell_procent_n,R.id.cell_procent_p,R.id.cell_procent_ks,R.id.cell_procent_kcl,R.id.cell_procent_k2,R.id.cell_procent_ca,R.id.cell_procent_mg,R.id.cell_procent_b,R.id.cell_procent_cu,R.id.cell_procent_k3,R.id.cell_procent_zn,R.id.cell_procent_mn,R.id.cell_procent_fe,R.id.cell_procent_k4,R.id.cell_procent_mo,R.id.cell_procent_co,R.id.cell_procent_j,R.id.cell_procent_k5};
    private static final int idKgOnHa[]={R.id.cell_kg_on_ha_k1,R.id.cell_kg_on_ha_n,R.id.cell_kg_on_ha_p,R.id.cell_kg_on_ha_ks,R.id.cell_kg_on_ha_kcl,R.id.cell_kg_on_ha_k2,R.id.cell_kg_on_ha_ca,R.id.cell_kg_on_ha_mg,R.id.cell_kg_on_ha_b,R.id.cell_kg_on_ha_cu,R.id.cell_kg_on_ha_k3,R.id.cell_kg_on_ha_zn,R.id.cell_kg_on_ha_mn,R.id.cell_kg_on_ha_fe,R.id.cell_kg_on_ha_k4,R.id.cell_kg_on_ha_mo,R.id.cell_kg_on_ha_co,R.id.cell_kg_on_ha_j,R.id.cell_kg_on_ha_k5};
    private static final int idDb[]={R.id.cell_dv_k1,R.id.cell_dv_n,R.id.cell_dv_p,R.id.cell_dv_ks,R.id.cell_dv_kcl,R.id.cell_dv_k2,R.id.cell_dv_ca,R.id.cell_dv_mg,R.id.cell_dv_b,R.id.cell_dv_cu,R.id.cell_dv_k3,R.id.cell_dv_zn,R.id.cell_dv_mn,R.id.cell_dv_fe,R.id.cell_dv_k4,R.id.cell_dv_mo,R.id.cell_dv_co,R.id.cell_dv_j,R.id.cell_dv_k5};

    private static TextView
            k1m, nm, pm, ksm, kclm, k2m, cam, mgm, bm, cum, k3m, znm, mnm, fem, k4m, mom, com, jm, k5m,
            k1100pr, n100pr, p100pr, ks100pr, kcl100pr, k2100pr, ca100pr, mg100pr, b100pr, cu100pr, k3100pr, zn100pr, mn100pr, fe100pr, k4100pr, mo100pr, co100pr, j100pr, k5100pr,
            k1pr, npr, ppr, kspr, kclpr, k2pr, capr, mgpr, bpr, cupr, k3pr, znpr, mnpr, fepr, k4pr, mopr, copr, jpr, k5pr,
            k1kgha, nkgha, pkgha, kskgha, kclkgha, k2kgha, cakgha, mgkgha, bkgha, cukgha, k3kgha, znkgha, mnkgha, fekgha, k4kgha, mokgha, cokgha, jkgha, k5kgha,
            k1db, ndb, pdb, ksdb, kcldb, k2db, cadb, mgdb, bdb, cudb, k3db, zndb, mndb, fedb, k4db, modb, codb, jdb, k5db;

    private static final TextView measuring[]={k1m, nm, pm, ksm, kclm, k2m, cam, mgm, bm, cum, k3m, znm, mnm, fem, k4m, mom, com, jm, k5m};
    private static final TextView hundPerc[]={k1100pr, n100pr, p100pr, ks100pr, kcl100pr, k2100pr, ca100pr, mg100pr, b100pr, cu100pr, k3100pr, zn100pr, mn100pr, fe100pr, k4100pr, mo100pr, co100pr, j100pr, k5100pr};
    private static final TextView percent[]={k1pr, npr, ppr, kspr, kclpr, k2pr, capr, mgpr, bpr, cupr, k3pr, znpr, mnpr, fepr, k4pr, mopr, copr, jpr, k5pr};
    private static final TextView kgOnHa[]={k1kgha, nkgha, pkgha, kskgha, kclkgha, k2kgha, cakgha, mgkgha, bkgha, cukgha, k3kgha, znkgha, mnkgha, fekgha, k4kgha, mokgha, cokgha, jkgha, k5kgha};
    private static final TextView db[]={k1db, ndb, pdb, ksdb, kcldb, k2db, cadb, mgdb, bdb, cudb, k3db, zndb, mndb, fedb, k4db, modb, codb, jdb, k5db};

    private static final double mMeasuring[]=new double[19];
    private static final double mHundPerc[]=new double[19];
    private static final double mPercent[]=new double[19];
    private static final double mKgOnHa[]=new double[19];
    private static final double mDb[]=new double[19];
    private static final double a[]=new double[19];
    private static final double b[]=new double[19];
    private static final double kgOnHa100perc[]={0,50,20,50,10,0,40,5,0.01,0.2,0,0.5,0.5,0.5,0,0.5,0.03,0.02,0};
    private static final double db100perc[]={0,23,5.4,22.5,5.2,0,8.8,0.5,11,52,0,115,160,100,0,270,13.6,15.3,0};



    public static TabTableFragment newInstance(int page, String title) {
        TabTableFragment tabTableFragment = new TabTableFragment();
        Bundle args = new Bundle();
        tabTableFragment.setArguments(args);
        return tabTableFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_table,container,false);


        for(int i = 0; i<= measuring.length-1; i++) measuring[i] = (TextView) view.findViewById(idMeasuring[i]);
        for(int i = 0; i<= hundPerc.length-1; i++) hundPerc[i] = (TextView) view.findViewById(idHundPerc[i]);
        for(int i = 0; i<= percent.length-1; i++) percent[i] = (TextView) view.findViewById(idPercent[i]);
        for(int i = 0; i<= kgOnHa.length-1; i++) kgOnHa[i] = (TextView) view.findViewById(idKgOnHa[i]);
        for(int i = 0; i<= db.length-1; i++) db[i] = (TextView) view.findViewById(idDb[i]);




        return view;
    }

    public void update(int[] values) {
        for(int i = 0; i<=values.length-1;i++) measuring[i].setText(Integer.toString(values[i]));
        count(values);
    }

    private void count(int[] values){
        String temp = null;
        for(int i = 0; i<=values.length-1;i++){
            if(i<=5)a[i]=values[0];else if(i<=10)a[i]=2*values[5]-values[10];else if(i<=14)a[i]=3.5*values[10]-2.5*values[14];else a[i]=4.5*values[14]-3.5*values[18];
            if(i<=5)b[i]=(values[5]-values[0])/5.0;else if(i<=10)b[i]=(values[10]-values[5])/5.0;else if(i<=14)b[i]=(values[14]-values[10])/4.0;else b[i]=(values[18]-values[14])/4.0;
            mHundPerc[i]=i*b[i]+a[i];
            if(values[i]>mHundPerc[i])if((values[i]-mHundPerc[i])*100/(double)mHundPerc[i]>100)mPercent[i]=100;else mPercent[i]=(values[i]-mHundPerc[i])*100/(double)mHundPerc[i];else mPercent[i]=0;
            mKgOnHa[i]=mPercent[i]*kgOnHa100perc[i]/100.0;
            mDb[i]=mPercent[i]*db100perc[i]/100.0;
            db[i].setText(String.valueOf(mDb[i]));
            kgOnHa[i].setText(String.valueOf(mKgOnHa[i]));
            percent[i].setText(String.valueOf(mPercent[i]));
            hundPerc[i].setText(String.valueOf(mHundPerc[i]));
        }
    }

}
