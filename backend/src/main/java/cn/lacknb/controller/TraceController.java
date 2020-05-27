package cn.lacknb.controller;

import cn.lacknb.enums.ErrorEnum;
import cn.lacknb.pojo.TraceInfo;
import cn.lacknb.schdule.TraceTask;
import cn.lacknb.service.ExpressBaidu;
import cn.lacknb.service.ExpressTrace;
import cn.lacknb.service.MailService;
import cn.lacknb.service.TraceInfoService;
import cn.lacknb.utils.Result;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 * ClassName: TraceController <br/>
 * Description:  <br/>
 * date: 2020年05月02日 20:06 <br/>
 *
 * @author nianshao <br/>
 */
@RestController
@RequestMapping("/trace")
@CrossOrigin("*")
public class TraceController {

    @Autowired
    private TraceInfoService traceInfoService;

    @Autowired
    private ExpressTrace expressTrace;

    @Autowired
    private MailService mailService;

    @Autowired
    private TraceTask traceTask;

    @Autowired
    private ExpressBaidu expressBaidu;

    /*
    * 邮件发送测试
    * */
    @RequestMapping("/test")
    public String test () throws IOException {
        String auto = JSON.parseObject(selectExpress("auto", "0000000000000"))
                .getJSONObject("info")
                .getJSONArray("context").getString(0);
        mailService.sendSimpleMail("1902436747@qq.com","物流跟踪", auto);
        return auto;
    }

    /*
    * 快递跟踪 测试
    * */
    @RequestMapping("/trace")
    public String trace ()  {
        traceTask.mainTrace();
        return "ok";
    }


    /**
     * 快递跟踪，添加单号以及相关信息
     * @param traceInfo
     * @return
     * @throws IOException
     */
    @RequestMapping("/addTrace")
    public Result addTrace (TraceInfo traceInfo) throws IOException {
        try {
            if ("auto".equals(traceInfo.getExpressName())) {
                traceInfo.setExpressName(expressTrace.getExpressName(traceInfo.getExpressNumber()));
            }
            traceInfoService.addTraceInfo(traceInfo);
            // 添加之后，查询一下
            trace();
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error(ErrorEnum.PATH_NOT_FOUND);
        }

        return new Result();

    }

    /**
     * 物流查询
     * @param expressName 快递的名字
     * @param expressNumber 快递单号
     * @return
     * @throws IOException
     */
    @RequestMapping("/express")
    public String selectExpress(String expressName, String expressNumber) throws IOException {
        if (!StringUtils.isEmpty(expressName) && !StringUtils.isEmpty(expressNumber)) {
            String latestMessage = expressBaidu.getLatestMessage(expressName, expressNumber);
            if (null != latestMessage) {
                String msg = null;
                try {
                    msg = JSON.parseObject(latestMessage).getJSONObject("info").getString("current");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (msg != null && !"暂无数据".equals(msg)) {
                    return latestMessage;
                } else {
                    String newName;
                    try {
                        newName = expressTrace.getExpressName(expressNumber);
                        System.out.println(newName);
                        return expressBaidu.getLatestMessage(newName, expressNumber);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return "{\"info\": {\"current\": \"暂无数据\"}}";

    }

}
