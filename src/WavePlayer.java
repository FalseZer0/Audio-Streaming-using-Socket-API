import javax.sound.sampled.*;
import java.io.*;

public class WavePlayer {
	public static void main(String args[]) {
		int totalFramesRead = 0;
		if (args.length != 1) {
			System.out.println("Usage: java WaveServer <wavefile>");
			return;
		}
   		try {
            File fileIn = new File(args[0]);
			AudioInputStream ais = AudioSystem.getAudioInputStream(fileIn);
            AudioFormat audioFormat = ais.getFormat();
            printAudioFormat(audioFormat);
            DataLine.Info dataLineInfo = new DataLine.Info (SourceDataLine.class,audioFormat);//
            if (!AudioSystem.isLineSupported(dataLineInfo)){
                System.out.println("Line matching " + dataLineInfo + " is not supported.");
            }else{
                System.out.println("Line matching " + dataLineInfo + " is supported.");
                //opening the sound output line
                SourceDataLine line = (SourceDataLine)AudioSystem.getLine(dataLineInfo);
                line.open(audioFormat);
                line.start();
                //Copy data from the input stream to the output data line
                int frameSizeInBytes = audioFormat.getFrameSize();
                int bufferLengthInFrames   = line.getBufferSize() / 8;
                int bufferLengthInBytes = bufferLengthInFrames * frameSizeInBytes;
                byte[] soundData = new byte[bufferLengthInBytes];
                int numOfBytesRead = 0;
                while ((numOfBytesRead = ais.read(soundData)) != -1){
                    line.write(soundData,0,numOfBytesRead);
                }
				line.drain();
                line.stop();
                line.close();
            }
        } catch(LineUnavailableException lue){
            System.err.println("LineUnavailableException: " +   lue.getMessage());
        } catch(UnsupportedAudioFileException uafe){
            System.err.println("UnsupportedAudioFileException: " +   uafe.getMessage());
        } catch(IOException ioe){
            System.err.println("IOException: " +   ioe.getMessage());
        }
    }
        
    private static void printAudioFormat(AudioFormat audioformat) {
        System.out.println("Format: " + audioformat.toString());
        System.out.println("Encoding: " + audioformat.getEncoding());
        System.out.println("SampleRate:" +   audioformat.getSampleRate());
        System.out.println("SampleSizeInBits: " + audioformat.getSampleSizeInBits());
        System.out.println("Channels: " + audioformat.getChannels());
        System.out.println("FrameSize: " + audioformat.getFrameSize());
        System.out.println("FrameRate: " + audioformat.getFrameRate());
        System.out.println("BigEndian: " + audioformat.isBigEndian());
    }
}
