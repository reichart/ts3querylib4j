package com.google.code.ts3query;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Map;

import com.google.code.ts3query.model.entity.Version;
import com.google.code.ts3query.model.entity.WhoAmI;
import com.google.code.ts3query.option.TargetMode;

public class TeamspeakProtocol {

  private final TeamspeakConnection server;

  public TeamspeakProtocol(TeamspeakConnection server) {
    this.server = server;
  }

  /**
   * Displays a list of IP addresses used by the server instance on multi-homed
   * machines.
   * 
   * @return <code>ip=0.0.0.0</code>
   */
  public TeamspeakResponse bindinglist() {
    return send(command("bindinglist"));
  }

  /**
   * Sends a text message to all clients on all virtual servers in the TeamSpeak
   * 3 Server instance.
   * 
   * @param msg
   */
  public void gm(String msg) {
    send(command("gm").with("msg", msg));
  }

  /**
   * Displays detailed configuration information about the server instance including uptime, number
   * of virtual servers online, traffic information, etc.
   * 
   * @return <code>instance_uptime=1903203 host_timestamp_utc=1259337246 virtualservers_running_total=1 connection_filetransfer_bandwidth_sent=0 ...</code>
   */
  public TeamspeakResponse hostinfo() {
    return send(command("hostinfo"));
  }

  /**
   * Changes the server instance configuration using given properties.
   * 
   * @param parameters
   */
  public void instanceedit(Map<String, ? extends Object> parameters) {
    send(command("instanceedit").with(parameters));
  }

  /**
   * Displays the server instance configuration including database revision
   * number, the file transfer port, default group IDs, etc.
   * 
   * @return
   * 
   *         <code>serverinstance_database_version=11 serverinstance_filetransfer_port=30033 serverinstance_template_guest_serverquery_group=1 serverinstance_template_serveradmin_group=3...</code>
   */
  public TeamspeakResponse instanceinfo() {
    return send(command("instanceinfo"));
  }

  /**
   * Authenticates with the TeamSpeak 3 Server instance using given ServerQuery
   * login credentials.
   * 
   * @param username
   * @param password
   */
  public void login(String username, String password) {
    send(command("login")
        .with("client_login_name", username)
        .with("client_login_password", password));
  }

  /**
   * Deselects the active virtual server and logs out from the server instance.
   */
  public void logout() {
    send(command("logout"));
  }

  /**
   * Closes the ServerQuery connection to the TeamSpeak 3 Server instance.
   */
  public void quit() {
    send(command("quit"));
  }

  /**
   * Sends a text message a specified target. The type of the target is
   * determined by targetmode while target specifies the ID of the recipient,
   * whether it be a virtual server, a channel or a client.
   * 
   * @param mode
   * @param targetId
   *          clientID|channelID|serverID, depending on target mode
   * @param message
   */
  public void sendTextMessage(TargetMode mode, long targetId, String message) {
    send(command("sendtextmessage")
        .with("targetmode", 1 + mode.ordinal())
        .with("target", targetId)
        .with("msg", message));
  }

  /**
   * Selects the virtual server specified with sid to allow further interaction.
   * The ServerQuery client will appear on the virtual server and acts like a
   * real TeamSpeak 3 Client, except it's unable to send or receive voice data.
   * 
   * @param serverId
   *          serverID
   */
  public void use(int serverId) {
    send(command("use").with("sid", serverId));
  }

  /**
   * Displays the servers version information including platform and build
   * number.
   * 
   * @return <code>version=3.0.0-alpha03 build=8779 platform=Linux</code>
   */
  public Version version() {
    return send(command("version")).as(Version.class);
  }

  /**
   * Displays information about your current ServerQuery connection including
   * the ID of the selected virtual server, your loginname, etc.
   * 
   * @return <code>virtualserver_status=online virtualserver_id=1 virtualserver_unique_identifier=zrPkjznB1tMnRwj01xx7RxXjqeY= client_channel_id=2 ...</code>
   */
  public WhoAmI whoAmI() {
    return send(command("whoami")).as(WhoAmI.class);
  }

  /**
   * Send a command directly to the query server.
   * 
   * @param command
   * @return the response sent back by the server or <code>null</code> if the
   *         server sent none
   */
  public TeamspeakResponse send(TeamspeakCommand command) {
    try {
      return server.execute(command);
    } catch (IOException ex) {
      throw new RuntimeException(ex);
    }
  }

  /**
   * Injects this server instance into any fields of matching type in another
   * object.
   * 
   * @param <T>
   *          the type of the object
   * @param object
   *          the instance to inject this server into
   * @return the object with this server injected
   */
  public <T> T inject(T object) {
    // look at both class and super class of object
    Class<? extends Object> clazz = object.getClass();
    inject(object, clazz.getDeclaredFields());
    inject(object, clazz.getSuperclass().getDeclaredFields());
    return object;
  }

  private <T> void inject(T object, Field[] fields) {
    for (Field field : fields) {
      if (field.getType().equals(getClass())) {
        try {
          field.setAccessible(true);
          field.set(object, this);
        } catch (Exception ex) {
          throw new RuntimeException(ex);
        }
      }
    }
  }

  /**
   * Factory method for easy creation of {@link TeamspeakCommand} instances.
   * 
   * @param keyword
   *          the keyword for the command
   * @return a new command instance
   */
  private static TeamspeakCommand command(String keyword) {
    return new TeamspeakCommand(keyword);
  }
}
