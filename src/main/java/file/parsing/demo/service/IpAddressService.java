package file.parsing.demo.service;

import file.parsing.demo.client.IpApiClient;
import file.parsing.demo.enums.Country;
import file.parsing.demo.enums.Isp;
import file.parsing.demo.exception.InvalidIpAddressException;
import file.parsing.demo.exception.IpAddressException;
import file.parsing.demo.model.IpAddress;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class IpAddressService {

    private final IpApiClient ipApiClient;
    private final AuditService auditService;

    public IpAddress validateIpAddress(String ipAddress, long startTime) {
        var ipObj = ipApiClient.getIpAddress(ipAddress);

        var status = ipObj.getStatus();
        if (!status.equals("success")) {
            auditService.add(HttpStatus.FORBIDDEN.value(), ipObj, startTime);
            throw new InvalidIpAddressException(ipObj.getMessage());
        }

        var country = ipObj.getCountry();
        if (country.contains(Country.CHINA.value) ||
            country.contains(Country.SPAIN.value) ||
            country.contains(Country.USA.value)) {
                auditService.add(HttpStatus.FORBIDDEN.value(), ipObj, startTime);
                throw new IpAddressException("HTTP requests from this country are not allowed");
        }

        String isp = ipObj.getIsp(), org = ipObj.getOrg();
        if ((isp.contains(Isp.AWS_ISP.value) && org.contains(Isp.AWS_ORG.value)) ||
            (isp.contains(Isp.GCP_ISP.value) && org.contains(Isp.GCP_ORG.value)) ||
            (isp.contains(Isp.AZURE_ISP.value) && org.contains(Isp.AZURE_ORG.value))) {
                auditService.add(HttpStatus.FORBIDDEN.value(), ipObj, startTime);
                throw new IpAddressException("HTTP requests from this ISP/Data Center are not allowed");
        }

        return ipObj;
    }
}
