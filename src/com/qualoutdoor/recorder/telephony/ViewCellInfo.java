package com.qualoutdoor.recorder.telephony;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.GridLayout;
import android.widget.TextView;

import com.qualoutdoor.recorder.R;

/**
 * This view allows to display dynamically, as a table, the infos of a ICellInfo
 */
/*
 * Here is the way we display a cellInfo >|CID (Type) | RSSI|
 * ----------------------------------------- collapsible | MCC | MNC | LAC/TAC |
 * PSC/PCI | | 208 | 15 | 65536 | 512 |
 * -----------------------------------------
 */

public class ViewCellInfo extends GridLayout {

    /** The number of column of this GridLayout */
    public static final int COLUMN_COUNT = 4;

    /** The number of rows in this GridLayout */
    public static final int ROW_COUNT = 3;

    /** The string to display in empty cells */
    private static final CharSequence EMPTY = "?";

    /** The ressource identifier for text view style */
    private final static int style = R.style.ListItem;

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

    // TODO handle MAX_INTEGER unknown values
    /** Set the CellInfo displayed */
    public void updateCellInfo(ICellInfo newCellInfo) {
        // Update this view
        // Get the type of the new cell
        int type = newCellInfo.getCellType();

        // Update the CID and type view with the CID and the radio type name
        viewCidType.setText(radioNames[type] + " (" + newCellInfo.getCid()
                + ")");

        // Update the MCC value
        viewMCCvalue.setText(newCellInfo.getMcc() + "");
        viewMNCvalue.setText(newCellInfo.getMnc() + "");
        viewRSSI.setText(newCellInfo.getSignalStrength().getDbm() + "("
                + getResources().getString(R.string.unit_dbm) + ")");

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
            viewLACTACvalue.setText(newCellInfo.getTac() + "");
            // Set PCI value
            viewPSCPCIvalue.setText(newCellInfo.getPci() + "");
            break;
        case ICellInfo.CELL_WCDMA:
            // Set LAC value
            viewLACTACvalue.setText(newCellInfo.getLac() + "");
            // Set PSC value
            viewPSCPCIvalue.setText(newCellInfo.getPsc() + "");
            break;
        default:
            // No specific values need to be updated
            break;
        }

        // Update the cellInfo variable
        this.cellInfo = newCellInfo;
    }

    /**
     * Return a centered text view with the given style and the given text
     * ressource id.
     */
    private TextView createCenteredTextView(int textRes, int style) {
        // Create a text view with the given style
        TextView result = new TextView(getContext(), null, style);
        // Set the text
        result.setText(getResources().getString(textRes));
        // Center the text
        result.setGravity(Gravity.CENTER);
        return result;
    }

    /**
     * Return a centered text view with the given style and the given text
     */
    private TextView createCenteredTextView(CharSequence text, int style) {
        // Create a text view with the given style
        TextView result = new TextView(getContext(), null, style);
        // Set the text
        result.setText(text);
        // Center the text
        result.setGravity(Gravity.CENTER);
        return result;
    }

    /** Add a view to this grid in the given cell position */
    private void addPositionedView(View view, int row, int column) {
        // Create the good spec
        GridLayout.LayoutParams params = new GridLayout.LayoutParams(
                GridLayout.spec(row), GridLayout.spec(column));
        // Add the view
        this.addView(view, params);
    }

}

//
// // Set the column count
// this.setColumnCount(COLUMN_COUNT);
// // Set the row count
// this.setRowCount(ROW_COUNT);
//
// // Initialize the views
// GridLayout.LayoutParams params; // Will contain the GridLayout row and
// // column specification for a cell group
// TextView textView; // Temporary variable for textviews
// {
// // Create the CID and Type view
// viewCidType = new TextView(getContext(), null, style);
// // Set unknown text
// viewCidType.setText(EMPTY);
// // Align left
// viewCidType.setGravity(Gravity.LEFT);
// // Position this view to 0:0 and make it span 3 columns
// params = new GridLayout.LayoutParams(GridLayout.spec(0),
// GridLayout.spec(0, 3));
// // Add it to the GridLayout
// this.addView(viewCidType, params);
//
// // Create the RSSI view
// viewRSSI = new TextView(getContext(), null, style);
// // Set unknown text
// viewRSSI.setText(EMPTY);
// // Align left
// viewRSSI.setGravity(Gravity.RIGHT);
// // Position this view to 0:3
// params = new GridLayout.LayoutParams(GridLayout.spec(0),
// GridLayout.spec(3));
// // Add it to the GridLayout
// this.addView(viewRSSI, params);
//
// // Create the MCC view
// textView = createCenteredTextView(R.string.cell_info_mcc, style);
// // Add the view in 1:0
// this.addPositionedView(textView, 1, 0);
//
// // Create the MNC view
// textView = createCenteredTextView(R.string.cell_info_mnc, style);
// // Add the view in 1:1
// this.addPositionedView(textView, 1, 1);
//
// // Create the LAC/TAC view
// viewLACTAC = createCenteredTextView("", style);
// // Add the view in 1:2
// this.addPositionedView(viewLACTAC, 1, 2);
//
// // Create the PCI/PSC view
// viewPSCPCI = createCenteredTextView("", style);
// // Add the view in 1:2
// this.addPositionedView(viewPSCPCI, 1, 3);
//
// // Create the MCC value view
// viewMCCvalue = createCenteredTextView(EMPTY, style);
// // Add the view in 2:0
// this.addPositionedView(viewMCCvalue, 2, 0);
//
// // Create the MNC value view
// viewMNCvalue = createCenteredTextView(EMPTY, style);
// // Add the view in 2:1
// this.addPositionedView(viewMNCvalue, 2, 1);
//
// // Create the LAC/TAC value view
// viewLACTACvalue = createCenteredTextView("", style);
// // Add the view in 2:2
// this.addPositionedView(viewLACTACvalue, 2, 2);
//
// // Create the PSC/PCI value view
// viewPSCPCIvalue = createCenteredTextView("", style);
// // Add the view in 2:3
// this.addPositionedView(viewPSCPCIvalue, 2, 3);
// }