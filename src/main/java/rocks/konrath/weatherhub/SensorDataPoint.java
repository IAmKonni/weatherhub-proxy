package rocks.konrath.weatherhub;

import java.time.LocalDateTime;

import io.quarkus.runtime.annotations.RegisterForReflection;

/** A single measured value at a point in time. */
@RegisterForReflection
public record SensorDataPoint(LocalDateTime time, float value) {
}
