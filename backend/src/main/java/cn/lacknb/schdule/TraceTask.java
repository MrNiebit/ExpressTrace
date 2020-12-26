package cn.lacknb.schdule;

import cn.lacknb.controller.TraceController;
import cn.lacknb.exception.ExpressException;
import cn.lacknb.pojo.TraceInfo;
import cn.lacknb.service.MailService;
import cn.lacknb.service.TraceInfoService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.servlet.ServletContext;
import java.util.List;

/**
 * ClassName: TraceTask <br/>
 * Description:  <br/>
 * date: 2020年05月02日 20:31 <br/>
 *
 * @author nianshao <br/>
 */
@Component
@Slf4j
public class TraceTask {


    @Autowired
    private TraceController traceController;

    @Autowired
    private TraceInfoService traceInfoService;

    @Autowired
    private MailService mailService;

    @Autowired
    private ServletContext context;

    /**
     * Description:
     *  每次执行，
     *      1. 从数据库中获取所有mail和单号
     *      2. 依次查询，先查询是否 是否有数据
     *          - 若没有数据，删除该mail相关信息
     *          - 若有 记住当前状态物流信息，与上次物流信息进行对比，若更新了，发送邮件； 否则不进行任何操作
     *
     *
     *
     * <br/>
     * @date: 20-5-2 下午8:34 <br/>
     * @param: [] <br/>
     * @return:void
     */
    // 从25分开始，每间隔25分钟执行一次。也就是 每个整点执行一次。
    @Scheduled(cron = "0 25/25 * * * ? ")
//    @Scheduled(fixedRate = 2000)
//    @Async
    public void mainTrace () {

        log.info("跟踪执行。。。。。。");
        List<TraceInfo> allTraceInfo = traceInfoService.findAllTraceInfo();
        String KEY = null;
        for (TraceInfo traceInfo : allTraceInfo) {
            KEY = traceInfo.getMail() + traceInfo.getExpressNumber();
            Integer lastTrace = (Integer) context.getAttribute(KEY);
            String status = null;
            try {
                JSONObject jsonObject = JSON.parseObject(traceController.selectExpress(traceInfo.getExpressName(), traceInfo.getExpressNumber())).getJSONObject("info");
                status = jsonObject.getString("current");
                // 如果状态为空，或者是 没找数据
                if (status == null || "暂无数据".equals(status)) {
                    // 删除该单号
//                    traceInfoService.deleteTranceInfo(traceInfo);
                    // 修改成 每天凌晨0点清理 暂无数据的 单号
                    context.removeAttribute(KEY);
                } else if ("已签收".equals(status)) {
                    // 签收之后
                    mailService.sendHtmlMail(traceInfo.getMail(), "物流跟踪", "单号：" + traceInfo.getExpressNumber() + "</br> 所属快递：" + traceInfo.getExpressName() + "</br>" + jsonObject.getString("latest_time") + "== ： " + jsonObject.getString("latest_progress"));
                    traceInfoService.deleteTranceInfo(traceInfo);
                    context.removeAttribute(KEY);
                } else /*("运输中".equals(status) || "已揽件".equals(status))*/{
                    if (lastTrace != null) {
                        int context = jsonObject.getJSONArray("context").size();
                        if (context > lastTrace) {
                            mailService.sendHtmlMail(traceInfo.getMail(), "物流跟踪", "单号：" + traceInfo.getExpressNumber() + "</br> 所属快递：" + traceInfo.getExpressName() + "</br>" + jsonObject.getString("latest_time") + "== ： " + jsonObject.getString("latest_progress"));
                        }
                    }
                    // 更新context
                    context.setAttribute(KEY, jsonObject.getJSONArray("context").size());
                }
//               // 增加1秒的延迟
                Thread.sleep(1000);
            } catch (ExpressException ex) {
                ex.printStackTrace();
                traceInfoService.deleteTranceInfo(traceInfo);
                context.removeAttribute(KEY);
            }  catch (Exception e) {
                e.printStackTrace();
            }

        }

    }

    @Scheduled(cron = "0 0 0 * * ?")
    @Async
    public void clearNull () {
        log.info("清理 无效的单号。。。。。。");
        List<TraceInfo> allTraceInfo = traceInfoService.findAllTraceInfo();
        for (TraceInfo traceInfo : allTraceInfo) {
            String status = null;
            try {
                JSONObject jsonObject = JSON.parseObject(traceController.selectExpress(traceInfo.getExpressName(), traceInfo.getExpressNumber())).getJSONObject("info");
                status = jsonObject.getString("current");
                // 如果状态为空，或者是 没找数据
                if (status == null || "暂无数据".equals(status)) {
                    // 删除该单号
                    traceInfoService.deleteTranceInfo(traceInfo);
                    // 修改成 每天凌晨0点清理 暂无数据的 单号
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

}
