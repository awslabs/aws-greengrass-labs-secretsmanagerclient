# aws.greengrass.labs.SecretsManagerClient

This component deploys a SecretManagerClient java cli tool that can be used by other components to retrieve secrets that have been synchronized locally to the Greengrass core via the `aws.greengrass.SecretManager` component.

This component does not perform any processing on its own and only deploys the executable. You need to invoke the executable from another component which you made dependent on `community.greengrass.SecretsManagerClient` by executing:

```
java -jar
  {aws.greengrass.labs.SecrectsManagerClient:artifacts:path}/secrects.jar
  <secretId>
```

To allow the component using the SecretManagerClient to access the secret, you need to add an `accessControl` section in the [Retrieve Secret Values](https://docs.aws.amazon.com/greengrass/v2/developerguide/ipc-secret-manager.html#ipc-secret-manager-authorization). Refer also to the [Requirements](https://docs.aws.amazon.com/greengrass/v2/developerguide/secret-manager-component.html#secret-manager-component-requirements) for the necessary authorization policies to be added to the Greengrass Token Exchange Role.



## Versions
This component has the following versions:

* 1.0.0

## Type

This component is a generic component. The [Greengrass nucleus](https://docs.aws.amazon.com/greengrass/v2/developerguide/greengrass-nucleus-component.html) runs the component's lifecycle scripts.

For more information, see [component types](https://docs.aws.amazon.com/greengrass/v2/developerguide/manage-components.html#component-types)


## Requirements

This component does not have any additional requirements to Greengrass Nucleus.

## Dependencies

When you deploy a component, AWS IoT Greengrass also deploys compatible versions of its dependencies. This means that you must meet the requirements for the component and all of its dependencies to successfully deploy the component. This section lists the dependencies for the released versions of this component and the semantic version constraints that define the component versions for each dependency. You can also view the dependencies for each version of the component in the [AWS IoT Greengrass console](https://console.aws.amazon.com/greengrass). On the component details page, look for the Dependencies list.

### 1.0.0

| Dependency | Compatible versions | Dependency type |
|---|---|---|
| Secret Manger | >=0.0.0 <2.5.0 | Soft |


## Configuration

This component does not have any configuration


## Local log file

This component does not generate any log. You can find log entries in the log file of the component using it.


## Changelog

The following table describes the changes in each version of the component.

| Version | Changes |
|---|---|
| 1.0.0 | Initial version |

