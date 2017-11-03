package io.github.plenglin.questofcon.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import io.github.plenglin.questofcon.QuestOfCon;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.useGL30 = true;
        config.foregroundFPS = 60;
        config.backgroundFPS = 30;
		config.width = 800;
		config.height = 600;
		new LwjglApplication(QuestOfCon.INSTANCE, config);
	}
}
