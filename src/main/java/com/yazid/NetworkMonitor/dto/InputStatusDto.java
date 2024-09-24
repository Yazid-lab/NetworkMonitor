package com.yazid.NetworkMonitor.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class InputStatusDto {
private String Snr;
private String SnrMargin;

private String BER;
private String Power;
private String  Frequency;
private String Mode;
private String Modulation;
private String RollOff;
private String Fec;
private String Pilots;
}
