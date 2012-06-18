package org.test.streaming.encoding;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.test.streaming.BackgroundCachoStreamer;

import com.xuggle.xuggler.Configuration;
import com.xuggle.xuggler.ICodec;
import com.xuggle.xuggler.IContainer;
import com.xuggle.xuggler.IStream;
import com.xuggle.xuggler.IStreamCoder;
/**
 * 
 * @author meidis
 *
 */
public class H264Encoder implements Encoder{

	protected static final Log log = LogFactory
			.getLog(H264Encoder.class);

	private static final String ENCODING_FOLDER = "/home/matias/Escritorio/L/";
	private final String FILE;
	private static final String BUFFER = "/dev/null";
	private static final String TEMP_SUFFIX = "_TEMP_";
	private static final String ORIGINAL_EXTENSION = "mkv";
	private static final String TARGET_EXTENSION = "mp4";

	public H264Encoder(String file){
		this.FILE = file;
	}
	
	/*
	 * TODO delete me 
	 */
	public H264Encoder(){
		this.FILE = "/home/matias/Descargas/decargasQueQueremos/vuze/Luther_Season_2_Complete_720p/Luther.S02E01.720p.HDTV.x264.mkv";
	}
	
	public static void main(String[] args)
	{
		new H264Encoder().encode();
	}

	@Override
	public void encode() {

		// If the user passes -Dxuggle.options, then we print
		// out all possible options as well.
		String optionString = System.getProperty("xuggle.options");
		if (optionString != null)
		{
			Configuration.printHelp(System.out);
		}

		// Create a Xuggler container object
		IContainer container = IContainer.make();

		String inFile = FILE;
		String buffer = BUFFER;
		String tempFile = ENCODING_FOLDER+FILE.replace(ORIGINAL_EXTENSION, TEMP_SUFFIX)+TARGET_EXTENSION;
		String outFile = tempFile.replace(TEMP_SUFFIX, StringUtils.EMPTY);
		int videoBitRate = 0; 
		int videoHeight = 0; 
		String audioCodec = null; 
		int audioBitRate = 0; 

		// Open up the container
		if (container.open(inFile, IContainer.Type.READ, null) < 0)
			throw new IllegalArgumentException("could not open file: " + inFile);

		// query how many streams the call to open found
		int numStreams = container.getNumStreams();
		videoBitRate = container.getBitRate()/1000;

		// and iterate through the streams to print their meta data
		for(int i = 0; i < numStreams; i++)
		{
			// Find the stream object
			IStream stream = container.getStream(i);
			// Get the pre-configured decoder that can decode this stream;
			IStreamCoder coder = stream.getStreamCoder();
			if (coder.getCodecType() == ICodec.Type.CODEC_TYPE_AUDIO)
			{
				audioCodec = "libf"+coder.getCodec().getName(); 
				audioBitRate = coder.getBitRate()/1024;
			} else if (coder.getCodecType() == ICodec.Type.CODEC_TYPE_VIDEO)
			{
				videoHeight = coder.getHeight();
			}
		}

		log.debug(inFile);
		log.debug(buffer);
		log.debug(outFile);
		log.debug(videoBitRate);
		log.debug(videoHeight);
		log.debug(audioCodec);
		log.debug(audioBitRate);

		String firstPass  = "ffmpeg -i "+inFile+" -vcodec libx264 -vprofile high -preset slow -b:v "+videoBitRate+"k -maxrate "+videoBitRate+"k -bufsize "+videoBitRate*2+"k -vf scale=-1:"+videoHeight+" -threads 0 -pass 1 -an -f mp4 "+buffer;
		String secondPass = "ffmpeg -i "+inFile+" -vcodec libx264 -vprofile high -preset slow -b:v "+videoBitRate+"k -maxrate "+videoBitRate+"k -bufsize "+videoBitRate*2+"k -vf scale=-1:"+videoHeight+" -threads 0 -pass 2 -acodec "+audioCodec+" -b:a "+audioBitRate+"k -f mp4 "+tempFile;
		String thirdPass  = "qt-faststart "+tempFile+" "+outFile;

		log.debug(firstPass);
		log.debug(secondPass);
		log.debug(thirdPass);

		List<String> procs = new ArrayList<String>();
		procs.add(firstPass);
		procs.add(secondPass);
		procs.add(thirdPass);
		try {
			launchEncoding(procs);
		} catch(IOException e){
			e.printStackTrace();
		}

	}

	public void launchEncoding(List<String> procs) throws IOException{
		for(String proc : procs){
			log.debug("About to launch " + proc);
			Process process = Runtime.getRuntime().exec(proc, new String[0], new File(ENCODING_FOLDER));
			InputHandler errorHandler = new
					InputHandler(process.getErrorStream(), "Error Stream");
			errorHandler.start();
			InputHandler inputHandler = new
					InputHandler(process.getInputStream(), "Output Stream");
			inputHandler.start();
			try {
				process.waitFor();
			} catch (InterruptedException e) {
				throw new IOException("process interrupted");
			}
			log.debug("exit code: " + process.exitValue());
		}
	}
}

/*
 * A rule of thumb to calculate file size from bitrate is: filesize (in MB) = (bitrate in Mbit/s * 8) * (video length in seconds)
 */
/*
 * -i [input file]  - this specifies the name of input file
 * 
-vcodec libx264 – tells FFmpeg to encode video to H.264 using libx264 library

-vprofile high – sets H.264 profile to “High” as per Step 2. Other valid options are baseline, main

-preset slow – sets encoding preset for x264 – slower presets give more quality at same bitrate, but need more time to encode. “Slow” is a good balance between encoding time and quality.

Other valid options are: ultrafast, superfast, veryfast, faster, fast, medium, slow, slower, veryslow, placebo (never use this one)

-b:v - sets video bitrate in bits/s

-maxrate and -bufsize – forces libx264 to build video in a way, that it could be streamed over 500kbit/s line considering device buffer of 1000kbits. Very useful for web – setting this to bitrate and 2x bitrate gives good results.

-vf scale – applies “scale” filter, which resizes video to desired resolution. “720:480″ would resize video to 720×480, “-1″ means “resize so the aspect ratio is same.”

Usually you set only height of the video, so for 380p you set “scale=-1:380″, for 720p “scale=-1:720″ etc.

-threads 0 – tells libx264 to choose optimal number of threads to encode, which will make sure all your processor cores in the computer are used

-acodec libvo_aacenc – tells FFmpeg to encode video to AAC using libvo_aacenc library

-b:a - sets audio bitrate in bits/s

-pass [1|2] – tells FFmpeg to process video in multiple passes and sets the current pass

-an – disables audio, audio processing has no effect on first pass so it’s best to disable it to not waste CPU
 */
