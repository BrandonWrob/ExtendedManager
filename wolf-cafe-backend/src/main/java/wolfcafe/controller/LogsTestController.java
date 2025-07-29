package wolfcafe.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Used to inform us if logs were properly sent, we can only use status code to tell (since msg is in AWS)
 */
@RestController
public class LogsTestController {

    private static final Logger log = LoggerFactory.getLogger(LogsTestController.class);

    @GetMapping("/api/log-test")
    public String testLogging() {
        log.info("✅ Test log: CloudWatch logging works!");
        return "Log sent to CloudWatch (check AWS Console)";
    }
}
