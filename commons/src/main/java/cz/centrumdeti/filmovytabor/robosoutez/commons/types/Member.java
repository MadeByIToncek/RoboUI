package cz.centrumdeti.filmovytabor.robosoutez.commons.types;

import java.io.Serializable;

public record Member(int id, String firstname, String lastname)implements Serializable {
}
