package net.runelite.client.plugins.musicplayer;

enum Images
{
	BACK_IMG("back.png"),
	CONFIG_IMG("config.png"),
	DONE_IMG("done.png"),
	LOOP_IMG("loop.png"),
	MINUS_IMG("minus.png"),
	MUSIC_IMG("music.png"),
	NEXT_IMG("next.png"),
	PAUSE_IMG("pause.png"),
	PLAY_IMG("play.png"),
	PLUS_IMG("plus.png"),
	SHUFFLE_IMG("shuffle.png");
	String fileName;
	Images(String fileName)
	{
		this.fileName = fileName;
	}
}
