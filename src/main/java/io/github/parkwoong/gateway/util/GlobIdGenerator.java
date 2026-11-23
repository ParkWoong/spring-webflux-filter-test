package io.github.parkwoong.gateway.util;

import static java.net.InetAddress.getLocalHost;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicInteger;

public class GlobIdGenerator {

    private static final int KEY_LENGTH = 3;
    private static final int POSTFIX_LENGTH = 5;

    private static final String DUMMY = "XXXXX";
    private static final String DEFAULT_KEY = "API";

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");

    private static final AtomicInteger sequence = new AtomicInteger(0);

    private static String lastYmdhms;
    private static String postfix = DUMMY;

    static {
        try {

            final String hostName = getLocalHost().getHostName();
            final int hostNameLength = hostName.length();

            postfix =
                    hostNameLength >= POSTFIX_LENGTH
                            ? hostName.substring(hostNameLength - POSTFIX_LENGTH)
                            : new StringBuilder()
                                    .append(hostName)
                                    .append(postfix)
                                    .substring(0, POSTFIX_LENGTH);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String generate(String infix) {

        if (infix == null || infix.length() == 0) infix = DEFAULT_KEY;
        else if (infix.length() > 3) infix = infix.substring(0, KEY_LENGTH);
        else if (infix.length() < 3) infix = infix.concat(DUMMY).substring(0, KEY_LENGTH);

        final String ymdhms = LocalDateTime.now().format(FORMATTER);

        if (!ymdhms.equals(lastYmdhms)) {
            sequence.set(0);
            lastYmdhms = ymdhms;
        }

        // yyyyMMddHHmmssSSSKEYXXXXX00000 (length == 30)
        return new StringBuilder()
                .append(ymdhms)
                .append(infix)
                .append(postfix)
                .append(String.format("%05d", sequence.getAndIncrement()))
                .toString();
    }

    public static String generate() {

        return generate(null);
    }
}
