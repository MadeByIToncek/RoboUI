/******************************************************************************
 *                    Copyright (c) 2024.                                     *
 *  Filmovy tabor Centrumdeti.cz & Roboticky tabor Centrumdeti.cz             *
 ******************************************************************************/

package cz.centrumdeti.filmovytabor.robosoutez.commons.comms.responses;

import cz.centrumdeti.filmovytabor.robosoutez.commons.comms.requests.IndexRequest;

import java.io.Serializable;

public record IndexResponse(IndexRequest ir) implements Serializable {
}
