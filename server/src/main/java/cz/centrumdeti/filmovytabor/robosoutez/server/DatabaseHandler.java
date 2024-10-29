/******************************************************************************
 *                    Copyright (c) 2024.                                     *
 *  Filmovy tabor Centrumdeti.cz & Roboticky tabor Centrumdeti.cz             *
 ******************************************************************************/

package cz.centrumdeti.filmovytabor.robosoutez.server;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import cz.centrumdeti.filmovytabor.robosoutez.commons.types.KeyStore;
import cz.centrumdeti.filmovytabor.robosoutez.commons.types.Member;
import cz.centrumdeti.filmovytabor.robosoutez.commons.types.Team;

public class DatabaseHandler implements Closeable {
	private static final Logger log = LoggerFactory.getLogger(DatabaseHandler.class);
	private final Connection conn;

	public DatabaseHandler(String url, String user, String password) throws SQLException {
		conn = DriverManager.getConnection(url, user, password);
	}

	public static void main(String[] args) throws SQLException, IOException {
//keep
	}

    public @Nullable String getValueFromKeystore(@NotNull KeyStore key) {
		try(Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM keystore WHERE 'key' = %s;".formatted(key))) {
			if(rs.next()) {
				return rs.getString("value");
			} else {
				return null;
			}
		} catch(SQLException e) {
			log.error("getTeams() exception", e);
			return null;
		}
	}

    public @NotNull String keystoreGetOrDefault(@NotNull KeyStore key, @NotNull String defval) {
		String val = getValueFromKeystore(key);
		if(val == null) {
			saveValueToKeystore(key, defval);
			return defval;
		} else {
			return val;
		}
	}

    public void saveValueToKeystore(@NotNull KeyStore key, @NotNull String value) {
		try(Statement stmt = conn.createStatement()) {
			ResultSet rs = stmt.executeQuery("SELECT * FROM keystore WHERE 'key' = %s;".formatted(key));
			if(rs.next()) {
				rs.close();
				stmt.executeUpdate("UPDATE keystore SET value='%s' WHERE `key`='%s';".formatted(value, key));
			} else {
				rs.close();
				stmt.executeUpdate("INSERT INTO keystore (`key`, value) VALUES('%s', '%s');".formatted(key, value));
			}
		} catch(SQLException e) {
			log.error("getTeams() exception", e);
		}
	}

	public int createMatch(int teamLeftID, int teamRightID) {
		try(Statement stmt = conn.createStatement()) {
			stmt.executeUpdate("INSERT INTO matches (team_l, team_r, points_left, points_right) VALUES(%d, %d, 0, 0);".formatted(teamLeftID, teamRightID), Statement.RETURN_GENERATED_KEYS);
			try(ResultSet rs = stmt.getGeneratedKeys()) {
				rs.next();
                int matchid = rs.getInt(1);
                saveValueToKeystore(KeyStore.CURRENT_MATCH_ID, String.valueOf(matchid));
                return matchid;
			}
		} catch(SQLException e) {
			log.error("getTeams() exception", e);
			return -1;
		}
	}

	public @Nullable List<Team> getTeams() {
		try(Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM teams;")) {
			List<Team> teams = new ArrayList<>();

			while(rs.next()) {
                int teamid = rs.getInt("team_id");
                String teamname = rs.getString("team_name");
				Team t = new Team(teamid, teamname, getMembersByTeam(teamid));
				teams.add(t);
			}
            log.info("Requested teams, returning {} teams.", teams.size());
			return teams;
		} catch(SQLException e) {
			log.error("getTeams() exception", e);
			return null;
		}
	}

	private @Nullable List<Member> getMembersByTeam(int team_id) {
		try(Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM members WHERE team_id = %d;".formatted(team_id))) {
			List<Member> members = new ArrayList<>();

			while(rs.next()) {
				int id = rs.getInt("member_id");
				String firstname = rs.getString("firstname");
				String lastname = rs.getString("surname");

				members.add(new Member(id, firstname, lastname));
			}
            log.info("Requested members of team {}, returnig {} results", team_id, members.size());
			return members;
		} catch(SQLException e) {
			log.error("getMembersByTeam() exception", e);
			return null;
		}
	}

	@Override
	public void close() throws IOException {
		try {
			conn.close();
		} catch(SQLException e) {
			log.error("Database handler", e);
		}
	}
}
