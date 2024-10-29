/******************************************************************************
 *                    Copyright (c) 2024.                                     *
 *  Filmovy tabor Centrumdeti.cz & Roboticky tabor Centrumdeti.cz             *
 ******************************************************************************/

package cz.centrumdeti.filmovytabor.robosoutez.server;

import cz.centrumdeti.filmovytabor.robosoutez.commons.comms.commands.ResetCommand;
import cz.centrumdeti.filmovytabor.robosoutez.commons.comms.requests.IndexRequest;
import cz.centrumdeti.filmovytabor.robosoutez.commons.comms.requests.RequestCurrentMatch;
import cz.centrumdeti.filmovytabor.robosoutez.commons.comms.requests.RequestTeams;
import cz.centrumdeti.filmovytabor.robosoutez.commons.comms.responses.IndexResponse;
import cz.centrumdeti.filmovytabor.robosoutez.commons.comms.responses.RequestCurrentMatchResponse;
import cz.centrumdeti.filmovytabor.robosoutez.commons.comms.responses.RequestTeamsResponse;
import cz.centrumdeti.filmovytabor.robosoutez.commons.comms.responses.UnknownRequestResponse;
import cz.centrumdeti.filmovytabor.robosoutez.commons.types.SimpleResponse;

public class Handler {
	private final Roboserver roboserver;

	public Handler(Roboserver roboserver) {
		this.roboserver = roboserver;
	}

    public Object executeRequest(Object o) {
        return switch (o) {
            case IndexRequest obj -> handleIndexRequest(obj);
            case ResetCommand ignored -> handleResetCommand();
            case RequestCurrentMatch ignored -> handleCurrentMatchRequest();
            case RequestTeams ignored -> handleTeamRequest();
            default -> new UnknownRequestResponse("Request could not be parsed");
        };
    }

    private RequestTeamsResponse handleTeamRequest() {
        return new RequestTeamsResponse(roboserver.db.getTeams());
    }

    private RequestCurrentMatchResponse handleCurrentMatchRequest() {
        return null;
    }

    private IndexResponse handleIndexRequest(IndexRequest ir) {
		return new IndexResponse(ir);
	}

    private SimpleResponse handleResetCommand() {
		return SimpleResponse.NOT_IMPLEMENTED_YET;
	}
}
