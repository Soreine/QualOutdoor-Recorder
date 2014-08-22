package com.qualoutdoor.recorder.home;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.qualoutdoor.recorder.telephony.ICellInfo;
import com.qualoutdoor.recorder.telephony.ViewCellInfo;

/**
 * This adapter can feed a ListView with a cell info list, creating the
 * appropriate ViewCellInfo.
 * 
 * @author Gaborit Nicolas
 */

public class CellInfoListAdapter extends BaseAdapter {

    /** The context that will contain the ViewCellInfo */
    private final Context context;

    /** The list of ICellInfo to display */
    private ArrayList<ICellInfo> cellInfos;

    /**
     * Create an adapter with the given cell info list.
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

    /**
     * Update the cell info list, replacing it with the given one.
     * 
     * @param cellInfos
     *            The new list of CellInfo
     */
    public void updateDataSet(List<ICellInfo> cellInfos) {
        /*
         * TODO rather than clearing the collection and filling it anew,
         * consider updating existing cells (compare using cell id) and
         * adding/removing the one that differs between new and old ICellInfo
         * collection.
         */

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