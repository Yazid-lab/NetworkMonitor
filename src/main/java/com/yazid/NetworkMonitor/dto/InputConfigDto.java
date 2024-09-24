package com.yazid.NetworkMonitor.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.service.annotation.GetExchange;

@Setter
@Getter
public class InputConfigDto {
private String InputType;
private Boolean LNBisPowered;
private String SatPolarization;
private Boolean Tone22kHz;
private String Interface;
private String Mode;
private String DownlinkFrequency;
private String OscillatorFrequency;
private String SearchRange;
private String SymbolRate;
private String GoldCode;
private Boolean isMultiStreamEnabled;
private String inputStreamIdentifier;
}
