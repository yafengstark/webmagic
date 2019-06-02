package us.codecraft.webmagic;



import us.codecraft.webmagic.monitor.SpiderMonitor;
import us.codecraft.webmagic.pipeline.ConsolePipeline;
import us.codecraft.webmagic.pipeline.FilePipeline;
import us.codecraft.webmagic.pipeline.JsonFilePipeline;
import us.codecraft.webmagic.processor.PageProcessor;

import javax.management.JMException;

/**
 * @author tony
 * @date 2019/6/2 19:52
 */
public class DoubanRentHouseDemo implements PageProcessor {

    // 部分一：抓取网站的相关配置，包括编码、抓取间隔、重试次数等
    // 一些模拟参数，代理的设置
    private Site site = Site.me()
            .setDomain("movie.douban.com")
            .setUserAgent(
                    "Mozilla/5.0 (Windows NT 6.2; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/31.0.1650.59 Safari/537.36")
            .addCookie("gr_user_id", "e9674f00-4d95-4c48-bb61-70b7ed19c811")
            .addCookie("ll", "118159")
            .addCookie(
                    "viewed",
                    "25885921_6709809_6811366_10750155_6709783_1089243_1016107_3224524_25965995_3735649")
            .addCookie("bid", "v205EAKxUho")
            .addCookie("_vwo_uuid_v2",
                    "9736679276E1838D988AE94ADBE05679|252f3512dd984088c29c6b5bea8804fa")
            .addCookie("ps", "y")
            .addCookie(
                    "_pk_ref.100001.8cb4",
                    "%5B%22%22%2C%22%22%2C1488798908%2C%22https%3A%2F%2Faccounts.douban.com%2Flogin%3Falias%3D15751866073%26redir%3Dhttps%253A%252F%252Fwww.douban.com%252Fgroup%252Fsearch%253Fstart%253D0%2526cat%253D1019%2526sort%253Drelevance%2526q%253D%2525E8%2525AF%2525B7%2525E4%2525B8%25258D%2525E8%2525A6%252581%2525E5%2525AE%2525B3%2525E7%2525BE%25259E%26source%3DNone%26error%3D1027%22%5D")
            .addCookie("__utmt", "1")
            .addCookie("ap", "1")
            .addCookie("ue", "452756565@qq.com")
            .addCookie("dbcl2", "45483389:8BjNYkc8Il8")
            .addCookie("ck", "ZdMH")
            .addCookie("push_noty_num", "0")
            .addCookie("push_doumail_num", "0")
            .addCookie("_pk_id.100001.8cb4",
                    "47b2976ee4f3a82f.1427790671.69.1488800072.1488471613.")
            .addCookie("_pk_ses.100001.8cb4", "*")
            .addCookie("__utma",
                    "30149280.1333754830.1427790902.1488467949.1488798909.74")
            .addCookie("__utmb", "30149280.56.5.1488800072003")
            .addCookie("__utmc", "30149280")
            .addCookie(
                    "__utmz",
                    "30149280.1488467949.73.24.utmcsr=accounts.douban.com|utmccn=(referral)|utmcmd=referral|utmcct=/login")
            .addCookie("__utmv", "30149280.4548")
            .addHeader("Accept",
                    "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
            .addHeader("Accept-Encoding", "gzip,deflate,sdch")
            .addHeader("Accept-Language", "zh-CN,zh;q=0.8")
            .addHeader("Connection", "keep-alive")
            .addHeader("Cache-Control", "max-age=0")
//            .addCookie("_vwo_uuid_v2","D9723EAD13343CC24253750F4491CDE2|d586d384e3a32dd198e1b242af255348")
//    _vwo_uuid_v2=D9723EAD13343CC24253750F4491CDE2|d586d384e3a32dd198e1b242af255348;)
            .setRetryTimes(3) // 重试参数为3
            .setSleepTime(1000).setTimeOut(10000);

    // process是定制爬虫逻辑的核心接口，在这里编写抽取逻辑
    @Override
    public void process(Page page) {
        // 从页面发现后续的url地址来抓取
//        page.addTargetRequests(page.getHtml().links().regex("(https://github\\.com/[\\w\\-]+/[\\w\\-]+)").all());
//        page.addTargetRequests(page.getHtml().links().regex("(https://github\\.com/[\\w\\-])").all());
        // 定义如何抽取页面信息，并保存下来
        // WebMagic里主要使用了三种抽取技术：XPath、正则表达式和CSS选择器。
        // 另外，对于JSON格式的内容，可使用JsonPath进行解析。
//        page.putField("author", page.getUrl().regex("https://github\\.com/(\\w+)/.*").toString());

        page.putField("标题",
                page.getHtml().xpath("//table[@class='olt']/tr/span/text()").toString());

        if (page.getResultItems().get("name")==null){
            //skip this page
            page.setSkip(true);
        }
//        page.putField("readme", page.getHtml().xpath("//div[@id='readme']/tidyText()"));
    }

    @Override
    public Site getSite() {
        return site;
    }

    public static void main(String[] args) {
        Spider githubSpider = Spider.create(new us.codecraft.webmagic.example.GithubRepoPageProcessor())
                //从"https://github.com/code4craft"开始抓
                .addUrl("https://www.douban.com/group/shanghaizufang/")
                // 指定各种保存方式
                // FilePipeline()
                .addPipeline(new FilePipeline("E:\\webmagic\\"))
                .thread(5);

//        将结果输出到控制台
        githubSpider.addPipeline(new ConsolePipeline());

        try {
            SpiderMonitor.instance().register(githubSpider);
            githubSpider.start();
        } catch (JMException e) {
            e.printStackTrace();
        }
    }
}

