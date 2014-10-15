import java.util.Arrays;

/*
 * Audio File object to hold details of Input file
 */

public class AudioFile {
	
	/* ------------------------------------------------
	 * Declare File states
	 *-----------------------------------------------*/	
	
	// RIFF
	// Canonical WAVE format starts with RIFF header
	private boolean RIFFval;
	
	// Audio File Format
	// MP3, WAVE, etc.
	private Format format;
	
	// Audio Format = 1 (PCM)
	// Values other than 1 indicate compression of some type
	private int audioFormat;
	
	// Number of Channels
	// Mono = 1, Stereo = 2 
	private int numChannels;
	
	// Bits per Sample
	// 8 bits = 8, 16 bits = 16
	private int bitsPerSample;
	
	// Sample Rate
	// 8800, 44100, etc.
	private int sampleRate;
	
	// Data array
	private byte[] data;
	
	// Constructor ----------------------------------------
	// Class can only be instantiated with a byte stream

	public AudioFile(byte[] data) {
		this.data = data;
	}
	
	// Setters --------------------------------------------
	
	public void setRIFFval(boolean RIFFval) {
		this.RIFFval = RIFFval;
	}
	
	public void setFormat(String format) {
		switch (format) {
		case "WAVE": 	this.format = Format.WAVE;
					 	break;
		case "MP3": 	this.format = Format.MP3;
						break;
		case "MIDI":	this.format = Format.MIDI;
						break;
		default: 		this.format = null;
				 		break;
		}
	}

	public void setCompression(int audioFormat) {
		this.audioFormat = audioFormat;
	}
	
	public void setChannels(int numChannels) {
		this.numChannels = numChannels;
	}
	
	public void setBPS(int bitsPerSample) {
		this.bitsPerSample = bitsPerSample;
	}
	
	public void setSampleRate(int sampleRate) {
		this.sampleRate = sampleRate;
	}
	
	// Getters---------------------------------------------
	
	public boolean getRIFFval() {
		return this.RIFFval;
	}
	
	public Format getFormat() {
		return this.format;
	}

	public int getCompression() {
		return this.audioFormat;
	}
	
	public int getChannels() {
		return this.numChannels;
	}
	
	public int getBPS() {
		return this.bitsPerSample;
	}
	
	public int getSampleRate() {
		return this.sampleRate;
	}
	
	public byte[] getData() {
		return this.data;
	}
	
	// Other Methods --------------------------------------
	
	public byte[] getAudioData() {
		if (this.format == Format.WAVE){
			return Arrays.copyOfRange(data, 36, data.length);
		} else if (this.format == Format.MP3){
			System.err.print("audio format not yet supported");
		} else if (this.format == Format.MIDI){
			System.err.print("audio format not yet supported");
		} else if (this.format == null) {
			System.err.print("audio format not supported");
		}
		return null;
	}

}
