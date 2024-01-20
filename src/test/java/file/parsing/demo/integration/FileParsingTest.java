package file.parsing.demo.integration;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import file.parsing.demo.DemoApplication;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import static file.parsing.demo.unit.service.FileServiceTest.*;
import static java.util.Objects.requireNonNull;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ContextConfiguration
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, classes = DemoApplication.class)
public class FileParsingTest {

    @Autowired
    MockMvc mockMvc;

    private WireMockServer wireMockServer;

    @BeforeEach
    void setUp() {
        wireMockServer = new WireMockServer(9999);
        wireMockServer.start();
    }

    @AfterEach
    void cleanup() {
        wireMockServer.stop();
    }

    @Test
    public void should_parseFile() throws Exception {
        mockGet();

        var result = mockMvc.perform(
                multipart("/parse")
                        .file(getFile())
                        .header("X-FORWARDED-FOR", "1.2.3.4")
                )
                .andExpect(status().isOk());

        var jsonString = result.andReturn().getResponse().getContentAsString();
        var jsonArray = (JSONArray) new JSONParser().parse(jsonString);

        JSONAssert.assertEquals("{\"name\":\"John Smith\",\"transport\":\"Rides A Bike\",\"topSpeed\":12.1}", jsonArray.getFirst().toString(), false);
        JSONAssert.assertEquals("{\"name\":\"Mike Smith\",\"transport\":\"Drives a SUV\",\"topSpeed\":95.5}", jsonArray.get(1).toString(), false);
        JSONAssert.assertEquals("{\"name\":\"Jenny Walters\",\"transport\":\"Rides A Scooter\",\"topSpeed\":15.3}", jsonArray.get(2).toString(), false);
    }

    @Test
    public void should_blockHttpRequest_when_ipAddressFromUSA() throws Exception {
        mockGet("/json/2.3.4.5", """
                        { "query":"2.3.4.5",
                          "status":"success",
                          "country":"United States",
                          "isp":"Test",
                          "org":"Test" }""");

        var result = mockMvc.perform(
                multipart("/parse")
                        .file(getFile())
                        .header("X-FORWARDED-FOR", "2.3.4.5")
                )
                .andReturn();

        assertEquals(HttpStatus.FORBIDDEN.value(), result.getResponse().getStatus());
        assertEquals("HTTP requests from this country are not allowed", requireNonNull(result.getResolvedException()).getMessage());
    }

    @Test
    public void should_blockHttpRequest_when_ipAddressFromAWS() throws Exception {
        mockGet("/json/3.4.5.6","""
                        { "query":"3.4.5.6",
                          "status":"success",
                          "country":"Australia",
                          "isp":"Amazon Data Services Ireland Ltd",
                          "org":"AWS EC2 (us-gov-west-1)" }""");

        var result = mockMvc.perform(
                multipart("/parse")
                        .file(getFile())
                        .header("X-FORWARDED-FOR", "3.4.5.6")
                )
                .andReturn();

        assertEquals(HttpStatus.FORBIDDEN.value(), result.getResponse().getStatus());
        assertEquals("HTTP requests from this ISP/Data Center are not allowed", requireNonNull(result.getResolvedException()).getMessage());
    }

    @Test
    public void should_returnBadRequestCode_when_fileIsMalformed() throws Exception {
        mockGet();

        var result = mockMvc.perform(
                        multipart("/parse")
                                .file(getFileWithWrongColumns())
                                .header("X-FORWARDED-FOR", "1.2.3.4")
                )
                .andReturn();

        assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus());
        assertEquals("The file has missing columns", requireNonNull(result.getResolvedException()).getMessage());
    }

    private void mockGet() {
        var responseBody = """
                   { "query":"1.2.3.4",
                     "status":"success",
                     "country":"Australia",
                     "isp":"Test ISP",
                     "org":"Test Org" }""";

        mockGet("/json/1.2.3.4", responseBody);
    }

    private void mockGet(String uri, String responseBody) {
        wireMockServer.stubFor(WireMock.get(WireMock.urlEqualTo(uri))
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", APPLICATION_JSON_VALUE)
                        .withBody(responseBody)
                ));
    }

}
