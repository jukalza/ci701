package ci701.hellojavafx;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Ticket {
    private static int idCounter = 1000;

    private String ticketId;
    private String title;
    private String description;
    private String creator;
    private String owner;
    private Priority priority;
    private Status status;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;

    public Ticket(String title, String description, String creator, Priority priority) {
        this.ticketId = "TKT-" + (idCounter++);
        this.title = title;
        this.description = description;
        this.creator = creator;
        this.owner = "Unassigned";
        this.priority = priority;
        this.status = Status.OPEN;
        this.createdDate = LocalDateTime.now();
        this.updatedDate = LocalDateTime.now();
    }

    // Getters and Setters
    public String getTicketId() { return ticketId; }

    public String getTitle() { return title; }
    public void setTitle(String title) {
        this.title = title;
        this.updatedDate = LocalDateTime.now();
    }

    public String getDescription() { return description; }
    public void setDescription(String description) {
        this.description = description;
        this.updatedDate = LocalDateTime.now();
    }

    public String getCreator() { return creator; }

    public String getOwner() { return owner; }
    public void setOwner(String owner) {
        this.owner = owner;
        this.updatedDate = LocalDateTime.now();
    }

    public Priority getPriority() { return priority; }
    public void setPriority(Priority priority) {
        this.priority = priority;
        this.updatedDate = LocalDateTime.now();
    }

    public Status getStatus() { return status; }
    public void setStatus(Status status) {
        this.status = status;
        this.updatedDate = LocalDateTime.now();
    }

    public LocalDateTime getCreatedDate() { return createdDate; }

    public LocalDateTime getUpdatedDate() { return updatedDate; }

    public String getFormattedCreatedDate() {
        return createdDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }

    public String getFormattedUpdatedDate() {
        return updatedDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }

    public enum Priority {
        CRITICAL("Critical - Security"),
        HIGH("High"),
        MEDIUM("Medium"),
        LOW("Low");

        private String displayName;

        Priority(String displayName) {
            this.displayName = displayName;
        }

        @Override
        public String toString() {
            return displayName;
        }
    }

    public enum Status {
        OPEN("Open"),
        IN_PROGRESS("In Progress"),
        RESOLVED("Resolved"),
        CLOSED("Closed");

        private String displayName;

        Status(String displayName) {
            this.displayName = displayName;
        }

        @Override
        public String toString() {
            return displayName;
        }
    }
}