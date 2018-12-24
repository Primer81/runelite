package net.runelite.client.plugins.musicplayer;

import javax.sound.midi.*;
import java.io.IOException;
import java.io.InputStream;

class MusicPlayer
{
	private Synthesizer synthesizer;
	Sequencer sequencer;
	private MetaEventListener meListener;

	MusicPlayer(MetaEventListener meListener)
	{
		this.meListener = meListener;
		init();
	}

	private void init()
	{
		try
		{
			// Open synthesizer
			this.synthesizer = javax.sound.midi.MidiSystem.getSynthesizer();
			this.synthesizer.open();

			// Open sequencer
			this.sequencer = MidiSystem.getSequencer(false);
			this.sequencer.open();
			this.sequencer.addMetaEventListener(this.meListener);
			this.sequencer.setLoopCount(0);

			// Initialize sequencer and synthesizer
			Transmitter transmitter = this.sequencer.getTransmitter();
			Receiver receiver = this.synthesizer.getReceiver();
			transmitter.setReceiver(receiver);

			// Load old school runescape sound font
			InputStream inputStreamSoundFont = getClass().getResourceAsStream("Old School RuneScape v3.0.sf2");
			synthesizer.loadAllInstruments(MidiSystem.getSoundbank(inputStreamSoundFont));
		}
		catch (MidiUnavailableException | InvalidMidiDataException | IOException e)
		{
			e.printStackTrace();
		}
	}

	void skip()
	{
		boolean wasRunning = this.sequencer.isRunning();
		if (this.sequencer.getTickLength() != 0)
		{
			if (wasRunning)
				this.sequencer.stop();

			this.sequencer.setTickPosition(this.sequencer.getTickLength() - 1);  // skip to end

			if (wasRunning)
				this.sequencer.start();
		}
	}

	void pause()
	{
		this.sequencer.stop();
	}

	void play()
	{
		this.sequencer.start();
	}

	void load(String songPath)
	{
		if (synthesizer == null)
		{
			init();
		}

		if (synthesizer == null)
		{
			return;
		}

		try
		{
			// Load midi song file
			InputStream inputStreamSong = getClass().getResourceAsStream(songPath);
			Sequence sequence = MidiSystem.getSequence(inputStreamSong);
			this.sequencer.setSequence(sequence);
		}
		catch (InvalidMidiDataException | IOException e)
		{
			e.printStackTrace();
		}
	}
}
