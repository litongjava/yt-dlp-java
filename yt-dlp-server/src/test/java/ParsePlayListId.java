import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.Test;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;

import nexus.io.tio.utils.json.FastJson2Utils;
import nexus.io.yt.model.YtVideoEntry;

public class ParsePlayListId {

  @Test
  public void ids() throws IOException {
    String filepath = "C:\\Users\\Administrator\\Desktop\\01.json";
    String string = Files.readString(Paths.get(filepath));
    JSONObject jsonObject = FastJson2Utils.parseObject(string);
    JSONArray entries = jsonObject.getJSONArray("entries");
    for (int i = 0; i < entries.size(); i++) {
      YtVideoEntry entry = entries.getObject(i, YtVideoEntry.class);
      if (!entry.getTitle().equals("[Private video]")) {
        System.out.println(entry.getId());
      }
    }
  }
}
