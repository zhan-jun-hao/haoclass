package com.haoclass.common.query;

import jakarta.validation.constraints.AssertTrue;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
public class TimeRangePageQuery extends PageQuery {

    private LocalDateTime createTimeStart;

    private LocalDateTime createTimeEnd;

    @AssertTrue(message = "开始时间不能晚于结束时间")
    public boolean isCreateTimeRangeValid() {
        return createTimeStart == null
                || createTimeEnd == null
                || !createTimeStart.isAfter(createTimeEnd);
    }
}