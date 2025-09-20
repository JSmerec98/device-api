package com.jansmerecki.web;

import com.jansmerecki.domain.Device;
import com.jansmerecki.dto.DeviceResponse;
import com.jansmerecki.dto.RegisterDeviceRequest;
import com.jansmerecki.service.DeviceService;
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

    @PostMapping("/devices")
    public ResponseEntity<DeviceResponse> register(@Valid @RequestBody RegisterDeviceRequest request) {
        Device saved = deviceService.register(request.deviceType(), request.macAddress(), request.uplinkMacAddress());
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(toResponse(saved));
    }

    @GetMapping("/devices")
    public List<DeviceResponse> all() {
        return deviceService.getAllSorted().stream()
                .map(this::toResponse)
                .toList();
    }

    @GetMapping("/devices/{mac}")
    public DeviceResponse byMac(@PathVariable String mac) {
        return toResponse(deviceService.getByMac(mac));
    }

    private DeviceResponse toResponse(Device d) {
        return new DeviceResponse(d.getDeviceType(), d.getMacAddress());
    }
}
