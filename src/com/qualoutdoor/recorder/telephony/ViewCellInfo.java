package com.qualoutdoor.recorder.telephony;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.GridLayout;
import android.widget.TextView;

import com.qualoutdoor.recorder.R;

/**
 * This view allows to display dynamically, as a table, the infos of a ICellInfo
 * 
 * @author Gaborit Nicolas
 */

public class ViewCellInfo extends GridLayout {

    /** The number of column of this GridLayout */
    public static final int COLUMN_COUNT = 4;

    /** The number of rows in this GridLayout */
    public static final int ROW_COUNT = 3;

    /** The string ressource for the radio types */
    private final CharSequence[] radioNames = getResources().getStringArray(
            R.array.radio_type_name);

    /** The displayed ICellInfo */
    private ICellInfo cellInfo;

    /** The CID and Type view */
    private TextView viewCidType;

    /** The RSSI view */
    private TextView viewRSSI;

    /** The MCC value view */
    private TextView viewMCCvalue;

    /** The MNC value view */
    private TextView viewMNCvalue;

    /** The LAC or TAC view depending on the type of the cell */
    private TextView viewLACTAC;

    /** The LAC or TAC value view */
    private TextView viewLACTACvalue;

    /** The PSC or PCI view depending on the type of the cell */
    private TextView viewPSCPCI;

    /** The PSC or PCI value view */
    private TextView viewPSCPCIvalue;

    public ViewCellInfo(Context context) {
        // Only specify the context
        this(context, null, 0);
    }

    public ViewCellInfo(Context context, AttributeSet attrs) {
        // Only specify context and attributes set
        this(context, attrs, 0);
    }

    public ViewCellInfo(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        // Inflate the corresponding view hierarchy
        View.inflate(context, R.layout.view_cell_info, this);

        // Initialize with an empty ICellInfo
        cellInfo = new CustomCellInfo();

        // Initialize the views
        {
            viewCidType = (TextView) this
                    .findViewById(R.id.view_cell_info_cid_type);
            viewRSSI = (TextView) this.findViewById(R.id.view_cell_info_rssi);
            viewMCCvalue = (TextView) this
                    .findViewById(R.id.view_cell_info_mcc_value);
            viewMNCvalue = (TextView) this
                    .findViewById(R.id.view_cell_info_mnc_value);
            viewLACTAC = (TextView) this
                    .findViewById(R.id.view_cell_info_lac_tac);
            viewLACTACvalue = (TextView) this
                    .findViewById(R.id.view_cell_info_lac_tac_value);
            viewPSCPCI = (TextView) this
                    .findViewById(R.id.view_cell_info_psc_pci);
            viewPSCPCIvalue = (TextView) this
                    .findViewById(R.id.view_cell_info_psc_pci_value);
        }

        updateCellInfo(cellInfo);
    }

    /** Set the CellInfo displayed */
    public void updateCellInfo(ICellInfo newCellInfo) {
        // Update this view
        // Get the type of the new cell
        int type = newCellInfo.getCellType();
        // If this is the registered cell, highlight with background drawable
        if (newCellInfo.isRegistered()) {
            // Set the registered background
            this.setBackground(new ColorDrawable(getResources().getColor(
                    R.color.blue_transparent)));
        } else {
            // Set a transparent background
            this.setBackground(new ColorDrawable(Color.TRANSPARENT));
        }

        // Update the CID and type view with the CID and the radio type name
        viewCidType.setText(radioNames[type] + " ("
                + stringify(newCellInfo.getCid()) + ")");

        // Update the MCC value
        viewMCCvalue.setText(stringify(newCellInfo.getMcc()));
        viewMNCvalue.setText(stringify(newCellInfo.getMnc()));
        viewRSSI.setText(stringify(newCellInfo.getSignalStrength().getDbm(),
                getResources().getString(R.string.unit_dbm)));

        // If the type has change, update the views
        if (type != cellInfo.getCellType()) {
            // Update specific views according to the type of cell info
            // displayed
            switch (type) {
            case ICellInfo.CELL_LTE:
                // Set TAC
                viewLACTAC.setText(getResources().getString(
                        R.string.cell_info_tac));
                // Set PCI
                viewPSCPCI.setText(getResources().getString(
                        R.string.cell_info_pci));
                break;
            case ICellInfo.CELL_WCDMA:
                // Set LAC
                viewLACTAC.setText(getResources().getString(
                        R.string.cell_info_lac));
                // Set PSC
                viewPSCPCI.setText(getResources().getString(
                        R.string.cell_info_psc));
                break;
            default:
                // Else clear the specific views
                viewLACTAC.setText("");
                viewPSCPCI.setText("");
                viewPSCPCIvalue.setText("");
                viewLACTACvalue.setText("");
            }
        }
        // Update the specific view values
        switch (type) {
        case ICellInfo.CELL_LTE:
            // Set TAC value
            viewLACTACvalue.setText(stringify(newCellInfo.getTac()));
            // Set PCI value
            viewPSCPCIvalue.setText(stringify(newCellInfo.getPci()));
            break;
        case ICellInfo.CELL_WCDMA:
            // Set LAC value
            viewLACTACvalue.setText(stringify(newCellInfo.getLac()));
            // Set PSC value
            viewPSCPCIvalue.setText(stringify(newCellInfo.getPsc()));
            break;
        default:
            // No specific values need to be updated
            break;
        }

        // Update the cellInfo variable
        this.cellInfo = newCellInfo;
    }

    /**
     * Return a string representing the given value, or the cell_info_empty
     * string if unknown (Integer.MAX_VALUE)
     */
    private String stringify(int value) {
        if (value == Integer.MAX_VALUE) {
            return getResources().getString(R.string.cell_info_empty);
        } else {
            return value + "";
        }
    }

    /**
     * Return a string representing the given value with the given unit string,
     * or the cell_info_empty string if unknown (Integer.MAX_VALUE)
     */
    private String stringify(int value, String unit) {
        if (value == Integer.MAX_VALUE) {
            return getResources().getString(R.string.cell_info_empty);
        } else {
            return value + " (" + unit + ")";
        }
    }

}