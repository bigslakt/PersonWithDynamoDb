package helloworld;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class CreatePerson implements RequestHandler<Map<String, Object>, GatewayResponse>{

    private final Logger logger = LogManager.getLogger(this.getClass());

    @Override
    public GatewayResponse handleRequest(Map<String, Object> input, Context context) {

        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("X-Custom-Header", "application/json");

        ObjectMapper mapper = new ObjectMapper();
        Person person = new Person();

        try {

            JsonNode body = new ObjectMapper().readTree((String)input.get("body"));
            person.setMittId(body.get("mittId").asText());
            person.setFirstName(body.get("firstName").asText());
            person.setLastName(body.get("lastName").asText());
            person.setAge(body.get("age").asText());
            person.setSex(body.get("sex").asText());
//            Person person2 =  mapper.treeToValue(body, Person.class);

            logger.info(person.toString());
            Person personResponse = person.save(person);
            logger.info(personResponse.toString());

            return GatewayResponse.builder()
                    .addHeaders(headers)
                    .addBody(personResponse)
                    .addStatusCode(201)
                    .build();

        }catch(Exception e) {

            logger.error(String.format("Error unknown Exception: %s", e));
            return GatewayResponse.builder()
                    .addHeaders(headers)
                    .addBody(e.getMessage())
                    .addStatusCode(500)
                    .build();
        }

    }
}
