//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.antonioaltieri.unisannio.avvisi;

import com.antonioaltieri.telegram.botapi.TelegramBot;
import com.antonioaltieri.telegram.botapi.types.Update;
import com.google.gson.Gson;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class UniIngServlet extends HttpServlet {
    TelegramBot bot;

    public UniIngServlet() {
    }

    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("text/plain");
        resp.getWriter().println("Bot avviato");
        this.bot = new UniIngBot();
        this.bot.start();
        this.bot.setWebHook(UniIngToken.WEBHOOK_URL);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (this.bot == null) {
            this.bot = new UniIngBot();
            this.bot.start();
            this.bot.setWebHook(UniIngToken.WEBHOOK_URL);
        }

        ServletInputStream in = req.getInputStream();
        String json = readAll(in);
        Gson gson = new Gson();
        Update upd = gson.fromJson(json, Update.class);
        this.bot.notifyNewUpdate(upd);
    }

    private static String readAll(InputStream input) {
        Scanner scanner = new Scanner(input);
        scanner.useDelimiter("\\A");
        return scanner.hasNext() ? scanner.next() : null;
    }
}
