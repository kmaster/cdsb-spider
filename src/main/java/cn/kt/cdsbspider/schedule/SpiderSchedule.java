package cn.kt.cdsbspider.schedule;


import cn.kt.cdsbspider.dao.SbrecordDao;
import cn.kt.cdsbspider.webmagic.RunSpiderTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Created by Admin on 2018/6/7.
 */
@Component
public class SpiderSchedule {
    private final static Logger logger = LoggerFactory.getLogger(SpiderSchedule.class);

    @Autowired
    RunSpiderTask runSpiderTask;

    @Autowired
    SbrecordDao sbrecordDao;

    //    每分钟启动
    @Scheduled(cron = "* 0/15 * * * ?")
//    @Scheduled(cron = "0/10 * * * * ?")
    public void timerToNow() {

        try {
            int sleeptime = (int) (Math.random() * 10) * 1000 * 30;//随机等待30-300秒
            logger.info("定时延时{}毫秒执行", sleeptime);
            Thread.sleep(sleeptime);

            runSpiderTask.run();

        } catch (Exception e) {
            e.printStackTrace();
            logger.info("定时任务执行出错 " + e.getMessage());
        }


    }


}
