package com.jansmerecki.dto;

import com.jansmerecki.domain.DeviceType;

public record DeviceResponse(DeviceType deviceType, String macAddress) {}