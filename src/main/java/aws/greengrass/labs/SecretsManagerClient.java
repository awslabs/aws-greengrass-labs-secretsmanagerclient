package aws.greengrass.labs;

import software.amazon.awssdk.aws.greengrass.GreengrassCoreIPCClientV2;
import software.amazon.awssdk.aws.greengrass.model.GetSecretValueResponse;
import software.amazon.awssdk.aws.greengrass.model.GetSecretValueRequest;
import software.amazon.awssdk.aws.greengrass.model.UnauthorizedError;
import software.amazon.awssdk.eventstreamrpc.EventStreamRPCConnection;

import java.io.IOException;
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
        try (GreengrassCoreIPCClientV2 ipcClient = GreengrassCoreIPCClientV2.builder().build()) {

            System.out.println(SecretsManagerClient.getSecret(ipcClient, secretId));
        } catch (InterruptedException e) {
            System.err.println("IPC interrupted.");
            System.exit(1);
        } catch (TimeoutException e) {
            System.err.println("Operation timed out.");
            System.exit(1);
        } catch (ExecutionException e) {
            System.err.println("Exception occurred when using IPC.");
            if (e.getCause() instanceof UnauthorizedError) {
                System.err.println("Unauthorized error while retrieving secret: " + secretId);
            }
            e.printStackTrace();
            System.exit(1);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String getSecret(GreengrassCoreIPCClientV2 ipcClient, String secretId) throws InterruptedException {
        GetSecretValueRequest request = new GetSecretValueRequest();
        request.setSecretId(secretId);
        GetSecretValueResponse response = ipcClient.getSecretValue(request);
        return response.getSecretValue().getSecretString();
    }
}
