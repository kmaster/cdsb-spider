package cn.kt.cdsbspider.webmagic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Spider;

@Component
public class RunSpiderTask{

    private final static Logger logger = LoggerFactory.getLogger(RunSpiderTask.class);

    @Autowired
    IndexPageProcessor indexPageProcessor;

    public void run(){
        try {
            logger.info("开始查询商报新闻。。。");
            Spider.create(indexPageProcessor).addUrl("http://www.cdsb.com/").run();
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("http://www.cdsb.com/ 抓取失败");
        }

    }


}
