package com.yazid.NetworkMonitor.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class NetworkEntryDto {
    private String connectionName;
    private boolean isDhcp;
    private boolean isEnabled;
    private String ConnectionAddress;
    private String SubnetMask;
}
