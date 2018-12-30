package net.runelite.client.plugins.musicplayer;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import javax.annotation.Nullable;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MetaEventListener;
import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.sound.midi.Synthesizer;
import javax.sound.midi.Transmitter;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Mixer;
import javax.swing.Timer;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class MusicPlayer
{
	private static final Logger logger = LoggerFactory.getLogger(MusicPlayer.class);
	private final List<ActionListener> tickListeners = new ArrayList<>();
	private final List<MetaEventListener> metaEventListeners = new ArrayList<>();
	private Timer timerTick;
	private Thread refreshThread;
	private Sequencer sequencer;
	private Synthesizer synthesizer;

	@Nullable
	private Mixer activeAudioDevice;

	@Getter
	private int volume;

	MusicPlayer()
	{
		startUp();
	}

	private void startUp()
	{
		Mixer.Info[] audioDevices = AudioSystem.getMixerInfo();
		try
		{
			synthesizer = MidiSystem.getSynthesizer();
			synthesizer.open();

			sequencer = MidiSystem.getSequencer(false);
			sequencer.open();

			Transmitter transmitter = sequencer.getTransmitter();
			transmitter.setReceiver(synthesizer.getReceiver());
		}
		catch (MidiUnavailableException e)
		{
			logger.debug("midi devices unavailable");
			return;
		}

		for (MetaEventListener metaEventListener : metaEventListeners)
		{
			sequencer.addMetaEventListener(metaEventListener);
		}

		final String SF_FILE_NAME = "Old School RuneScape v3.0.sf2";
		InputStream inputStreamSoundFont = getClass().getResourceAsStream(SF_FILE_NAME);

		try
		{
			synthesizer.loadAllInstruments(MidiSystem.getSoundbank(inputStreamSoundFont));
		}
		catch (InvalidMidiDataException e)
		{
			logger.debug("soundfont file has an invalid format: {}", SF_FILE_NAME);
		}
		catch (IOException e)
		{
			logger.debug("I/O Exception reading file: {}", SF_FILE_NAME);
		}

		if (audioDevices.length != 0)
		{
			try
			{
				activeAudioDevice = AudioSystem.getMixer(audioDevices[0]);
			}
			catch (IllegalArgumentException e)  // Audio device disabled (unplugged) after getting mixer info
			{
				logger.debug("audio device no longer available");
				activeAudioDevice = null;
			}
		}
		else
		{
			activeAudioDevice = null;
		}

		timerTick = new Timer(100, e -> tick());
		refreshThread = new Thread(() ->
		{
			while (true)
			{
				refreshAudioDevice();
				try
				{
					Thread.sleep(1000);
				}
				catch (InterruptedException e)
				{
					return;
				}
			}
		});
		refreshThread.start();
	}

	void shutDown()
	{
		timerTick.stop();
		refreshThread.interrupt();
		sequencer.stop();
		if (timeoutRunnable(() -> synthesizer.close(), Duration.ofMillis(300)))  // hangs occasionally
		{
			logger.debug("synthesizer.close() timed out");
		}
		if (timeoutRunnable(() -> sequencer.close(), Duration.ofMillis(300)))
		{
			logger.debug("sequencer.close() timed out");
		}
	}

	void setPercentProgress(double percentPosition)
	{
		long length = sequencer.getTickLength();
		sequencer.setTickPosition((long) (length * percentPosition));
	}

	double getPercentProgress()
	{
		return (double) sequencer.getTickPosition() / (double) sequencer.getTickLength();
	}

	void restart()
	{
		sequencer.setTickPosition(0);
	}

	boolean isPlaying()
	{
		return sequencer.isRunning();
	}

	void pause()
	{
		timerTick.stop();
		sequencer.stop();
	}

	void play()
	{
		timerTick.start();
		sequencer.start();
	}

	void load(String songId)
	{
		boolean isNull = synthesizer == null || sequencer == null;

		if (isNull || !synthesizer.isOpen() || !sequencer.isOpen())
		{
			startUp();
			if (isNull)
			{
				return;
			}
		}

		String midiFilePath =  "music/" + songId + " - " + MusicPlayerPlugin.musicNameIndex.get(songId) + ".mid";
		InputStream inputStreamSong = getClass().getResourceAsStream(midiFilePath);
		Sequence sequence;
		try
		{
			sequence = MidiSystem.getSequence(inputStreamSong);
		}
		catch (InvalidMidiDataException e)
		{
			logger.debug("file is not in a valid midi format: {}", midiFilePath);
			return;
		}
		catch (IOException e)
		{
			logger.debug("I/O Exception reading file: {}", midiFilePath);
			return;
		}

		try
		{
			boolean wasRunning = sequencer.isRunning();
			if (wasRunning)
				pause();
			sequencer.setSequence(sequence);
			if (wasRunning)
				play();
		}
		catch (InvalidMidiDataException e)
		{
			logger.debug("sequence invalid or unsupported by sequencer");
		}
	}

	void addTickListener(ActionListener actionListener)
	{
		tickListeners.add(actionListener);
	}

	void addMetaEventListener(MetaEventListener metaEventListener)
	{
		metaEventListeners.add(metaEventListener);
		sequencer.addMetaEventListener(metaEventListener);
	}

	long getMicrosecondPosition()
	{
		return sequencer.getMicrosecondPosition();
	}

	long getMicrosecondLength()
	{
		return sequencer.getMicrosecondLength();
	}

	void setVolume(int volume)
	{
		this.volume = volume > 100 ? 100 : volume < 0 ? 0 : volume;
		double gain = this.volume / 100.0;
		MidiChannel[] channels = synthesizer.getChannels();
		for (MidiChannel channel : channels)
		{
			channel.controlChange(7, (int) (gain * 127.0));
		}
	}

	private void tick()
	{
		for (ActionListener actionListener : tickListeners)
		{
			actionListener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "tick"));
		}
	}

	private void refreshAudioDevice()
	{
		Mixer.Info[] audioDevices = AudioSystem.getMixerInfo();
		if (audioDevices.length == 0)
		{
			activeAudioDevice = null;
		}
		else
		{
			boolean doInit = true;
			if (activeAudioDevice != null)
			{
				try
				{
					if (AudioSystem.getMixer(audioDevices[0]).equals(activeAudioDevice))
					{
						doInit = false;
					}
				}
				catch (IllegalArgumentException e)  // Audio device disabled (unplugged) after getting mixer info
				{
					logger.debug("audio device no longer available");
				}
			}
			if (doInit)
			{
				logger.debug("reinitializing synthesizer");
				long position = sequencer.getTickPosition();
				Sequence sequence = sequencer.getSequence();
				boolean wasRunning = sequencer.isRunning();
				shutDown();
				startUp();
				try
				{
					sequencer.setSequence(sequence);
					sequencer.setTickPosition(position);
					if (wasRunning)
					{
						play();
					}
				}
				catch (InvalidMidiDataException e)
				{
					logger.debug("sequence invalid or unsupported by the sequencer");
				}
			}
		}
	}

	private boolean timeoutRunnable(Runnable runnable, Duration timeoutDuration)
	{
		boolean timedOut = false;
		ExecutorService executor = Executors.newSingleThreadExecutor();
		final Future handler = executor.submit((Callable<Object>) () ->
		{
			runnable.run();
			return "";
		});
		try
		{
			handler.get(timeoutDuration.toMillis(), TimeUnit.MILLISECONDS);
		}
		catch (TimeoutException | InterruptedException | ExecutionException e)
		{
			handler.cancel(true);
			timedOut = true;
		}
		executor.shutdownNow();
		return timedOut;
	}
}
