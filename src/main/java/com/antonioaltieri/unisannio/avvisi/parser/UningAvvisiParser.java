//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.antonioaltieri.unisannio.avvisi.parser;

import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import java.io.IOException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;

public class UningAvvisiParser {
    URL url = new URL("http://www.ding.unisannio.it/html/rss/rss.php");

    public UningAvvisiParser() throws Exception {
    }

    public List<SyndEntry> getStream() throws IOException, FeedException {
        XmlReader reader = null;

        List<SyndEntry> newsList;
        try {
            reader = new XmlReader(this.url);
            SyndFeed feed = (new SyndFeedInput()).build(reader);
            newsList = feed.getEntries();
        } finally {
            if (reader != null) {
                reader.close();
            }

        }

        return newsList;
    }

    public void printRssStream() throws IOException, FeedException {
        Iterator var1 = this.getStream().iterator();

        while(var1.hasNext()) {
            SyndEntry entry = (SyndEntry)var1.next();
            System.out.println("titolo:" + entry.getTitle().replace("<br />", ""));
            System.out.println("link:" + entry.getLink().replace("<br />", ""));
            System.out.println("descrizione:" + entry.getDescription().getValue().replace("<br />", "") + "\n");
        }

    }
}
