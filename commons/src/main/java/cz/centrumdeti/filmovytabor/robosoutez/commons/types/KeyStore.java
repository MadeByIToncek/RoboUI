/******************************************************************************
 *                    Copyright (c) 2024.                                     *
 *  Filmovy tabor Centrumdeti.cz & Roboticky tabor Centrumdeti.cz             *
 ******************************************************************************/

package cz.centrumdeti.filmovytabor.robosoutez.commons.types;

public enum KeyStore {
    CURRENT_MATCH_ID("-1"),
    MIGRATION_LEVEL("0");

    public final String def;

    KeyStore(String def) {
        this.def = def;
    }
}
