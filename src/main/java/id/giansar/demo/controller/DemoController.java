package id.giansar.demo.controller;

import id.giansar.demo.dto.ResponseDto;
import id.giansar.demo.service.DemoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class DemoController {
    private final DemoService demoService;

    @Autowired
    public DemoController(DemoService demoService) {
        this.demoService = demoService;
    }

    @PostMapping("/")
    public ResponseEntity uploadFile(@RequestParam("file") MultipartFile file) {
        ResponseDto responseDto = demoService.uploadFile(file);
        return ResponseEntity.status(responseDto.status).body(responseDto.message);
    }
}
