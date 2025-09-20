package com.jansmerecki.domain;

public class Device {
    private final DeviceType deviceType;
    private final String macAddress;
    private final String uplinkMacAddress;

    public Device(DeviceType deviceType, String macAddress, String uplinkMacAddress) {
        this.deviceType = deviceType;
        this.macAddress = macAddress;
        this.uplinkMacAddress = uplinkMacAddress;
    }

    public DeviceType getDeviceType() {
        return deviceType;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public String getUplinkMacAddress() {
        return uplinkMacAddress;
    }
}
