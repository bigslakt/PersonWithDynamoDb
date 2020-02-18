package helloworld;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import java.util.HashMap;
import java.util.Map;

public class CreatePerson implements RequestHandler<Map<String, Object>, GatewayResponse>{

    @Override
    public GatewayResponse handleRequest(Map<String, Object> input, Context context) {

        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("X-Custom-Header", "application/json");

        try {

            @SuppressWarnings("unchecked") Map<String, String> pathParameters = (Map<String, String>)input.get("pathParameters");
        }catch(Exception e) {
            return null;
        }

        return null;
    }
}
