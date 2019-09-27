# Multicloud Monitoring Library
A Java library that uses [Grafana](https://grafana.com/ "Grafana") and helps you monitoring your Multicloud System, providing ready-to-use and configurable **Dashboards**.

![AWS Lambda](https://i.imgur.com/l4QlKYj.jpg)

You don't need to have any Grafana files installed on your system. The library is responsible to deploy the Grafana server folder for you and runs it on localhost. The only thing you need to do is to add the Dashboards of the **services** you want to monitor.
# Basic Usage
In order to monitor a specific service, you need to get a Grafana instance first specifying the web server port and a flag indicating whether or not to print the console logs:
```java
Grafana grafana = new Grafana(3000, false);
```
Then specify the appropiate **Datasource** of the service you want to monitor, *e.g. CloudWatch for Amazon Web Services*:
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
