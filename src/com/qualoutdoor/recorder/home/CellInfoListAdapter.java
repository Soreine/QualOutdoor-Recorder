package com.qualoutdoor.recorder.home;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.qualoutdoor.recorder.telephony.ICellInfo;
import com.qualoutdoor.recorder.telephony.ViewCellInfo;

/** This adapter can feed a ListView with a cell info list. */

public class CellInfoListAdapter extends BaseAdapter {

    private final Context context;
    private ArrayList<ICellInfo> cellInfos;

    /**
     * Create an adapter with the give cell info list.
     * 
     * @param context
     *            The context used to create the views
     * @param cellInfos
     *            The data source
     */
    public CellInfoListAdapter(Context context, List<ICellInfo> cellInfos) {
        // Keep a reference to the context
        this.context = context;
        // Create an array list with the given collection
        this.cellInfos = new ArrayList<ICellInfo>(cellInfos);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Create a ViewCellInfo
        ViewCellInfo cellInfoView = new ViewCellInfo(context);
        // Set it with the required cell info
        cellInfoView.updateCellInfo(cellInfos.get(position));

        return cellInfoView;
    }

    /** Update the cell info list */
    public void updateDataSet(List<ICellInfo> cellInfos) {
        // Clear the previous array
        this.cellInfos.clear();
        // Add all the cell infos
        this.cellInfos.addAll(cellInfos);
        // Notify data set changed
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        // Return the size of the collection
        return cellInfos.size();
    }

    @Override
    public Object getItem(int position) {
        // Return the cell info at the given position
        return cellInfos.get(position);
    }

    @Override
    public long getItemId(int position) {
        // The items are identified by their position
        return position;
    }
}