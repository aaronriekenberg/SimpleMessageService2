package org.aaron.sms.util;

import java.time.Duration;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public class DurationUtils {

    private DurationUtils() {

    }

    public static Duration checkNotNullAndPositive(Duration duration, String durationName) {
        checkNotNull(duration, "%s is null", durationName);
        checkArgument(!duration.isNegative(), "%s is negative", durationName);
        checkArgument(!duration.isZero(), "%s is zero", durationName);
        return duration;
    }
}
