package com.qualoutdoor.recorder.telephony;

import android.os.Bundle;
import android.telephony.CellInfo;
import android.telephony.CellInfoCdma;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;

public class CustomCellInfo implements ICellInfo {

    /**
     * This bundle hold all the cell infos. We are using bundles because they
     * let us provide partial informations. Plus they are easily passed between
     * activities.
     */
    private Bundle bundle;

    /************ The bundle keys ****************/
    /** Stores the cell type code. Holds an int. */
    public static final String CELL_TYPE = "cell_type";
    /** Stores the timestamp value. Holds a long integer. */
    public static final String TIMESTAMP = "timestamp";
    /** Stores if the cell is registered. Holds a boolean. */
    public static final String IS_REGISTERED = "is_registered";
    /**
     * Stores the signal strength. Holds a bundle containing the signal strength
     * values.
     */
    public static final String SIGNAL_STRENGTH = "signal_strength";
    /** Stores the Cell ID. Holds an int. */
    public static final String CID = "cid";
    /** Stores the Location Area Code. Holds an int. */
    public static final String LAC = "lac";
    /** Stores the Mobile Country Code. Holds an int. */
    public static final String MCC = "mcc";
    /** Stores the Mobile Network Code. Holds an int. */
    public static final String MNC = "mnc";
    /** Stores the Primary Scrambling Code. Holds an int. */
    public static final String PSC = "psc";

    /** Create an empty CellInfo */
    public CustomCellInfo() {
	this.bundle = new Bundle();
    }

    /** Create a new CustomCellInfo from a bundle containing the
	necessary informations.
	@param infos The bundle that contains all the known
	informations about the cell. */
    public CustomCellInfo(Bundle infos) {
	this.bundle = new Bundle();
	bundle.putAll(infos);
    }

    /** Create a new CustomCellInfo from the Android CellInfo
     * implementation. This only initialize the generic fields from
     * the CellInfo class.
     * @param cell The CellInfo to initialize from. */
    public CustomCellInfo(CellInfo cell) {
	bundle = new Bundle();
	bundle.putLong(TIMESTAMP, cell.getTimeStamp());
	bundle.putBoolean(IS_REGISTERED, cell.isRegistered());
    }

    /** Create a new CustomCellInfo from the Android CellInfoGsm
     * implementation.
     * @param cell The CellInfoGsm to initialize from. */
    public CustomCellInfo(CellInfoGsm cell) {
	// Initialize the the generic fields from the CellInfo class
	this((CellInfo) cell);
	// Initialize the GSM specific values
	
    }
    /** Create a new CustomCellInfo from the Android CellInfoLte
     * implementation.
     * @param cell The CellInfoLte to initialize from. */
    public CustomCellInfo(CellInfoLte cell) {
	// Initialize the the generic fields from the CellInfo class
	this((CellInfo) cell);
	// Initialize the LTE specific values
	
    }
    /** Create a new CustomCellInfo from the Android CellInfoCdma
     * implementation.
     * @param cell The CellInfoCdma to initialize from. */
    public CustomCellInfo(CellInfoCdma cell) {
	// Initialize the the generic fields from the CellInfo class
	this((CellInfo) cell);
	// Initialize the CDMA specific values
	
    }
    /** Create a new CustomCellInfo from the Android CellInfoWcdma
     * implementation.
     * @param cell The CellInfoWcdma to initialize from. */
    public CustomCellInfo(CellInfoWcdma cell) {
	// Initialize the the generic fields from the CellInfo class
	this((CellInfo) cell);
	// Initialize the WCDMA specific values
	
    }

    /** Create a new CustomCellInfo from an Android CellInfo, trying
     * to detect the actual type of the provided CellInfo
     * (CellInfoLte, CellInfoGsm, CellInfoWcdma or CellInfoCdma).
     * @param The CellInfo to inspect thoroughly
     * @return A CustomCellInfo containing all the information that
     * could be grabbed from the input CellInfo.
     */
    public static CustomCellInfo buildFromCellInfo(CellInfo cell) {
	// Initialize the result
	CustomCellInfo result = null;
	// Continue by inspecting which type of CellInfo we actually
	// have and cast accordingly.
	if(cell instanceof CellInfoCdma) {
	    // Call the CDMA constructor
	    result = new CustomCellInfo((CellInfoCdma) cell);
	} else if(cell instanceof CellInfoGsm) {
	    // Call the GSM constructor
	    result = new CustomCellInfo((CellInfoGsm) cell);
	} else if(cell instanceof CellInfoLte) {
	    // Call the LTE constructor
	    result = new CustomCellInfo((CellInfoLte) cell);
	} else if(cell instanceof CellInfoWcdma) {
	    // Call the WCDMA constructor
	    result = new CustomCellInfo((CellInfoWcdma) cell);
	} else {
	    // We only have a generic CellInfo...
	    result = new CustomCellInfo(cell);
	}
	return result;
    }

    /** Return a bundle containing all the cell infos */
    public Bundle getCellInfo() {
	return this.bundle;
    }

    @Override
    public int getCellType() {
	return 0;
    }

    @Override
    public long getTimeStamp() {
	// TODO Auto-generated method stub
	return 0;
    }

    @Override
    public boolean isRegistered() {
	// TODO Auto-generated method stub
	return false;
    }

    @Override
    public ISignalStrength getSignalStrength() {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public int getCid() {
	// TODO Auto-generated method stub
	return 0;
    }

    @Override
    public int getLac() {
	// TODO Auto-generated method stub
	return 0;
    }

    @Override
    public int getMcc() {
	// TODO Auto-generated method stub
	return 0;
    }

    @Override
    public int getMnc() {
	// TODO Auto-generated method stub
	return 0;
    }

    @Override
    public int getPsc() {
	// TODO Auto-generated method stub
	return 0;
    }

}
