package cz.centrumdeti.filmovytabor.records;

import cz.centrumdeti.filmovytabor.Main;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

public record Team(String id, String name, String color, ArrayList<Hrac> hraci) implements Serializable {
    public JSONObject getJSON() {
        return new JSONObject()
                .put("name", name)
                .put("teamColor", color)
                .put("members", remapPlayers());
    }

    private JSONArray remapPlayers() {
        JSONArray ja = new JSONArray();
        for (Hrac hrac : hraci) {
            ja.put(new JSONObject()
                    .put("name", hrac.jmeno)
                    .put("color", hrac.color));
        }
        return ja;
    }

    public record Hrac(String jmeno, String color) {
    }
}
