package com.yazid.NetworkMonitor.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class SystemInfoDto {
    private String serialNumber;
    private String name;
    private String systemVersion;
    private String systemTime;
    private String temperature;

}
