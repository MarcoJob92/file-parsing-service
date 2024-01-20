package file.parsing.demo.unit.service;

import file.parsing.demo.client.IpApiClient;
import file.parsing.demo.exception.InvalidIpAddressException;
import file.parsing.demo.exception.IpAddressException;
import file.parsing.demo.model.IpAddress;
import file.parsing.demo.service.AuditService;
import file.parsing.demo.service.IpAddressService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
public class IpAddressServiceTest {

    private IpAddressService ipAddressService;
    @Mock
    private IpApiClient ipApiClient;
    @Mock
    private AuditService auditService;
    private final String ip = "1.2.3.4";

    @BeforeEach
    void setUp() {
        ipAddressService = new IpAddressService(ipApiClient, auditService);
    }

    @Test
    void should_getIpAddress() {
        var mockedIpAddressObj = IpAddress.builder()
                .query(ip)
                .status("success")
                .isp("ISP")
                .org("ORG")
                .country("Country")
                .countryCode("CountryCode")
                .build();

        when(ipApiClient.getIpAddress(ip)).thenReturn(mockedIpAddressObj);

        var result = ipAddressService.validateIpAddress(ip, 0);

        assertEquals(mockedIpAddressObj, result);
    }

    @Test
    void should_throwIpAddressException_when_countryIsSpain() {
        var mockedIpAddressObj = IpAddress.builder()
                .query(ip)
                .status("success")
                .isp("ISP")
                .org("ORG")
                .country("Spain")
                .countryCode("ES")
                .build();

        when(ipApiClient.getIpAddress(ip)).thenReturn(mockedIpAddressObj);

        var exception = assertThrows(IpAddressException.class, () -> ipAddressService.validateIpAddress(ip, 0));
        assertTrue(exception.getMessage().contains("HTTP requests from this country are not allowed"));
        verify(auditService).add(HttpStatus.FORBIDDEN.value(), mockedIpAddressObj, 0);
        verifyNoMoreInteractions(auditService);
    }

    @Test
    void should_throwIpAddressException_when_ispIsAzure() {
        var mockedIpAddressObj = IpAddress.builder()
                .query(ip)
                .status("success")
                .isp("Microsoft Corporation")
                .org("Microsoft Azure Cloud (westeurope)")
                .country("Country")
                .countryCode("CountryCode")
                .build();

        when(ipApiClient.getIpAddress(ip)).thenReturn(mockedIpAddressObj);

        var exception = assertThrows(IpAddressException.class, () -> ipAddressService.validateIpAddress(ip, 0));
        assertTrue(exception.getMessage().contains("HTTP requests from this ISP/Data Center are not allowed"));
        verify(auditService).add(HttpStatus.FORBIDDEN.value(), mockedIpAddressObj, 0);
        verifyNoMoreInteractions(auditService);
    }

    @Test
    void should_throwInvalidIpAddressException_when_ipAddressIsNotValid() {
        var mockedIpAddressObj = IpAddress.builder()
                .query("1.2.3")
                .status("fail")
                .message("invalid query")
                .build();

        when(ipApiClient.getIpAddress(ip)).thenReturn(mockedIpAddressObj);

        var exception = assertThrows(InvalidIpAddressException.class, () -> ipAddressService.validateIpAddress(ip, 0));
        assertTrue(exception.getMessage().contains(mockedIpAddressObj.getMessage()));
        verify(auditService).add(HttpStatus.FORBIDDEN.value(), mockedIpAddressObj, 0);
        verifyNoMoreInteractions(auditService);
    }
}
