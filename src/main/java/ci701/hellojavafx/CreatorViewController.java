package ci701.hellojavafx;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class CreatorViewController {

    @FXML private Label userLabel;
    @FXML private TableView<Ticket> ticketsTable;
    @FXML private TableColumn<Ticket, String> idColumn;
    @FXML private TableColumn<Ticket, Ticket.Priority> priorityColumn;
    @FXML private TableColumn<Ticket, String> titleColumn;
    @FXML private TableColumn<Ticket, String> ownerColumn;
    @FXML private TableColumn<Ticket, Ticket.Status> statusColumn;
    @FXML private TableColumn<Ticket, String> createdColumn;
    @FXML private TableColumn<Ticket, String> updatedColumn;
    @FXML private ComboBox<Ticket.Status> statusFilterComboBox;

    private TicketManager ticketManager;

    @FXML
    public void initialize() {
        userLabel.setText("Welcome, " + SessionManager.getCurrentUsername());

        ticketManager = TicketManager.getInstance();

        // Setup table columns
        idColumn.setCellValueFactory(new PropertyValueFactory<>("ticketId"));
        priorityColumn.setCellValueFactory(new PropertyValueFactory<>("priority"));
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        ownerColumn.setCellValueFactory(new PropertyValueFactory<>("owner"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        createdColumn.setCellValueFactory(new PropertyValueFactory<>("formattedCreatedDate"));
        updatedColumn.setCellValueFactory(new PropertyValueFactory<>("formattedUpdatedDate"));

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

        // Setup filter combo box
        statusFilterComboBox.getItems().add(null); // For "All"
        statusFilterComboBox.getItems().addAll(Ticket.Status.values());

        // Load tickets
        loadTickets();
    }

    private void loadTickets() {
        ObservableList<Ticket> tickets = ticketManager.getTicketsByCreator(SessionManager.getCurrentUsername());

        // Apply filter
        Ticket.Status statusFilter = statusFilterComboBox.getValue();

        if (statusFilter != null) {
            ObservableList<Ticket> filteredTickets = FXCollections.observableArrayList();
            for (Ticket ticket : tickets) {
                if (ticket.getStatus() == statusFilter) {
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
    private void handleCreateTicket(ActionEvent event) {
        Dialog<Ticket> dialog = new Dialog<>();
        dialog.setTitle("Create New Ticket");
        dialog.setHeaderText("Submit a new IT support request");

        ButtonType createButtonType = new ButtonType("Create", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(createButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField titleField = new TextField();
        titleField.setPromptText("Ticket Title");
        titleField.setPrefWidth(300);

        TextArea descriptionArea = new TextArea();
        descriptionArea.setPromptText("Describe the issue...");
        descriptionArea.setPrefRowCount(5);
        descriptionArea.setPrefWidth(300);

        ComboBox<Ticket.Priority> priorityComboBox = new ComboBox<>();
        priorityComboBox.getItems().addAll(Ticket.Priority.values());
        priorityComboBox.setValue(Ticket.Priority.MEDIUM);

        grid.add(new Label("Title:"), 0, 0);
        grid.add(titleField, 1, 0);
        grid.add(new Label("Description:"), 0, 1);
        grid.add(descriptionArea, 1, 1);
        grid.add(new Label("Priority:"), 0, 2);
        grid.add(priorityComboBox, 1, 2);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == createButtonType) {
                if (titleField.getText().isEmpty() || descriptionArea.getText().isEmpty()) {
                    showAlert("Validation Error", "Please fill in all fields.");
                    return null;
                }
                return new Ticket(
                        titleField.getText(),
                        descriptionArea.getText(),
                        SessionManager.getCurrentUsername(),
                        priorityComboBox.getValue()
                );
            }
            return null;
        });

        dialog.showAndWait().ifPresent(ticket -> {
            ticketManager.addTicket(ticket);
            loadTickets();
            showAlert("Success", "Ticket created successfully!\nTicket ID: " + ticket.getTicketId());
        });
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
                        "Assigned To: " + selected.getOwner() + "\n" +
                        "Created: " + selected.getFormattedCreatedDate() + "\n" +
                        "Last Updated: " + selected.getFormattedUpdatedDate()
        );
        alert.showAndWait();
    }

    @FXML
    private void handleEditTicket(ActionEvent event) {
        Ticket selected = ticketsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("No Selection", "Please select a ticket to edit.");
            return;
        }

        if (selected.getStatus() == Ticket.Status.CLOSED) {
            showAlert("Cannot Edit", "Closed tickets cannot be edited.");
            return;
        }

        Dialog<Boolean> dialog = new Dialog<>();
        dialog.setTitle("Edit Ticket");
        dialog.setHeaderText("Edit Ticket: " + selected.getTicketId());

        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField titleField = new TextField(selected.getTitle());
        titleField.setPrefWidth(300);

        TextArea descriptionArea = new TextArea(selected.getDescription());
        descriptionArea.setPrefRowCount(5);
        descriptionArea.setPrefWidth(300);

        ComboBox<Ticket.Priority> priorityComboBox = new ComboBox<>();
        priorityComboBox.getItems().addAll(Ticket.Priority.values());
        priorityComboBox.setValue(selected.getPriority());

        grid.add(new Label("Title:"), 0, 0);
        grid.add(titleField, 1, 0);
        grid.add(new Label("Description:"), 0, 1);
        grid.add(descriptionArea, 1, 1);
        grid.add(new Label("Priority:"), 0, 2);
        grid.add(priorityComboBox, 1, 2);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                selected.setTitle(titleField.getText());
                selected.setDescription(descriptionArea.getText());
                selected.setPriority(priorityComboBox.getValue());
                return true;
            }
            return false;
        });

        dialog.showAndWait().ifPresent(result -> {
            if (result) {
                ticketManager.updateTicket(selected);
                ticketsTable.refresh();
                showAlert("Success", "Ticket updated successfully.");
            }
        });
    }

    @FXML
    private void handleDeleteTicket(ActionEvent event) {
        Ticket selected = ticketsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("No Selection", "Please select a ticket to delete.");
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirm Delete");
        confirmation.setHeaderText("Delete Ticket: " + selected.getTicketId());
        confirmation.setContentText("Are you sure you want to delete this ticket?");

        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                ticketManager.removeTicket(selected);
                loadTickets();
                showAlert("Success", "Ticket deleted successfully.");
            }
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