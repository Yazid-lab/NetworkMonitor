package com.yazid.NetworkMonitor.controller;

import com.yazid.NetworkMonitor.Service.VideoService;
import lombok.AllArgsConstructor;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Controller
@RequestMapping("api/video")
@AllArgsConstructor
public class VideoController {
    private final VideoService videoService;

    @GetMapping(value = "/frame",
            produces = MediaType.IMAGE_JPEG_VALUE
    )
    public @ResponseBody ResponseEntity<byte[]> getFrame() throws IOException, InterruptedException, ExecutionException, TimeoutException {
    return    ResponseEntity.ok()
                .cacheControl(CacheControl.maxAge(30, TimeUnit.DAYS))
                .body(videoService.captureSnapshot("225.1.1.1"));
    }
    @GetMapping(value = "/programFrame",
            produces = MediaType.IMAGE_JPEG_VALUE
    )
    public @ResponseBody ResponseEntity<byte[]> getProgramFrame(@RequestParam(name = "id") String programId) throws IOException, InterruptedException, ExecutionException, TimeoutException {
        return    ResponseEntity.ok()
                .cacheControl(CacheControl.maxAge(30, TimeUnit.DAYS))
                .body(videoService.captureSnapshotFromProgram("225.1.1.1",programId));
    }
}
