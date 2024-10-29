package cz.centrumdeti.filmovytabor.robosoutez.commons.types;

import java.io.Serializable;

public enum SimpleResponse implements Serializable {
	OK,
	RESET,
	BAD_REQUEST,
	NOT_IMPLEMENTED_YET,
	ERROR_GAME_RUNNING
}
