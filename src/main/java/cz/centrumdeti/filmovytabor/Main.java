package cz.centrumdeti.filmovytabor;

import io.javalin.Javalin;
import io.javalin.http.ContentType;
import io.javalin.websocket.WsConnectContext;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.ClosedChannelException;
import java.nio.file.Files;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.logging.Logger;


public class Main {
    static ArrayList<WsConnectContext> clientList = new ArrayList<>();
    static CasparController cc;
    static CountdownManager cdm;
    static final int total_time = 120;
    static String mode = "preload";
    public static void main(String[] args) {
        Javalin app = Javalin.create(config -> {
            config.useVirtualThreads = true;
            config.http.asyncTimeout = 10_000L;
        });

        app.get("/", ctx -> ctx.result(":)"));

        app.ws("/", conf -> {
            conf.onConnect(wscc -> {
                wscc.session.setIdleTimeout(Duration.ofDays(365));
                clientList.add(wscc);
            });
            conf.onMessage(msg -> {
                switch (msg.message()) {
                    case "requestMode" -> msg.send(new JSONObject()
                            .put("act", "setMode")
                            .put("body", mode).toString(4));
                }
            });
        });

        app.get("/ui/stream", ctx -> ctx.contentType(ContentType.TEXT_HTML).result(getResource("/stream.html")));
        app.get("/ui/tv", ctx -> ctx.contentType(ContentType.TEXT_HTML).result(getResource("/tv.html")));
        app.get("/ui/fel.svg", ctx -> ctx.contentType(ContentType.IMAGE_SVG).result(getResource("/fel.svg")));

        app.post("/api/setupCC", ctx -> {
            JSONObject body = new JSONObject(ctx.body());
            cc = new CasparController(body.getJSONObject("stream").getString("addr"), body.getJSONObject("stream").getInt("port"),
                    body.getJSONObject("tv").getString("addr"), body.getJSONObject("tv").getInt("port"),
                    body.getString("panelURL"));
            ctx.result();
        });

        app.post("/api/hardreset", ctx -> {
            Logger.getGlobal().info("Hard reset!");
            hardReset();
            ctx.result();
        });
        app.post("/api/reset", ctx -> {
            Logger.getGlobal().info("Soft reset!");
            reset();
            ctx.result();
        });

        app.get("/api/mode", ctx -> ctx.result(mode));

        app.post("/api/changeMode", ctx -> {
            setMode(ctx.body());
            Logger.getGlobal().info("Changed mode to " + mode);
            ctx.result();
        });

        app.post("/api/preload", ctx -> {
            JSONObject body = new JSONObject(ctx.body());
            Logger.getGlobal().info("Preloading with following teams:\n" +
                    " - " + body.getString("team1") + "\n" +
                    " - " + body.getString("team2"));
            try {
                preload(loadTeam(body.getString("team1")), loadTeam(body.getString("team2")));
            } catch (TeamNotFoundError e) {
                throw new RuntimeException(e);
            }
            ctx.result();
        });
        app.post("/api/play", ctx -> {
            Logger.getGlobal().info("Starting playout!");
            startCountdown();
            ctx.result();
        });
        app.post("/api/interrupt", ctx -> {
            Logger.getGlobal().info("Interrupting!");
            interrupt();
            ctx.result();
        });
        app.post("/api/results", ctx -> {
            JSONObject body = new JSONObject(ctx.body());
            Logger.getGlobal().info("Displaying results of " + body.getInt("team1") + " vs " + body.getInt("team2") + "!");
            setResults(body.getInt("team1"), body.getInt("team2"));
            ctx.result();
        });

        app.post("/api/balls", ctx -> {
            String[] split = ctx.body().split("\\r?;");
            JSONObject jo = new JSONObject()
                    .put("act", "setBalls")
                    .put("body", new JSONObject()
                            .put("team1pool1", split[0])
                            .put("team1pool2", split[1])
                            .put("team2pool1", split[2])
                            .put("team2pool2", split[3]));
            //Logger.getGlobal().info(jo.toString(4));
            broadcast(jo);
            ctx.result();
        });

        app.post("/api/changeTeams", ctx -> {
            System.out.println(ctx.body());
            String[] split = ctx.body().split("\\r?;");
            try {
                preload(loadTeam(split[0]), loadTeam(split[1]));
            } catch (TeamNotFoundError e) {
                throw new RuntimeException(e);
            }
            ctx.result();
        });

        app.get("/api/listTeams", ctx -> {
            if(!new File("./teams/").exists()) {
                new File("./teams/").mkdirs();
            }
            StringJoiner sj = new StringJoiner("\n");
            for (File file : Objects.requireNonNull(new File("./teams/").listFiles())) {
                if(file.getName().endsWith(".robo")) {
                    sj.add(file.getName().substring(0,file.getName().length()-5));
                }
            }
            ctx.result(sj.toString());
        });

        app.post("/api/submitScore", ctx -> {
            Integer[] points = Arrays.stream(ctx.body().split("\\r?:")).map(Integer::parseInt).toList().toArray(new Integer[0]);
            Logger.getGlobal().info("Set score to " + points[0] + ":" +  points[1]);
            setResults(points[0], points[1]);
            ctx.result();
        });

        Runtime.getRuntime().addShutdownHook(new Thread(()-> {
            reset();
            app.stop();
        }));

        app.start(7777);
    }


    private static String loadTeam(String teamID) throws TeamNotFoundError, IOException {
        if(!new File("./teams/").exists()) {
            new File("./teams/").mkdirs();
        }
        File teamfile = new File("./teams/" + teamID + ".robo");

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
            reset();
            cc.loadbg();
            sleep(100);
        }
    }

    private static void reset() {
        setMode("hidden");
        if(cdm != null) cdm.cancel();
        broadcast(new JSONObject()
                .put("act", "reset"));
        setMode("preload");
    }

    private static void preload(String team1, String team2) {
        broadcast(new JSONObject()
                .put("act", "setTeams")
                .put("body", new JSONObject()
                        .put("maxtime", total_time)
                        .put("team1", new JSONObject(team1))
                        .put("team2", new JSONObject(team2))));
        setMode("loaded");
    }

    private static void startCountdown() {
        if(cc != null) {
            cc.play();
        }
        if (cdm == null) {
            cdm = new CountdownManager(total_time) {
                @Override
                void updateSecond(int current) {
                    broadcast(new JSONObject()
                            .put("act", "setTime")
                            .put("body", current));
                }

                @Override
                void timeIsUp() {
                    counting();
                    cdm = null;
                }
            };
        }
        setMode("ingame");
    }

    private static void interrupt() {
        cdm.cancel();
        cdm = null;
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
            try {
                wscc.send(text.toString(4));
            } catch (Exception e) {
                //ignore
            }
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