package cn.kt.cdsbspider.webmagic;

import cn.kt.cdsbspider.dao.SbrecordDao;
import cn.kt.cdsbspider.domain.Sbrecord;
import cn.kt.cdsbspider.domain.SbrecordExample;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Html;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 爬虫处理类
 */
@Component
public class IndexPageProcessor extends Thread implements PageProcessor {


    @Autowired
    ItemPageProcessor itemPageProcessor;

    @Autowired
    SbrecordDao sbrecordDao;


    private final static Logger logger = LoggerFactory.getLogger(IndexPageProcessor.class);
    ExecutorService cachedThreadPool = Executors.newFixedThreadPool(40);


    //CountDownLatch 作为计数器记录线程
    private static CountDownLatch latch = new CountDownLatch(9);

    //原子计数变量
    private static AtomicInteger allCount = new AtomicInteger(0);

    //原子计数变量
    private static AtomicInteger pageCount = new AtomicInteger(1);

    public IndexPageProcessor() {
    }

    public IndexPageProcessor(CountDownLatch countDownLatch) {
        this.latch = countDownLatch;
    }

    //抓取配置
    private Site site = Site.me().setRetryTimes(30).setCharset("utf-8").setTimeOut(300000)
            .setUserAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_2) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.65 Safari/537.31");


    @Override
    public void process(Page page) {

        //获取当前html文本
        Html html = page.getHtml();

        //获取所有文章链接
//        https://static.cdsb.com/micropub/Articles/201812/e5c6930ff047a0218e516f84414d2478.html
        List<String> detailUrl = new ArrayList<>();
        detailUrl.clear();

        detailUrl = html.links().regex("https://static.cdsb.com/micropub/Articles\\/\\d*\\/\\w*.html").all();

        AtomicInteger currentCount = new AtomicInteger(0);
        for (String itemurl : detailUrl) {
            try {
                SbrecordExample sbrecordExample = new SbrecordExample();
                sbrecordExample.createCriteria().andUrlEqualTo(itemurl);
                List<Sbrecord> sbrecords = sbrecordDao.selectByExample(sbrecordExample);

                if (sbrecords == null || sbrecords.size() == 0) {



                    cachedThreadPool.execute(new Runnable() {
                        @Override
                        public void run() {
                            Spider.create(itemPageProcessor).addUrl(itemurl).run();
                        }
                    });
                    currentCount.getAndIncrement();
                }
            } catch (Exception e) {
                logger.error("当前url {} 抓取失败", itemurl);
                e.printStackTrace();
            }

        }
        logger.info("查新完成，本次抓取url数量： {}", currentCount.get());


    }

    @Override
    public Site getSite() {
        return site;
    }

    @Override
    public void run() {
        logger.info("run");
    }
}
