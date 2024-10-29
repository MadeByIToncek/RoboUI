/******************************************************************************
 *                    Copyright (c) 2024.                                     *
 *  Filmovy tabor Centrumdeti.cz & Roboticky tabor Centrumdeti.cz             *
 ******************************************************************************/

package cz.centrumdeti.filmovytabor.robosoutez.commons.comms.responses;

import cz.centrumdeti.filmovytabor.robosoutez.commons.types.Team;

import java.io.Serializable;
import java.util.List;

public record RequestTeamResponse(List<Team> teams) implements Serializable {
}
