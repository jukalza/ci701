package ci701.hellojavafx;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

public class AdminViewController {

    @FXML private Label userLabel;
    @FXML private TableView<Ticket> ticketsTable;
    @FXML private TableColumn<Ticket, String> idColumn;
    @FXML private TableColumn<Ticket, Ticket.Priority> priorityColumn;
    @FXML private TableColumn<Ticket, String> titleColumn;
    @FXML private TableColumn<Ticket, String> creatorColumn;
    @FXML private TableColumn<Ticket, String> ownerColumn;
    @FXML private TableColumn<Ticket, Ticket.Status> statusColumn;
    @FXML private TableColumn<Ticket, String> createdColumn;
    @FXML private ComboBox<Ticket.Status> statusFilterComboBox;
    @FXML private ComboBox<Ticket.Priority> priorityFilterComboBox;

    private TicketManager ticketManager;

    @FXML
    public void initialize() {
        userLabel.setText("Welcome, " + SessionManager.getCurrentUsername());

        ticketManager = TicketManager.getInstance();

        // Setup table columns
        idColumn.setCellValueFactory(new PropertyValueFactory<>("ticketId"));
        priorityColumn.setCellValueFactory(new PropertyValueFactory<>("priority"));
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        creatorColumn.setCellValueFactory(new PropertyValueFactory<>("creator"));
        ownerColumn.setCellValueFactory(new PropertyValueFactory<>("owner"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        createdColumn.setCellValueFactory(new PropertyValueFactory<>("formattedCreatedDate"));

        // Color code priority column
        priorityColumn.setCellFactory(column -> new TableCell<Ticket, Ticket.Priority>() {
            @Override
            protected void updateItem(Ticket.Priority priority, boolean empty) {
                super.updateItem(priority, empty);
                if (empty || priority == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(priority.toString());
                    switch (priority) {
                        case CRITICAL:
                            setStyle("-fx-background-color: #ff4444; -fx-text-fill: white; -fx-font-weight: bold;");
                            break;
                        case HIGH:
                            setStyle("-fx-background-color: #ff8800; -fx-text-fill: white;");
                            break;
                        case MEDIUM:
                            setStyle("-fx-background-color: #ffbb33; -fx-text-fill: black;");
                            break;
                        case LOW:
                            setStyle("-fx-background-color: #00C851; -fx-text-fill: white;");
                            break;
                    }
                }
            }
        });

        // Setup filter combo boxes
        statusFilterComboBox.getItems().add(null);
        statusFilterComboBox.getItems().addAll(Ticket.Status.values());

        priorityFilterComboBox.getItems().add(null); 
        priorityFilterComboBox.getItems().addAll(Ticket.Priority.values());

        // Load tickets
        loadTickets();
    }

    private void loadTickets() {
        ObservableList<Ticket> tickets = ticketManager.getAllTickets();

        // Apply filters
        Ticket.Status statusFilter = statusFilterComboBox.getValue();
        Ticket.Priority priorityFilter = priorityFilterComboBox.getValue();

        if (statusFilter != null || priorityFilter != null) {
            ObservableList<Ticket> filteredTickets = FXCollections.observableArrayList();
            for (Ticket ticket : tickets) {
                boolean matchesStatus = (statusFilter == null || ticket.getStatus() == statusFilter);
                boolean matchesPriority = (priorityFilter == null || ticket.getPriority() == priorityFilter);

                if (matchesStatus && matchesPriority) {
                    filteredTickets.add(ticket);
                }
            }
            ticketsTable.setItems(filteredTickets);
        } else {
            ticketsTable.setItems(tickets);
        }
    }

    @FXML
    private void handleFilterChange(ActionEvent event) {
        loadTickets();
    }

    @FXML
    private void handleRefresh(ActionEvent event) {
        loadTickets();
    }

    @FXML
    private void handleViewDetails(ActionEvent event) {
        Ticket selected = ticketsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("No Selection", "Please select a ticket to view details.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Ticket Details");
        alert.setHeaderText("Ticket ID: " + selected.getTicketId());
        alert.setContentText(
                "Title: " + selected.getTitle() + "\n\n" +
                        "Description: " + selected.getDescription() + "\n\n" +
                        "Priority: " + selected.getPriority() + "\n" +
                        "Status: " + selected.getStatus() + "\n" +
                        "Creator: " + selected.getCreator() + "\n" +
                        "Owner: " + selected.getOwner() + "\n" +
                        "Created: " + selected.getFormattedCreatedDate() + "\n" +
                        "Last Updated: " + selected.getFormattedUpdatedDate()
        );
        alert.showAndWait();
    }

    @FXML
    private void handleAssignToMe(ActionEvent event) {
        Ticket selected = ticketsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("No Selection", "Please select a ticket to assign.");
            return;
        }

        selected.setOwner(SessionManager.getCurrentUsername());
        if (selected.getStatus() == Ticket.Status.OPEN) {
            selected.setStatus(Ticket.Status.IN_PROGRESS);
        }
        ticketManager.updateTicket(selected);
        ticketsTable.refresh();

        showAlert("Success", "Ticket assigned to you successfully.");
    }

    @FXML
    private void handleUpdateStatus(ActionEvent event) {
        Ticket selected = ticketsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("No Selection", "Please select a ticket to update.");
            return;
        }

        ChoiceDialog<Ticket.Status> dialog = new ChoiceDialog<>(selected.getStatus(), Ticket.Status.values());
        dialog.setTitle("Update Status");
        dialog.setHeaderText("Update ticket status");
        dialog.setContentText("Choose new status:");

        dialog.showAndWait().ifPresent(status -> {
            selected.setStatus(status);
            ticketManager.updateTicket(selected);
            ticketsTable.refresh();
        });
    }

    @FXML
    private void handleUpdatePriority(ActionEvent event) {
        Ticket selected = ticketsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("No Selection", "Please select a ticket to update.");
            return;
        }

        ChoiceDialog<Ticket.Priority> dialog = new ChoiceDialog<>(selected.getPriority(), Ticket.Priority.values());
        dialog.setTitle("Update Priority");
        dialog.setHeaderText("Update ticket priority");
        dialog.setContentText("Choose new priority:");

        dialog.showAndWait().ifPresent(priority -> {
            selected.setPriority(priority);
            ticketManager.updateTicket(selected);
            ticketsTable.refresh();
        });
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        try {
            SessionManager.clearSession();
            Stage stage = (Stage) userLabel.getScene().getWindow();
            Parent root = FXMLLoader.load(getClass().getResource("login.fxml"));
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Login");
            stage.setResizable(false);
            stage.setMaximized(false);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}