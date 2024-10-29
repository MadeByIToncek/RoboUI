package cz.centrumdeti.filmovytabor.robosoutez.commons.types;

import java.io.Serializable;

public record Match(int id, Team leftTeam, Team rightTeam, int leftPoints, int rightPoints) implements Serializable {
}
