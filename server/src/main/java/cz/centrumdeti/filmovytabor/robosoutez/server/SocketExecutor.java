/******************************************************************************
 *                    Copyright (c) 2024.                                     *
 *  Filmovy tabor Centrumdeti.cz & Roboticky tabor Centrumdeti.cz             *
 ******************************************************************************/

package cz.centrumdeti.filmovytabor.robosoutez.server;

import cz.centrumdeti.filmovytabor.robosoutez.commons.comms.commands.ResetCommand;
import cz.centrumdeti.filmovytabor.robosoutez.commons.comms.requests.IndexRequest;
import cz.centrumdeti.filmovytabor.robosoutez.commons.comms.requests.RequestCurrentMatch;
import cz.centrumdeti.filmovytabor.robosoutez.commons.comms.requests.RequestNewMatch;
import cz.centrumdeti.filmovytabor.robosoutez.commons.comms.requests.RequestTeam;
import cz.centrumdeti.filmovytabor.robosoutez.commons.comms.requests.RequestTeams;
import cz.centrumdeti.filmovytabor.robosoutez.commons.comms.responses.IndexResponse;
import cz.centrumdeti.filmovytabor.robosoutez.commons.comms.responses.RequestCurrentMatchResponse;
import cz.centrumdeti.filmovytabor.robosoutez.commons.comms.responses.RequestNewMatchResponse;
import cz.centrumdeti.filmovytabor.robosoutez.commons.comms.responses.RequestTeamResponse;
import cz.centrumdeti.filmovytabor.robosoutez.commons.comms.responses.RequestTeamsResponse;
import cz.centrumdeti.filmovytabor.robosoutez.commons.comms.responses.UnknownRequestResponse;
import cz.centrumdeti.filmovytabor.robosoutez.commons.types.KeyStore;
import cz.centrumdeti.filmovytabor.robosoutez.commons.types.SimpleResponse;

public class SocketExecutor {
	private final Roboserver roboserver;

    public SocketExecutor(Roboserver roboserver) {
		this.roboserver = roboserver;
	}

    public Object executeRequest(Object o) {
        return switch (o) {
            case IndexRequest obj -> handleIndexRequest(obj);
            case ResetCommand obj -> handleResetCommand(obj);
            case RequestCurrentMatch ignored -> handleCurrentMatchRequest();
            case RequestNewMatch obj -> handleNewMatchResponse(obj);
            case RequestTeams ignored -> handleAllTeamsRequest();
            case RequestTeam obj -> handleSingleTeamRequest(obj);
            default -> new UnknownRequestResponse("Request could not be parsed");
        };
    }

    private RequestTeamResponse handleSingleTeamRequest(RequestTeam obj) {
        return new RequestTeamResponse(roboserver.db.getTeamByID(obj.id()));
    }

    private RequestNewMatchResponse handleNewMatchResponse(RequestNewMatch obj) {
        return new RequestNewMatchResponse(roboserver.db.getMatchByID(roboserver.db.createMatch(obj.teamL(), obj.teamR())));
    }

    private RequestTeamsResponse handleAllTeamsRequest() {
        return new RequestTeamsResponse(roboserver.db.getTeams());
    }

    private RequestCurrentMatchResponse handleCurrentMatchRequest() {
        int id = Integer.parseInt(roboserver.db.getValueFromKeystore(KeyStore.CURRENT_MATCH_ID));
        if (id >= 0) {
            return new RequestCurrentMatchResponse(roboserver.db.getMatchByID(id));
        } else return null;
    }

    private IndexResponse handleIndexRequest(IndexRequest ir) {
		return new IndexResponse(ir);
	}

    private SimpleResponse handleResetCommand(ResetCommand obj) {
        return switch (obj.reason()) {
            case UNSPECIFIED -> {
                yield SimpleResponse.BAD_REQUEST;
            }
            case GAME_END -> {
                roboserver.db.saveValueToKeystore(KeyStore.CURRENT_MATCH_ID, "-1");
                yield SimpleResponse.RESET;
            }
            case MISCONFIGURED -> {
                int a = 1; // to keep IDE from merging, remove later
                roboserver.db.saveValueToKeystore(KeyStore.CURRENT_MATCH_ID, "-1");
                yield SimpleResponse.RESET;
            }
            case INTERRUPT -> {
                roboserver.db.setCurrentMatchPoints(true, 0);
                roboserver.db.setCurrentMatchPoints(false, 0);
                yield SimpleResponse.RESET;
            }
        };
	}
}
