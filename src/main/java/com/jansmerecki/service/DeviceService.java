package com.jansmerecki.service;

import com.jansmerecki.domain.Device;
import com.jansmerecki.domain.DeviceType;
import com.jansmerecki.repository.DeviceRepository;
import com.jansmerecki.util.MacUtils;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;

import static com.jansmerecki.util.MacUtils.normalize;

@Service
public class DeviceService {

    private final DeviceRepository deviceRepository;

    public DeviceService(DeviceRepository deviceRepository) {
        this.deviceRepository = deviceRepository;
    }

    public Device register(DeviceType deviceType, String mac, String uplink) {
        String normalizedMac = normalize(mac);
        String normalizedUplink = normalize(uplink);

        if (deviceRepository.existsByMac(normalizedMac)) {
            throw new IllegalArgumentException(String.format("Device with MAC %s already exists: " , normalizedMac));
        }
        Device device = new Device(deviceType, normalizedMac, normalizedUplink);
        return deviceRepository.save(device);
    }

    public Device getByMac(String mac) {
        String normalizedMac = normalize(mac);
        return deviceRepository.findByMac(normalizedMac)
                .orElseThrow(() -> new NoSuchElementException("Device not found: " + normalizedMac));
    }

    public List<Device> getAllSorted() {
        Comparator<Device> comparator = Comparator
                .comparingInt((Device d) -> d.getDeviceType().getOrder())
                .thenComparing(Device::getMacAddress);
        return deviceRepository.findAll().stream().sorted(comparator).toList();
    }
}
