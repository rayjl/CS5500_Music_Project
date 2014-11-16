import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;


/**
 * Rapid Prototype
 * This prototype will need re-factoring, especially with
 * managing the files to compare.
 * 
 * Canonical Form:
 * Mono 16bit 44.1 khz WAVE format
 * 
 * Last Edited: 15 November 2014
 */

public class AudioMatching {

    // File names
    private static String file_name1;
    private static String file_name2;
    
    // Path names from application arguments
    private static String path1;
    private static String path2;
    
    // File match variable
    private static boolean match;
    
    // File time shift variable
    private static int offset1;
    private static int offset2;
    
    // Global fixed variables to be used - DO NOT CHANGE
    private static final int WINDOW = 16384; //~0.37s frames
    private static final int BITWIDTH = 16;
    private static final int CHANNELS = 1;
    private static final int SAMPLERATE = 44100;
    private static final int OFFSET = 512;

    public static void main(String[] args) {    
        // Read wave files in to File objects
        // 2 Arguments should be passed in from shell script
        // Format: 
        // -f <pathname> -f <pathname>
        if (args.length != 2) {
            System.err.println("ERROR");
            System.exit(1);
        }
        
        // Initialize paths from the params
        path1 = args[0];
        path2 = args[1];
    
        // For debugging purposes
        // Passing file paths manually to java app
//        path1 = "/Users/Ray/Documents/wayfaring2.wav";
//        path2 = "/Users/Ray/Documents/wayfaring2mp3.wav";
        
        // Compare songs
        compareFiles(path1, path2);
//        System.out.println("File Comparison Successful.");
        
        // Report results and exit program
        summaryReport(match, offset1, offset2);
        System.exit(0);
    }
    
    /* boolean int int -> Void
     * Given: true if the files match, false otherwise and 2 int values
     * representing where the offset in the files are when the match occurs
     * Returns: Void
     * Note: only prints to System.out if files match
     */
    private static void summaryReport(boolean b, 
            int offset1, int offset2) {
        if (b)
            System.out.println("MATCH" + " " 
                    + file_name1 + " " + file_name2 + " "
                    + offset1 + " " + offset2);
        else
            return;
    }
    
    /* String String -> Void
     * Given: 2 file paths passed into Java application
     * Returns: Void
     * Note: this function is the main operation
     */
    private static void compareFiles(String p1, String p2) {
        
        // Grab file names from paths
        file_name1 = shortFileName(p1);
        file_name2 = shortFileName(p2);
        
        // Create file objects from file paths
        File file1 = new File(path1);
        File file2 = new File(path2);
                
        // Read files into byte arrays and compare to 
        // WAVE and MP3 formats
        // Each index contains decimal value stored in corresponding
        // byte offset
        try {
                    
            byte[] b1 = getByteArray(file1);
            byte[] b2 = getByteArray(file2);
            AudioFile af1 = new AudioFile(b1, file_name1, path1);
            AudioFile af2 = new AudioFile(b2, file_name2, path2);
                    
            // Set parameters for AudioFile
            // Method includes a file extension check
            setAudioFileParams(af1);
            setAudioFileParams(af2);
                    
            // Format Check for Data
            fileFormatCheck(af1);
            fileFormatCheck(af2);
            
            // Convert file type to Canonical form
            convertToWaveCanonical(af1);
            convertToWaveCanonical(af2);
            
            // Convert little-endian data to int format
            // Entire file stream is converted
            int[] audio_data1 = littleEndianToInt(af1.getData());
            int[] audio_data2 = littleEndianToInt(af2.getData());
            
            // Frame audio data into Intervals
            // and load into an ArrayList
            // Hanning Window will have been applied to data
            // 31/32 overlap @ 44.1khz - 16384 samples overlap
            ArrayList<ComplexNumber[]> AL1 = frameData(audio_data1);
            ArrayList<ComplexNumber[]> AL2 = frameData(audio_data2);
        
            // Apply FFT to data arrays in ArrayList
            applyFFT(AL1);
            applyFFT(AL2);
            
            // Convert data into FingerPrints and load into an ArrayList
            ArrayList<FingerPrint> fingerPrint1 = logFingerPrints(AL1);
            ArrayList<FingerPrint> fingerPrint2 = logFingerPrints(AL2);
                    
            // Compare FingerPrints
            compareFingerPrints(fingerPrint1, fingerPrint2);
                    
        } catch (IOException e) {
            e.printStackTrace();
        }
        
    }
    
    /* ArrayList<FingerPrint> ArrayList<FingerPrint> -> Void
     * Given: 2 ArrayLists containing finger prints of the audio files
     * Returns: Void
     * 
     * Note: method will set global variable match to true
     * if threshold met and the offsets where match occurs
     * in their corresponding files
     *
     */
    private static void compareFingerPrints(
            ArrayList<FingerPrint> fingerPrint1,
            ArrayList<FingerPrint> fingerPrint2) {
     
    	// Beginning of a bin is used
    	int binThreshold = (int) (5 * SAMPLERATE) / (2 * OFFSET);
    	
    	int[] result = LCS.lcs(fingerPrint1, fingerPrint2);
//    	System.out.println(binThreshold);
//    	System.out.println(result[0]);
//    	System.out.println(result[1]);
//    	System.out.println(result[2]);
    	if (result[2] >= binThreshold) {
    		match = true;
    		offset1 = result[0];
    		offset2 = result[1];
    	}
    }
    
    /* ArrayList<ComplexNumber[]> -> ArrayList<FingerPrint>
     * Given: an ArrayList loaded with data from audio file
     * Returns: an ArrayList loaded with the finger printed data
     */
    private static ArrayList<FingerPrint> logFingerPrints(
            ArrayList<ComplexNumber[]> AL) {
        
        // Structure to be used to hold FingerPrints
        ArrayList<FingerPrint> fingerPrint = 
                new ArrayList<FingerPrint>();
                
        // Iterate through intervals of sample data
        for (int i = 0; i < AL.size(); i ++) {
  
            // Create finger prints from data
        	// Pass in ComplexNumber array at each index of ArrayList
            FingerPrint fp = makeFingerPrint(AL.get(i));
            
            // Set finger prints into ArrayList
            // SINGLE FINGERPRINT NOT AN ARRAY ADDED
            fingerPrint.add(fp);        
        }
        
        return fingerPrint;
    }
    
    /* ComplexNumber[] -> FingerPrint
     * Given: the real and imag part of the the sample data after FFT
     * Returns: a FingerPrint
     *
     * Need to implement bin amplitude as a finger print
     */
    private static FingerPrint 
            makeFingerPrint(ComplexNumber[] c) {
//    	System.out.println(c.length);
//    	System.out.println(WINDOW);
    	FingerPrint fp = new FingerPrint(c, WINDOW);
        
        return fp;
    }
    
    /* ArrayList<ComplexNumber[]> -> Void
     * Given: an ArrayList<ComplexNumber[]> with data representing the audio file
     * Returns: Void
     * Note:
     * The data is transformed using FFT from this method
     */
    private static void applyFFT(ArrayList<ComplexNumber[]> AL) {
        FFT func = new FFT(WINDOW);
        
        // Iterate over the ArrayList and apply FFT
        // to every ComplexNumber[]
        for (int i = 0; i < AL.size(); i++) {
        	
        	// Extract data from ComplexNumbers
        	ComplexNumber[] temp = AL.get(i);

            // Apply in-place mutator FFT
            func.fft(temp);
            
            // Set the data back into the ArrayList
            AL.set(i, temp);
        }
        
    }
    
    /* int[] -> ArrayList<ComplexNumber[]>
     * Given: the audio data in an int[]
     * Returns: the same audio data intervaled and loaded into an
     * ArrayList<ComplexNumber[]>
     * 
     * Notes:
     * If the shifts cause the window to be longer than the data array 
     * 0s will be used as fillers
     * 
     * Overlap = 16384 samples: ~0.37 sec
     */
    private static ArrayList<ComplexNumber[]> frameData(int[] audio_data) {
        // Initializations
        ArrayList<ComplexNumber[]> AL = new ArrayList<ComplexNumber[]>();
        
        // Calculate number of full intervals
        int intervals = (int) Math.floor(audio_data.length / OFFSET);
        
        // Calculate remaining samples
//      int rem = audio_data.length % window;
        
//      System.out.println(audio_data.length);
//     	System.out.println(intervals);
//      System.out.println(rem);
        
        // Iterate through audio data samples
        for (int i = 0; i < intervals; i++) {
            // Iterate through and create temp arrays of intervals
            ComplexNumber[] temp = new ComplexNumber[WINDOW];
            for (int j = 0; j < WINDOW; j++) {
            	// Window exceeds sample array length
                if ((i * OFFSET + WINDOW) >= audio_data.length) {
                	// Data point exceeds array length
                    if ((i * OFFSET + j) >= audio_data.length)
                        temp[j] = new ComplexNumber(0, 0);
                    else
                        temp[j] = 
                        new ComplexNumber(audio_data[i * OFFSET + j], 0);
                }
                else
                    temp[j] = 
                    new ComplexNumber(audio_data[i * OFFSET + j], 0);
                
            }
            // Apply hanning function to each interval
            hanningWindow(temp);
            
            // Append temp array to end of ArrayList
            AL.add(temp);
        }    
//      System.out.println(AL.size());
        return AL;
    }
    
    /* AudioFile -> Void
     * Given: an AudioFile to be converted to WAVE format
     * Returns: Void
     * Notes: in-place mutator
     */
    private static void convertToWaveCanonical(AudioFile af) {
        
        // File is already in WAVE format
        if (af.getFormat() == Format.WAVE) {
            
            // Validate if in canonical format
            if (af.getChannels() == CHANNELS
                    && af.getBitWidth() == BITWIDTH
                    && af.getSampleRate() == SAMPLERATE)
                return;
            
            // Channels or Sample rate is wrong
            else if (af.getBitWidth() == BITWIDTH
                    && (af.getChannels() != CHANNELS
                    || af.getSampleRate() != SAMPLERATE)) {
                // Resample - wave to mp3
                String resampled = lameResample(af, af.getPath());
                
                // lame decode - convert mp3 to wave
                String decodedRS = lameDecode(af, resampled);
                
                // Update the AudioFile object with temp file data
                updateAudioFileData(af, decodedRS);
                
                // Remove temp files created in /tmp
                removeFile(resampled);
                removeFile(decodedRS);
            }
            
            // Only Bit width is wrong
            else if (af.getBitWidth() != BITWIDTH
                    && af.getChannels() == CHANNELS
                    && af.getSampleRate() == SAMPLERATE) {
                // Convert to 16bits per sample - wave to wave
                String wav16 = convertBitWidth(af, af.getPath());
                
                // Update the AudioFile object with temp file data
                updateAudioFileData(af, wav16);
                
                // Remove temp file create in /tmp
                removeFile(wav16);
            }
            
            // Both Bit Width and channels or sample rate is wrong
            else {
                // Convert to 16bits per sample - wave to wave
                String wav16 = convertBitWidth(af, af.getPath());
                
                // Resample - wave to mp3
                String resampled = lameResample(af, wav16);
                
                // lame decode - convert mp3 to wave
                String decodedRS = lameDecode(af, resampled);
                
                // Update the AudioFile object with temp file data
                updateAudioFileData(af, decodedRS);
                
                // Remove temp files created in /tmp
                removeFile(wav16);
                removeFile(resampled);
                removeFile(decodedRS);
            }

        }
        else {
            // Call helper to convert mp3 file to wave format
            String destPath = lameDecode(af, af.getPath());
            
            // Update the AudioFile object with temp file data
            updateAudioFileData(af, destPath);
            
            // Remove temp file created in /tmp    
            removeFile(destPath);
        }
        
    }
    
    /* AudioFile String -> Void
     * Given: the AudioFile to update and the String of the file path
     * Returns: Void
     */
    private static void updateAudioFileData(AudioFile af, String destPath) {
        // Load created temp file into buffer and extract byte data
        File tempFile = new File(destPath);
        
        // Debugging purposes
//        System.out.println(tempFile.exists());
//        System.out.println(tempFile.canRead());
//        System.out.println(tempFile.getAbsolutePath());
        
        try {
            byte[] tempByte = getByteArray(tempFile);
            // Overwrite current AudioFile object data
            af.setData(tempByte);
//            System.out.println("Data set successful.");
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /* AudioFile String -> String
     * Given: the object and the source path of a wave file 
     * to be converted to 16bits
     * Returns: the destination path of the temp file created
     * Notes: wav convert - wave output
     */
    private static String convertBitWidth(AudioFile af, String sourcePath) {
        String command = "/course/cs5500f14/bin/wav";
        String op1 = "-bitwidth";
        String arg1 = "16";
        String destPath = "/tmp/temp" + af.getFileName() + "16" + ".wav";
        
        // Execute file conversion with ProcessBuilder
        ProcessBuilder pb = new ProcessBuilder(command, op1, arg1, 
                sourcePath, destPath);
        executeProcess(pb);
        
        // Return the file path of the temp wave file created
        return destPath;
    }
    
    /* AudioFile String -> String
     * Given: the object and the source path of a file to 
     * resample with lame converter
     * Returns: the destination path of the resampled temp file
     * Notes: lame convert - mp3 output
     */
    private static String lameResample(AudioFile af, String sourcePath) {
        // Command to execute LAME application in CCIS box
        String command = "/course/cs5500f14/bin/lame";
        String op1 = "-a";
        String op2 = "--resample";
        String arg1 = "44.1";
        String destPath = "/tmp/temp" + af.getFileName() + "rs" + ".mp3";
        
        // Execute file conversion with ProcessBuilder
        ProcessBuilder pb = new ProcessBuilder(command, op1, op2, arg1, 
                sourcePath, destPath);
        executeProcess(pb);

        // Return the file path of the temp file
        return destPath;
    }
    
    /* AudioFile String -> String
     * Given: the object and the source path of an mp3 file 
     * to convert to wave
     * Returns: the destination path of the converted file
     * Notes: lame convert - wave output
     */
    private static String lameDecode(AudioFile af, String sourcePath) {
        // Command to execute LAME application in CCIS box
        String command = "/course/cs5500f14/bin/lame";
        String op = "--decode";
        String destPath = "/tmp/temp" + af.getFileName() + "d" + ".wav";
        
        // Execute file conversion with ProcessBuilder
        ProcessBuilder pb = new ProcessBuilder(command, op, 
                sourcePath, destPath);
        executeProcess(pb);

        // Return the file path of the temp file
        return destPath;
    }
    
    /* ProcessBuilder -> Void
     * Given: a ProcessBuilder to execute
     * Returns: Void
     * Note: This method is to simply factor out the try-catch blocks
     */
    private static void executeProcess(ProcessBuilder pb) {
//        System.out.println("Creating new process.");
//        System.out.println(af.getFileName());
        try {
            Process p = pb.start();
            
//            System.out.println("Creating BufferedReader.");
            BufferedReader reader = 
                    new BufferedReader(
                            new InputStreamReader(p.getErrorStream()));
            String line;
//            System.out.println("Begin readLine Loop.");
            while ((line = reader.readLine()) != null) {
                // Reading output stream
            }
//            System.out.println("Begin waiting for process to complete.");
        
            p.waitFor();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        catch (InterruptedException ie) {
            ie.printStackTrace();
        }
    }
    
    /* String -> Void
     * Given: the path of a file to be removed
     * Returns: Void
     */
    private static void removeFile(String destPath) {
        // Execute a process to remove a temp file created
        ProcessBuilder pb = new ProcessBuilder("rm", destPath);
        try {
            Process p = pb.start();
            p.waitFor();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        catch (InterruptedException ie) {
            ie.printStackTrace();
        }
    }

    
    /* ComplexNumber[] -> Void
     * Given: a ComplexNumber array containing the sample data 
     * as its real part
     * Returns: Void
     * Note: in-place mutator
     * This is a frame of data.
     */
    private static void hanningWindow(ComplexNumber[] sample) {
        // Iterate through the sample with the hanning window function
        double multiplier;
        for (int i = 0; i < sample.length; i++) {
            multiplier = 0.5 
            * (1.0 + Math.cos(2.0 * Math.PI * i / sample.length));
            double val = (double) (sample[i].getReal() * multiplier);
            sample[i].setReal(val);
        }
    }
    
    /* byte[] -> int[]
     * Given: the byte stream data of an audio file
     * Returns: the int form of the file 
     * Note: The entire byte stream stream is passed 
     * Assumes mono audio (single channel)
     */
    private static int[] littleEndianToInt(byte[] b) {
        // First data sample begins at offset 44
        // Sample size is 4 bytes (16 bits)
        int dataLength = (b.length - 44) / 4;
        
        int[] sample = new int[dataLength];
        
        // Counter to iterate across byte[]
        int offset = 44;
        
        // add converted data to sample[]
        for (int i = 0; i < sample.length; i++) {
            sample[i] = convertLittleEndian(b, offset, 4, true);
            offset += 4;
        }
        
        return sample;
    }
    
    /* byte[] int int boolean -> int
     * Given: the byte array, the offset location, size of the chunk
     * @param bool : true - little-endian / false - big-endian
     * Returns: the int value form of the little-endian chunk
     * Note: helper function for littleEndianToInt
     * This can be used for BigEndian to int conversion
     */
    private static int convertLittleEndian(byte[] b,
            int offset, int size, boolean bool) {
        // value to return
        // will add to it based on bitwise operations
        int sample = 0;
        
        for (int i = 0; i < size; i++) {
            // temp value for each byte of data
            int val = 0;
            
            // 2's complement value is negative
            // Use bitwise operations to convert to binary
            if (b[offset + i] < 0) {
                // remove negative value
                val = b[offset + i] * -1;
                
                // reverse bits
                val = val ^ 0xFF;
                
                // add 1 to bits
                val = val + 1;
            }
            else {
                // set value to current byte
                val = b[offset + i] & 0xFF;
            }
            
            // set bits based on endian-ness
            if (bool)
                sample = (val << 8*i) + sample;
            else
                sample = (sample << 8*i) + val;
        }
        
        // Return int value of the little-endian chunk
        return sample;
    }

    /* String -> String
     * Given: the path name of a file
     * Returns: the short name of the file
     * Note: the name of the file is contained in the path name
     * This function is fixed - does not need to be modified
     */
    private static String shortFileName(String s) {
        // temp string to concatenate file name to
        String temp = "";
        
        // Loop through path from end until '/' is detected
        for (int i = s.length() - 1; i >= 0; i--) {
            char c = s.charAt(i);
            if (c == '/')
                break;
            else
                temp = c + temp;
        }
        
        // Return the concatenated string representing
        // the file name with n characters in O(n^2) time
        return temp;    
    }
    
    /* AudioFile -> Void
     * Given: an AudioFile object to have its fields set
     * Returns: Void
     * Notes: calls helper methods based on file extension
     */
    private static void setAudioFileParams(AudioFile af) {
        // Get the file extension of the file
        String fileExtension = getFileExtension(af.getFileName());
        
        // Check file extensions - .wav .mp3 or .ogg
        // Set parameters based on extension
        if (fileExtension.equals(".wav"))
            setWaveFileParams(af);
        else if (fileExtension.equals(".mp3"))
            setMP3FileParams(af);
        else if (fileExtension.equals(".ogg"))
            setOGGFileParams(af);
        else
            error(1, fileExtension);
    }
    
    /* AudioFile -> Void
     * Given: the AudioFile object with a file extension of ".wav"
     * Returns: Void
     * Notes: Helper function for setAudioFileParams
     */
    private static void setWaveFileParams(AudioFile af) {
        // Grab the byte array from the object
        // Values in array are decimal format
        byte[] data = af.getData();
        
        // RIFF scan - offset 0 size 4
        if ((char)data[0] == 'R' 
                && (char)data[1] == 'I'
                && (char)data[2] == 'F' 
                && (char)data[3] == 'F')
            af.setRIFFval(true);
    
        // File Format - offset 8 size 4
        if ((char)data[8] == 'W' 
                && (char)data[9] == 'A'
                && (char)data[10] == 'V' 
                && (char)data[11] == 'E')
            af.setFormat("WAVE");
        
        // Audio Format - offset 20 size 2
        af.setCompression(data[20]);
        
        // Number of Channels - offset 22 size 2
        af.setChannels(data[22]);
        
        // Bits per Sample - offset 34 size 2
        af.setBitWidth(data[34]);
        
        // Sampling rate - offset 24 size 4
        int sampleRate = convertLittleEndian(data,
                24, 4, true);
        af.setSampleRate(sampleRate);
    }

    /* AudioFile -> Void
     * Given: the AudioFile object with a file extension of ".mp3"
     * Returns: Void
     * Notes: Helper function for setAudioFileParams
     * 
     */
    private static void setMP3FileParams(AudioFile af) {
        // Grab the byte array from the object
        byte[] data = af.getData();
        
        // MPEG-1 Layer III Corresponds to first 2 bytes reading :
        // 0xFFFA or 0xFFFB depending on CRC
        String hex1 = "FFFA";
        String hex2 = "FFFB";
        
        // Parse Hex Strings to int values for comparison
        int dec1 = Integer.parseInt(hex1, 16);
        int dec2 = Integer.parseInt(hex2, 16);
        
        
        // Read the first 2 bytes (16bits) of the file
        // Convert the big-endian value to process
        int val = convertLittleEndian(data, 0, 2, false);
        
        if ((val == dec1) || (val == dec2))
            af.setFormat("MP3");
        else
            error(2, af.getFileName());
    }
    
    /* AudioFile -> Void
     * Given: the AudioFile object with a file extension of ".gg"
     * Returns: Void
     * Notes: Helper function for setAudioFileParams
     * 
     * TODO - 
     * 
     */
    private static void setOGGFileParams(AudioFile af) {
        
    }

    /* String -> String
     * Given: the file name to check its extension
     * Returns: the file extension
     */
    private static String getFileExtension(String fileName) {
        // Use a StringBuilder for file extension
        StringBuilder temp = new StringBuilder();
        
        // Loop through from end of string and append to StringBuilder
        for (int i = fileName.length() - 1; i >= 0; i--) {
            if (fileName.charAt(i) == '.') {
                temp.append(fileName.charAt(i));
                break;
            }
            else
                temp.append(fileName.charAt(i));
        }
        // Reverse the appended string to be returned
        temp.reverse();
        
        // Return the file extension
        return temp.toString();
    }
    
    /* AudioFile -> Void
     * Given: an AudioFile object to check its format
     * Returns: Void
     */
    private static void fileFormatCheck(AudioFile af) {
        // Validate to check if its either in wave, mp3, or ogg format
        if (waveFormatCheck(af) || mp3FormatCheck(af) 
                || oggFormatCheck(af))
            return;
        else
            error(2, af.getFileName());
    }
    
    /* AudioFile -> boolean
     * Given: an AudioFile to check if its in WAVE format
     * Returns: true if it is, false otherwise 
     */
    private static boolean waveFormatCheck(AudioFile af) {
        return (af.getRIFFval() 
                && af.getFormat() == Format.WAVE);
    }
    
    
    /* AudioFile -> boolean
     * Given: an AudioFile to check if its in MP3 format
     * Returns: true if it is, false otherwise 
     * 
     * TODO - 
     * This and setMP3FileParams is redundant in a way.
     * Refactor this section in the future
     */
    private static boolean mp3FormatCheck(AudioFile af) {
        return (af.getFormat() == Format.MP3);
    }
    
    /* AudioFile -> boolean
     * Given: an AudioFile to check if its in OGG format
     * Returns: true if it is, false otherwise 
     * 
     * TODO - 
     * This is for later use for OGG file formats.
     */
    private static boolean oggFormatCheck(AudioFile af) {
        
        return false;
    }
    
    /* File -> byte[]
     * Given: a file that is read 
     * Returns: the byte stream representation of the file
     * Note: Fixed - does not need to be modified
     */
    private static byte[] getByteArray(File file) throws IOException {
        // Create FileInputStream from file argument
        FileInputStream fis = null;
        fis = new FileInputStream(file);

        // Read bytes from buffer
        byte[] b = new byte[fis.available()];
        fis.read(b);
        fis.close();

        // return the array read from the buffer
        return b;
    }    

    /* int String -> Void
     * Given: an int value that will determine the error to throw
     * for the given file String s
     * Returns: Void
     * Note: this function is for printing error messages to stderr
     */
    private static void error(int e, String s) {
        // switch block for error messages
        switch (e) {
            case 1:
                System.err.println("ERROR : " + s + " " 
                    + "is not a supported extension.");
                break;
            case 2:
                System.err.println("ERROR : " + s + " "
                    + "has incorrect file format.");
                break;
            default:
                System.err.println("ERROR");
                break;
        }
        // Terminate the application
        System.exit(1);
    }
    
}
