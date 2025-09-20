package com.jansmerecki.repository;

import com.jansmerecki.domain.Device;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class DeviceRepository {

    private final Map<String, Device> devices = new ConcurrentHashMap<>();

    public Device save(Device device){
        devices.put(device.getMacAddress(), device);
        return device;
    }

    public Optional<Device> findByMac(String mac) {
        return Optional.ofNullable(devices.get(mac));
    }

    public boolean existsByMac(String mac) {
        return devices.containsKey(mac);
    }

    public Collection<Device> findAll() {
        return devices.values();
    }

    public void clear() {
        devices.clear();
    }
}
