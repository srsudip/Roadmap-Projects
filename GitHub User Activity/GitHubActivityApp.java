import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GitHubActivityApp {

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Usage: java GitHubActivityApp <username>");
            return;
        }
        GitHubClient client = new GitHubClient();
        try {
            String jsonResponse = client.fetchUserActivity(args[0]);
            parseAndDisplayActivity(jsonResponse);
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    private static void parseAndDisplayActivity(String json) {
        String[] events = json.split("\\},\\s*\\{");
        if (json.trim().equals("[]")) {
            System.out.println("No activity found.");
            return;
        }

        for (String event : events) {
            if (!event.startsWith("{"))
                event = "{" + event;
            if (!event.endsWith("}"))
                event = event + "}";

            String type = JsonParserHelper.getString(event, "type");
            String repoName = extractRepoName(event);

            System.out.print("- ");
            switch (type) {
                case "PushEvent":
                    int commits = JsonParserHelper.countArraySize(event, "commits");
                    System.out.println("Pushed " + commits + " commit(s) to " + repoName);
                    break;
                case "CreateEvent":
                    String refType = JsonParserHelper.getString(event, "ref_type");
                    System.out.println("Created a new " + refType + " in " + repoName);
                    break;
                case "WatchEvent":
                    System.out.println("Starred " + repoName);
                    break;
                case "IssueCommentEvent":
                    System.out.println("Commented on an issue in " + repoName);
                    break;
                case "IssuesEvent":
                    String action = JsonParserHelper.getString(event, "action");
                    System.out.println(capitalize(action) + "ed an issue in " + repoName);
                    break;
                default:
                    System.out.println("Performed a " + type + " in " + repoName);
                    break;
            }
        }
    }

    private static String extractRepoName(String event) {
        Pattern pattern = Pattern.compile("\"name\":\"([^\"]+)\"");
        Matcher matcher = pattern.matcher(event);
        return matcher.find() ? matcher.group(1) : "unknown repo";
    }

    private static String capitalize(String s) {
        return (s == null || s.isEmpty()) ? s : s.substring(0, 1).toUpperCase() + s.substring(1);
    }
}

class GitHubClient {
    public String fetchUserActivity(String user) throws Exception {
        HttpClient client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build();
        HttpRequest req = HttpRequest.newBuilder().uri(URI.create("https://api.github.com/users/" + user + "/events"))
                .header("User-Agent", "Java-App").GET().build();
        HttpResponse<String> res = client.send(req, HttpResponse.BodyHandlers.ofString());
        if (res.statusCode() == 404)
            throw new Exception("User not found.");
        return res.body();
    }
}

class JsonParserHelper {
    public static String getString(String json, String key) {
        Pattern p = Pattern.compile("\"" + key + "\":\\s*\"?([^,\"}]+)\"?");
        Matcher m = p.matcher(json);
        return m.find() ? m.group(1).trim() : "";
    }

    public static int countArraySize(String json, String key) {
        Pattern p = Pattern.compile("\"" + key + "\":\\s*\\[([^\\]]*)\\]");
        Matcher m = p.matcher(json);
        if (m.find()) {
            String content = m.group(1).trim();
            return content.isEmpty() ? 0 : content.split("(?<=\\}),\\s*(?=\\{)").length + 1;
        }
        return 0;
    }
}
