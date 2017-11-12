package io.github.plenglin.questofcon.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import io.github.plenglin.questofcon.Config;
import io.github.plenglin.questofcon.Constants;
import io.github.plenglin.questofcon.QuestOfCon;
import io.github.plenglin.questofcon.game.GameData;
import io.github.plenglin.questofcon.net.Matchmaker;
import org.apache.commons.cli.*;

public class DesktopLauncher {

	public static void main(String[] args) {
	    Options options = new Options();
        Option server = new Option("s", "start dedicated server");
        Option client = new Option("c", true,"start multiplayer client");
        options.addOption(server);
        options.addOption(client);

        CommandLineParser parser = new DefaultParser();
        try {
            int port = Constants.INSTANCE.getSERVER_PORT();
            CommandLine cmd = parser.parse(options, args);
            if (cmd.hasOption("s")) {
                startServer(port);
            } else if (cmd.hasOption("c")) {
                Config.name = cmd.getOptionValue("c");
                Config.mode = Config.Mode.CLIENT;
                startClient();
            } else {
                Config.mode = Config.Mode.PNP;
                startClient();
            }
        } catch (ParseException e) {
            e.printStackTrace();
            System.exit(1);
        }
	}

    private static void startServer(int port) {
        GameData.INSTANCE.register();
        Matchmaker.INSTANCE.acceptSockets(port);
        System.exit(0);
    }

    private static void startClient() {
        String title = Config.name;
        String x = System.getenv("windowx");

        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.useGL30 = true;
        config.foregroundFPS = 60;
        config.backgroundFPS = 30;
        config.width = 800;
        config.height = 600;
        config.title = String.format("QuestOfCon%s", title == null ? "" : (": " + title));
        config.x = x != null ? Integer.parseInt(x) : -1;
        new LwjglApplication(QuestOfCon.INSTANCE, config);
    }

}
