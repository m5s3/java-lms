package nextstep.courses.domain;

import java.util.Arrays;

public enum SessionState {

    READY("준비중"),
    RECRUITING("모집중"),
    END("종료");

    SessionState(String state) {
        this.state = state;
    }

    private final String state;

    public boolean isAllowed() {
        return this == RECRUITING;
    }

    public String getState() {
        return state;
    }

    public static SessionState findState(String findState) {
        return Arrays.stream(values())
                .filter(state -> state.name().toLowerCase().equals(findState))
                .findAny()
                .orElseThrow(IllegalArgumentException::new);
    }
}
