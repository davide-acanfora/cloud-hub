
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
|   | *Lambda* |
|   | *SQS* |
| Microsoft Azure | *Functions* |
|   | *Storage Queue* |

You can also run a simple HTTP Web Server to monitor the **local system** (CPU usage and temperature, Memory usage) in real time.
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

 - grafana.Grafana
	 - [Constructor](#GrafanaCostructor)
	 - [start](#GrafanaStart)
	 - [addDashboard](#GrafanaAddDashboard)
	 - [removeDashboard](#GrafanaRemoveDashboard)
	 - [enableLocalMonitoring](#GrafanaEnableLocalMonitoring)
	 - [disableLocalMonitoring](#GrafanaDisableLocalMonitoring)
	 - [waitFor](#GrafanaWaitFor)
- grafana.datasource
	- grafana.datasource.AzureMonitorDataSource
		- Constructor
	- grafana.datasource.CloudWatchDataSource
		- Constructor
	- grafana.datasource.JSONDataSource
		- Constructor
- grafana.dashboard
	- grafana.dashboard.AWSLambdaDashboard
		- Constructor
		- addFunction
	-  grafana.dashboard.AWSSQSDashboard
		- Constructor
		- addQueue
	- grafana.dashboard.AWSBillingDashboard
		- Constructor
	- grafana.dashboard.AzureFunctionsDashboard
		- Constructor
		- addFunction
	- grafana.dashboard.AzureQueueDashboard
		- Constructor

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
Adds a dashboard to Grafana.
 - `dashboard` is one of the supported dashboards (which extends the abstract *Dashboard* class)

<a name="GrafanaRemoveDashboard"></a>
### `void removeDashboard(Dashboard dashboard)`
Removes a dashboard from Grafana.
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
Prevents the Grafana process to close when the are no more instructions left in Java (note that this method is blocking).

## AzureMonitorDatasource
It represents the Azure Monitor Datasource and is responsible to create its configuration file for provisioning.
###### `AzureMonitorDataSource(String tenantId, String clientId, String clientSecret, String defaultSubscription, String applicationId, String apiKey)`
 - `tenantId` lorem ipsum dolor sit amet
 - `clientId` lorem ipsum dolor sit amet
 - `clientSecret` lorem ipsum dolor sit amet
 - `defaultSubscription` lorem ipsum dolor sit amet
 - `applicationId` lorem ipsum dolor sit amet
 - `apiKey` lorem ipsum dolor sit amet

lorem ipsum dolor sit amet
