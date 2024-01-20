package file.parsing.demo.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Entry {
    private String name;
    private String transport;
    private double topSpeed;
}
