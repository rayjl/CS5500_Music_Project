/*
 * Audio File object to hold details of Input file
 */

public class AudioFile {
	
	/* ------------------------------------------------
	 * Declare File states
	 *-----------------------------------------------*/	
	
	// File name
	private String fileName;
	
	// Path name
	private String path;

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
	private int bitWidth;
	
	// Sample Rate
	// 8800, 44100, etc.
	private int sampleRate;
	
	// Data array
	private byte[] data;
	
	// Constructor ----------------------------------------
	// Class can only be instantiated with a byte stream

	public AudioFile(byte[] data, String fileName, String path) {
		this.data = data;
		this.fileName = fileName;
		this.path = path;
	}
	
	// Setters --------------------------------------------
	
	public void setRIFFval(boolean RIFFval) {
		this.RIFFval = RIFFval;
	}
	
	public void setFormat(String format) {
		// Necessary for Java6...
		int val;
		if (format == "WAVE")
			val = 1;
		else if (format == "MP3")
			val = 2;
		else if (format == "OGG")
			val = 3;
		else
			val = 0;
			
		// Switch statement for file types
		// Will need to be modified for other file formats
		switch (val) {
			case 1: 		this.format = Format.WAVE;
					 		break;
			case 2: 		this.format = Format.MP3;
							break;
			case 3:			this.format = Format.OGG;
							break;
			default: 		this.format = null;
				 			break;
		}
	}
	
	// This function is used to overwrite audioFile data
	// after file format conversion
	// e.g. mp3 -> wave
	public void setData(byte[] data) {
		this.data = data;
	}

	public void setCompression(int audioFormat) {
		this.audioFormat = audioFormat;
	}
	
	public void setChannels(int numChannels) {
		this.numChannels = numChannels;
	}
	
	public void setBitWidth(int bitWidth) {
		this.bitWidth = bitWidth;
	}
	
	public void setSampleRate(int sampleRate) {
		this.sampleRate = sampleRate;
	}
	
	// Getters---------------------------------------------
	
	public String getPath() {
		return this.path;
	}

	public String getFileName() {
		return this.fileName;
	}
	
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
	
	public int getBitWidth() {
		return this.bitWidth;
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
			return data;
		} else if (this.format == Format.MP3){
			return data;
		} else if (this.format == Format.OGG){
			System.err.print("audio format not yet supported");
		} else if (this.format == null) {
			System.err.print("audio format not supported");
		}
		return null;
	}

}
