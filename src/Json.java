import com.google.gson.Gson;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

public class Json {
    String url;

    public Json(String isbn){
        this.url = "https://www.googleapis.com/books/v1/volumes?q=isbn:"+isbn;
    }

    public String[] getGsonBookData() {
        String[] out = new String[4];
        Container gson;
        try {
            URL uri = new URL(url);
            InputStreamReader reader = new InputStreamReader(uri.openStream());
            gson = new Gson().fromJson(reader, Container.class);
            out[0] = gson.items[0].volumeInfo.title;
            out[1] = gson.items[0].volumeInfo.authors[0];
            out[2] = gson.items[0].volumeInfo.publishedDate;
            out[3] = Integer.toString(gson.items[0].volumeInfo.pageCount);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        System.out.println(out[0]);
        return out;
    }

    private class Container {
        Item[] items;
    }

    private class Item {
        VolumeInfo volumeInfo;

    }

    private class VolumeInfo {
        String title;
        String[] authors;
        String publishedDate;
        int pageCount;
    }
}