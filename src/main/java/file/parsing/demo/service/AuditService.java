package file.parsing.demo.service;

import file.parsing.demo.model.Audit;
import file.parsing.demo.model.IpAddress;
import file.parsing.demo.repository.AuditRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuditService {

    private final AuditRepository auditRepository;

    @Transactional
    public void add(int httpResponseCode, IpAddress ipAddress, long startTime) {
        long timeLapsed = System.nanoTime() - startTime;
        var audit = Audit.builder()
                .uri("/parse")
                .timestamp(startTime)
                .httpResponseCode(httpResponseCode)
                .requestIpAddress(ipAddress.getQuery())
                .countryCode(ipAddress.getCountryCode())
                .requestIpProvider(ipAddress.getIsp())
                .requestTimeLapsed(timeLapsed)
                .build();
        auditRepository.save(audit);
    }

}
