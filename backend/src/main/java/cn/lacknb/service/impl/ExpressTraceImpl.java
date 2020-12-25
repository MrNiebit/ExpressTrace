package cn.lacknb.service.impl;

import cn.lacknb.service.ExpressTrace;
import com.alibaba.fastjson.JSON;
import org.jsoup.Jsoup;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * ClassName: ExpressTraceImpl <br/>
 * Description:  <br/>
 * date: 2020年05月02日 17:56 <br/>
 *
 * @author nianshao <br/>
 */
@Service
public class ExpressTraceImpl implements ExpressTrace {

    private static Map<String, String> headers = new HashMap<>();

    static {
        headers.put("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/73.0.3683.86 Safari/537.36");
    }

    @Override
    public String getExpressName (String number) throws IOException {
        Map<String, String> datas = new HashMap<>();
//        datas.put("result", "1");
//        datas.put("text", number);

        /*
          更新了，快递100 采用 Pathvariable方式 获取参数
         */

        String expressUrl = "https://www.kuaidi100" +
                ".com/autonumber/autoComNum?text=" + number;
        String name = "auto";
        try {
            String text = Jsoup.connect(expressUrl)
                    .ignoreContentType(true)
                    .headers(headers)
                    .data(datas).post().body().text();
            return JSON.parseObject(text).getJSONArray("auto").getJSONObject(0).getString("comCode");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return name;
    }

}
