//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.antonioaltieri.unisannio.avvisi;

import com.antonioaltieri.telegram.botapi.CommandHandler;
import com.antonioaltieri.telegram.botapi.DefaultHandler;
import com.antonioaltieri.telegram.botapi.TelegramBot;
import com.antonioaltieri.telegram.botapi.requests.OptionalArgs;
import com.antonioaltieri.telegram.botapi.types.Message;
import com.antonioaltieri.telegram.botapi.types.ParseModes;
import com.antonioaltieri.telegram.botapi.types.StringHTML;
import com.antonioaltieri.unisannio.avvisi.parser.UningAvvisiParser;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.io.FeedException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

public class UniIngBot extends TelegramBot {
    private int chatWithMe = UniIngToken.CHAT_WITH_ME;

    public UniIngBot() {
        super(UniIngToken.TOKEN);
    }

    @DefaultHandler
    public void handleDefault(Message message) {
    }

    @CommandHandler({"feed"})
    public void handleFeed(Message message) {
        if (message.getFrom().getUsername().equals(UniIngToken.USERNAME_FOR_DEBUG)) {
            this.sendNewFeed(String.valueOf(message.getChat().getId()), false);
        }

    }

    @CommandHandler({"debug"})
    public void debug(Message message) {
        if (message.getFrom().getUsername().equalsIgnoreCase(UniIngToken.USERNAME_FOR_DEBUG)) {
            this.sendMessage(message.getChat().getId(), String.valueOf(message.getChat().getId()));
        }

    }

    @CommandHandler({"clean"})
    public void cleanLast(Message message) {
        if (message.getFrom().getUsername().equalsIgnoreCase(UniIngToken.USERNAME_FOR_DEBUG)) {
            this.saveString((String)null);
        }

    }

    @CommandHandler({"set"})
    public void setLink(Message message) {
        if (message.getFrom().getUsername().equalsIgnoreCase(UniIngToken.USERNAME_FOR_DEBUG)) {
            this.saveString(this.extractParam(message.getText())[1]);
        }

    }

    @CommandHandler({"publish"})
    public void publish(Message message) {
        if (message.getFrom().getUsername().equals(UniIngToken.USERNAME_FOR_DEBUG)) {
            this.sendNewFeed(UniIngToken.CHANNEL_NAME, true);
        }

    }

    @CommandHandler({"feedUpdate"})
    public void setLast(Message message) {
        if (message.getFrom().getUsername().equals(UniIngToken.USERNAME_FOR_DEBUG)) {
            this.sendNewFeed(String.valueOf(message.getChat().getId()), true);
        }

    }

    @CommandHandler({"publish"})
    public void handlePublish(Message message) {
        if (message.getFrom().getUsername().equals(UniIngToken.USERNAME_FOR_DEBUG)) {
            this.sendNewFeed(this.chatWithMe + "");
        }

    }

    public void sendNewFeed(String id) {
        this.sendNewFeed(id, true);
    }

    public void sendNewFeed(String id, boolean saveLast) {
        UningAvvisiParser vp = null;
        OptionalArgs optionalArgs = (new OptionalArgs()).parsMode(ParseModes.HTML);

        try {
            vp = new UningAvvisiParser();
        } catch (Exception var13) {
            this.sendMessage((long)this.chatWithMe, "URL malformato o irragiungibile");
            return;
        }

        List feeds;
        try {
            feeds = vp.getStream();
        } catch (IOException var11) {
            this.sendMessage((long)this.chatWithMe, "Indirizzo irragiungibile.");
            return;
        } catch (FeedException var12) {
            this.sendMessage((long)this.chatWithMe, "Impossibile parsare il feed");
            return;
        }

        ArrayList<String> lastLink = this.getString();
        Stack<SyndEntry> toPublish = new Stack();
        Iterator se = feeds.iterator();

        SyndEntry i;
        while(se.hasNext()) {
            i = (SyndEntry)se.next();
            if (lastLink != null && lastLink.contains(i.getLink())) {
                break;
            }

            toPublish.push(i);
        }

        while(!toPublish.isEmpty()) {
            i = (SyndEntry)toPublish.pop();
            String toSend = StringHTML.bold(i.getTitle()) + "\n\n";
            toSend = toSend + i.getDescription().getValue();
            toSend = toSend.replace("<br />", "");
            try {

                this.sendMessage(id, toSend, optionalArgs);
                if (saveLast) {
                    this.saveString(i.getLink());
                }
            } catch (Exception var14) {
                OptionalArgs htmlModeDisabled = (new OptionalArgs()).parsMode(null);

                this.sendMessage(id, toSend, htmlModeDisabled);
                if (saveLast) {
                    this.saveString(i.getLink());
                }
            }
        }

    }

    private void saveString(String s) {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        Entity lastLink = new Entity("Queue", "lastLink");
        ArrayList<String> LLink;
        if (s != null) {
            LLink = this.getString();
            LLink.add(s);
            if (LLink.size() >= 10) {
                LLink.remove(0);
            }
        } else {
            LLink = new ArrayList<String>();
        }

        lastLink.setProperty("LLink", LLink);
        datastore.put(lastLink);
    }

    private String[] extractParam(String text) {
        return text.split(" ");
    }

    private ArrayList<String> getString() {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        Entity lastLink = new Entity("Queue", "lastLink");
        Key ll = lastLink.getKey();

        try {
            ArrayList<String> toReturn = (ArrayList)datastore.get(ll).getProperty("LLink");
            return toReturn == null ? new ArrayList() : toReturn;
        } catch (EntityNotFoundException var5) {
            return new ArrayList(10);
        }
    }
}
