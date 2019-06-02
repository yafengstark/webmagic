package us.codecraft.webmagic.example;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.monitor.SpiderMonitor;
import us.codecraft.webmagic.pipeline.ConsolePipeline;
import us.codecraft.webmagic.pipeline.FilePipeline;
import us.codecraft.webmagic.pipeline.JsonFilePipeline;
import us.codecraft.webmagic.processor.PageProcessor;

import javax.management.JMException;

/**
 * @author code4crafter@gmail.com <br>
 * @since 0.3.2
 */
public class GithubRepoPageProcessor implements PageProcessor {

    // 部分一：抓取网站的相关配置，包括编码、抓取间隔、重试次数等
    // 一些模拟参数，代理的设置
    private Site site = Site.me()
            .setRetryTimes(3) // 重试参数为3
            .setSleepTime(1000).setTimeOut(10000);

    // process是定制爬虫逻辑的核心接口，在这里编写抽取逻辑
    @Override
    public void process(Page page) {
        // 从页面发现后续的url地址来抓取
        page.addTargetRequests(page.getHtml().links().regex("(https://github\\.com/[\\w\\-]+/[\\w\\-]+)").all());
        page.addTargetRequests(page.getHtml().links().regex("(https://github\\.com/[\\w\\-])").all());
        // 定义如何抽取页面信息，并保存下来
        // WebMagic里主要使用了三种抽取技术：XPath、正则表达式和CSS选择器。
        // 另外，对于JSON格式的内容，可使用JsonPath进行解析。
        page.putField("author", page.getUrl().regex("https://github\\.com/(\\w+)/.*").toString());

        page.putField("name",
                page.getHtml().xpath("//h1[@class='public']/strong/a/text()").toString());

        if (page.getResultItems().get("name")==null){
            //skip this page
            page.setSkip(true);
        }
        page.putField("readme", page.getHtml().xpath("//div[@id='readme']/tidyText()"));
    }

    @Override
    public Site getSite() {
        return site;
    }

    public static void main(String[] args) {
        Spider githubSpider = Spider.create(new GithubRepoPageProcessor())
                //从"https://github.com/code4craft"开始抓
                .addUrl("https://github.com/code4craft")
                // 指定各种保存方式
                // FilePipeline()
                .addPipeline(new JsonFilePipeline("E:\\webmagic\\"))
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
