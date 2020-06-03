public class Validator { //Class to validate user input for IP adress and port

    public static boolean isValidIpAddress(String ipAddress )
    {
        if (ipAddress.matches("^(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})$")) {
            String[] groups = ipAddress.split("\\.");

            for (int i = 0; i <= 3; i++) {
                String segment = groups[i];
                if (segment == null || segment.length() <= 0) {
                    return false;
                }

                int value = 0;
                try {
                    value = Integer.parseInt(segment);
                } catch (NumberFormatException e) {
                    return false;
                }
                if (value > 255) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    public static boolean isValidPort(int port) {
        return (5000 <= port && port <= 5050);
    }
}
