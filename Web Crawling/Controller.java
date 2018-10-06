import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;

public class Controller {

    public static void main(String[] args) throws Exception {

        String crawlStorageFolder = "data/crawl";
        int numberOfCrawlers = 7;
        CrawlConfig config = new CrawlConfig();
        config.setIncludeBinaryContentInCrawling(true);
        config.setCrawlStorageFolder(crawlStorageFolder);
        config.setMaxPagesToFetch(20000);
        config.setMaxDepthOfCrawling(16);
        config.setPolitenessDelay(300);

        PageFetcher pageFetcher = new PageFetcher(config);
        RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
        RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
        CrawlController controller = new CrawlController(config, pageFetcher, robotstxtServer);

        controller.addSeed("https://www.latimes.com/");
        controller.start(MyCrawler.class, numberOfCrawlers);
    }
}
