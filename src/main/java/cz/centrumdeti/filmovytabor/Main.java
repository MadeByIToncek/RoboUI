package cz.centrumdeti.filmovytabor;

import io.javalin.Javalin;
import io.javalin.http.ContentType;
import io.javalin.websocket.WsConnectContext;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;


public class Main {
    static ArrayList<WsConnectContext> clientList = new ArrayList<>();
    static CasparController cc;
    static CountdownManager cdm;
    static String mode = "preload";
    public static void main(String[] args) {
        Javalin app = Javalin.create(config -> {
            config.useVirtualThreads = true;
            config.http.asyncTimeout = 10_000L;
        });

        app.get("/", ctx -> ctx.result(":)"));

        app.ws("/", conf -> {
            conf.onConnect(wscc -> {
                clientList.add(wscc);
            });
        });

        app.get("/ui/stream", ctx -> ctx.contentType(ContentType.TEXT_HTML).result(getResource("/stream.html")));

        app.get("/ui/tv1", ctx -> ctx.contentType(ContentType.TEXT_HTML).result(getResource("/tv1.html")));

        app.get("/frontend", ctx -> ctx.contentType(ContentType.TEXT_HTML).result(getResource("/frontend.html")));

        app.post("/api/setupCC", ctx -> {
            JSONObject body = new JSONObject(ctx.body());
            cc = new CasparController(body.getJSONObject("stream").getString("addr"), body.getJSONObject("stream").getInt("port"),
                    body.getJSONObject("tv").getString("addr"), body.getJSONObject("tv").getInt("port"),
                    body.getString("panelURL"));
            ctx.result();
        });

        app.post("/api/hardreset", ctx -> {
            hardReset();
            ctx.result();
        });
        app.post("/api/reset", ctx -> {
            reset();
            ctx.result();
        });

        app.get("/api/mode", ctx -> ctx.result(mode));
        app.post("/api/changeMode", ctx -> {
            mode = ctx.body();
            ctx.result();
        });

        app.post("/api/preload", ctx -> {
            JSONObject body = new JSONObject(ctx.body());
            try {
                preload(loadTeam(body.getString("team1")), loadTeam(body.getString("team2")));
            } catch (TeamNotFoundError e) {
                throw new RuntimeException(e);
            }
            ctx.result();
        });
        app.post("/api/play", ctx -> {
            play();
            ctx.result();
        });
        app.post("/api/start", ctx -> {
            startCountdown();
            ctx.result();
        });
        app.post("/api/interrupt", ctx -> {
            interrupt();
            ctx.result();
        });
        app.post("/api/results", ctx -> {
            JSONObject body = new JSONObject(ctx.body());
            setResults(body.getInt("team1"), body.getInt("team2"));
            ctx.result();
        });

        app.start(7777);
    }


    private static String loadTeam(String teamID) throws TeamNotFoundError, IOException {
        if(!new File("./teams/").exists()) {
            new File("./teams/").mkdirs();
        }
        File teamfile = new File("./teams/" + teamID);

        if (!teamfile.exists()) {
            throw new TeamNotFoundError("Unable to find team " + teamID);
        }

        return Files.readString(teamfile.getAbsoluteFile().toPath());
    }

    private static InputStream getResource(String path) {
        return Main.class.getResourceAsStream(path);
    }

    private static void hardReset() {
        if(cc != null) {
            cc.resetAll();
            sleep(100);
            cc.loadbg();
            sleep(100);
        }
    }

    private static void reset() {
        setMode("hidden");
        broadcast(new JSONObject()
                .put("act", "reset"));
        setMode("preload");
    }

    private static void preload(String team1, String team2) {
        broadcast(new JSONObject()
                .put("act", "setTeams")
                .put("body", new JSONObject()
                        .put("team1", new JSONObject(team1))
                        .put("team2", new JSONObject(team2))));
    }

    private static void play() {
        if(cc != null) {
            cc.play();
        }
    }

    private static void startCountdown() {
        cdm = new CountdownManager() {
            @Override
            void updateSecond(int current) {
                broadcast(new JSONObject()
                        .put("act", "setTime")
                        .put("body", current + "s"));
            }

            @Override
            void timeIsUp() {
                broadcast(new JSONObject()
                        .put("act", "setTime")
                        .put("body", "Konec!"));
                counting();
            }
        };
        setMode("ingame");
    }

    private static void interrupt() {
        cdm.cancel();
        counting();
    }

    private static void counting() {
        setMode("counting");
    }

    private static void setResults(int team1, int team2) {
        broadcast(new JSONObject()
                .put("act", "setPoints")
                .put("body", new JSONObject()
                        .put("team1", team1)
                        .put("team2", team2)));

        setMode("results");
    }

    public static void broadcast(JSONObject text) {
        for (WsConnectContext wscc : clientList) {
            wscc.send(text.toString());
        }
    }

    private static void setMode(String targetMode) {
        mode = targetMode;
        broadcast(new JSONObject()
                .put("act", "setMode")
                .put("body", mode));
    }

    private static void sleep(int i) {
        try {
            Thread.sleep(i);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}