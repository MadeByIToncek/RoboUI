/******************************************************************************
 *                    Copyright (c) 2024.                                     *
 *  Filmovy tabor Centrumdeti.cz & Roboticky tabor Centrumdeti.cz             *
 ******************************************************************************/

package cz.centrumdeti.filmovytabor.robosoutez.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import cz.centrumdeti.filmovytabor.robosoutez.commons.comms.requests.RequestTeams;
import cz.centrumdeti.filmovytabor.robosoutez.commons.comms.responses.RequestTeamsResponse;
import cz.centrumdeti.filmovytabor.robosoutez.commons.types.Member;
import cz.centrumdeti.filmovytabor.robosoutez.commons.types.Team;

public class TestClient {
    private static final Logger log = LoggerFactory.getLogger(TestClient.class);

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        Socket clientSocket = new Socket("localhost", 7777);
        ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
        ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
        long start = System.currentTimeMillis();
        out.writeObject(new RequestTeams());
        out.flush();

        RequestTeamsResponse ir = ((RequestTeamsResponse) in.readObject());
        for (Team team : ir.teams()) {
            for (Member member : team.members()) {
                log.info("{} - {} {}", team.teamName(), member.firstname(), member.lastname());
            }
        }
        log.error("Took {}ms", System.currentTimeMillis() - start);
        in.close();
        out.close();
        clientSocket.close();
    }
}
