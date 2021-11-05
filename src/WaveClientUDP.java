import javax.sound.sampled.*;
import java.io.*;
import java.net.*;

/*
 * Example usage: java WaveClientUDP localhost 1111 2222 3
 */

public class WaveClientUDP {
	public static void main(String args[]) throws InterruptedException {
		if (args.length != 4) {
			System.out.println("Usage: java WaveClientUDP <serverHost> <serverAckPort> <clientAudioPort> <playTime>");
			return;
		}
		int bufSize = 1600; // 50ms per frame for 16kHz 16bit
		int srate = 16000; // 16kHz sampling rate
		int serverAckPort = Integer.parseInt(args[1]);
		int clientAudioPort = Integer.parseInt(args[2]);
		int numFrames = (int) (Integer.parseInt(args[3]) * srate / (bufSize / 2));
		try {
			InetAddress serverHost = InetAddress.getByName(args[0]);
			AudioFormat audioFormat = new AudioFormat(srate, 16, 1, true, false);
			DataLine.Info dataLineInfo = new DataLine.Info(SourceDataLine.class, audioFormat);
			if (!AudioSystem.isLineSupported(dataLineInfo)) {
				System.out.println("Line matching " + dataLineInfo + " is not supported.");
			} else {
				System.out.println("Line matching " + dataLineInfo + " is supported.");
				SourceDataLine line = (SourceDataLine) AudioSystem.getLine(dataLineInfo);
				line.open(audioFormat, bufSize);
				line.start();

				DatagramSocket clientAudioSocket = new DatagramSocket(clientAudioPort);
				byte[] soundData = new byte[bufSize]; // Audio buffer
				DatagramPacket dpacket = new DatagramPacket(soundData, bufSize);

				
				DatagramSocket clientAckSocket = new DatagramSocket();
				byte[] ack = new byte[] { 1 }; // 1 // Acknowledgment byte
				
				
				DatagramPacket ackPacket = new DatagramPacket(ack, 1, serverHost, serverAckPort);
				// Receive audio data from WaveServerUDP frame-by-frame. After playing back a
				// frame,
				// send an acknowledgment byte (ack) to serverAckPort of WaveServerUDP.
				int frame = 0;
				while (frame <= numFrames) {
					// Put your code here
					clientAudioSocket.receive(dpacket);
					System.out.println("Frame: " + frame++);
					line.write(soundData, 0, soundData.length);
//					Thread.sleep(100); //for delay simulation in part c
					clientAckSocket.send(ackPacket);
					System.out.println("Positive ack");

				}
				ack[0] = 0;
				clientAudioSocket.send(ackPacket);
				System.out.println("Negative ack sent");
				System.out.println("Finish playback");
				// Send a 0 to serverAckPort of WaveServerUDP so that it will stop sending audio
				// data
				
				clientAckSocket.send(ackPacket);

				line.drain();
				line.stop();
				line.close();
				System.out.println("Line close");
				clientAudioSocket.close();
				clientAckSocket.close();
				System.out.println("Socket close");
			}
		} catch (LineUnavailableException lue) {
			System.err.println("LineUnavailableException: " + lue.getMessage());
		} catch (IOException ioe) {
			System.err.println("IOException: " + ioe.getMessage());
		}
	}
}
