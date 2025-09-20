package com.jansmerecki.dto;

import com.jansmerecki.domain.DeviceType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record DeviceRequest(
        @NotNull DeviceType deviceType,
        @NotBlank
        @Schema(description="MAC address. Accepted formats: AA:BB:.., AA-BB-.., AABBCCDDEEFF",
                example="AA:BB:CC:DD:EE:FF")
        @Pattern(
                regexp = "^([0-9A-Fa-f]{2}[:-]){5}([0-9A-Fa-f]{2})$",
                message = "Invalid MAC address format"
        )
        String macAddress,
        @Schema(description="Optional uplink MAC address", example="11:22:33:44:55:66")
        String uplinkMacAddress
) {}
