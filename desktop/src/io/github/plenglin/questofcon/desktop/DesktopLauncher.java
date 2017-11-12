package io.github.plenglin.questofcon.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import io.github.plenglin.questofcon.Constants;
import io.github.plenglin.questofcon.QuestOfCon;
import io.github.plenglin.questofcon.net.Matchmaker;
import org.apache.commons.cli.*;

public class DesktopLauncher {

	public static void main(String[] args) {
	    Options options = new Options();
	    Option serverOpt = new Option("s", "start dedicated server");
	    serverOpt.setOptionalArg(true);
	    options.addOption(serverOpt);

        CommandLineParser parser = new DefaultParser();
        try {
            int port = Constants.INSTANCE.getSERVER_PORT();
            CommandLine cmd = parser.parse(options, args);
            if (cmd.hasOption("s")) {
                startServer(port);
            } else {
                startClient();
            }
        } catch (ParseException e) {
            e.printStackTrace();
            System.exit(1);
        }
	}

    private static void startServer(int port) {
        Matchmaker.INSTANCE.acceptSockets(port);
        System.exit(0);
    }

    private static void startClient() {
        String title = System.getenv("title");
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
