package com.qualoutdoor.recorder.telephony;

import android.os.Bundle;
import android.telephony.CellIdentityCdma;
import android.telephony.CellIdentityGsm;
import android.telephony.CellIdentityLte;
import android.telephony.CellIdentityWcdma;
import android.telephony.CellInfo;
import android.telephony.CellInfoCdma;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellInfoWcdma;
import android.telephony.CellSignalStrength;

public class CustomCellInfo implements ICellInfo {

    /**
     * This bundle hold all the cell infos. We are using bundles because they
     * let us provide partial informations. Plus they are easily passed between
     * activities.
     */
    private Bundle              infoBundle;

    /************ The bundle keys ****************/
    /** Stores the cell type code. Holds an int. */
    public static final String  CELL_TYPE       = "cell_type";
    /** Stores the timestamp value. Holds a long integer. */
    public static final String  TIMESTAMP       = "timestamp";
    /** Stores if the cell is registered. Holds a boolean. */
    public static final String  IS_REGISTERED   = "is_registered";
    /**
     * Stores the signal strength. Holds a bundle containing the signal strength
     * values.
     */
    public static final String  SIGNAL_STRENGTH = "signal_strength";
    /** Stores the Cell ID. Holds an int. */
    public static final String  CID             = "cid";
    /** Stores the Location Area Code. Holds an int. */
    public static final String  LAC             = "lac";
    /** Stores the Mobile Country Code. Holds an int. */
    public static final String  MCC             = "mcc";
    /** Stores the Mobile Network Code. Holds an int. */
    public static final String  MNC             = "mnc";
    /** Stores the Primary Scrambling Code. Holds an int. */
    public static final String  PSC             = "psc";

    /** This is a bundle we use to initialize the default values for a CustomCellInfo */
    private static final Bundle defaultBundle   = new Bundle();
    {
        defaultBundle.putInt(CELL_TYPE, CELL_UNKNOWN);
        defaultBundle.putLong(TIMESTAMP, Long.MAX_VALUE);
        defaultBundle.putBoolean(IS_REGISTERED, false);
        defaultBundle.putInt(CID, Integer.MAX_VALUE);
        defaultBundle.putInt(MCC, Integer.MAX_VALUE);
        defaultBundle.putInt(MNC, Integer.MAX_VALUE);
        defaultBundle.putInt(LAC, Integer.MAX_VALUE);
        defaultBundle.putInt(PSC, Integer.MAX_VALUE);
    }

    /** Create an empty CellInfo */
    public CustomCellInfo() {
        // Clone the default bundle
        this.infoBundle = (Bundle) defaultBundle.clone();
    }

    /**
     * Create a new CustomCellInfo from a bundle containing the necessary
     * informations.
     * 
     * @param infos
     *            The bundle that contains all the known informations about the
     *            cell.
     */
    public CustomCellInfo(Bundle infos) {
        // initialize empty
        this();
        // Copy the infos
        infoBundle.putAll(infos);
    }

    /**
     * Create a new CustomCellInfo from the Android CellInfo implementation.
     * This only initialize the generic fields from the CellInfo class.
     * 
     * @param cell
     *            The CellInfo to initialize from.
     */
    public CustomCellInfo(CellInfo cell) {
        // initialize empty
        this();
        // Timestamp the data
        infoBundle.putLong(TIMESTAMP, cell.getTimeStamp());
        // Indicate if this cell is registered
        infoBundle.putBoolean(IS_REGISTERED, cell.isRegistered());
    }

    /**
     * Create a new CustomCellInfo from the Android CellInfoGsm implementation.
     * 
     * @param cell
     *            The CellInfoGsm to initialize from.
     */
    public CustomCellInfo(CellInfoGsm cell) {
        // Initialize the the generic fields from the CellInfo class
        this((CellInfo) cell);
        // We have a GSM type of cell
        infoBundle.putInt(CELL_TYPE, ICellInfo.CELL_GSM);
        // Initialize Signal Strength
        putSignalStrength(cell.getCellSignalStrength());
        // Initialize Cell identity
        {
            // Get the cell identity
            CellIdentityGsm cellId = cell.getCellIdentity();
            // Fill the available fields
            infoBundle.putInt(CID, cellId.getCid());
            infoBundle.putInt(LAC, cellId.getLac());
            infoBundle.putInt(MCC, cellId.getMcc());
            infoBundle.putInt(MNC, cellId.getMnc());
        }
    }

    /**
     * Create a new CustomCellInfo from the Android CellInfoLte implementation.
     * 
     * @param cell
     *            The CellInfoLte to initialize from.
     */
    public CustomCellInfo(CellInfoLte cell) {
        // Initialize the the generic fields from the CellInfo class
        this((CellInfo) cell);
        // We have a LTE type of cell
        infoBundle.putInt(CELL_TYPE, ICellInfo.CELL_LTE);
        // Initialize Signal Strength
        putSignalStrength(cell.getCellSignalStrength());
        // Initialize Cell identity
        {
            // Get the cell identity
            CellIdentityLte cellId = cell.getCellIdentity();
            // Fill the available fields
            infoBundle.putInt(CID, cellId.getCi());
            infoBundle.putInt(MCC, cellId.getMcc());
            infoBundle.putInt(MNC, cellId.getMnc());
        }
    }

    /**
     * Create a new CustomCellInfo from the Android CellInfoCdma implementation.
     * 
     * @param cell
     *            The CellInfoCdma to initialize from.
     */
    public CustomCellInfo(CellInfoCdma cell) {
        // Initialize the the generic fields from the CellInfo class
        this((CellInfo) cell);
        // We have a CDMA type of cell
        infoBundle.putInt(CELL_TYPE, ICellInfo.CELL_CDMA);
        // Initialize Signal Strength
        putSignalStrength(cell.getCellSignalStrength());
        // Initialize Cell identity
        {
            // Get the cell identity
            CellIdentityCdma cellId = cell.getCellIdentity();
            // TODO Fill the available fields
            // The CDMA cells are actually very different from the others...
        }
    }

    /**
     * Create a new CustomCellInfo from the Android CellInfoWcdma
     * implementation.
     * 
     * @param cell
     *            The CellInfoWcdma to initialize from.
     */
    public CustomCellInfo(CellInfoWcdma cell) {
        // Initialize the the generic fields from the CellInfo class
        this((CellInfo) cell);
        // We have a WCDMA type of cell
        infoBundle.putInt(CELL_TYPE, ICellInfo.CELL_WCDMA);
        // Initialize Signal Strength
        putSignalStrength(cell.getCellSignalStrength());
        // Initialize Cell identity
        {
            // Get the cell identity
            CellIdentityWcdma cellId = cell.getCellIdentity();
            // Fill the available fields
            infoBundle.putInt(CID, cellId.getCid());
            infoBundle.putInt(MCC, cellId.getMcc());
            infoBundle.putInt(MNC, cellId.getMnc());
            infoBundle.putInt(LAC, cellId.getLac());
            infoBundle.putInt(PSC, cellId.getPsc());
        }
    }

    /** Parse and put the signal strength in the bundle */
    private void putSignalStrength(CellSignalStrength cellSS) {
        // Parse the CellSignalStrength by creating a CustomSignalStrength
        CustomSignalStrength ss = new CustomSignalStrength(cellSS);
        // Bundle it and store it
        infoBundle.putBundle(SIGNAL_STRENGTH, ss.getBundle());
    }

    /**
     * Create a new CustomCellInfo from an Android CellInfo, trying to detect
     * the actual type of the provided CellInfo (CellInfoLte, CellInfoGsm,
     * CellInfoWcdma or CellInfoCdma).
     * 
     * @param cell
     *            The CellInfo to inspect thoroughly
     * @return A CustomCellInfo containing all the information that could be
     *         grabbed from the input CellInfo.
     */
    public static CustomCellInfo buildFromCellInfo(CellInfo cell) {
        // Initialize the result
        CustomCellInfo result = null;
        // Continue by inspecting which type of CellInfo we actually
        // have and cast accordingly.
        if (cell instanceof CellInfoCdma) {
            // Call the CDMA constructor
            result = new CustomCellInfo((CellInfoCdma) cell);
        } else if (cell instanceof CellInfoGsm) {
            // Call the GSM constructor
            result = new CustomCellInfo((CellInfoGsm) cell);
        } else if (cell instanceof CellInfoLte) {
            // Call the LTE constructor
            result = new CustomCellInfo((CellInfoLte) cell);
        } else if (cell instanceof CellInfoWcdma) {
            // Call the WCDMA constructor
            result = new CustomCellInfo((CellInfoWcdma) cell);
        } else {
            // We only have a generic CellInfo...
            result = new CustomCellInfo(cell);
        }
        return result;
    }

    /** Return a bundle containing the cell infos */
    public Bundle getBundle() {
        return this.infoBundle;
    }

    @Override
    public int getCellType() {
        return infoBundle.getInt(CELL_TYPE);
    }

    @Override
    public long getTimeStamp() {
        return infoBundle.getLong(TIMESTAMP);
    }

    @Override
    public boolean isRegistered() {
        return infoBundle.getBoolean(IS_REGISTERED);
    }

    @Override
    public ISignalStrength getSignalStrength() {
        // Get the bundled signal strength and create a CustomSignalStrength
        // from it
        return new CustomSignalStrength(infoBundle.getBundle(SIGNAL_STRENGTH));
    }

    @Override
    public int getCid() {
        return infoBundle.getInt(CID);
    }

    @Override
    public int getLac() {
        return infoBundle.getInt(LAC);
    }

    @Override
    public int getMcc() {
        return infoBundle.getInt(MCC);
    }

    @Override
    public int getMnc() {
        return infoBundle.getInt(MNC);
    }

    @Override
    public int getPsc() {
        return infoBundle.getInt(PSC);
    }

}
