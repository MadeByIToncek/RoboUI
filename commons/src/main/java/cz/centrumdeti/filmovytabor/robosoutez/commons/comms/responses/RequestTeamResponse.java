/******************************************************************************
 *                    Copyright (c) 2024.                                     *
 *  Filmovy tabor Centrumdeti.cz & Roboticky tabor Centrumdeti.cz             *
 ******************************************************************************/

package cz.centrumdeti.filmovytabor.robosoutez.commons.comms.responses;

import java.io.Serializable;

import cz.centrumdeti.filmovytabor.robosoutez.commons.types.Team;

public record RequestTeamResponse(Team team) implements Serializable {
}
