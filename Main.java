import java.io.IOException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.util.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Objects;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) throws IOException {
        String[] Contents = content();

        Main lineNotify = new Main();
        for (String content : Contents) {
            lineNotify.notify(content);
        }
        System.out.println("成功！！");
    }

    public static String[] content() throws IOException {
        Document document = Jsoup.connect("https://news.yahoo.co.jp/topics/it").get();
        Elements courses = document.select(".newsFeed_item_link");
        String[] arrContents = new String[1];
        arrContents[0] = " Yahooのスクレイピング";
        List<String> contents = new ArrayList<>(Arrays.asList(arrContents));
        for (Element course : courses) {
            contents.add(course.attr("href") + "\n" + course.text());
        }
        arrContents = new String[contents.size()];
        arrContents = contents.toArray(arrContents);
        return arrContents;
    }

    private final String TOKEN = "xxxxxxxxx";

    public void notify(String message) {
        HttpURLConnection connection = null;
        try {
            URL url = new URL("https://notify-api.line.me/api/notify");
            connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.addRequestProperty("Authorization", "Bearer " + TOKEN);
            try (OutputStream os = connection.getOutputStream();
                    PrintWriter writer = new PrintWriter(os)) {
                writer.append("message=").append(URLEncoder.encode(message, "UTF-8")).flush();
                try (InputStream is = connection.getInputStream();
                        BufferedReader r = new BufferedReader(new InputStreamReader(is))) {
                    String res = r.lines().collect(Collectors.joining());
                    if (!res.contains("\"message\":\"ok\"")) {
                        System.out.println(res);
                    }
                }
            }
        } catch (Exception ignore) {
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
}