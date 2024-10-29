/******************************************************************************
 *                    Copyright (c) 2024.                                     *
 *  Filmovy tabor Centrumdeti.cz & Roboticky tabor Centrumdeti.cz             *
 ******************************************************************************/

package cz.centrumdeti.filmovytabor.robosoutez.commons.comms.commands;

import cz.centrumdeti.filmovytabor.robosoutez.commons.types.ResetReason;

import java.io.Serializable;

public record ResetCommand(ResetReason reason) implements Serializable {
}
