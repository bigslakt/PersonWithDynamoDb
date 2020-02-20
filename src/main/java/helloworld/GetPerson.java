package helloworld;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import java.util.HashMap;
import java.util.Map;

public class GetPerson implements RequestHandler<Map<String, Object>, GatewayResponse> {

    @Override
    public GatewayResponse handleRequest(Map<String, Object> input, Context context) {

        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("X-Custom-Header", "application/json");
        Map<String, String> body = new HashMap<>();

        try {

            Map<String, String> pathParameters = (Map<String, String>) input.get("pathParameters");
            String mittId = pathParameters.get("mittId");
           // String firstName = pathParameters.get("firstName");

            Person person = new Person();
            Person personResponse = person.get(mittId);

            return GatewayResponse.builder()
                    .addHeaders(headers)
                    .addBody(personResponse)
                    .addStatusCode(200)
                    .build();

        }catch(PersonNotFoundException nfe) {

            body.put("message", nfe.getMessage());
            return GatewayResponse.builder()
                    .addHeaders(headers)
                    .addBody(body)
                    .addStatusCode(404)
                    .build();

        }catch (Exception e) {

            body.put("message", e.getMessage());
            return GatewayResponse.builder()
                    .addHeaders(headers)
                    .addBody(body)
                    .addStatusCode(500)
                    .build();
        }
    }
}
