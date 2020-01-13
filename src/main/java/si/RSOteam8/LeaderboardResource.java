package si.RSOteam8;


import com.kumuluz.ee.logs.LogManager;
import com.kumuluz.ee.logs.cdi.Log;
import com.kumuluz.ee.logs.cdi.LogParams;


import org.eclipse.microprofile.metrics.annotation.Counted;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

@ApplicationScoped
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Path("leaderboards")
@Log
public class LeaderboardResource {
    

    @Inject
    private ConfigProperties cfg;

    @GET
    public Response getAllLeaderboards() {
        List<Leaderboard> leaderboards = new LinkedList<Leaderboard>();

        try (
                Connection conn = DriverManager.getConnection(cfg.getDburl(), cfg.getDbuser(), cfg.getDbpass());
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT * FROM leaderboards.leaderboards ORDER BY \"score\" DESC");
        ) {
            while (rs.next()) {
                Leaderboard leaderboard = new Leaderboard();
                leaderboard.setId(rs.getString(1));
                leaderboard.setLeaderboard(rs.getString(2));
                leaderboard.setScore(rs.getString(3));
                leaderboards.add(leaderboard);
            }
        }
        catch (SQLException e) {
            System.err.println(e);
            return Response.status(Response.Status.FORBIDDEN).build();
        }

        return Response.ok(leaderboards).build();
    }
    /*@Counted(name = "getAllLeaderboards-count")
    @GET
    public Response getAllLeaderboards() {
        Logger.getLogger(LeaderboardHealthCheck.class.getSimpleName()).info("just testing");
        List<Leaderboard> leaderboards = new LinkedList<Leaderboard>();
        Leaderboard leaderboard = new Leaderboard();
        leaderboard.setId("1");
        leaderboard.setLeaderboardname(cfg.getTest());
        leaderboards.add(leaderboard);
        leaderboard = new Leaderboard();
        leaderboard.setId("2");
        leaderboard.setLeaderboardname("peterklepec");
        leaderboards.add(leaderboard);
        return Response.ok(leaderboards).build();
    }*/

    /*@GET
    @Path("{userId}/{leaderboardId}")
    public Response getLeaderboard(@PathParam("leaderboardId") String leaderboardid) {

        try (
                Connection conn = DriverManager.getConnection(cfg.getDburl(), cfg.getDbuser(), cfg.getDbpass());
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT * FROM leaderboards.leaderboards WHERE \"id\" = "+"'"+leaderboardid+"'");
        ) {
            if (rs.next()){
                Leaderboard leaderboard = new Leaderboard();
                leaderboard.setId(rs.getString(1));
                leaderboard.setLeaderboard(rs.getString(2));
                return Response.ok(leaderboard).build();

            }
        }
        catch (SQLException e) {
            System.err.println(e);
            return Response.status(Response.Status.FORBIDDEN).build();
        }

        return Response.status(Response.Status.NOT_FOUND).build();
    }*/

   /* @POST
    public Response addNewLeaderboard(Leaderboard leaderboard) {
        //Database.addCustomer(customer);
        return Response.noContent().build();
    }*/
    @GET
    @Path("add")
    public Response addNewLeaderboard(@QueryParam("userId") String username,
                                      @QueryParam("score") int score
                                  ) {
        try (
                Connection conn = DriverManager.getConnection(cfg.getDburl(), cfg.getDbuser(), cfg.getDbpass());
                Statement stmt = conn.createStatement();
        ) {
            stmt.executeUpdate("INSERT INTO leaderboards.leaderboards (username, score) VALUES ('"
                    + username + "', '" + score + "')",
            Statement.RETURN_GENERATED_KEYS);
        }
        catch (SQLException e) {
            System.err.println(e);
            return Response.status(Response.Status.FORBIDDEN).build();
        }

        return Response.noContent().build();
    }
    @GET
    @Path("update")
    public Response updateLeaderboard(@QueryParam("userId") String username
    ) {
        try (
                Connection conn = DriverManager.getConnection(cfg.getDburl(), cfg.getDbuser(), cfg.getDbpass());
                Statement stmt = conn.createStatement();
        ) {
            stmt.executeUpdate("UPDATE leaderboards.leaderboards SET score = score + 1 WHERE \"username\" ="+"'"+username+"'");
        }
        catch (SQLException e) {
            System.err.println(e);
            return Response.status(Response.Status.FORBIDDEN).build();
        }

        return Response.noContent().build();
    }
    @DELETE
    public Response deleteLeaderboard(@QueryParam("userId") String username) {
        try (
                Connection conn = DriverManager.getConnection(cfg.getDburl(), cfg.getDbuser(), cfg.getDbpass());
                Statement stmt = conn.createStatement();
        ) {
            stmt.executeUpdate("DELETE FROM leaderboards.leaderboards WHERE \"username\" = " + "'"+username+"'");
        }
        catch (SQLException e) {
            System.err.println(e);
            return Response.status(Response.Status.FORBIDDEN).build();
        }

            return Response.noContent().build();
    }
    /*@DELETE
    @Path("{leaderboardId}")
    public Response deleteLeaderboard(@PathParam("leaderboardId") String leaderboardId) {
        //Database.deleteCustomer(customerId);
        return Response.noContent().build();
    }*/
}
