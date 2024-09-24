package com.yazid.NetworkMonitor;

import com.yazid.NetworkMonitor.Service.SnmpService;
import org.apache.commons.io.FileUtils;
import org.opencv.core.Core;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.concurrent.*;

@Service
public class VideoService {
    @Autowired
    private SnmpService snmpService;
    private String streamUrl = "225.2.2.2:1234";
    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
//        OpenCV.loadLocally();
    }
    public byte[] captureSnapshot(String ipAddress) throws IOException, InterruptedException {
        String primaryServiceId = snmpService.snmpGetPrimaryServiceId("192.168.10.24");
        File old =  new File("output2.jpg");
        if (old.delete()){
            System.out.println("old frame deleted");
        }
        try {

            Runtime rt = Runtime.getRuntime();
            Process pr = rt.exec("ffmpeg -loglevel quiet -y -i rtp://225.1.1.1:1234 -map 0:p:"+primaryServiceId+":v -frames:v 1 src/main/resources/com/yazid/NetworkMonitor/output2.jpg");
            InputStream stderr = pr.getErrorStream();
            InputStreamReader isr = new InputStreamReader(stderr);
            int exitVal = pr.waitFor();
            if (exitVal != 0 ){
                System.out.println("Something went wrong exit code :"+exitVal);
                throw new IOException("Error during frame capture");
            }
            pr.destroy();
        } catch (Throwable t){
            t.printStackTrace();

        }
        File image = new File("src/main/resources/com/yazid/NetworkMonitor/output2.jpg");
        byte[] bytes = FileUtils.readFileToByteArray(image);

//        TimeUnit.SECONDS.sleep(5);
//        InputStream in = getClass().getResourceAsStream("output2.jpg");
        return bytes;
    }

    public byte[] captureSnapshotFromProgram(String ipAddress,String id) throws IOException, InterruptedException, ExecutionException, TimeoutException {

        File old =  new File("output2.jpg");
        if (old.delete()){
            System.out.println("old frame deleted");
        }
        int timeOutSeconds = 10;
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<Integer> future = executor.submit( () ->{
            try {
                Runtime rt = Runtime.getRuntime();
                Process ffmpegProcess = rt.exec("ffmpeg -loglevel quiet -y -i rtp://225.1.1.1:1234 -map 0:p:"+id+":v -frames:v 1 src/main/resources/com/yazid/NetworkMonitor/output2.jpg");
                InputStream stderr = ffmpegProcess.getErrorStream();
                InputStreamReader isr = new InputStreamReader(stderr);
                int exitVal = ffmpegProcess.waitFor();
                if (exitVal != 0 ){
                    System.out.println("Something went wrong exit code :"+exitVal);
                    // Try to kill the FFmpeg process
                    if (ffmpegProcess != null && ffmpegProcess.isAlive()) {
                        ffmpegProcess.destroy(); // First attempt to terminate gracefully
                        if (ffmpegProcess.isAlive()) {
                            System.out.println("Forcefully killing the process...");
                            ffmpegProcess.destroyForcibly(); // Forcefully terminate if still running
                        }
                    }
                }
                return exitVal;
            } catch (Exception e){
                e.printStackTrace();
                throw new IOException("Frame capture failed");
            }
        });
        try{
            future.get(timeOutSeconds,TimeUnit.SECONDS);
        }catch(TimeoutException e){
            System.out.println("ffmpeg time out");
            future.cancel(true);
            throw e;
        }finally {
executor.shutdown();
        }
        File image = new File("src/main/resources/com/yazid/NetworkMonitor/output2.jpg");
        byte[] bytes = FileUtils.readFileToByteArray(image);

//        TimeUnit.SECONDS.sleep(5);
//        InputStream in = getClass().getResourceAsStream("output2.jpg");
        return bytes;
    }
}
