package wolfcafe.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests the /api/log-test endpoint.
 * 
 * 
 * NOTE: The automated test show that logs are being sent, not the logs themself. To check the logs, do the following:
 * 1) Go to: https://console.aws.amazon.com/ and log in with the AWS credentials you're using for your app.
 * 2) Search for CloudWatch in the AWS services search bar.
 * 3) In the left sidebar, click on Logs → Log groups.
 * 4) Look for the log group name you configured in CloudWatchAppender, it should be private String logGroupName = "SpringBootLogs";
 * 
 */
@SpringBootTest(
	    classes = wolfcafe.WolfCafeApplication.class,
	    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
	)
@ActiveProfiles("localtest")
public class LogsTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void testLogEndpoint() {
        String url = "http://localhost:" + port + "/api/log-test";
        String response = restTemplate.getForObject(url, String.class);
        assertEquals("Log sent to CloudWatch (check AWS Console)", response);
    }
}
