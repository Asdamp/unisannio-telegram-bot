//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.antonioaltieri.unisannio.avvisi;

import java.io.IOException;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class PostFeedServlet extends HttpServlet {
    UniIngBot bot;
    Logger log = Logger.getLogger("sendNewFeeds");

    public PostFeedServlet() {
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (this.bot == null) {
            this.bot = new UniIngBot();
            this.bot.start();
            this.bot.setWebHook(UniIngToken.WEBHOOK_URL);
        }

        this.bot.sendNewFeed(UniIngToken.CHANNEL_NAME);
    }
}
