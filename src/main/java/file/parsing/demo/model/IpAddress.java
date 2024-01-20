package file.parsing.demo.model;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class IpAddress {
    private String query;
    private String status;
    private String country;
    private String countryCode;
    private String isp;
    private String org;
    private String message;
}
