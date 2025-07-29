package wolfcafe.logs;
import ch.qos.logback.core.AppenderBase;
import ch.qos.logback.classic.spi.ILoggingEvent;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cloudwatchlogs.CloudWatchLogsClient;
import software.amazon.awssdk.services.cloudwatchlogs.model.*;

import java.time.Instant;
import java.util.Collections;

/**
 * Class used to create logs to send to AWS Cloud watch Logs
 * 
 * 
 * To add loggers to your class, you don't need to call this class directly since Sprint Boot's Logback system discovers
 *  it automatically, so you just have to do the following: 
 
1) Import these libraries: import org.slf4j.Logger; import org.slf4j.LoggerFactory;
2) Create a logging instance with: private static final Logger log = LoggerFactory.getLogger(OrderService.class);
3) Use the following commands

log.info(...) – useful application info

log.debug(...) – detailed diagnostics (off in prod usually)

log.error(...) – for exceptions or failed operations

log.warn(...) – non-breaking issues or edge cases

Ex: 
public void placeOrder(String userId) {
	log.info("Placing order for user {}", userId);

	try {
		// order logic...
		log.debug("Order details: {...}");

	} catch (Exception e) {
		log.error("Failed to place order for user {}", userId, e);
	}
}

Since all login utilizes the same functionality as the test case here, if the logs work for it then you are set!
 */
public class CloudWatchAppender extends AppenderBase<ILoggingEvent> {

	/** AWS CloudWatch Logs client instance */
    private CloudWatchLogsClient cloudWatchLogsClient;

    /** The name of the CloudWatch log group to send logs to */
    private String logGroupName = "SpringBootLogs";

    /** The name of the CloudWatch log stream to send logs to */
    private String logStreamName = "LocalStream";

    /** The AWS region for the CloudWatch service, read from environment */
    private String region = System.getenv("AWS_REGION");

    /** Upload sequence token required by CloudWatch Logs */
    private String sequenceToken;

    /**
     * Initializes the CloudWatch Logs client and ensures that the log group and log stream exist.
     */
    @Override
    public void start() {
        cloudWatchLogsClient = CloudWatchLogsClient.builder()
            .region(Region.of(region))
            .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
            .build();

        ensureLogGroupAndStreamExist();
        super.start();
    }

    /**
     * Sends a log event to AWS CloudWatch Logs.
     *
     * @param event the logging event to append
     */
    @Override
    protected void append(ILoggingEvent event) {
    	System.out.println("📤 Logging to CloudWatch: " + event.getFormattedMessage());
        try {
            InputLogEvent logEvent = InputLogEvent.builder()
                .message(event.getFormattedMessage())
                .timestamp(Instant.now().toEpochMilli())
                .build();

            PutLogEventsRequest request = PutLogEventsRequest.builder()
                .logGroupName(logGroupName)
                .logStreamName(logStreamName)
                .logEvents(Collections.singletonList(logEvent))
                .sequenceToken(sequenceToken)
                .build();

            PutLogEventsResponse response = cloudWatchLogsClient.putLogEvents(request);
            sequenceToken = response.nextSequenceToken();

        } catch (Exception e) {
            System.err.println("Failed to send log to CloudWatch: " + e.getMessage());
        }
    }

    /**
     * Ensures the specified log group and stream exist in CloudWatch Logs.
     * If they do not exist, they will be created.
     * Also retrieves the current upload sequence token for the stream.
     */
    private void ensureLogGroupAndStreamExist() {
        try {
            cloudWatchLogsClient.createLogGroup(CreateLogGroupRequest.builder()
                .logGroupName(logGroupName)
                .build());
        } catch (ResourceAlreadyExistsException ignored) {
            // Log group already exists, do nothing
        }

        try {
            cloudWatchLogsClient.createLogStream(CreateLogStreamRequest.builder()
                .logGroupName(logGroupName)
                .logStreamName(logStreamName)
                .build());
        } catch (ResourceAlreadyExistsException ignored) {
            // Log stream already exists, do nothing
        }

        DescribeLogStreamsResponse streamsResponse = cloudWatchLogsClient.describeLogStreams(
            DescribeLogStreamsRequest.builder()
                .logGroupName(logGroupName)
                .logStreamNamePrefix(logStreamName)
                .build()
        );

        if (!streamsResponse.logStreams().isEmpty()) {
            sequenceToken = streamsResponse.logStreams().get(0).uploadSequenceToken();
        }
    }
}