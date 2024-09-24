package com.yazid.NetworkMonitor.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class ProgramDto {
    private String name;
    private String type;
    private Boolean isSelected;
    private String number;

}
