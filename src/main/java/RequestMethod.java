public enum RequestMethod {
    UNDEFINED, GET, POST, PUT, DELETE;

    public static RequestMethod contains(String method) {
        for (RequestMethod m : RequestMethod.values()) {
            if (m.toString().equalsIgnoreCase(method)) {
                return m;
            }
        }

        return UNDEFINED;
    }
}
