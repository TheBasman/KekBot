package com.godson.kekbot.Settings;

import com.godson.kekbot.GSONUtils;
import com.godson.kekbot.KekBot;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.dv8tion.jda.JDA;
import net.dv8tion.jda.entities.User;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TicketManager {
    private List<Ticket> tickets = new ArrayList<>();

    public TicketManager() {}

    public void addTicket(Ticket ticket) {
        tickets.add(ticket);
    }

    public void closeTicket(Ticket ticket) {
        tickets.remove(ticket);
    }

    public void replyToTicketManager(Ticket ticket, String response, User replier) {
        String replierName = replier.getUsername() + "#" + replier.getDiscriminator();
        for (JDA jda : KekBot.jdas) {
            try {
                jda.getUserById(ticket.getAuthorID()).getPrivateChannel().sendMessageAsync("You have received a reply for your ticket. (**" + ticket.getTitle() + "**)\n**" + replierName
                        + "**:\n\n" + response, null);
                closeTicket(ticket);
                ticket.setStatus(TicketStatus.AWAITING_REPLY);
                addTicket(ticket);
                save();
                break;
            } catch (NullPointerException e) {
                //do nothing.
            }
        }
    }

    public void replyToTicketUser(Ticket ticket, String response, User replier) {
        String replierName = replier.getUsername() + "#" + replier.getDiscriminator();
        for (JDA jda : KekBot.jdas) {
            try {
                jda.getUserById(GSONUtils.getConfig().getBotOwner()).getPrivateChannel().sendMessageAsync("You have received a reply for a ticket. (**" + ticket.getTitle() + "**)\n**" + replierName
                        + "**:\n\n" + response, null);
                closeTicket(ticket);
                ticket.setStatus(TicketStatus.RECEIVED_REPLY);
                addTicket(ticket);
                save();
                break;
            } catch (NullPointerException e) {
                //do nothing.
            }
        }
    }

    public List<Ticket> getTickets() {
        return tickets;
    }

    @Override
    public String toString() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(this, this.getClass());
    }

    public void save() {
        File tickets = new File("tickets.json");
        try {
            FileWriter writer = new FileWriter(tickets);
            writer.write(this.toString());
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
