public class InterceptedClass implements InterceptInterface {

    @Override
    public String foo() {
        return "foo";
    }

    @Override
    public String bar() {
        return "bar";
    }
}