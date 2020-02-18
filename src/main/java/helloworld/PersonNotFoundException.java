package helloworld;

public class PersonNotFoundException extends IllegalArgumentException {

    public PersonNotFoundException(String s) {
        super(s);
    }
}
