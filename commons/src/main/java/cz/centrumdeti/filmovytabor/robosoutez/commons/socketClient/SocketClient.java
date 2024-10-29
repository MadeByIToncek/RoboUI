/******************************************************************************
 *                    Copyright (c) 2024.                                     *
 *  Filmovy tabor Centrumdeti.cz & Roboticky tabor Centrumdeti.cz             *
 ******************************************************************************/

package cz.centrumdeti.filmovytabor.robosoutez.commons.socketClient;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;

import cz.centrumdeti.filmovytabor.robosoutez.commons.comms.commands.ResetCommand;
import cz.centrumdeti.filmovytabor.robosoutez.commons.comms.requests.RequestCurrentMatch;
import cz.centrumdeti.filmovytabor.robosoutez.commons.comms.requests.RequestNewMatch;
import cz.centrumdeti.filmovytabor.robosoutez.commons.comms.requests.RequestTeam;
import cz.centrumdeti.filmovytabor.robosoutez.commons.comms.requests.RequestTeams;
import cz.centrumdeti.filmovytabor.robosoutez.commons.comms.responses.RequestCurrentMatchResponse;
import cz.centrumdeti.filmovytabor.robosoutez.commons.comms.responses.RequestNewMatchResponse;
import cz.centrumdeti.filmovytabor.robosoutez.commons.comms.responses.RequestTeamResponse;
import cz.centrumdeti.filmovytabor.robosoutez.commons.comms.responses.RequestTeamsResponse;
import cz.centrumdeti.filmovytabor.robosoutez.commons.types.ResetReason;
import cz.centrumdeti.filmovytabor.robosoutez.commons.types.SimpleResponse;

public class SocketClient {
    private final String address;
    private final int port;

    public SocketClient(String address, int port) {
        this.address = address;
        this.port = port;
    }

    public SimpleResponse reset(ResetReason reason) {
        try {
            return (SimpleResponse) requestObject(new ResetCommand(reason));
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public RequestCurrentMatchResponse requestCurrentMatch() {
        try {
            return (RequestCurrentMatchResponse) requestObject(new RequestCurrentMatch());
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public RequestNewMatchResponse requestNewMatch(int teamL, int teamR) {
        try {
            return (RequestNewMatchResponse) requestObject(new RequestNewMatch(teamL, teamR));
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public RequestTeamResponse requestSingleTeam(int team_id) {
        try {
            return (RequestTeamResponse) requestObject(new RequestTeam(team_id));
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public RequestTeamsResponse requestAllTeams() {
        try {
            return (RequestTeamsResponse) requestObject(new RequestTeams());
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public Serializable requestObject(Serializable request) throws IOException, ClassNotFoundException {
        Socket clientSocket = new Socket(address, port);
        ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
        ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());

        out.writeObject(request);
        out.flush();

        Object ir = in.readObject();
        in.close();
        out.close();
        clientSocket.close();
        if (ir instanceof Serializable) {
            return (Serializable) ir;
        } else return null;
    }
}
