package cn.kt.cdsbspider.webmagic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Spider;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class RunSpiderTask {

    private final static Logger logger = LoggerFactory.getLogger(RunSpiderTask.class);
    ExecutorService cachedThreadPool = Executors.newFixedThreadPool(10);

    @Autowired
    IndexPageProcessor indexPageProcessor;

    public void run() {
        logger.info("开始查询商报新闻。。。");
        ArrayList<String> sburls = new ArrayList<String>() {{
            add("http://www.cdsb.com/Home/index/index/channel_id/2.html");//推荐
            add("http://www.cdsb.com/Home/index/index/channel_id/17.html");//爱上钓鱼
            add("http://www.cdsb.com/Home/index/index/channel_id/18.html");//成都出发
            add("http://www.cdsb.com/Home/index/index/channel_id/16.html");//成都伙食
//            add("http://www.cdsb.com/Home/index/index/channel_id/22.html");//时尚生活 -url存数据库是空
            add("http://www.cdsb.com/Home/index/index/channel_id/12.html");//教育
            add("http://www.cdsb.com/Home/index/index/channel_id/7.html");//成都财经
            add("http://www.cdsb.com/Home/index/index/channel_id/13.html");//成都房产
            add("http://www.cdsb.com/Home/index/index/channel_id/23.html");//成都名医
        }};

        for (String sburl : sburls) {
            try {

                cachedThreadPool.execute(new Runnable() {
                    @Override
                    public void run() {
                        logger.info("开始抓取" + sburl);
                        Spider.create(indexPageProcessor).addUrl(sburl).run();
                    }
                });


//                new Thread(new Runnable() {
////                    @Override
////                    public void run() {
////                        logger.info("开始抓取" + sburl);
////                        Spider.create(indexPageProcessor).addUrl(sburl).run();
////                    }
////                }).start();


            } catch (Exception e) {
                e.printStackTrace();
                logger.error(sburl + "抓取失败");
            }
        }
    }


}
