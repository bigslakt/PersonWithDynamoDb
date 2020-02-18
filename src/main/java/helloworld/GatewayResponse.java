package helloworld;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class GatewayResponse {

    private final String body;
    private final Map<String, String> headers;
    private final int statusCode;

    public GatewayResponse(Object body, int statusCode, Map<String, String> headers) {

        try{
            this.statusCode = statusCode;
            this.headers = headers;
            this.body = Builder.mapper.writeValueAsString(body);

        }catch(JsonProcessingException e){
            throw new RuntimeException(e);
        }
    }

    public static Builder builder(){
        return new Builder();
    }

    public String getBody() {
        return body;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public static class Builder{

        Object body;
        int statusCode;
        Map<String, String> headers = Collections.emptyMap();
        private static ObjectMapper mapper = new ObjectMapper();


        public Builder addheader(String key, String value){
            this.headers.put(key, value);
            return this;
        }

        public Builder addHeaders(Map<String, String> headers){
            this.headers = headers;
            return this;
        }

        public Builder addStatusCode(int statusCode){
            this.statusCode = statusCode;
            return this;
        }

        public Builder addBody(Object body){
            this.body = body;
            return this;

        }

        public GatewayResponse build(){
            return new GatewayResponse(this.body, this.statusCode, this.headers);
        }
    }
}
