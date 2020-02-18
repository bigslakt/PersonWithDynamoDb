package helloworld;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import jdk.nashorn.internal.ir.CatchNode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListPersonsHandler implements RequestHandler<Map<String, Object>, GatewayResponse> {
    @Override
    public GatewayResponse handleRequest(Map<String, Object> stringObjectMap, Context context) {

        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("X-Custom-Header", "application/json");
        Map<String, Object> responseBody = new HashMap<>();

        try {

            Person person = new Person();
            List<Person> persons = person.list();

            return GatewayResponse.builder()
                    .addBody(persons)
                    .addHeaders(headers)
                    .addStatusCode(200)
                    .build();

        } catch(Exception e) {

            responseBody.put("message", e.getMessage());
            return GatewayResponse.builder()
                    .addBody(responseBody)
                    .addHeaders(headers)
                    .addStatusCode(500)
                    .build();
        }
    }
}
