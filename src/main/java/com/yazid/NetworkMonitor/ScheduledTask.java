package com.yazid.NetworkMonitor;

import com.yazid.NetworkMonitor.Service.MailService;
import com.yazid.NetworkMonitor.Service.SnmpService;
import com.yazid.NetworkMonitor.dto.NotificationEmail;
import com.yazid.NetworkMonitor.dto.SystemInfoDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


@Component
public class ScheduledTask {
    @Autowired
    private SnmpService snmpService;
    @Autowired
    private MailService mailService;
    private int maxTemp;

    @Scheduled(fixedRate = 60000)
    public void reportTemperature() {
        String[] ips = {"192.168.10.24", "192.168.10.23", "192.168.10.22", "192.168.10.21"};
        int[] temperatures = new int[3];
        int maxTemp=50;
        for (int i = 0; i < ips.length; i++) {
            try {
                temperatures[i] = Integer.parseInt(snmpService.snmpGetSystemInfo(ips[i]).getTemperature());
            } catch (Exception e) {
                System.err.println("Failed to retrieve temperature from IP " + ips[i] + ": " + e.getMessage());
                temperatures[i] = -1;
            }
        }
        System.out.println("Triggered mail task");
        for (int i = 0; i < temperatures.length; i++) {
            if (temperatures[i] > maxTemp) {
                NotificationEmail notificationEmail = new NotificationEmail(
                        "Temperature Alert",
                        "yazidbougrine99@gmail.com",
                        "Excessive Temperature at " + ips[i] + " exceeding "+maxTemp+"°C"
                );
                mailService.sendMail(notificationEmail);
            }
        }
        //int temperature = Integer.parseInt(snmpService.snmpGetSystemInfo("192.168.10.24").getTemperature());

/*
        if(temperature>maxTemp){
            NotificationEmail notificationEmail = new NotificationEmail("Temperature","yazidbougrine99@gmail.com","Excessive Temperature exceeding "+temperature);
            mailService.sendMail(notificationEmail);
        }
*/
        //System.out.println("triggered mail task");
        /* NotificationEmail notificationEmail = new NotificationEmail("Temperature",
                "yazidbougrine99@gmail.com",
                "Excessive Temperature exceeding  50°C. Follow this link to take action: http://localhost:5173");
        mailService.sendMail(notificationEmail);

         */
    }
}
