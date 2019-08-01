import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;
import ru.vladimir.web.JsonService;
import ru.vladimir.web.impl.JsonServiceImpl;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.Assert.assertTrue;

public class JsonParseTest {

    private final int MAX_KEYS = 100;
    private final int MIN_KEYS = 3;
    // ~1 byte per character = 10 KB, may differ on other machines, but that's OK for test
    // real length differs depending on random
    private final int DESIRED_CHARACTERS_COUNT = 10_000;

    private final Random random = new Random(23);

    @Test
    public void testParse() {
        int count = 1000;
        List<String> input = generateInput(count);

        JsonService jsonService = new JsonServiceImpl();
        long start = System.nanoTime();
        input.forEach(jsonService::parseSynchronization);
        long end = System.nanoTime();
        long timeMillis = (end - start) / 1_000_000;
        long millisecondsPerJson = timeMillis / count;
        System.out.printf("Total parse time: %s milliseconds (%s per input string) \r\n", timeMillis, millisecondsPerJson);
        assertTrue(millisecondsPerJson < 2);
    }

    private List<String> generateInput(int count) {
        return IntStream.range(0, count)
                .mapToObj(i -> this.generateSingleSynchronizationJson())
                .collect(Collectors.toList());
    }

    private String generateSingleSynchronizationJson() {
        // 10 KB
        int keysNumber = random.nextInt(MAX_KEYS - MIN_KEYS) + MIN_KEYS;
        int entryLength = DESIRED_CHARACTERS_COUNT / keysNumber;
        int moneyPosition = random.nextInt(keysNumber);
        int countryPosition = random.nextInt(keysNumber);
        countryPosition = countryPosition == moneyPosition ? countryPosition + 1 : countryPosition;

        System.out.println(String.format("Generate json with %s keys, %s entry length, money at %s, country at %s",
                keysNumber, entryLength, moneyPosition, countryPosition));

        List<String> entries = new ArrayList<>(keysNumber);
        Set<String> knownKeys = new HashSet<>(keysNumber);
        for (int i = 0; i < keysNumber; i++) {
            if (i == moneyPosition) {
                entries.add("\"money\":" + random.nextInt());
            } else if (i == countryPosition) {
                entries.add("\"country\":\"" + randomString(2) + "\"");
            } else {
                String key;
                String entry;
                do {
                    int keyLength = 1 + random.nextInt(entryLength - 4);
                    key = randomString(keyLength);
                    entry = randomString(entryLength - keyLength);
                } while (!knownKeys.add(key));
                entries.add("\"" + key + "\":" + "\"" + entry + "\"");
            }
        }
        String json = entries.stream().collect(Collectors.joining(",", "{", "}"));
        assertTrue(json.getBytes().length < 11500);
        return json;
    }

    private String randomString(int length) {
        return RandomStringUtils.random(length, true, true);
    }
}
