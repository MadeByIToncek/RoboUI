package cz.centrumdeti.filmovytabor.robosoutez.commons.comms.requests;

import java.io.Serializable;

public record RequestNewMatch(int teamL, int teamR) implements Serializable {
}
