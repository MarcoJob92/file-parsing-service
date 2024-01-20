package file.parsing.demo.controller;

import file.parsing.demo.service.FileService;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@AllArgsConstructor
public class FileController {

    private final FileService fileService;

    @PostMapping(value = "/parse", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public byte[] parseFile(@RequestParam MultipartFile file,
                            @RequestHeader(name = "X-FORWARDED-FOR") String ipAddress) {
        long startTime = System.nanoTime();
        return fileService.parse(file, ipAddress, startTime);
    }
}
