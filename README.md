Installation:

Make sure Java `17.0.5` is installed.  

You can always use [SDKMAN](https://www.sdkman.io) and install it by doing:  

1. `sdk i java 17.0.5-tem`
2. `sdk u java 17.0.5`

Once Java is installed:  

1. `mvn clean install`  
2. `java -jar ~/.m2/repository/com/nulltwenty/OrdersAggregation/1.0/OrdersAggregation-1.0.jar`

Alternatively, one can also use: `mvn spring-boot:run`  
If it still doesn't work, open the project with IntelliJ and just run the `OrdersAggregationApplication.java` class.

This projects aggregates the three external APIs into one, in the `/aggregation` endpoint.
It expects three request parameters whose values are non-required comma-separated numbers, althought the last one, `pricingCountryCodes` expects comma-separated String values.

- `shipmentsOrderNumbers`
- `trackOrderNumbers`
- `pricingCountryCodes`

There is one controller with one method: `OrdersAggregationController#aggregation` which will serve the response.

This method will basically call each of the three services and gather their results. These results are lists of different DTO objects for each of the service response:  

- `ShipmentDTO` for the shipments service.
- `TrackingDTO` for the track service.
- `PricingDTO` for the pricing service.

Once the results are obtained, it'll simply construct maps with each result and will add them to response object, which is called `AggregatedResponse`.  
This object is composed of three `Map`, with a key of type `String` and a value that varies with the data type that it should hold:

- `String[]` for the shipments.
- `String` for the track.
- `Double` for the pricing.

Each service will call the third-party or external service and will create a proper response of the proper DTO for each of them depending on the type of answer that the aforementioned external service returns. It also contains business logic to check for possible errors when calling the external services, in which case, it will log an error to the default logger.  
It will also return an empty `List` in case of error, which will facilitate returning an empty JSON of the failing service in the final `AggregatedResponse` response.

There is a default timeout of 5 seconds whose value is defined in the `application.properties` under the name of `default-connection-timeout`.  
As the services will be using a RestTemplate in order to exchange information with the external services, this timeout is achieved by creating a `SimpleClientHttpRequestFactory` inside the `@Bean` creation of the `RestTemplate`, in the `OrdersAggregationApplication` class.

There are several unit tests created for both services and controller, making up to a total of 14 different tests. I would have loved to include way more tests but unfortunately I've run out of time here.

