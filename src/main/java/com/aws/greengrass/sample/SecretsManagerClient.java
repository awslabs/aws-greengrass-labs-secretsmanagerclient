package com.aws.greengrass.sample;

import com.aws.greengrass.sample.util.IPCUtils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import software.amazon.awssdk.aws.greengrass.GetSecretValueResponseHandler;
import software.amazon.awssdk.aws.greengrass.GreengrassCoreIPCClient;
import software.amazon.awssdk.aws.greengrass.model.GetSecretValueRequest;
import software.amazon.awssdk.aws.greengrass.model.GetSecretValueResponse;
import software.amazon.awssdk.aws.greengrass.model.UnauthorizedError;
import software.amazon.awssdk.eventstreamrpc.EventStreamRPCConnection;
import com.google.gson.JsonParser;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class SecretsManagerClient {

    public static final int TIMEOUT_SECONDS = 10;

    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Too few arguments. Specify the secret id");
            System.exit(1);
        }
        String secretId = args[0];

        try (EventStreamRPCConnection eventStreamRPCConnection =
                     IPCUtils.getEventStreamRpcConnection()) {
            GreengrassCoreIPCClient ipcClient =
                    new GreengrassCoreIPCClient(eventStreamRPCConnection);
            GetSecretValueRequest req = new GetSecretValueRequest();
            req.setSecretId(secretId);

            GetSecretValueResponseHandler responseHandler =
                    SecretsManagerClient.getSecretValue(ipcClient, secretId);
            CompletableFuture<GetSecretValueResponse> futureResponse =
                    responseHandler.getResponse();
            try {
                GetSecretValueResponse response = futureResponse.get(TIMEOUT_SECONDS, TimeUnit.SECONDS);
                response.getSecretValue().postFromJson();
                String secretString = response.getSecretValue().getSecretString();
                System.out.println(secretString);
            } catch (TimeoutException e) {
                System.err.println("Timeout occurred while retrieving secret: " + secretId);
            } catch (ExecutionException e) {
                if (e.getCause() instanceof UnauthorizedError) {
                    System.err.println("Unauthorized error while retrieving secret: " + secretId);
                } else {
                    throw e;
                }
            }
        } catch (InterruptedException e) {
            System.out.println("IPC interrupted.");
        } catch (ExecutionException e) {
            System.err.println("Exception occurred when using IPC.");
            e.printStackTrace();
            System.exit(1);
        }
    }

    public static GetSecretValueResponseHandler getSecretValue(GreengrassCoreIPCClient greengrassCoreIPCClient, String secretArn) {
        GetSecretValueRequest getSecretValueRequest = new GetSecretValueRequest();
        getSecretValueRequest.setSecretId(secretArn);

        return greengrassCoreIPCClient.getSecretValue(getSecretValueRequest, Optional.empty());
    }
}
