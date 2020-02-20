package helloworld;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.*;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import org.apache.logging.log4j.LogManager;


import java.io.IOException;
import java.util.*;

@DynamoDBTable(tableName = "PLACEHOLDER_PERSONS_TABLE_NAME")
public class Person {

    // private static final String BOOKINGS_TABLE_NAME = System.getenv("BOOKINGS_TABLE_NAME");
    private static final String PERSONS_TABLE_NAME = System.getenv("PERSONS_TABLE_NAME");

    String mittId;
    String firstName;
    String lastName;
    String age;
    String sex;

    private final AmazonDynamoDB client;
    private final DynamoDBMapper mapper;
    private final DynamoDB dynamoDB;   //TODO: Används inte???

    private final LoggerAdapter logger;
    private final StringBuilder sb = new StringBuilder();

    public Person() {

        // build the mapper config
        DynamoDBMapperConfig mapperConfig = DynamoDBMapperConfig.builder()
                .withTableNameOverride(new DynamoDBMapperConfig.TableNameOverride(PERSONS_TABLE_NAME))
                .build();
        // get the db adapter
        DynamoDBAdapter db_adapter = DynamoDBAdapter.getInstance();
        this.client = db_adapter.getDbClient();
        this.dynamoDB = db_adapter.getDynamoDB();
        // create the mapper with config
        this.mapper = db_adapter.createDbMapper(mapperConfig);

        this.logger = new LoggerAdapter(LogManager.getLogger(this.getClass()));
    }

    public Person(AmazonDynamoDB client, DynamoDBMapperConfig config) {
        this.client = client;
        this.dynamoDB = new DynamoDB(client);
        this.mapper = new DynamoDBMapper(client, config);
        this.logger = new LoggerAdapter();
        //this.logger = LogManager.getLogger(this.getClass());
    }

    @DynamoDBHashKey(attributeName = "mittId")
    public String getMittId() {
        return mittId;
    }
    public void setMittId(String mittId) {
        this.mittId = mittId;
    }

    @DynamoDBRangeKey(attributeName = "firstName")
    public String getFirstName() {
        return firstName;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @DynamoDBIndexRangeKey(attributeName = "lastName", localSecondaryIndexName = "identity")
    public String getLastName() {
        return lastName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @DynamoDBIndexRangeKey(attributeName = "age", globalSecondaryIndexName = "ageOfSex")
    public String getAge() {
        return age;
    }
    public void setAge(String age) {
        this.age = age;
    }

    @DynamoDBIndexHashKey(attributeName = "sex", globalSecondaryIndexName = "ageOfSex")
    public String getSex() {
        return sex;
    }
    public void setSex(String sex) {
        this.sex = sex;
    }

    @Override
    public String toString() {
        return "Person{" +
                "id='" + mittId + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", age='" + age + '\'' +
                ", sex='" + sex + '\'' +
                '}';
    }

    public Boolean ifTableExists() {
        System.out.println("i iftabelexists");

        return this.client.describeTable(PERSONS_TABLE_NAME).getTable().getTableStatus().equals("ACTIVE");
    }

    public List<Person> list() throws IOException {
        DynamoDBScanExpression scanExp = new DynamoDBScanExpression();
        List<Person> results = this.mapper.scan(Person.class, scanExp);
        for (Person p : results) {
           // logger.info("Booking - list(): " + p.toString());
        }
        return results;
    }


    public Person get(String mittId) throws IOException {
        Person person = null;

        HashMap<String, AttributeValue> av = new HashMap<String, AttributeValue>();
        av.put(":v1", new AttributeValue().withS(mittId));

        DynamoDBQueryExpression<Person> queryExp = new DynamoDBQueryExpression<Person>()
                .withKeyConditionExpression("mittId = :v1")
                .withExpressionAttributeValues(av)
                .withConsistentRead(false);
        //queryExp.setIndexName("bookingIndex");

        PaginatedQueryList<Person> result = this.mapper.query(Person.class, queryExp);
        if (!result.isEmpty()) {
            person = result.get(0);
           // logger.info("Booking - get(): person - " + person.toString());
        } else {
          //  logger.info("Booking - get(): person - Not Found.");
            throw new PersonNotFoundException("Person not found");
        }
        return person;
    }



    public Person save(Person person) throws IOException {

        logger.info("Booking - save(): " + person.toString());
        this.mapper.save(person);
        return person;
    }

    //TODO: Använda denna metod för validering?
    public List<Person> validateBooking(Person person) throws IOException{

        Map<String, AttributeValue> values = new HashMap<>();
        values.put(":mittId", new AttributeValue().withS(person.getMittId()));
        values.put(":firstName", new AttributeValue().withS(person.getFirstName()));
        values.put(":lastName", new AttributeValue().withS(person.getLastName()));
        values.put(":age", new AttributeValue().withS(person.getAge()));
        values.put(":sex", new AttributeValue().withS(person.getSex()));

        DynamoDBQueryExpression<Person> queryExp = new DynamoDBQueryExpression<>();

        queryExp.withKeyConditionExpression("mittId = :mittId")
                .withExpressionAttributeValues(values)
                .withConsistentRead(true)
                //.withFilterExpression("startTime < :end AND bookingStatus = :validState")
                .withFilterExpression("startTime < :end AND bookingStatus <> :invalidState AND bookingStatus <> :invalidState2");

        return mapper.query(Person.class, queryExp);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Person)) return false;
        Person person = (Person) o;
        return getMittId().equals(person.getMittId()) &&
                getFirstName().equals(person.getFirstName()) &&
                getLastName().equals(person.getLastName()) &&
                getAge().equals(person.getAge()) &&
                getSex().equals(person.getSex());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getMittId(), getFirstName(), getLastName(), getAge(), getSex());
    }
}
