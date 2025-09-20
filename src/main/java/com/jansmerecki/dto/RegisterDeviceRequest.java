package com.jansmerecki.dto;

import com.jansmerecki.domain.DeviceType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record RegisterDeviceRequest(
        @NotNull DeviceType deviceType,
        @NotBlank
        @Pattern(
                regexp = "^([0-9A-Fa-f]{2}[:-]){5}([0-9A-Fa-f]{2})$",
                message = "Invalid MAC address format"
        )
        String macAddress,
        String uplinkMacAddress
) {}
