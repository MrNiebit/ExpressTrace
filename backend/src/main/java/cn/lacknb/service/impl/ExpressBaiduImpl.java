package cn.lacknb.service.impl;

import cn.lacknb.enums.ErrorEnum;
import cn.lacknb.service.ExpressBaidu;
import cn.lacknb.utils.Result;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * ClassName: ExpressBaiduImpl <br/>
 * Description:  <br/>
 * date: 2020年04月15日 19:15 <br/>
 *
 * @author nianshao <br/>
 */
@Service
@Slf4j
public class ExpressBaiduImpl implements ExpressBaidu {

    private static Map<String, String> map = new HashMap<>();
    private static final String URL = "https://www.baidu.com/s?wd=%E5%BF%AB%E9%80%92%E6%9F%A5%E8%AF%A2";
    private static final Pattern P = Pattern.compile("apiUrl: (.*)");
    private static String apiUrl = "https://express.baidu.com/express/api/express?query_from_srcid=&tokenV2=vOP02-p-ZXnuozCx_dkaGpodfRYtROPD884FxtiL89Vn5EzXNxQqB0sRxhaMvPK3";

    static {
        map.put("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/73.0.3683.86 Safari/537.36");
        map.put("Content-type", "application/x-www-form-urlencoded");
        map.put("Cookie", "PSTM=1586697712; BIDUPSID=01B9F3C057BE8E367BF78861C6D46C69; BAIDUID=80D581BD7CB3D2FAA3E686FCB1709C7F:FG=1; BD_UPN=123353; BDORZ=B490B5EBF6F3CD402E515D22BCDA1598; ispeed_lsm=2; BDSFRCVID=38POJeC62AC8P5OurxQpeiargHhXIjvTH6aoIbOX9GwHCxP3GYAkEG0PoU8g0Ku-S2VIogKKBeOTHgAF_2uxOjjg8UtVJeC6EG0Ptf8g0f5; H_BDCLCKID_SF=tRAOoCt5tCvjDb7GbKTD-tFO5eT22-ustbbT2hcH0KLKEq8905oc5JDb0q3-a4oH52b3hpT_bfb1MRjvyt5cW5F15P7mhp8e3a5CBh5TtUJoSDnTDMRhqqJX34ryKMni0DT9-pnj0lQrh459XP68bTkA5bjZKxtq3mkjbPbDfn02eCKu-n5jHjobjGDj3H; BDUSS=5CZWZncm8wcDNWfnJrN0FBQTBmSEdmWTMwcnpzbXM1cXVjdmlkdW9HeC05YjFlRUFBQUFBJCQAAAAAAAAAAAEAAAAveM7FcHVycGxlZmxvd2Vyb2sAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAH5oll5-aJZeUk; H_PS_PSSID=1437_31169_21094_31186_30905_31271_30823_31085_26350_31164; delPer=0; BD_CK_SAM=1; PSINO=1; BDRCVFR[M7pOaqtZgJR]=I67x6TjHwwYf0; H_PS_645EC=690cm9k%2BautZn11%2B1YG1AM%2FZMktNgHf8%2Bwz3TrTjvfLNqGzhJaCmH%2Fmxc7o; BDSVRTM=0; COOKIE_SESSION=9_0_5_7_0_5_0_0_5_3_1_3_0_0_0_0_0_0_1586916513%7C9%23212569_3_1586915440%7C2");
    }

    @Override
    public String getLatestMessage(String expressName, String expressNumber) {
        String text = null;
        apiUrl = getApiUrl();
        if (null != apiUrl) {
            try {
                     text = Jsoup.connect(apiUrl + "&appid=4001&com=" + expressName + "&nu=" + expressNumber)
                         .ignoreContentType(true)
                        .headers(map).get().body().text();
//                JSONObject jsonObject = new JSONObject    (text);
//                 if (!"1".equals(jsonObject.getString("error_code"))) {
//                     log.info("失效。。。。重新获取");
//                     apiUrl = getApiUrl();
//                     text = getLatestMessage(expressName, expressNumber);
//                 }
                text = JSON.parseObject(text).get("data").toString();
            } catch (Exception e) {
                e.printStackTrace();
                return JSON.toJSONString(Result.error(ErrorEnum.PATH_NOT_FOUND));
            }
        }
        return text;
    }

    public String getApiUrl() {

        String apiUrl = null;
        try{
            String text = Jsoup.connect(URL).ignoreContentType(true).headers(map)
                    .get().html();
            Matcher m = P.matcher(text);
            while (m.find()) {
                apiUrl = m.group();
            }
            apiUrl = apiUrl.replace("apiUrl: ", "").replaceAll("'", "").replaceAll(",", "");
            log.info(apiUrl);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return apiUrl;

    }
}
