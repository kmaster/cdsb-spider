package cn.kt.cdsbspider.webmagic;

import cn.kt.cdsbspider.dao.SbrecordDao;
import cn.kt.cdsbspider.domain.Sbrecord;
import cn.kt.cdsbspider.domain.SbrecordExample;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Html;
import us.codecraft.webmagic.selector.Selectable;

import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 爬虫处理类
 */
@Component
public class ItemPageProcessor extends Thread implements PageProcessor {

    @Autowired
    SbrecordDao sbrecordDao;

    @Autowired
    JavaMailSender jms;
    private final static Logger logger = LoggerFactory.getLogger(ItemPageProcessor.class);

    ExecutorService cachedThreadPool = Executors.newCachedThreadPool();

    //CountDownLatch 作为计数器记录线程
    private static CountDownLatch latch = new CountDownLatch(9);

    //原子计数变量
    private static AtomicInteger urlCount = new AtomicInteger(0);

    //原子计数变量
    private static AtomicInteger pageCount = new AtomicInteger(1);

    public ItemPageProcessor() {
    }

    public ItemPageProcessor(CountDownLatch countDownLatch) {
        this.latch = countDownLatch;
    }

    //抓取配置
    private Site site = Site.me().setRetryTimes(30).setCharset("utf-8").setTimeOut(300000)
            .setUserAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_2) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.65 Safari/537.31");


    @Override
    public void process(Page page) {
        //获取当前html文本
        Html html = page.getHtml();

        //获取当前html页面的所有class名称为detail-wrapper的div下的所有class名称为header下的a标签里面的img标签中的src地址
        Object s0 = page.getHtml().xpath("//div[@class='detail-wrapper']//div[@class='header ']/a/img/@src");

        Sbrecord sbrecord = new Sbrecord();
        sbrecord.setCreatetime(new Date());

        Selectable xpath = html.xpath("//article[@class=\"cd-article_content\"]");


        String content = xpath.toString();
        if (content != null && content.length() > 10000) {
            content = content.substring(0, 9995);
        }
//        sbrecord.setContent(content);

        List<Selectable> detailItem2 = html.xpath("//script [@type=\"text/javascript\"]").nodes();
        RegexUtil regexUtil = new RegexUtil();
        for (Selectable selectable1 : detailItem2) {
            if (selectable1.toString().contains("pageInfo")) {
                String pageInfo = selectable1.toString();

                String sbid = regexUtil.getSubUtilSimple(pageInfo, "_id: '(.*?)',");
                String title = regexUtil.getSubUtilSimple(pageInfo, "title: '(.*?)',");
                String sourceType = regexUtil.getSubUtilSimple(pageInfo, "sourceType: '(.*?)',");
                String desc = regexUtil.getSubUtilSimple(pageInfo, "desc: '(.*?)',");
                String img = regexUtil.getSubUtilSimple(pageInfo, "img: '(.*?)',");
                String env = regexUtil.getSubUtilSimple(pageInfo, "env: '(.*?)',");
                String url = regexUtil.getSubUtilSimple(pageInfo, "url: '(.*?)' +");

                sbrecord.setSbid(sbid);
                sbrecord.setTitle(title);
                sbrecord.setSourcetype(sourceType);
                sbrecord.setDesc(desc);
                sbrecord.setImg(img);
                sbrecord.setEnv(env);
                sbrecord.setUrl(url);
            }


            if (selectable1.toString().contains("nlJson.postauthor")) {
                String authorInfo = selectable1.toString();
                String name = regexUtil.getSubUtilSimple(authorInfo, "nlJson.postauthor = \"(.*?)\";");
                String category = regexUtil.getSubUtilSimple(authorInfo, "nlJson.category = \"(.*?)\";");
                String posttime = regexUtil.getSubUtilSimple(authorInfo, "nlJson.posttime = \"(.*?)\";");

                sbrecord.setName(name);
                sbrecord.setCategory(category);
                sbrecord.setPosttime(posttime);
            }


        }
        SbrecordExample sbrecordExample = new SbrecordExample();
        sbrecordExample.createCriteria().andSbidEqualTo(sbrecord.getSbid());
        List<Sbrecord> sbrecords = sbrecordDao.selectByExample(sbrecordExample);
        if (sbrecords == null || sbrecords.size() == 0) {
            sbrecordDao.insert(sbrecord);
            logger.info("已添加新纪录：" + sbrecord);


            cachedThreadPool.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        if("唐欢".equals(sbrecord.getName())){
                            SimpleMailMessage message = new SimpleMailMessage();
                            message.setFrom("18116628807@163.com");
                            message.setTo("396367775@qq.com");
                            message.setSubject("商报有新news");
                            message.setText("编辑:" + sbrecord.getName() + "类型:" + sbrecord.getCategory() + ",标题:" + sbrecord.getTitle() + ",url:" + sbrecord.getUrl());

                            int sleeptime = (int) (Math.random() * 10) * 1000 * 2;
                            Thread.sleep(sleeptime);
                            jms.send(message);
                            logger.info("邮件发送成功，编辑:" + sbrecord.getName() + ",新闻标题是:" + sbrecord.getTitle());
                        }

                    } catch (Exception e) {
                        logger.error("邮件发送失败 " + e.getMessage());
                        e.printStackTrace();
                    }

                }
            });



//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    try {
//                        SimpleMailMessage message = new SimpleMailMessage();
//                        message.setFrom("18116628807@163.com");
//                        message.setTo("396367775@qq.com");
//                        message.setSubject("商报有新news");
//                        message.setText("编辑:" + sbrecord.getName() + ",标题:" + sbrecord.getTitle() + ",url:" + sbrecord.getUrl());
//
//                        int sleeptime = (int) (Math.random() * 10) * 1000 * 2;//随机了1-10分钟
////                        Thread.sleep(sleeptime);
////
////                        jms.send(message);
//                        logger.info("邮件发送成功，编辑:" + sbrecord.getName() + ",新闻标题是:" + sbrecord.getTitle());
//                    } catch (Exception e) {
//                        logger.error("邮件发送失败 " + e.getMessage());
//                        e.printStackTrace();
//                    }
//                }
//            }).start();


        } else {
            logger.info("此新闻已存在，不再添加");
        }


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
