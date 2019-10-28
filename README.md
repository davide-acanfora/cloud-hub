# Cloud Hub
*Cloud Hub* is a Java library that uses [Grafana](https://grafana.com/ "Grafana") and helps you monitoring your Multicloud System, providing ready-to-use and configurable **Dashboards**.

![AWS Lambda](https://i.imgur.com/l4QlKYj.jpg)

You don't need to have any Grafana files installed on your system. The library is responsible to deploy the Grafana server folder for you and runs it on localhost. The only thing you need to do is to add the Dashboards of the **services** you want to monitor. 

**How it works:**  A compressed folder of a portable Grafana server instance is embedded in the *Cloud Hub* package. On startup the folder is moved to a temporary directory, decompressed and finally the *grafana-server* executable is started. When you configure and then add a new Dashboard to a Grafana instance, the library will automatically make the *provisioning* for you, creating its configuration files and placing them in the server directory.

# Supported Systems
Currently  **Cloud Hub** is developed on Ubuntu 19.04, but we are planning to support other Operating Systems. It should not be difficult since it's just a matter of resolving OS's dependencies (Grafana binaries files, process management, ...)

| OS | Supported |
| :------------: | :------------: |
| Ubuntu | :heavy_check_mark:  |
|  Windows | :hammer: |
| Mac OS | :heavy_multiplication_x: |

# Supported Services
This is a list of the currently supported services you can monitor through the library:

| Cloud Provider | Service Name |
| :------------: | :------------: |
| Amazon Web Services | *Billing* |
|   | *EC2* |
|   | *Lambda* |
|   | *S3* |
|   | *SQS* |
| Microsoft Azure | *Functions* |
|   | *Storage Container* |
|   | *Storage Queue* |
|   | *VMs* |

You can also run a simple HTTP Web Server to monitor the **local system** (CPU usage and temperature, Memory usage) in real time.

# Dependencies
 - [Zip4j](https://github.com/srikanth-lingala/zip4j) - to manage (essentially extract) the archive containing the Grafana server;
 - [JSON-java](https://github.com/stleary/JSON-java) - to better manage the data exchange with Grafana for the Local Monitoring;
 - [jSensors](https://github.com/profesorfalken/jSensors) - to get the CPU temperature.

Thanks to all the creators for their awesome work!

# Quick Start
In order to monitor a specific service, you need to get a Grafana instance first specifying the web server port and a flag indicating whether or not to print the console logs:
```java
Grafana grafana = new Grafana(SERVER_PORT, FLAG);
```
Then specify the appropriate **Datasource** of the service you want to monitor, *e.g. CloudWatch for Amazon Web Services*:
```java
CloudWatchDataSource datasource = new CloudWatchDataSource("ACCESS_KEY", "SECRET_KEY", "DEFAULT_REGION");
```
Now you have to define a Dashboard compatible with the Datasource we created earlier, *e.g. AWS Lambda*:
```java
AWSLambdaDashboard dashboard = new AWSLambdaDashboard("DASHBOARD_NAME", datasource);
```
You finally have to add the Dashboard to the Grafana instance in order to effectively deploy its configuration:
```java
grafana.add(dashboard);
```
**NOTE:** optionally for this particular Dashboard, in order to hide the rest of the functions listed in the account, you can filter specific Lambda functions using their names in the method:
```java
dashboard.addFunction("FUNCTION_NAME");
```
either before or after adding the Dashboard to Grafana.
If you want to enable the **Local Monitoring**, you need to specify the port where the server is going to run and the delay that will be applied for collecting local data:
```java
grafana.enableLocalMonitoring(API_PORT, COLLECTOR_DELAY);
```

# Documentation
 - [grafana.Grafana](#Grafana)
	 - [Constructor](#GrafanaCostructor)
	 - [start](#GrafanaStart)
	 - [addDashboard](#GrafanaAddDashboard)
	 - [removeDashboard](#GrafanaRemoveDashboard)
	 - [enableLocalMonitoring](#GrafanaEnableLocalMonitoring)
	 - [disableLocalMonitoring](#GrafanaDisableLocalMonitoring)
	 - [waitFor](#GrafanaWaitFor)
- grafana.datasource
	- [grafana.datasource.AzureMonitorDataSource](#AzureMonitor)
		- [Constructor](#AzureMonitorConstructor)
	- [grafana.datasource.CloudWatchDataSource](#CloudWatch)
		- [Constructor](#CloudWatchConstructor)
- grafana.dashboard
	- [grafana.dashboard.AWSLambdaDashboard](#AWSLambda)
		- [Constructor](#AWSLambdaConstructor)
		- [addFunction](#AWSLambdaAddFunction)
	-  [grafana.dashboard.AWSSQSDashboard](#AWSSQSDashboard)
		- [Constructor](#AWSSQSDashboardConstructor)
		- [addQueue](#AWSSQSDashboardAddQueue)
	- [grafana.dashboard.AWSBillingDashboard](#AWSBillingDashboard)
		- [Constructor](#AWSBillingDashboardConstructor)
	- [grafana.dashboard.AzureFunctionsDashboard](#AzureFunctionsDashboard)
		- [Constructor](#AzureFunctionsDashboardConstructor)
		- [addFunction](#AzureFunctionsDashboardAddFunction)
	- [grafana.dashboard.AzureQueueDashboard](#AzureQueueDashboard)
		- [Constructor](#AzureQueueDashboardConstructor)

<a name ="Grafana"></a>
## Grafana
It's the core of the library as it represents the Grafana server itself and it's used to control the dashboards.

<a name="GrafanaCostructor"></a>
### `Grafana(int httpPort, boolean consoleLog)`
Deploys a new Grafana server instance in a temporary system folder.
 - `httpPort` is the port where the Grafana Web interface will listen on
 - `consoleLog` enable/disable the Grafana console logs

<a name="GrafanaStart"></a>
### `void start() throws IOException`
Runs the Grafana server previously deployed. 

<a name="GrafanaAddDashboard"></a>
### `void addDashboard(Dashboard dashboard)`
Adds a dashboard to Grafana by deploying its configuration file.
 - `dashboard` is one of the supported dashboards (which extends the abstract *Dashboard* class)

<a name="GrafanaRemoveDashboard"></a>
### `void removeDashboard(Dashboard dashboard)`
Removes a dashboard from Grafana by deleting its configuration file.
 - `dashboard` is one of the supported dashboards (which extends the abstract *Dashboard* class)

<a name="GrafanaEnableLocalMonitoring"></a>
### `void enableLocalMonitoring(int apiPort, int collectorDelay)`
Enables the local monitoring dashboard along with the HTTP API server that provides data to Grafana and the thread that collects the informations about the local resources.
 - `apiPort` is the port of the HTTP API server
 - `collectorDelay` is the time in milliseconds between one measurement and another

<a name="GrafanaDisableLocalMonitoring"></a>
 ### `void disableLocalMonitoring()`
Disables the local monitoring by removing its dashboard and stopping the collector thread along with the HTTP API server.

<a name="GrafanaWaitFor"></a>
 ### `void waitFor() throws InterruptedException`
Prevents the Grafana process to close when there are no more instructions left in Java (note that this method is blocking).

<a name="AzureMonitor"></a>
## AzureMonitorDatasource
Represents the Azure Monitor Datasource and is responsible to create its configuration file for provisioning.

<a name="AzureMonitorConstructor"></a>
### `AzureMonitorDataSource(String tenantId, String clientId, String clientSecret, String defaultSubscription, String applicationId, String apiKey)`
You need 4 pieces of information from the Azure Portal:
 - `tenantId` (Azure Active Directory -> Properties -> Directory ID)
 - `clientId` (Azure Active Directory -> App Registrations -> Choose your app -> Application ID)
 - `clientSecret` (Azure Active Directory -> App Registrations -> Choose your app -> Keys)
 - `defaultSubscription` (Subscriptions -> Choose subscription -> Overview -> Subscription ID)

You need 2 more pieces of information from the Azure Portal:
 - `applicationId` 
 - `apiKey`

Please refer to this [link](https://dev.applicationinsights.io/quickstart/) to know how to get them. If you are still having problems, please refer to the Grafana documentation [here](https://grafana.com/grafana/plugins/grafana-azure-monitor-datasource).

<a name="CloudWatch"></a>
## CloudWatchDatasource
Represents the CloudWatch Datasource from AWS and is responsible to create its configuration file for provisioning.

### `CloudWatchDatasource(String accessKey, String secretKey, String defaultRegion)`
Grafana needs permissions granted via IAM to be able to read CloudWatch metrics. So after creating the role needed you have to provide:
 - `accessKey` 
 - `secretKey`

You can find more about that on the Grafana documentation [here](https://grafana.com/docs/features/datasources/cloudwatch/). If you need a quick guide have a look at [this](https://medium.com/@_oleksii_/using-aws-cloudwatch-in-grafana-8294b7a2e7dd).

You also need to specify:
 - `defaultRegion` (e.g. *us-east-1*)

<a name="AWSLambdaDashboard"></a>
## AWSLambdaDashboard
Represents the Dashboard used to monitor the AWS Lambda functions.
<a name="AWSLambdaDashboardConstructor"></a>
### `AWSLambdaDashboard(String name, CloudWatchDataSource cloudWatchDataSource)`
 - `name` is a unique name used to identify the Dashboard. It is also its title in Grafana
 - `cloudWatchDataSource` is the Datasource where to get the informations from

 <a name="AWSLambdaDashboardAddFunction"></a>
### `void addFunction(String functionName)`
Adds a function to be monitored from the Dashboard.
- `functionName` is the name of the function you want to monitor

<a name="AWSSQSDashboard"></a>
## AWSSQSDashboard
Represents the Dashboard used to monitor the AWS SQS service.

<a name="AWSSQSDashboardConstructor"></a>
### `AWSSQSDashboard(String name, CloudWatchDataSource cloudWatchDataSource)`
 - `name` is a unique name used to identify the Dashboard. It is also its title in Grafana
 - `cloudWatchDataSource` is the Datasource where to get the informations from

<a name="AWSSQSDashboardAddQueue"></a>
### `void addQueue(String queueName)`
Adds a queue to be monitored from the Dashboard.
- `queueName` is the name of the queue you want to monitor

<a name="AWSBillingDashboard"></a>
## AWSBillingDashboard
Represents the Dashboard used to monitor the AWS Billing service.

<a name="AWSBillingDashboardConstructor"></a>
### `AWSBillingDashboard(String name, CloudWatchDataSource cloudWatchDataSource)`
 - `name` is a unique name used to identify the Dashboard. It is also its title in Grafana
 - `cloudWatchDataSource` is the Datasource where to get the informations from

<a name="AzureFunctionsDashboard"></a>
## AzureFunctionsDashboard
Represents the Dashboard used to monitor the Azure Functions service.

<a name="AzureFunctionsDashboardConstructor"></a>
### `AzureFunctionsDashboard(String name, AzureMonitorDataSource azureMonitorDataSource)`
 - `name` is a unique name used to identify the Dashboard. It is also its title in Grafana
 - `azureMonitorDataSource` is the Datasource where to get the informations from

<a name="AzureFunctionsDashboardAddFunction"></a>
### `void addFunction(String functionName)`
Adds a function to be monitored from the Dashboard.
- `functionName` is the name of the function you want to monitor

<a name="AzureQueueDashboard"></a>
## AzureQueueDashboard
Represents the Dashboard used to monitor the Azure Queue Storage service.

<a name="AzureQueueDashboardConstructor"></a>
### `AzureQueueDashboard(String name, String resourceGroup, String resourceName, AzureMonitorDataSource azureMonitorDataSource)`
 - `name` is a unique name used to identify the Dashboard. It is also its title in Grafana
 - `resourceGroup` is the name of the resource group where the storage account is located
 - `resourceName` is the name of the storage account
 - `azureMonitorDataSource` is the Datasource where to get the informations from

The only Azure Monitor limitation is that you can't filter the queue you want to monitor, but you will get all of them together.
In order to overcome this limitation there is the Azure Service Bus which is not implemented yet.
