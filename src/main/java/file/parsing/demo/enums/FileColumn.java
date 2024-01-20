package file.parsing.demo.enums;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum FileColumn {
    UUID(0),
    ID(1),
    NAME(2),
    LIKES(3),
    TRANSPORT(4),
    AVG_SPEED(5),
    TOP_SPEED(6);

    public final int value;
}
