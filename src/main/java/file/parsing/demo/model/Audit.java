package file.parsing.demo.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Audit {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;
	private String uri;
	private long timestamp;
	private int httpResponseCode;
	private String requestIpAddress;
	private String countryCode;
	private String requestIpProvider;
	private long requestTimeLapsed;

}
