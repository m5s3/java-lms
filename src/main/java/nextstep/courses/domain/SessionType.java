package nextstep.courses.domain;

import java.util.Arrays;

public enum SessionType {

    PAID("유료강의"),

    FREE("무료강의");

    SessionType(String type) {
        this.type = type;
    }

    private final String type;

    public boolean isPaid() {
        return this == PAID;
    }

    public static SessionType findType(String type) {
        return Arrays.stream(values())
                .filter(sessionType -> sessionType.name().toLowerCase().equals(type))
                .findAny()
                .orElseThrow(IllegalArgumentException::new);
    }
}
