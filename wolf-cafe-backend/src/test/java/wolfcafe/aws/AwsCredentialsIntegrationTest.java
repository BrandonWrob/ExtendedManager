package wolfcafe.aws;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sts.StsClient;
import software.amazon.awssdk.services.sts.model.GetCallerIdentityRequest;

/**
 * test that it connects to Springboot successfully 
 */
@SpringBootTest
public class AwsCredentialsIntegrationTest {

    private String region = System.getenv("AWS_REGION");

    // if this test fails then your regions are not properly stored in env variables
    @Test
    void testAwsEnvVariablesAreSet() {
        String accessKeyId = System.getenv("AWS_ACCESS_KEY_ID");
        String secretAccessKey = System.getenv("AWS_SECRET_ACCESS_KEY");
        assertNotNull(accessKeyId, "AWS_ACCESS_KEY_ID environment variable is not set");
        assertNotNull(secretAccessKey, "AWS_SECRET_ACCESS_KEY environment variable is not set");
        assertNotNull(region, "aws.region property is not set");
    }
    
    @Test
    void testAwsStsCallSucceeds() {
        try (StsClient stsClient = StsClient.builder()
                .region(Region.of(region))
                .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
                .build()) {

            var response = stsClient.getCallerIdentity(GetCallerIdentityRequest.builder().build());

            assertNotNull(response.account());
            assertNotNull(response.arn());
            assertNotNull(response.userId());
        } catch (Exception e) {
            fail("AWS STS call failed: " + e.getMessage());
        }
    }

}

