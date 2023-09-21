package aws.greengrass.labs;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doThrow;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import software.amazon.awssdk.aws.greengrass.GetSecretValueResponseHandler;
import software.amazon.awssdk.aws.greengrass.GreengrassCoreIPCClientV2;
import software.amazon.awssdk.aws.greengrass.model.GetSecretValueRequest;
import software.amazon.awssdk.aws.greengrass.model.GetSecretValueResponse;
import software.amazon.awssdk.aws.greengrass.model.SecretValue;
import software.amazon.awssdk.aws.greengrass.model.UnauthorizedError;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

class SecretsTest {
    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();

    private GreengrassCoreIPCClientV2 client;
    private GetSecretValueRequest existingSecret;
    private GetSecretValueRequest missingSecret;
    private GetSecretValueResponse existingResponse;
    private GetSecretValueResponse response;
    @BeforeEach
    public void setup() {
        System.setOut(new PrintStream(outputStreamCaptor));
        existingSecret = new GetSecretValueRequest();
        existingSecret.setSecretId("test");
        missingSecret = new GetSecretValueRequest();
        missingSecret.setSecretId("null");
        client = mock(GreengrassCoreIPCClientV2.class);
        response = mock(GetSecretValueResponse.class);
        existingResponse = new GetSecretValueResponse();
        existingResponse.setSecretId("test");
        SecretValue sv = new SecretValue();
        sv.setSecretString("Hello");
        existingResponse.setSecretValue(sv);
        try {
            when(client.getSecretValue(existingSecret)).thenReturn(existingResponse);
            doThrow(new InterruptedException()).when(client).getSecretValue(missingSecret);
        } catch (InterruptedException ex){
            ex.printStackTrace();
        }
    }

    @Test
    void getSecretValueTest() {

        try {
            String secretString = SecretsManagerClient.getSecret(client, "test");
            assertEquals("Hello", secretString);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Test
    void getMissingSecretValueTest() {
        try {
            String secretString = SecretsManagerClient.getSecret(client, "null");
            assertEquals("Hello", secretString);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}