package com.aws.greengrass.labs;

import com.aws.greengrass.labs.util.IPCUtils;

import software.amazon.awssdk.aws.greengrass.GetSecretValueResponseHandler;
import software.amazon.awssdk.aws.greengrass.GreengrassCoreIPCClient;
import software.amazon.awssdk.aws.greengrass.model.GetSecretValueRequest;
import software.amazon.awssdk.aws.greengrass.model.GetSecretValueResponse;
import software.amazon.awssdk.aws.greengrass.model.UnauthorizedError;
import software.amazon.awssdk.eventstreamrpc.EventStreamRPCConnection;

import java.sql.Time;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class SecretsManagerClient {

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
            System.out.println(Secret.get(ipcClient, secretId));
        } catch (InterruptedException e) {
            System.err.println("IPC interrupted.");
            System.exit(1);
        } catch (TimeoutException e) {
            System.err.println("Operation timed out.");
            System.exit(1);
        } catch (ExecutionException e) {
            System.err.println("Exception occurred when using IPC.");
            e.printStackTrace();
            System.exit(1);
        }
    }
}
