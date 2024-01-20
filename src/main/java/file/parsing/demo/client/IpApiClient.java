package file.parsing.demo.client;

import file.parsing.demo.model.IpAddress;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ipApiClient", url = "${ipApi.url}")
public interface IpApiClient {
    @GetMapping(value="/json/{query}")
    IpAddress getIpAddress(@PathVariable(value = "query") String ipAddress);
}
