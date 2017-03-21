
## Introduction

API Gateway that proxies incoming requests into the math evaluator service (eval-me) and sends back the response to the client.

## System requirements

You need to have maven 3.x as well as java 8 in order to build and run the service

## Running the service

The artifact produced by is an executable jar with an embedded tomcat. To run the service you will need to run:

*hector@Iscariot ~/projects/personal/eval-api $ java -jar ./target/eval-api-1.0.jar &*

Application and access logs are located in the *logs* directory under the main project location

*hector@Iscariot ~/projects/personal/eval-api/logs*

## Technical considerations
The service has been built with SpringBoot. It provides a REST endpoint ( POST /api/* ) that expects "expression" as the parameter containing
the expression to be evaluated by the backend service.

Some responsibilities of the Gateway:

* Forward to the backend the HttpHeaders configured (*request.headers.to.forward*)
* Cache results coming from the math service and serve from the cache directly if there is a hit, in order to reduce the load and round trips to the backend
* Recover from IO (Connection errors, timeours...), being able to configure multiple math services *evalme.service.urls*, and per service the max
number of retries in case of an IO failure *retry.max.attempts*
* Forward back to the client business errors coming from the backend maintaining the original HTTP status code and error message
* XML and JSON support, if the client does not explicitly send the accept HTTP header JSON will be used as default

## Testing

To manually test the service you can use curl command, see an example below:

*curl -F "expression=(25*25+12)" -H "Accept: application/json" -X POST http://localhost:8080/api/eval*
*{"expression":"(25*25)+12","result":"637.0"}*
