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
# Basic Usage
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

If you want to enable the **local monitoring**, you need to specify the port where the server is going to run and the delay that will be applied for collecting local data:
```java
grafana.enableLocalMonitoring(API_PORT, COLLECTOR_DELAY);
```
