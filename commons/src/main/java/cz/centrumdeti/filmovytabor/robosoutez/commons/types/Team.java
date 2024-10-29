package cz.centrumdeti.filmovytabor.robosoutez.commons.types;

import java.io.Serializable;
import java.util.List;

public record Team(int teamID, String teamName, List<Member> members) implements Serializable {
}
