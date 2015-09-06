package org.aaron.sms.util;


import com.google.common.base.Preconditions;

import java.time.Duration;

public class DurationUtils {

    private DurationUtils() {

    }

    public static Duration checkNotNullAndPositive(Duration duration, String durationName) {
        Preconditions.checkNotNull(duration, "%s is null", durationName);
        Preconditions.checkArgument(!duration.isNegative(), "%s is negative", durationName);
        Preconditions.checkArgument(!duration.isZero(), "%s is zero", durationName);
        return duration;
    }
}
