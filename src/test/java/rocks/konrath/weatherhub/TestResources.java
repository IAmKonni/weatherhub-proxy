package rocks.konrath.weatherhub;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;

final class TestResources {

	private TestResources() {
	}

	static String read(String name) {
		try (InputStream in = TestResources.class.getResourceAsStream("/" + name)) {
			if (in == null) {
				throw new IllegalArgumentException("Missing test resource " + name);
			}
			return new String(in.readAllBytes(), StandardCharsets.UTF_8);
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}
}
