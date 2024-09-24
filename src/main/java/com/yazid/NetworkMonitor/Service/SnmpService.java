package com.yazid.NetworkMonitor.Service;

import ch.qos.logback.core.util.SystemInfo;
import com.yazid.NetworkMonitor.dto.*;
import lombok.AllArgsConstructor;
import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.*;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.snmp4j.util.DefaultPDUFactory;
import org.snmp4j.util.TableEvent;
import org.snmp4j.util.TableUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@AllArgsConstructor
public class SnmpService {
    //private static String ipAddress = "192.168.10.24";
    private static String selectedProgram;

    public float snmpGetSnrMargin(String ipAddress){
        String snrString = snmpGet(".1.3.6.1.4.1.27338.5.5.3.3.3.0","private",ipAddress);
        return Float.parseFloat(snrString)/10;
    }

    public SystemInfoDto snmpGetSystemInfo(String ipAddress){
        String[] oids = {".1.3.6.1.4.1.27338.5.2.3.0",//serial number
        ".1.3.6.1.4.1.27338.5.2.1.0",//system name
        ".1.3.6.1.4.1.27338.5.6.1.0",//system version
        ".1.3.6.1.4.1.27338.5.10.1.0",//system time
        ".1.3.6.1.4.1.27338.5.7.1.0",//temperature
        };
        List<String> systemInfoArray = snmpGetMultiple(oids,"private",ipAddress);
        SystemInfoDto systemInfoDto = new SystemInfoDto();
        systemInfoDto.setSerialNumber(systemInfoArray.getFirst());
        systemInfoDto.setName(systemInfoArray.get(1));
        systemInfoDto.setSystemVersion(systemInfoArray.get(2));
        systemInfoDto.setSystemTime(systemInfoArray.get(3));
        systemInfoDto.setTemperature(systemInfoArray.get(4));
        return systemInfoDto;
    }
    public InputStatusDto snmpGetInputStatus(String ipAddress) {
        String[] oid = {".1.3.6.1.4.1.27338.5.5.3.3.2.0",".1.3.6.1.4.1.27338.5.5.3.3.3.0",".1.3.6.1.4.1.27338.5.5.3.3.4.0",
        ".1.3.6.1.4.1.27338.5.5.3.3.5.0",".1.3.6.1.4.1.27338.5.5.3.3.6.0",
        ".1.3.6.1.4.1.27338.5.5.3.3.10.0",".1.3.6.1.4.1.27338.5.5.3.3.8.0",
        ".1.3.6.1.4.1.27338.5.5.3.3.11.0",".1.3.6.1.4.1.27338.5.5.3.3.9.0",
        ".1.3.6.1.4.1.27338.5.5.3.3.12.0"};
        List<String> inputStatusArray =  snmpGetMultiple(oid, "private",ipAddress);
        InputStatusDto statusDto = new InputStatusDto();
        float floatSnr = Float.parseFloat(inputStatusArray.getFirst())/10;
        statusDto.setSnr(String.valueOf(floatSnr));
        float floatSnrMargin = Float.parseFloat(inputStatusArray.get(1))/10;
        statusDto.setSnrMargin(String.valueOf(floatSnrMargin));
        statusDto.setBER(inputStatusArray.get(2));
        statusDto.setPower(inputStatusArray.get(3));
        float floatFrequency = Float.parseFloat(inputStatusArray.get(4))/1000;
        statusDto.setFrequency(String.valueOf(Math.ceil(floatFrequency)));
        switch (inputStatusArray.get(5))
        {

            case "1":
                statusDto.setMode("automatic");
                break;
            case "2":
                statusDto.setMode("DVB-S");
                break;
            case "3":
                statusDto.setMode("DVB-S2");
                break;
            case "4":
                statusDto.setMode("DVB-S2X");
                break;
        }
        switch (inputStatusArray.get(6))
        {
            case "1":
                statusDto.setModulation("Unknown");
                break;
            case "2":
                statusDto.setModulation("QPSK");
                break;
        }
        switch  (inputStatusArray.get(7))
        {
            case "1":
                statusDto.setRollOff("Unknown");
                break;
            case "2":
                statusDto.setRollOff("0.35");
                break;
            case "3":
                statusDto.setRollOff("0.25");
                break;
        }
        switch (Integer.parseInt(inputStatusArray.get(8))){
            case 1:
                statusDto.setFec("Unknown");
                break;
            case 2:
                statusDto.setFec("1/4");
                break;
            case 3:
                statusDto.setFec("1/3");
                break;
            case 4:
                statusDto.setFec("2/5");
                break;
            case 5:
                statusDto.setFec("1/2");
                break;
            case 6:
                statusDto.setFec("3/5");
                break;
            case 10:
                statusDto.setFec("5/6");
                break;
        }
        if (inputStatusArray.getLast().equals("1")){
            statusDto.setPilots("On");
        }else {
            statusDto.setPilots("OFF");
        }

        return statusDto;
    }

    public InputConfigDto snmpGetInputConfig(String ipAddress) {
        String[] oids = {
                ".1.3.6.1.4.1.27338.5.3.2.2.1.0",".1.3.6.1.4.1.27338.5.3.2.2.4.7.1.0",
                ".1.3.6.1.4.1.27338.5.3.2.2.4.7.3.0",".1.3.6.1.4.1.27338.5.3.2.2.4.7.2.0",
                ".1.3.6.1.4.1.27338.5.3.2.2.4.1.0",".1.3.6.1.4.1.27338.5.3.2.2.4.2.0",
                ".1.3.6.1.4.1.27338.5.3.2.2.4.5.0",".1.3.6.1.4.1.27338.5.3.2.2.4.6.0",
                ".1.3.6.1.4.1.27338.5.3.2.2.4.4.0",".1.3.6.1.4.1.27338.5.3.2.2.4.3.0",
                ".1.3.6.1.4.1.27338.5.3.2.2.4.8.0",".1.3.6.1.4.1.27338.5.3.2.2.4.9.1.0",
                ".1.3.6.1.4.1.27338.5.3.2.2.4.9.2.0"
        };
        List<String> inputConfigArray = snmpGetMultiple(oids, "private", ipAddress);
        InputConfigDto inputConfigDto = new InputConfigDto();
        switch (inputConfigArray.getFirst()){
            case "1":
                inputConfigDto.setInputType("IP");
                break;
            case "2":
                inputConfigDto.setInputType("ASI");
                break;
            case "3":
                inputConfigDto.setInputType("DVB-S/S2/S2X");
                break;
            case "4":
                inputConfigDto.setInputType("Zixi");
                break;
            default:
                inputConfigDto.setInputType("Unknown");
        }

        if (inputConfigArray.get(1).equals("1")) {
            inputConfigDto.setLNBisPowered(true);
        } else if (inputConfigArray.get(1).equals("2")) {
            inputConfigDto.setLNBisPowered(false);
        }
        if (inputConfigArray.get(2).equals("1")) {
            inputConfigDto.setSatPolarization("Vertical");
        } else if (inputConfigArray.get(2).equals("2")) {
            inputConfigDto.setSatPolarization("Horizontal");
        }
        if (inputConfigArray.get(3).equals("1")){
            inputConfigDto.setTone22kHz(true);
        }else if (inputConfigArray.get(3).equals("2")){
            inputConfigDto.setTone22kHz(false);
        }
        switch (inputConfigArray.get(4)){
            case "1":
                inputConfigDto.setInterface("RF1");
                break;
            case "2":
                inputConfigDto.setInterface("RF2");
                break;
            case "3":
                inputConfigDto.setInterface("RF3");
                break;
            case "4":
                inputConfigDto.setInterface("RF4");
                break;
        }
        switch (inputConfigArray.get(5)){
            case "1":
                inputConfigDto.setMode("Automatic");
                break;
            case "2":
                inputConfigDto.setMode("DVB-S");
                break;
            default:
                inputConfigDto.setMode("DVB-S2/S2X");
        }
        float downlinkFrequency = Float.parseFloat( inputConfigArray.get(6))/1000;
        inputConfigDto.setDownlinkFrequency(String.valueOf(downlinkFrequency));
        float oscilatorFrequency = Float.parseFloat(inputConfigArray.get(7))/1000;
        inputConfigDto.setOscillatorFrequency(String.valueOf(oscilatorFrequency));
        int searchRange = Integer.parseInt(inputConfigArray.get(8))/1000;
        inputConfigDto.setSearchRange(String.valueOf(searchRange));
        float symbolRate = Float.parseFloat(inputConfigArray.get(9))/1000;
        inputConfigDto.setSymbolRate(String.valueOf(symbolRate));
        inputConfigDto.setGoldCode(inputConfigArray.get(10));
        if (inputConfigArray.get(11).equals("1")){
            inputConfigDto.setIsMultiStreamEnabled(true);
        }else if (inputConfigArray.get(11).equals("2")){
            inputConfigDto.setIsMultiStreamEnabled(false);
        }
        inputConfigDto.setInputStreamIdentifier(inputConfigArray.getLast());

        System.out.printf("Input config for device with ip " + ipAddress+" requested and returned\n");
        return inputConfigDto;
    }

    public List<String> snmpGetTest(String ipAddress){
        String[] oids ={".1.3.6.1.4.1.27338.5.5.3.3.2.0"};
        return snmpGetMultiple(oids,"public",ipAddress);
    }

    public List<NetworkEntryDto> snmpGetNetworkTable(String ipAddress){
        List<NetworkEntryDto> networkEntries = new ArrayList<>();
        try{
            DefaultUdpTransportMapping transport = new DefaultUdpTransportMapping();
            transport.listen();
            Snmp snmp = new Snmp(transport);
            CommunityTarget communityTarget = createCommunityTarget(ipAddress);
            TableUtils tableUtils = new TableUtils(snmp, new DefaultPDUFactory());
            OID[] columns = new OID[]{new OID(".1.3.6.1.4.1.27338.5.8.1.1.1.2"), //Connection name
                    new OID(".1.3.6.1.4.1.27338.5.8.1.1.1.3"), // is enabled
                    new OID(".1.3.6.1.4.1.27338.5.8.1.1.1.4"), // is dhcp
                    new OID(".1.3.6.1.4.1.27338.5.8.1.1.1.5"), // Connection address
                    new OID(".1.3.6.1.4.1.27338.5.8.1.1.1.6"), // net mask

            };
            List<TableEvent> events = tableUtils.getTable(communityTarget,columns,null,new OID("3"));
            VariableBinding[] columns1 = events.getFirst().getColumns();
            String variable = columns1[3].getVariable().toString();
            for (TableEvent event : events){
                NetworkEntryDto networkEntry = new NetworkEntryDto();
                VariableBinding[] vbs = event.getColumns();
                networkEntry.setConnectionName(vbs[0].getVariable().toString());
                networkEntry.setEnabled(vbs[1].getVariable().toString().equals("1"));
                networkEntry.setDhcp(vbs[2].getVariable().toString().equals("1"));
                String connectionAddressHex = vbs[3].getVariable().toString();
                networkEntry.setConnectionAddress(hexToDecimalIp(connectionAddressHex));
                String subnetHex = vbs[4].getVariable().toString();
                networkEntry.setSubnetMask(hexToDecimalIp(subnetHex));
                networkEntries.add(networkEntry);
            }
            snmp.close();
            transport.close();
        }catch(Exception e){e.printStackTrace();}
        return networkEntries;
    }

    public List<ProgramDto> snmpGetProgramsTable(String ipAddress) {
        List<ProgramDto> programs = new ArrayList<>();
        try {
            DefaultUdpTransportMapping transport = new DefaultUdpTransportMapping();
            transport.listen();
            Snmp snmp = new Snmp(transport);
            CommunityTarget communityTarget = createCommunityTarget(ipAddress);
            TableUtils tableUtils = new TableUtils(snmp, new DefaultPDUFactory());
            OID[] columns = new OID[]{new OID(".1.3.6.1.4.1.27338.5.5.1.5.1.1.9"),
                    new OID(".1.3.6.1.4.1.27338.5.5.1.5.1.1.11"), new OID(".1.3.6.1.4.1.27338.5.5.1.5.1.1.2"),
            new OID(".1.3.6.1.4.1.27338.5.5.1.5.1.1.5")};
            List<TableEvent> events = tableUtils.getTable(communityTarget, columns, null, new OID("17"));

            for (TableEvent event : events) {
                VariableBinding[] columns1 = event.getColumns();
                ProgramDto program = new ProgramDto();
                program.setName(columns1[0].getVariable().toString());
                program.setType(columns1[1].getVariable().toString());
                program.setIsSelected(columns1[2].getVariable().toString().equals("1"));
                program.setNumber(columns1[3].getVariable().toString());
                programs.add(program);
                if (program.getIsSelected()){
                    selectedProgram=program.getNumber();
                    System.out.println("the selected program is "+ selectedProgram);
                }
            }

            snmp.close();
            transport.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return programs;
    }
    private CommunityTarget createCommunityTarget(String address) {
        CommunityTarget target = new CommunityTarget();
        target.setCommunity(new OctetString("private")); // Replace with your community string
        Address targetAddress = new UdpAddress(address+"/161");
        target.setAddress(targetAddress);
        target.setRetries(2);
        target.setTimeout(1500);
        target.setVersion(SnmpConstants.version2c);
        return target;
    }

    public String snmpGetPrimaryServiceId(String ipAddress){
        String responseValue="11";
        try {

            DefaultUdpTransportMapping transport = new DefaultUdpTransportMapping();
            transport.listen();
            Snmp snmp = new Snmp(transport);
            Address targetAddress = new UdpAddress(ipAddress + "/161");
            CommunityTarget target = createCommunityTarget(ipAddress);
            target.setCommunity(new OctetString("private"));
            target.setAddress(targetAddress);
            target.setRetries(2);
            target.setTimeout(2000);
            target.setVersion(SnmpConstants.version2c);

            ResponseEvent<Address> event = snmp.get(createGetPdu(new OID(".1.3.6.1.4.1.27338.5.3.2.3.2.1.0")), target);

            if (event != null && event.getResponse() != null) {
                responseValue = event.getResponse().get(0).getVariable().toString();
            } else {
                throw new IOException("getting the primary service id went wrong " );
            }
            snmp.close();
            transport.close();


        } catch (Exception e) {
            e.printStackTrace();
        }
        return responseValue;
    }
    private String snmpGet(String oid, String community,String ipAddress) {
        String responseValue = "";
        try {

            DefaultUdpTransportMapping transport = new DefaultUdpTransportMapping();
            transport.listen();
            Snmp snmp = new Snmp(transport);
            Address targetAddress = new UdpAddress(ipAddress + "/161");
            CommunityTarget target = new CommunityTarget<>();
            target.setCommunity(new OctetString(community));
            target.setAddress(targetAddress);
            target.setRetries(2);
            target.setTimeout(2000);
            target.setVersion(SnmpConstants.version2c);

            ResponseEvent<Address> event = snmp.get(createGetPdu(new OID(oid)), target);

            if (event != null && event.getResponse() != null) {
                responseValue = event.getResponse().get(0).getVariable().toString();
            } else {
                responseValue = "No response from SNMP agent.";
            }
            snmp.close();
            transport.close();


        } catch (Exception e) {
            responseValue = "Error: " + e.getMessage();
        }
        return responseValue;
    }

    private List<String> snmpGetMultiple(String[] oids, String community,String ipAddress) {
        List<String> responseList = new ArrayList<>();
        try {

            DefaultUdpTransportMapping transport = new DefaultUdpTransportMapping();
            transport.listen();
            Snmp snmp = new Snmp(transport);
            Address targetAddress = new UdpAddress(ipAddress + "/161");
            CommunityTarget target = new CommunityTarget<>();
            target.setCommunity(new OctetString(community));
            target.setAddress(targetAddress);
            target.setRetries(2);
            target.setTimeout(2000);
            target.setVersion(SnmpConstants.version2c);

            PDU pdu = new PDU();
            Arrays.stream(oids).toList().forEach((oid) -> pdu.add(new VariableBinding(new OID(oid))));

            ResponseEvent<Address> event = snmp.get(pdu, target);

            snmp.close();
            transport.close();
            if (event != null && event.getResponse() != null) {
                event.getResponse().getVariableBindings().forEach((v) -> responseList.add(v.getVariable().toString()));
            } else {
                System.out.println("No response from SNMP agent.");
            }


        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
        return responseList;
    }

    private PDU createGetPdu(OID oid) {
        PDU pdu = new PDU();
        pdu.add(new VariableBinding(oid));
        pdu.setType(PDU.GET);
        return pdu;
    }
    public void snmpReboot(String ipAddress){
        try {

            DefaultUdpTransportMapping transport = new DefaultUdpTransportMapping();
            transport.listen();
            Snmp snmp = new Snmp(transport);
            Address targetAddress = new UdpAddress(ipAddress + "/161");
            CommunityTarget target = new CommunityTarget<>();
            target.setCommunity(new OctetString("private"));
            target.setAddress(targetAddress);
            target.setRetries(2);
            target.setTimeout(2000);
            target.setVersion(SnmpConstants.version2c);

            PDU pdu = new PDU();
            Variable value = new Integer32(1);
            VariableBinding vb = new VariableBinding();
            vb.setOid(new OID(".1.3.6.1.4.1.27338.5.4.2.1.0"));
            vb.setVariable(value);
            pdu.add(vb);
            pdu.setType(PDU.SET);
            ResponseEvent event = snmp.set(pdu, target);
            snmp.close();
            transport.close();


        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
    public void snmpSetPrimaryService(String ipAddress, String serviceId){

        try {

            DefaultUdpTransportMapping transport = new DefaultUdpTransportMapping();
            transport.listen();
            Snmp snmp = new Snmp(transport);
            Address targetAddress = new UdpAddress(ipAddress + "/161");
            CommunityTarget target = new CommunityTarget<>();
            target.setCommunity(new OctetString("private"));
            target.setAddress(targetAddress);
            target.setRetries(2);
            target.setTimeout(2000);
            target.setVersion(SnmpConstants.version2c);

            PDU pdu = new PDU();
            Variable value = new Integer32(Integer.parseInt(serviceId));
            VariableBinding vb = new VariableBinding();
            vb.setOid(new OID(".1.3.6.1.4.1.27338.5.3.2.3.2.1.0"));
            vb.setVariable(value);
            pdu.add(vb);
            pdu.setType(PDU.SET);
            ResponseEvent event = snmp.set(pdu, target);
            snmp.close();
            transport.close();


        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }


    private static String hexToDecimalIp(String hexIp) {
        String[] hexParts = hexIp.split(":");
        StringBuilder decimalIp = new StringBuilder();

        for (int i = 0; i < hexParts.length; i++) {
            // Convert each hex part to an integer
            int decimalPart = Integer.parseInt(hexParts[i], 16);

            // Append the decimal part to the result
            decimalIp.append(decimalPart);

            // Add a dot between the parts, except after the last part
            if (i < hexParts.length - 1) {
                decimalIp.append(".");
            }
        }

        return decimalIp.toString();
    }
}
