/**
 * Created by Christopher on 7/30/2016.
 */
public class SessionStoredProcedures {
        public static String[] StoredProcedures = {
                String.format("CREATE FUNCTION createSession(phoneNumber VARCHAR(12), authCode VARCHAR(6))\n" +
                        "RETURNS TABLE (%s)\n" +
                        "AS\n" +
                        "$$\n" +
                        "\n" +
                        "BEGIN\n" +
                        "RETURN QUERY\n" +
                        "INSERT INTO Sessions (SessionPhoneNumber, SessionAuthCode)\n" +
                        "VALUES (phoneNumber, authCode)\n" +
                        "RETURNING SessionId;\n" +
                        "END\n" +
                        "$$ LANGUAGE plpgsql", Database.SessionIdAndType),
                String.format("CREATE FUNCTION updateSessionAuthCodeByPhoneNumber(phoneNumber VARCHAR(12), authCode VARCHAR(6))\n" +
                        "RETURNS VOID\n" +
                        "AS\n" +
                        "$$\n" +
                        "\n" +
                        "BEGIN\n" +
                        "UPDATE Sessions\n" +
                        "SET SessionAuthCode = authCode\n" +
                        "WHERE SessionPhoneNumber = phoneNumber;\n" +
                        "END\n" +
                        "$$ LANGUAGE plpgsql"),
                String.format("CREATE FUNCTION getSessionIdByPhoneNumberAndAuthCode(phoneNumber VARCHAR(12), authCode VARCHAR(6))\n" +
                        "RETURNS TABLE (%s)\n" +
                        "AS\n" +
                        "$$\n" +
                        "\n" +
                        "BEGIN\n" +
                        "RETURN QUERY\n" +
                        "SELECT SessionId\n" +
                        "FROM Sessions\n" +
                        "WHERE SessionPhoneNumber = phoneNumber AND SessionAuthCode = authCode;\n" +
                        "END\n" +
                        "$$ LANGUAGE plpgsql", Database.SessionIdAndType)
        };
}
