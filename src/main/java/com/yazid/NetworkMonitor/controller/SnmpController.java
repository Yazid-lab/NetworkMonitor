package com.yazid.NetworkMonitor.controller;

import com.yazid.NetworkMonitor.Service.SnmpService;
import com.yazid.NetworkMonitor.Service.VideoService;
import com.yazid.NetworkMonitor.dto.*;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Random;

@Controller
@RequestMapping("/api/snmp")
@AllArgsConstructor
public class SnmpController {
    private final SnmpService snmpService;
    private final VideoService videoService;

    @PostMapping("/inputStatus")
    public ResponseEntity<InputStatusDto> GetInputStatus(@RequestBody String ipAddress) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(snmpService.snmpGetInputStatus(ipAddress));
    }

    @PostMapping("/inputConfig")
    public ResponseEntity<InputConfigDto> GetInputConfig(@RequestBody String ipAddress) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(snmpService.snmpGetInputConfig(ipAddress));
    }
    @GetMapping("/test")
    public ResponseEntity<String> getTest(@RequestBody String ipAddress){
        System.out.println(ipAddress);
                return ResponseEntity.status(HttpStatus.OK).body(ipAddress);
    }
//    @GetMapping("/interface")
//    public ResponseEntity<String> getInterface(){
//        return ResponseEntity.status(HttpStatus.OK)
//                .body(snmpService.snmpGetInterface());
//    }
//    @GetMapping("/temp")
//    public ResponseEntity<String> getTemperature(){
//        return ResponseEntity.status(HttpStatus.OK)
//                .body(snmpService.snmpGetSystemTemp());
//    }
    @PostMapping("/programs")
    public ResponseEntity<List<ProgramDto>> getPrograms(@RequestBody String ipAddress){
        return ResponseEntity.status(HttpStatus.OK)
                .body(snmpService.snmpGetProgramsTable(ipAddress));

    }
    @PostMapping("/network")
    public ResponseEntity<List<NetworkEntryDto>> getNetworkEntries(@RequestBody String ipAddress){
        return ResponseEntity.status(HttpStatus.OK)
                .body(snmpService.snmpGetNetworkTable(ipAddress));
    }
    @PostMapping("/reboot")
    public ResponseEntity snmpReboot(@RequestBody String ipAddress){
        snmpService.snmpReboot(ipAddress);
        return new ResponseEntity(HttpStatus.OK);
    }

    @PostMapping("/primaryService")
    public ResponseEntity setPrimaryService(@RequestBody String ipAddress, @RequestParam String id){
        snmpService.snmpSetPrimaryService(ipAddress,id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("systemInfo")
    public ResponseEntity<SystemInfoDto> getSystemInfo(@RequestBody String ipAddress){
        return ResponseEntity.status(HttpStatus.OK)
                .body(snmpService.snmpGetSystemInfo(ipAddress));
    }
    @PostMapping("/snrMargin")
    public ResponseEntity<Float> getSnrMargin(@RequestBody String ipAddress){
        return ResponseEntity.status(HttpStatus.OK)
                .body(snmpService.snmpGetSnrMargin(ipAddress));
    }
    @PostMapping("/snrMargintest")
    public ResponseEntity<Float> getSnrMargintest(@RequestBody String ipAddress){
        Random random = new Random();
        return ResponseEntity.status(HttpStatus.OK)
                .body(random.nextFloat()*10);
    }

}
