package ci701.hellojavafx;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.util.Comparator;

public class TicketManager {
    private static TicketManager instance;
    private ObservableList<Ticket> allTickets;

    private TicketManager() {
        allTickets = FXCollections.observableArrayList();
        initializeSampleData();
    }

    public static TicketManager getInstance() {
        if (instance == null) {
            instance = new TicketManager();
        }
        return instance;
    }

    private void initializeSampleData() {
        // Add some sample tickets
        Ticket t1 = new Ticket(
                "Security Breach - Unauthorized Access",
                "Multiple failed login attempts detected from external IP address. Immediate investigation required.",
                "user",
                Ticket.Priority.CRITICAL
        );
        t1.setOwner("admin");
        t1.setStatus(Ticket.Status.IN_PROGRESS);

        Ticket t2 = new Ticket(
                "Password Reset Request",
                "Unable to access email account. Need password reset for john.doe@company.com",
                "user",
                Ticket.Priority.MEDIUM
        );

        Ticket t3 = new Ticket(
                "Printer Not Working",
                "Office printer on 3rd floor is not responding. Shows error code E23.",
                "user",
                Ticket.Priority.LOW
        );

        Ticket t4 = new Ticket(
                "VPN Connection Issues",
                "Cannot connect to company VPN from home. Getting 'Authentication failed' error.",
                "user",
                Ticket.Priority.HIGH
        );
        t4.setOwner("admin");

        allTickets.addAll(t1, t2, t3, t4);
    }

    public ObservableList<Ticket> getAllTickets() {
        return allTickets;
    }

    public ObservableList<Ticket> getTicketsByCreator(String creator) {
        ObservableList<Ticket> userTickets = FXCollections.observableArrayList();
        for (Ticket ticket : allTickets) {
            if (ticket.getCreator().equals(creator)) {
                userTickets.add(ticket);
            }
        }
        return userTickets;
    }

    public void addTicket(Ticket ticket) {
        allTickets.add(ticket);
        sortTicketsByPriority();
    }

    public void removeTicket(Ticket ticket) {
        allTickets.remove(ticket);
    }

    // Sort tickets by priority (CRITICAL first)
    private void sortTicketsByPriority() {
        FXCollections.sort(allTickets, new Comparator<Ticket>() {
            @Override
            public int compare(Ticket t1, Ticket t2) {
                return t1.getPriority().compareTo(t2.getPriority());
            }
        });
    }

    public void updateTicket(Ticket ticket) {
        // Trigger UI refresh
        int index = allTickets.indexOf(ticket);
        if (index >= 0) {
            allTickets.set(index, ticket);
        }
        sortTicketsByPriority();
    }
}