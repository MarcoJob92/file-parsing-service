package file.parsing.demo.enums;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum FileValidation {
    FULL_NAME(2),
    FILE_LENGTH(7);

    public final int value;
}
