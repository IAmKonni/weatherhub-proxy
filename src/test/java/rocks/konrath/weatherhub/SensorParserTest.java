package rocks.konrath.weatherhub;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

class SensorParserTest {

	private final SensorParser parser = new SensorParser();

	private Map<String, Sensor> parseOverview() {
		return parser.parseAllSensorData(TestResources.read("overview.html")).stream()
				.collect(Collectors.toMap(Sensor::getId, Function.identity()));
	}

	@Test
	void parsesAllSupportedSensorsAndSkipsUnknownOnes() {
		Map<String, Sensor> sensors = parseOverview();
		assertEquals(4, sensors.size());
		assertTrue(sensors.keySet().containsAll(List.of("0305E29CC32E", "0320B7FB6650", "085148DB8CA0", "115CCE323FAD")));
	}

	@Test
	void parsesThermalSensorIncludingNegativeTemperature() {
		ThermalSensor sensor = assertInstanceOf(ThermalSensor.class, parseOverview().get("0305E29CC32E"));
		assertEquals("Außen", sensor.getLabel());
		assertEquals(LocalDateTime.of(2022, 9, 23, 9, 24, 26), sensor.getTime());
		assertEquals(-6.5f, sensor.getTemperature());
		assertEquals(87, sensor.getHumidity());
		assertEquals("rocks.konrath.weatherhub.ThermalSensor", sensor.getSensorClass());
	}

	@Test
	void sensorWithoutDataFallsBackToZero() {
		ThermalSensor sensor = assertInstanceOf(ThermalSensor.class, parseOverview().get("0320B7FB6650"));
		assertEquals(0f, sensor.getTemperature());
		assertEquals(0, sensor.getHumidity());
	}

	@Test
	void parsesRainSensor() {
		RainSensor sensor = assertInstanceOf(RainSensor.class, parseOverview().get("085148DB8CA0"));
		assertEquals("Regensensor", sensor.getLabel());
		assertEquals(0.5f, sensor.getRainfall());
	}

	@Test
	void parsesMultiSensor() {
		ThermalMultiSensor sensor = assertInstanceOf(ThermalMultiSensor.class, parseOverview().get("115CCE323FAD"));
		assertEquals("Wohnen", sensor.getLabel());
		assertEquals(22.8f, sensor.getTemperature());
		assertEquals(60, sensor.getHumidity());
		assertEquals(23.1f, sensor.getTemperature2());
		assertEquals(46, sensor.getHumidity2());
		assertEquals(22.7f, sensor.getTemperature3());
		assertEquals(59, sensor.getHumidity3());
		assertEquals(21.7f, sensor.getTemperature4());
		assertEquals(49, sensor.getHumidity4());
	}

	@Test
	void emptyPageYieldsNoSensors() {
		assertTrue(parser.parseAllSensorData("").isEmpty());
	}

	@Test
	void parsesHistory() {
		SensorHistory history = parser.parseSensorHistoryData(TestResources.read("history.html"),
				new SensorHistory("0305E29CC32E"));

		assertEquals("0305E29CC32E", history.getId());
		assertEquals("Außen", history.getLabel());
		assertEquals(2, history.getDataPointStreams().size());

		SensorDataPointStream temperature = history.getDataPointStreams().get(0);
		assertEquals("Temperatur", temperature.getName());
		assertEquals("C", temperature.getUnit());
		assertEquals(List.of(
				new SensorDataPoint(LocalDateTime.of(2022, 9, 23, 9, 24, 26), -6.5f),
				new SensorDataPoint(LocalDateTime.of(2022, 9, 23, 9, 17, 26), 7.1f)),
				temperature.getValues());

		SensorDataPointStream humidity = history.getDataPointStreams().get(1);
		assertEquals("Luftfeuchte", humidity.getName());
		assertEquals("%", humidity.getUnit());
		assertEquals(1, humidity.getValues().size(), "unparsable values are skipped");
	}

	@Test
	void emptyHistoryPageYieldsEmptyHistory() {
		SensorHistory history = parser.parseSensorHistoryData("", new SensorHistory("X"));
		assertEquals("", history.getLabel());
		assertTrue(history.getDataPointStreams().isEmpty());
	}
}
