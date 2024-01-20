package file.parsing.demo.unit.service;

import file.parsing.demo.model.IpAddress;
import file.parsing.demo.repository.AuditRepository;
import file.parsing.demo.service.AuditService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class AuditServiceTest {

    @Autowired
    private AuditService auditService;
    @Autowired
    private AuditRepository auditRepository;

    @Test
    void should_saveAuditData() {
        var mockedIpAddressObj = IpAddress.builder()
                .query("2.3.4.5")
                .isp("ISP")
                .countryCode("ES")
                .build();

        var list = auditRepository.findAll();
        assertEquals(0, list.size());

        auditService.add(200, mockedIpAddressObj, 0);

        list = auditRepository.findAll();
        assertEquals(1, list.size());
        assertEquals("/parse", list.getFirst().getUri());
        assertEquals("ES", list.getFirst().getCountryCode());
        assertEquals(200, list.getFirst().getHttpResponseCode());
        assertEquals("2.3.4.5", list.getFirst().getRequestIpAddress());
        assertEquals("ISP", list.getFirst().getRequestIpProvider());

        auditService.add(400, mockedIpAddressObj, 0);

        list = auditRepository.findAll();
        assertEquals(2, list.size());
    }
}
