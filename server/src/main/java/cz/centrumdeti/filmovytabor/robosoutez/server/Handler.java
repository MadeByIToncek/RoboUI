/******************************************************************************
 *                    Copyright (c) 2024.                                     *
 *  Filmovy tabor Centrumdeti.cz & Roboticky tabor Centrumdeti.cz             *
 ******************************************************************************/

package cz.centrumdeti.filmovytabor.robosoutez.server;

import cz.centrumdeti.filmovytabor.robosoutez.commons.comms.commands.ResetCommand;
import cz.centrumdeti.filmovytabor.robosoutez.commons.comms.requests.IndexRequest;
import cz.centrumdeti.filmovytabor.robosoutez.commons.comms.responses.IndexResponse;
import cz.centrumdeti.filmovytabor.robosoutez.commons.types.SimpleResponse;

public class Handler {
	private final Roboserver roboserver;

	public Handler(Roboserver roboserver) {
		this.roboserver = roboserver;
	}

	public IndexResponse handleIndexRequest(IndexRequest ir) {
		return new IndexResponse(ir);
	}

	public SimpleResponse handleResetCommand(ResetCommand obj) {
		return SimpleResponse.NOT_IMPLEMENTED_YET;
	}
}
