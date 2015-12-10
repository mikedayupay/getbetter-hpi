package com.dlsu.thesis.getbetter.adminpages;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.dlsu.thesis.getbetter.R;
import com.dlsu.thesis.getbetter.database.DataAdapter;
import com.dlsu.thesis.getbetter.objects.Impressions;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class ViewImpressionsFragment extends Fragment {

    private DataAdapter getBetterDb;
    private ArrayList<Impressions> impressionList;


    public ViewImpressionsFragment() {
        // Required empty public constructor
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        impressionList = new ArrayList<>();

        initializeDatabase();
        getImpressions();

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_view_impressions, container, false);

        TableLayout table = (TableLayout)rootView.findViewById(R.id.table_impression_content);

        for(int i = 0; i < impressionList.size(); i++) {

            TableRow row = new TableRow(rootView.getContext());
            TextView idText = new TextView(rootView.getContext());
            TextView impressionName = new TextView(rootView.getContext());
            idText.setText(impressionList.get(i).getImpressionId() + "");
            impressionName.setText(impressionList.get(i).getImpression());
            row.addView(idText);
            row.addView(impressionName);
            table.addView(row);
        }



        return rootView;
    }

    private void initializeDatabase () {

        getBetterDb = new DataAdapter(getActivity());

        try {
            getBetterDb.createDatabase();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void getImpressions () {

        try {
            getBetterDb.openDatabaseForRead();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        impressionList = getBetterDb.getImpressions();
        getBetterDb.closeDatabase();

    }

}
