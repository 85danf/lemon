package org.danf.lemon;

import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.fluent.Request;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * An end-to-end test to verify expected behavior of all REST endpoints
 */
@RunWith(SpringRunner.class)
//Starts the test env with a random port
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class LemonAppITest {

	private static final String API_ROOT = "api/v1/words";
	private static final String ENDPOINT_COUNT = "count";
	private static final String ENDPOINT_STATISTICS = "statistics/";
	private static final String ENDPOINT_PING = "/api/v1/system/ping";

	@Autowired
	private TestRestTemplate restTemplate;

	private String rootUrl;

	@Before
	public void init() {
		rootUrl = restTemplate.getRootUri();
	}

	@Test
	public void testCounters() throws IOException {
		countWords("Hello my name is");
		assertWordCount("Hello", 1);
		assertWordCount("my", 1);
		assertWordCount("name", 1);
		assertWordCount("is", 1);
		countWords("Hello");
		assertWordCount("Hello", 2);
	}

	@Test
	public void testResetCounter() throws IOException {
		countWords("I'm a little teapot");
		assertWordCount("teapot", 1);
		Request.Delete(url(ENDPOINT_STATISTICS + "teapot"))
				.execute()
				.handleResponse(assertResponseOk());
		assertWordCount("teapot", 0);
	}

	@Test
	public void testCountFromUrl() throws IOException {
		 Request.Post(url(ENDPOINT_COUNT) + "?syncExecution=true" + "&url=" + rootUrl + ENDPOINT_PING)
				.execute()
				.handleResponse(assertResponseOk());
		 assertWordCount("PONG", 1);
	}

	@Test
	public void testCountFromFile() throws IOException {
		Path fileToRead = Files.createTempFile(getClass().getSimpleName(), Long.toString(System.currentTimeMillis()));
		try (InputStream in = getClass().getResourceAsStream("/config/application.yaml")) {
			Files.copy(in, fileToRead.toAbsolutePath(), REPLACE_EXISTING);
		}
		Request.Post(url(ENDPOINT_COUNT) + "?syncExecution=true" + "&filePath=" + fileToRead.toAbsolutePath())
				.execute()
				.handleResponse(assertResponseOk());
		assertWordCount("server:", 1);
		assertWordCount("lemondb", 1);
	}

	@Test
	public void testPing() throws IOException {
		Request.Get(rootUrl + ENDPOINT_PING)
				.execute()
				.handleResponse(assertResponseOk());
		String pingResponse = Request.Get(rootUrl + ENDPOINT_PING)
				.execute()
				.returnContent()
				.asString();
		assertThat(pingResponse).isEqualTo("PONG\n");
	}

	@Test
	public void testSwaggerEndpoint() throws IOException {
		Request.Get(rootUrl + "/swagger-ui.html")
				.execute()
				.handleResponse(assertResponseOk());
	}

	private void assertWordCount(String word, int expected) throws IOException {
		String count = Request.Get(url(ENDPOINT_STATISTICS + word))
				.execute().returnContent().asString();
		assertThat(Integer.parseInt(count)).isEqualTo(expected);
	}

	private void countWords(String words) throws IOException {
		StatusLine status = Request.Post(url(ENDPOINT_COUNT))
				.bodyString(words, null)
				.execute().returnResponse().getStatusLine();
		assertThat(status.getStatusCode()).isEqualTo(HttpStatus.SC_OK);
	}

	private ResponseHandler<?> assertResponseOk() {
		return response -> assertThat(response.getStatusLine().getStatusCode()).isEqualTo(HttpStatus.SC_OK);
	}

	private String url(String suffix) {
		return String.join("/", rootUrl, API_ROOT, suffix);
	}

}
