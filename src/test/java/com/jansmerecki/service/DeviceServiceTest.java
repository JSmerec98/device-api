package com.jansmerecki.service;

import com.jansmerecki.domain.Device;
import com.jansmerecki.domain.DeviceType;
import com.jansmerecki.dto.TopologyNode;
import com.jansmerecki.repository.DeviceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class DeviceServiceTest {

    private DeviceRepository deviceRepository;

    private DeviceService deviceService;

    @BeforeEach
    void setup() {
        deviceRepository = new DeviceRepository();
        deviceService = new DeviceService(deviceRepository);
    }

    @Test
    void shouldRegisterAndNormalizeMac() {
        Device device = deviceService.register(DeviceType.GATEWAY, "aa-bb-cc-dd-ee-ff", null);
        assertEquals("AA:BB:CC:DD:EE:FF", device.getMacAddress());

        Device byHyphen = deviceService.getByMac("AA-BB-CC-DD-EE-FF");
        assertEquals("AA:BB:CC:DD:EE:FF", byHyphen.getMacAddress());
    }

    @Test
    void shouldThrowWhenMacAlreadyExists() {
        deviceService.register(DeviceType.GATEWAY, "AABBCCDDEEFF", null);
        assertThatThrownBy(() ->
                deviceService.register(DeviceType.SWITCH, "aa:bb:cc:dd:ee:ff", null)
        ).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("already exists");
    }

    @Test
    void shouldThrowWhenDeviceNotFound() {
        assertThatThrownBy(() -> deviceService.getByMac("00:00:00:00:00:01"))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void shouldReturnDevicesSortedByTypeThenMac() {
        deviceService.register(DeviceType.ACCESS_POINT, "00:00:00:00:00:03", null);
        deviceService.register(DeviceType.SWITCH,       "00:00:00:00:00:02", null);
        deviceService.register(DeviceType.GATEWAY,      "00:00:00:00:00:01", null);

        List<Device> all = deviceService.getAllSorted();
        assertThat(all).extracting(Device::getDeviceType)
                .containsExactly(DeviceType.GATEWAY, DeviceType.SWITCH, DeviceType.ACCESS_POINT);
    }

    @Test
    void shouldBuildFullAndSubtreeTopology() {
        var gateway = deviceService.register(DeviceType.GATEWAY, "00:00:00:00:00:01", null);
        var networkSwitch = deviceService.register(DeviceType.SWITCH, "00:00:00:00:00:02", gateway.getMacAddress());
        var accessPoint = deviceService.register(DeviceType.ACCESS_POINT, "00:00:00:00:00:03", networkSwitch.getMacAddress());

        var fullTopology = deviceService.getFullTopology();
        assertThat(fullTopology).hasSize(1);
        assertThat(fullTopology.getFirst().getMacAddress()).isEqualTo(gateway.getMacAddress());
        assertThat(fullTopology.getFirst().getChildren()).extracting(TopologyNode::getMacAddress)
                .containsExactly(networkSwitch.getMacAddress());
        assertThat(fullTopology.getFirst().getChildren().getFirst().getChildren())
                .extracting(TopologyNode::getMacAddress).containsExactly(accessPoint.getMacAddress());

        var subtreeTopology = deviceService.getTopologyFrom(networkSwitch.getMacAddress());
        assertThat(subtreeTopology.getMacAddress()).isEqualTo(networkSwitch.getMacAddress());
        assertThat(subtreeTopology.getChildren()).hasSize(1);
    }
}