package edu.np.ece.assettracking.model;

import com.estimote.sdk.Beacon;

/**
 * Created by zqi2 on 19/9/2015.
 */
public class BeaconData {

    private int id;
    private String uuid;
    private int major;
    private int minor;
    private String label;
    private int locationId;
    private int equipmentId;
    private String created;
    private String modified;

    private String name;
    private String mac;
    private int rssi;
    private int measuredPower;

    private LocationData location = null;
    private EquipmentData equipment = null;

    public BeaconData(Beacon beacon) {
        this.uuid = beacon.getProximityUUID();
        this.major = beacon.getMajor();
        this.minor = beacon.getMinor();
        this.name = beacon.getName();
        this.mac = beacon.getMacAddress();
        this.rssi = beacon.getRssi();
        this.measuredPower = beacon.getMeasuredPower();
    }

    public BeaconData() { }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void setLocationId(int locationId) {
        this.locationId = locationId;
    }

    public int getId() {
        return id;
    }

    public int getLocationId() {
        return locationId;
    }

    public int getEquipmentId() {
        return equipmentId;
    }

    public void setEquipmentId(int equipmentId) {
        this.equipmentId = equipmentId;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getModified() {
        return modified;
    }

    public void setModified(String modified) {
        this.modified = modified;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public int getMajor() {
        return major;
    }

    public void setMajor(int major) {
        this.major = major;
    }

    public int getMinor() {
        return minor;
    }

    public void setMinor(int minor) {
        this.minor = minor;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public int getRssi() {
        return rssi;
    }

    public void setRssi(int rssi) {
        this.rssi = rssi;
    }

    public int getMeasuredPower() {
        return measuredPower;
    }

    public void setMeasuredPower(int measuredPower) {
        this.measuredPower = measuredPower;
    }

    public LocationData getLocation() {
        return location;
    }

    public void setLocation(LocationData location) {
        this.location = location;
    }

    public EquipmentData getEquipment() {
        return equipment;
    }

    public void setEquipment(EquipmentData equipment) {
        this.equipment = equipment;
    }
}
