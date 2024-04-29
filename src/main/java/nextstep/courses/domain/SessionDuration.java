package nextstep.courses.domain;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class SessionDuration {

    private final LocalDateTime startDate;
    private final LocalDateTime endDate;

    public SessionDuration(LocalDateTime startDate, LocalDateTime endDate) {
        validate(startDate, endDate);
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public Map<String , LocalDateTime> getSessionDuration() {
        HashMap<String, LocalDateTime> sessionDuration = new HashMap<>();
        sessionDuration.put("startDate", startDate);
        sessionDuration.put("endDate", endDate);
        return sessionDuration;
    }

    private void validate(LocalDateTime startDate, LocalDateTime endDate) {
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("시작일은 종료일 이전이어야 합니다.");
        }
    }
}
