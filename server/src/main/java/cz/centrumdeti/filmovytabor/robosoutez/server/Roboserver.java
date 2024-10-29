package cz.centrumdeti.filmovytabor.robosoutez.server;

import cz.centrumdeti.filmovytabor.robosoutez.commons.comms.commands.ResetCommand;
import cz.centrumdeti.filmovytabor.robosoutez.commons.comms.requests.IndexRequest;
import cz.centrumdeti.filmovytabor.robosoutez.commons.comms.responses.UnknownRequestResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.SQLException;

public class Roboserver {
	private static final Logger log = LoggerFactory.getLogger(Roboserver.class);
	public final SocketServer server;
	public final Handler handler;
	public final DatabaseHandler db;

	public Roboserver() {
		server = new SocketServer(7777, this);
		handler = new Handler(this);

		try {
			db = new DatabaseHandler("jdbc:mariadb://localhost:3306/robosoutez", "robot", "robot");
		} catch(SQLException e) {
			log.error("DatabaseHandler startup", e);
			throw new RuntimeException();
		}

		// STARTUP
		server.start();

		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			try {
				server.stop();
			} catch(IOException e) {
				log.error("SocketServer shutdown", e);
			}
			try {
				db.close();
			} catch(IOException e) {
				log.error("Database Handler shutdown", e);
			}
		}));
	}

	public static void main(String[] args) {
		new Roboserver();
	}

	public Object executeRequest(Object o) {
		return switch(o) {
			case IndexRequest obj -> handler.handleIndexRequest(obj);
			case ResetCommand obj -> handler.handleResetCommand(obj);
			default -> new UnknownRequestResponse("Request could not be parsed");
		};
	}
}
