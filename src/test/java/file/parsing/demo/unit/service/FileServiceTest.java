package file.parsing.demo.unit.service;

import file.parsing.demo.exception.MalformedFileException;
import file.parsing.demo.model.Entry;
import file.parsing.demo.service.AuditService;
import file.parsing.demo.service.FileService;
import file.parsing.demo.service.IpAddressService;
import org.json.JSONException;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import java.nio.charset.StandardCharsets;

import static file.parsing.demo.service.FileService.getJsonObject;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.skyscreamer.jsonassert.JSONAssert.assertEquals;

@ExtendWith(MockitoExtension.class)
public class FileServiceTest {

    private FileService fileService;
    @Mock
    private IpAddressService ipAddressService;
    @Mock
    private AuditService auditService;

    @BeforeEach
    void setUp() {
        fileService = new FileService(ipAddressService, auditService);
    }

    @Test
    void should_parseFile() throws ParseException, JSONException {
        var byteStream = fileService.parse(getFile(), any(), anyLong());

        String jsonString = new String(byteStream, StandardCharsets.UTF_8);
        JSONArray jsonArray = (JSONArray) new JSONParser().parse(jsonString);

        verify(ipAddressService).validateIpAddress(any(), anyLong());
        verify(auditService).add(eq(HttpStatus.OK.value()), any(), anyLong());
        assertEquals("{\"name\":\"John Smith\",\"transport\":\"Rides A Bike\",\"topSpeed\":12.1}", jsonArray.getFirst().toString(), true);
        assertEquals("{\"name\":\"Mike Smith\",\"transport\":\"Drives a SUV\",\"topSpeed\":95.5}", jsonArray.get(1).toString(), false);
        assertEquals("{\"name\":\"Jenny Walters\",\"transport\":\"Rides A Scooter\",\"topSpeed\":15.3}", jsonArray.get(2).toString(), false);
    }

    @Test
    void should_parseFile_when_fileContainsDelimeters() throws ParseException, JSONException {
        var byteStream = fileService.parse(getFileWithDelimiters(), any(), anyLong());

        String jsonString = new String(byteStream, StandardCharsets.UTF_8);
        JSONArray jsonArray = (JSONArray) new JSONParser().parse(jsonString);

        verify(ipAddressService).validateIpAddress(any(), anyLong());
        verify(auditService).add(eq(HttpStatus.OK.value()), any(), anyLong());
        assertEquals("{\"name\":\"John Smith\",\"transport\":\"Rides A Bike\",\"topSpeed\":12.1}", jsonArray.getFirst().toString(), true);
        assertEquals("{\"name\":\"Mike Smith\",\"transport\":\"Drives a SUV\",\"topSpeed\":95.5}", jsonArray.get(1).toString(), false);
        assertEquals("{\"name\":\"Jenny Walters\",\"transport\":\"Rides A Scooter\",\"topSpeed\":15.3}", jsonArray.get(2).toString(), false);
    }

    @Test
    void should_throwMalformedFileException_when_missingColumns() {
        var exception = assertThrows(MalformedFileException.class, () -> fileService.parse(getFileWithWrongColumns(), "", 0));
        assertTrue(exception.getMessage().contains("The file has missing columns"));
    }

    @Test
    void should_throwMalformedFileException_when_malformedFullName() {
        var exception = assertThrows(MalformedFileException.class, () -> fileService.parse(getFileWithMalformedFullName(), "", 0));
        assertTrue(exception.getMessage().contains("Full name JohnSmith is malformed"));
    }

    @Test
    void should_throwNumberFormatException_when_malformedNumber() {
        var exception = assertThrows(NumberFormatException.class, () -> fileService.parse(getFileWithMalformedNumber(), "", 0));
        assertTrue(exception.getMessage().contains("For input string: \"NotANumber\""));
    }

    @Test
    void should_getJsonObject() throws JSONException {
        var entry = new Entry("John Smith", "Rides A Bike", 12.1);
        var json = getJsonObject(entry);

        assertEquals("{\"name\":\"John Smith\",\"transport\":\"Rides A Bike\",\"topSpeed\":12.1}", json.toString(), true);
    }

    public static MockMultipartFile getFile() {
        String mockStr = """
                18148426-89e1-11ee-b9d1-0242ac120002|1X1D14|John Smith|Likes Apricots|Rides A Bike|6.2|12.1
                3ce2d17b-e66a-4c1e-bca3-40eb1c9222c7|2X2D24|Mike Smith|Likes Grape|Drives a SUV|35.0|95.5
                1afb6f5d-a7c2-4311-a92d-974f3180ff5e|3X3D35|Jenny Walters|Likes Avocados|Rides A Scooter|8.5|15.3""";
        return getMockMultipartFile(mockStr);
    }

    public static MockMultipartFile getFileWithWrongColumns() {
        String mockStr = """
                1X1D14|John Smith|Likes Apricots|Rides A Bike|6.2|12.1
                2X2D24|Mike Smith|Likes Grape|Drives a SUV|35.0|95.5
                3X3D35|Jenny Walters|Likes Avocados|Rides A Scooter|8.5|15.3""";
        return getMockMultipartFile(mockStr);
    }

    private static MockMultipartFile getFileWithMalformedFullName() {
        String mockStr = """
                18148426-89e1-11ee-b9d1-0242ac120002|1X1D14|JohnSmith|Likes Apricots|Rides A Bike|6.2|12.1
                3ce2d17b-e66a-4c1e-bca3-40eb1c9222c7|2X2D24|Mike Smith|Likes Grape|Drives a SUV|35.0|95.5
                1afb6f5d-a7c2-4311-a92d-974f3180ff5e|3X3D35|Jenny Walters|Likes Avocados|Rides A Scooter|8.5|15.3""";
        return getMockMultipartFile(mockStr);
    }

    public static MockMultipartFile getFileWithMalformedNumber() {
        String mockStr = """
                18148426-89e1-11ee-b9d1-0242ac120002|1X1D14|John Smith|Likes Apricots|Rides A Bike|6.2|NotANumber
                3ce2d17b-e66a-4c1e-bca3-40eb1c9222c7|2X2D24|Mike Smith|Likes Grape|Drives a SUV|35.0|95.5
                1afb6f5d-a7c2-4311-a92d-974f3180ff5e|3X3D35|Jenny Walters|Likes Avocados|Rides A Scooter|8.5|15.3""";
        return getMockMultipartFile(mockStr);
    }

    public static MockMultipartFile getFileWithDelimiters() {
        String mockStr = """
                18148426-89e1-11ee-b9d1-0242ac120002|1X1D14|John Smith|Likes Apricots|Rides A Bike|6.2|12.1 \n
                3ce2d17b-e66a-4c1e-bca3-40eb1c9222c7|2X2D24|Mike Smith|Likes Grape|Drives a SUV|35.0|95.5 \n
                1afb6f5d-a7c2-4311-a92d-974f3180ff5e|3X3D35|Jenny Walters|Likes Avocados|Rides A Scooter|8.5|15.3
                """;
        return getMockMultipartFile(mockStr);
    }

    private static MockMultipartFile getMockMultipartFile(String string) {
        return new MockMultipartFile(
                "file",
                "EntryFile.txt",
                MediaType.TEXT_PLAIN_VALUE,
                string.getBytes()
        );
    }
}
