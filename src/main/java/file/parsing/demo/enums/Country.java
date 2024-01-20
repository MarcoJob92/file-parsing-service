package file.parsing.demo.enums;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum Country {
    CHINA("China"),
    SPAIN("Spain"),
    USA("United States");

    public final String value;
}
