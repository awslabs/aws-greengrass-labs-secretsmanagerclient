package com.aws.greengrass.labs;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import software.amazon.awssdk.aws.greengrass.GetSecretValueResponseHandler;
import software.amazon.awssdk.aws.greengrass.GreengrassCoreIPCClient;
import software.amazon.awssdk.aws.greengrass.model.GetSecretValueRequest;
import software.amazon.awssdk.aws.greengrass.model.GetSecretValueResponse;
import software.amazon.awssdk.aws.greengrass.model.SecretValue;
import software.amazon.awssdk.aws.greengrass.model.UnauthorizedError;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.concurrent.CompletableFuture;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

class SecretsTest {
    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();

    private GreengrassCoreIPCClient client;
    private GetSecretValueRequest existingSecret;
    private GetSecretValueRequest missingSecret;
    private GetSecretValueResponse existingResponse;
    private GetSecretValueResponseHandler handler;
    @BeforeEach
    public void setup() {
        System.setOut(new PrintStream(outputStreamCaptor));
        existingSecret = new GetSecretValueRequest();
        existingSecret.setSecretId("test");
        missingSecret = new GetSecretValueRequest();
        missingSecret.setSecretId("null");
        client = mock(GreengrassCoreIPCClient.class);
        handler = mock(GetSecretValueResponseHandler.class);
        existingResponse = new GetSecretValueResponse();
        existingResponse.setSecretId("test");
        SecretValue sv = new SecretValue();
        sv.setSecretString("Hello");
        existingResponse.setSecretValue(sv);
        when(handler.getResponse()).thenReturn(CompletableFuture.completedFuture(existingResponse));

        when(client.getSecretValue(existingSecret, Optional.empty())).thenReturn(handler);
        when(client.getSecretValue(missingSecret, Optional.empty())).thenReturn(handler);
    }

    @Test
    void getSecretValueTest() {
        GetSecretValueResponseHandler handler = Secret.getSecretValue(client, "test");
        CompletableFuture<GetSecretValueResponse> futureResponse =
                handler.getResponse();
        try {
            GetSecretValueResponse response = futureResponse.get(10, TimeUnit.SECONDS);
            response.getSecretValue().postFromJson();
            String secretString = response.getSecretValue().getSecretString();
            assertEquals("Hello", secretString);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Test
    void getMissingSecretValueTest() {

        GetSecretValueResponseHandler handler = Secret.getSecretValue(client, "null");
        CompletableFuture<GetSecretValueResponse> futureResponse =
                handler.getResponse();
        try {
            GetSecretValueResponse response = futureResponse.get(10, TimeUnit.SECONDS);
            response.getSecretValue().postFromJson();
            String secretString = response.getSecretValue().getSecretString();
            assertEquals("Hello", secretString);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void getSecretTest() {
        try {
            String s = Secret.get(client, "test");
            assertEquals( "Hello", s);
        } catch (ExecutionException | TimeoutException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    void getSecretTestUnauthorized() {
        System.setErr(new PrintStream(outputStreamCaptor));
        UnauthorizedError ex = new UnauthorizedError();
        when(handler.getResponse()).thenReturn(CompletableFuture.failedFuture(ex));
        try {
            Secret.get(client, "test");
            assertEquals( "Unauthorized error while retrieving secret: test", outputStreamCaptor.toString().trim());
        } catch (ExecutionException | TimeoutException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    void getSecretTestTimeout() {
        System.setErr(new PrintStream(outputStreamCaptor));
        TimeoutException ex = new TimeoutException();
        when(handler.getResponse()).thenReturn(CompletableFuture.failedFuture(ex));
        try {
            Secret.get(client, "test");
            assertEquals( "Timeout occurred while retrieving secret: test", outputStreamCaptor.toString().trim());
        } catch (ExecutionException | TimeoutException| InterruptedException e ) {
            e.printStackTrace();
        }
    }

}