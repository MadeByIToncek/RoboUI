package cz.centrumdeti.filmovytabor.robosoutez.commons.comms.responses;

import cz.centrumdeti.filmovytabor.robosoutez.commons.comms.requests.IndexRequest;

import java.io.Serializable;

public record IndexResponse(IndexRequest ir) implements Serializable {
}
