package com.jansmerecki.web;

import com.jansmerecki.domain.Device;
import com.jansmerecki.dto.DeviceResponse;
import com.jansmerecki.dto.DeviceRequest;
import com.jansmerecki.dto.TopologyNode;
import com.jansmerecki.service.DeviceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class DeviceController {

    private final DeviceService deviceService;

    public DeviceController(DeviceService deviceService) {
        this.deviceService = deviceService;
    }

    @Tag(name = "Device", description = "Device operations")
    @Operation(summary = "Register a device",
            description = "Registers a new device. MAC is normalized. Duplicate MACs return 409.")
    @ApiResponse(responseCode = "201", description = "Created")
    @ApiResponse(responseCode = "400", description = "Validation error")
    @ApiResponse(responseCode = "409", description = "Duplicate MAC")
    @PostMapping("/devices")
    public ResponseEntity<DeviceResponse> register(@Valid @RequestBody DeviceRequest request) {
        Device saved = deviceService.register(request.deviceType(), request.macAddress(), request.uplinkMacAddress());
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(toResponse(saved));
    }

    @Tag(name = "Device", description = "Device operations")
    @Operation(summary = "List all devices (sorted)",
            description = "Sorted by type (Gateway > Switch > Access Point), then MAC address.")
    @ApiResponse(responseCode = "200", description = "OK")
    @GetMapping("/devices")
    public List<DeviceResponse> all() {
        return deviceService.getAllSorted().stream()
                .map(this::toResponse)
                .toList();
    }

    @Tag(name = "Device", description = "Device operations")
    @Operation(summary = "Get device by MAC",
            description = "Retrieves a single device by MAC. Any MAC format is accepted.")
    @ApiResponse(responseCode = "200", description = "OK")
    @ApiResponse(responseCode = "404", description = "Device not found")
    @GetMapping("/devices/{mac}")
    public DeviceResponse byMac(@PathVariable("mac") String mac) {
        return toResponse(deviceService.getByMac(mac));
    }

    @Tag(name = "Topology", description = "Device topology operations")
    @Operation(summary = "Get full topology",
            description = "Returns a forest of root nodes with their children.")
    @ApiResponse(responseCode = "200", description = "OK")
    @GetMapping("/topology")
    public List<TopologyNode> fullTopology() {
        return deviceService.getFullTopology();
    }

    @Tag(name = "Topology", description = "Device topology operations")
    @Operation(summary = "Get topology from MAC",
            description = "Returns the subtree starting from the given MAC.")
    @ApiResponse(responseCode = "200", description = "OK")
    @ApiResponse(responseCode = "404", description = "Device not found")
    @GetMapping("/topology/{mac}")
    public TopologyNode topologyFrom(@PathVariable("mac") String mac) {
        return deviceService.getTopologyFrom(mac);
    }

    private DeviceResponse toResponse(Device d) {
        return new DeviceResponse(d.getDeviceType(), d.getMacAddress());
    }
}
