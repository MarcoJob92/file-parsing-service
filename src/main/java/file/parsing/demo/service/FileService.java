package file.parsing.demo.service;

import file.parsing.demo.enums.FileColumn;
import file.parsing.demo.enums.FileValidation;
import file.parsing.demo.exception.MalformedFileException;
import file.parsing.demo.model.Entry;

import file.parsing.demo.model.IpAddress;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

import static io.micrometer.common.util.StringUtils.isEmpty;

@Service
@AllArgsConstructor
public class FileService {

    private final IpAddressService ipAddressService;
    private final AuditService auditService;
    final String delimiter = "\\|";

    @SneakyThrows
    public byte[] parse(MultipartFile file, String ipAddress, long startTime) {
        var ip = ipAddressService.validateIpAddress(ipAddress, startTime);

        InputStream inputStream = file.getInputStream();
        var list = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                .lines()
                .filter(line -> !isEmpty(line))
                .map(line -> getEntry(line, ip, startTime))
                .map(FileService::getJsonObject)
                .toList();

        JSONArray array = new JSONArray();
        array.addAll(list);
        auditService.add(HttpStatus.OK.value(), ip, startTime);
        return array.toString().getBytes();
    }

    private Entry getEntry(String line, IpAddress ip, long startTime) {
        var values = line.split(delimiter);
        if (values.length != FileValidation.FILE_LENGTH.value) {
            auditService.add(HttpStatus.BAD_REQUEST.value(), ip, startTime);
            throw new MalformedFileException("The file has missing columns");
        }

        var fullName = values[FileColumn.NAME.value];
        if (fullName.split(" ").length < FileValidation.FULL_NAME.value) {
            auditService.add(HttpStatus.BAD_REQUEST.value(), ip, startTime);
            throw new MalformedFileException("Full name " + fullName + " is malformed");
        }
        var transport = values[FileColumn.TRANSPORT.value];
        var topSpeed = Double.parseDouble(values[FileColumn.TOP_SPEED.value]);

        return new Entry(fullName, transport, topSpeed);
    }

    public static JSONObject getJsonObject(Entry entry) {
        var map = new HashMap<>();
        map.put("name", entry.getName());
        map.put("transport", entry.getTransport());
        map.put("topSpeed", entry.getTopSpeed());
        return new JSONObject(map);
    }
}
