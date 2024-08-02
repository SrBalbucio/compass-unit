package balbucio.compass.api.utilities;

import org.ajbrown.namemachine.Name;
import org.ajbrown.namemachine.NameGenerator;

import java.util.*;

public class Utilities {

    public static List<String> MAIL_PROVIDERS = Arrays.asList("gmail", "hotmail", "outlook", "uol");

    private NameGenerator nameGenerator;
    private Random random;

    public Utilities() {
        this.nameGenerator = new NameGenerator();
        this.random = new Random();
    }

    public Map<String, String> getRandomMap() {
        Map<String, String> map = new HashMap<String, String>();

        for (int i = 0; i < getRandomNumber(5, 15); i++) {
            map.put(generateToken(8), generateToken(8));
        }

        return map;
    }

    public String generateEmail(String firstName, String lastName) {
        return firstName + "." + lastName + "@" + MAIL_PROVIDERS.get(getRandomNumber(0, MAIL_PROVIDERS.size())) + ".com";
    }

    public Name generateName() {
        return nameGenerator.generateName();
    }

    public String generateNameAsString() {
        return generateName().getFirstName() + " " + generateName().getLastName();
    }

    public int getRandomNumber(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }

    public String generateToken(int size) {
        int leftLimit = 48;
        int rightLimit = 122;

        return random.ints(leftLimit, rightLimit + 1)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(size)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }

    public String generateToken(int min, int max) {
        int leftLimit = 48;
        int rightLimit = 122;
        int targetStringLength = getRandomNumber(min, max);

        return random.ints(leftLimit, rightLimit + 1)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }
}
