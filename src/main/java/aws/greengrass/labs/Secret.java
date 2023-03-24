package aws.greengrass.labs;

import software.amazon.awssdk.aws.greengrass.GetSecretValueResponseHandler;
import software.amazon.awssdk.aws.greengrass.GreengrassCoreIPCClient;
import software.amazon.awssdk.aws.greengrass.model.GetSecretValueRequest;
import software.amazon.awssdk.aws.greengrass.model.GetSecretValueResponse;
import software.amazon.awssdk.aws.greengrass.model.UnauthorizedError;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class Secret {
    public static final int TIMEOUT_SECONDS = 10;
    public static String get(GreengrassCoreIPCClient ipcClient, String secretId) throws ExecutionException, TimeoutException, InterruptedException {
        GetSecretValueRequest req = new GetSecretValueRequest();
        req.setSecretId(secretId);

        GetSecretValueResponseHandler responseHandler =
                Secret.getSecretValue(ipcClient, secretId);
        CompletableFuture<GetSecretValueResponse> futureResponse =
                responseHandler.getResponse();
        try {
            GetSecretValueResponse response = futureResponse.get(TIMEOUT_SECONDS, TimeUnit.SECONDS);
            response.getSecretValue().postFromJson();
            return response.getSecretValue().getSecretString();
        } catch (ExecutionException e) {
            if (e.getCause() instanceof UnauthorizedError) {
                System.err.println("Unauthorized error while retrieving secret: " + secretId);
            }
            throw e;
        }
    }

    public static GetSecretValueResponseHandler getSecretValue(GreengrassCoreIPCClient greengrassCoreIPCClient, String secretArn) {
        GetSecretValueRequest getSecretValueRequest = new GetSecretValueRequest();
        getSecretValueRequest.setSecretId(secretArn);

        return greengrassCoreIPCClient.getSecretValue(getSecretValueRequest, Optional.empty());
    }
}
