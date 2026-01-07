package ci701.hellojavafx;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TicketDAO {

    private static final String SELECT_ALL =
            "SELECT ticket_id, title, description, creator, owner, priority, status, created_date, updated_date FROM tickets";

    private static final String INSERT =
            "INSERT INTO tickets (ticket_id, title, description, creator, owner, priority, status, created_date, updated_date) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

    private static final String UPDATE =
            "UPDATE tickets SET title=?, description=?, owner=?, priority=?, status=?, updated_date=? WHERE ticket_id=?";

    private static final String DELETE =
            "DELETE FROM tickets WHERE ticket_id=?";

    public List<Ticket> fetchAll() throws SQLException {
        List<Ticket> result = new ArrayList<>();

        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement(SELECT_ALL);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                result.add(mapRow(rs));
            }
        }

        return result;

    }

    public void insert(Ticket)


}
