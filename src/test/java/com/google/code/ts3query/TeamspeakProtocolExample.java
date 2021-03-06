package com.google.code.ts3query;

import java.io.IOException;

import com.google.code.ts3query.model.entity.WhoAmI;

public class TeamspeakProtocolExample {

  public static void main(String[] args) throws IOException {
    String host = args[0];
    String username = args[1]; // probably "serveradmin"
    String password = args[2]; // this is in your server log

    TeamspeakConnection conn = new TeamspeakConnection(host);
    TeamspeakProtocol server = new TeamspeakProtocol(conn);

    try {
      server.login(username, password);

      // very little works outside of a virtual server
      server.use(1);

      // get some information about ourselves and print it out
      WhoAmI me = server.whoAmI();
      System.err.println("I'm " + me.getClientNickname()
          + " connected as " + me.getClientLoginName()
          + " in channel #" + me.getClientChannelId()
          + " on server #" + me.getVirtualServerId());

      // be nice, log out and quite
      server.logout();
      server.quit();
    } finally {
      // close connection to free resources
      conn.close();
    }
  }

}
