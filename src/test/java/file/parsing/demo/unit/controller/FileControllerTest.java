package file.parsing.demo.unit.controller;

import file.parsing.demo.controller.FileController;
import file.parsing.demo.model.Entry;
import file.parsing.demo.service.FileService;
import org.json.JSONException;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import java.nio.charset.StandardCharsets;

import static file.parsing.demo.service.FileService.getJsonObject;
import static file.parsing.demo.unit.service.FileServiceTest.getFile;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.skyscreamer.jsonassert.JSONAssert.assertEquals;

@ExtendWith(MockitoExtension.class)
public class FileControllerTest {

	private FileController fileController;
	@Mock
	private FileService fileService;

	@BeforeEach
	void setUp() {
		fileController = new FileController(fileService);
	}

	@Test
	void should_parseFile() throws JSONException, ParseException {
		MockMultipartFile file = getFile();

		var array = new JSONArray();
		array.add(getJsonObject(new Entry("John Smith", "Rides A Bike", 12.1)));
		array.add(getJsonObject(new Entry("Mike Smith", "Drives a SUV", 95.5)));
		array.add(getJsonObject(new Entry("Jenny Walters", "Rides A Bike", 15.3)));

		when(fileService.parse(eq(file), any(), anyLong())).thenReturn(array.toString().getBytes());

		var byteStream = fileController.parseFile(file, null);
		var jsonString = new String(byteStream, StandardCharsets.UTF_8);
		var jsonArray = (JSONArray) new JSONParser().parse(jsonString);

		assertEquals("{\"name\":\"John Smith\",\"transport\":\"Rides A Bike\",\"topSpeed\":12.1}", jsonArray.getFirst().toString(), false);
		assertEquals("{\"name\":\"Mike Smith\",\"transport\":\"Drives a SUV\",\"topSpeed\":95.5}", jsonArray.get(1).toString(), false);
		assertEquals("{\"name\":\"Jenny Walters\",\"transport\":\"Rides A Bike\",\"topSpeed\":15.3}", jsonArray.get(2).toString(), false);
	}
}
