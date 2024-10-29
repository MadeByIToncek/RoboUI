/******************************************************************************
 *                    Copyright (c) 2024.                                     *
 *  Filmovy tabor Centrumdeti.cz & Roboticky tabor Centrumdeti.cz             *
 ******************************************************************************/

package cz.centrumdeti.filmovytabor.robosoutez.commons.types;

import java.io.Serializable;

public enum SimpleResponse implements Serializable {
	OK,
	RESET,
	BAD_REQUEST,
	NOT_IMPLEMENTED_YET,
	ERROR_GAME_RUNNING
}
