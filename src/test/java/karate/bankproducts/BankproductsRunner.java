package karate.bankproducts;

import com.intuit.karate.junit5.Karate;

public class BankproductsRunner {

    @Karate.Test
    Karate testAll() {
        return Karate.run().relativeTo(getClass());
    }
}

/*
 * @Karate.Test            -> Marks this method as a Karate test so JUnit 5 can discover and execute it
 * Karate.run()            -> Tells Karate to run feature files
 * .relativeTo(getClass()) -> Searches for feature files in the same package as this runner class.
 */