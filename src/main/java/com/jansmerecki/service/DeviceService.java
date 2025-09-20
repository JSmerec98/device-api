package com.jansmerecki.service;

import com.jansmerecki.domain.Device;
import com.jansmerecki.domain.DeviceType;
import com.jansmerecki.dto.TopologyNode;
import com.jansmerecki.repository.DeviceRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

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
            throw new IllegalArgumentException(String.format("Device with MAC %s already exists: ", normalizedMac));
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

    public List<TopologyNode> getFullTopology() {
        Map<String, Device> devicesByMac = deviceRepository.findAll().stream()
                .collect(Collectors.toMap(Device::getMacAddress, d -> d));

        Map<String, List<Device>> childrenIndex = buildChildrenIndex(devicesByMac);

        List<TopologyNode> roots = new ArrayList<>();
        for (Device device : devicesByMac.values()) {
            if (device.getUplinkMacAddress() == null || !devicesByMac.containsKey(device.getUplinkMacAddress())) {
                roots.add(buildNode(device, childrenIndex));
            }
        }

        sortTopology(roots, devicesByMac);
        return roots;
    }

    public TopologyNode getTopologyFrom(String mac) {
        String normalizedMac = normalize(mac);

        Map<String, Device> devicesByMac = deviceRepository.findAll().stream()
                .collect(Collectors.toMap(Device::getMacAddress, d -> d));

        Device root = devicesByMac.get(normalizedMac);
        if (root == null) {
            throw new NoSuchElementException("Device not found: " + normalizedMac);
        }

        Map<String, List<Device>> childrenIndex = buildChildrenIndex(devicesByMac);
        TopologyNode node = buildNode(root, childrenIndex);
        sortTopology(Collections.singletonList(node), devicesByMac);
        return node;
    }

    private Map<String, List<Device>> buildChildrenIndex(Map<String, Device> devicesByMac) {
        Map<String, List<Device>> childrenIndex = new HashMap<>();
        for (Device device : devicesByMac.values()) {
            String upLink = device.getUplinkMacAddress();
            if (upLink != null) {
                childrenIndex.computeIfAbsent(upLink, k -> new ArrayList<>()).add(device);
            }
        }
        return childrenIndex;
    }

    private TopologyNode buildNode(Device device, Map<String, List<Device>> childrenIndex) {
        TopologyNode node = new TopologyNode(device.getMacAddress());
        List<Device> kids = childrenIndex.getOrDefault(device.getMacAddress(), List.of());
        for (Device child : kids) {
            node.getChildren().add(buildNode(child, childrenIndex));
        }
        return node;
    }

    private void sortTopology(List<TopologyNode> nodes, Map<String, Device> byMac) {
        Comparator<TopologyNode> comparator = Comparator
                .comparingInt((TopologyNode node) -> byMac.get(node.getMacAddress()).getDeviceType().getOrder())
                .thenComparing(TopologyNode::getMacAddress);

        nodes.sort(comparator);
        for (TopologyNode node : nodes) {
            node.getChildren().sort(comparator);
            sortTopology(node.getChildren(), byMac);
        }
    }
}
