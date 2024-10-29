/******************************************************************************
 *                    Copyright (c) 2024.                                     *
 *  Filmovy tabor Centrumdeti.cz & Roboticky tabor Centrumdeti.cz             *
 ******************************************************************************/

package cz.centrumdeti.filmovytabor.robosoutez.commons.types;

import java.io.Serializable;

/**
 * Used with {@link cz.centrumdeti.filmovytabor.robosoutez.commons.comms.commands.ResetCommand}, signalises the reason for reset
 */
public enum ResetReason implements Serializable {
	/**
	 * DO NOT USE! Will not do anything, voids request with {@link SimpleResponse#BAD_REQUEST}
	 */
	UNSPECIFIED,
	/**
	 * Game has ended, scores are to be saved.
	 */
	GAME_END,
	/**
	 * Misconfigured setup, do not save anything, discard current round
	 */
	MISCONFIGURED,
	/**
	 * Mid-game interrupt, game is to be reset to the state at the start of this round
	 */
	INTERRUPT,
}
