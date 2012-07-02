package org.test.streaming.encoding;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.common.collect.Lists;
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

	
	
	private final String originDir;
	private String tempDir;
	private final String targetDir;

	private String targetExtension = "mp4";
	private String tempExtension = "temp";

	private String buffer;

	private int fixedVideoBitRate = 875;
	private String audioCodec = "libfaac"; 
	private String fileName;
	
	int videoBitRate; 
	int videoHeight; 
	int audioBitRate; 

	public H264Encoder(String file, String originDir, String targetDir){
		this.fileName = file;
		this.originDir = originDir;
		this.targetDir = targetDir;
		this.tempDir = originDir + "_TEMP_ENCODING_"+fileName+File.separatorChar;
		this.buffer = tempDir+"_SAFE_TO_DELETE_ANYTIME.garbage";

		File origin = new File(originDir+fileName);
		if(!origin.exists()) {
			throw new IllegalStateException("origin file does not exist: "+originDir+fileName);
		}
		File tmp = new File(tempDir);
		this.removeDirectory(tmp);
		tmp.mkdirs();
		new File(targetDir).mkdirs();
	}

	@Override
	public File encode() {

		String inFile = originDir+fileName;
		String[] splittedFileName = fileName.split("\\.");
		String extension  = splittedFileName[splittedFileName.length-1];
		String tempFile = tempDir+fileName.replace(extension, tempExtension)+"."+targetExtension;
		String outFile = targetDir+fileName.replace(extension, targetExtension);


		scanFile(inFile);

		log.info("inFile: "+inFile);
		log.info("buffer: "+buffer);
		log.info("outFile: "+outFile);
		log.info("videoBitRate: "+videoBitRate);
		log.info("videoHeight: "+videoHeight);
		log.info("audioCodec: "+audioCodec);
		log.info("audioBitRate: "+audioBitRate);

		String firstPass  = "ffmpeg -i "+inFile+" -vcodec libx264 -vprofile high -preset slow -b:v "+videoBitRate+"k -maxrate "+videoBitRate+"k -bufsize "+videoBitRate*2+"k -vf scale=-1:"+videoHeight+" -threads 0 -pass 1 -an -f mp4 "+buffer;
		String secondPass = "ffmpeg -i "+inFile+" -vcodec libx264 -vprofile high -preset slow -b:v "+videoBitRate+"k -maxrate "+videoBitRate+"k -bufsize "+videoBitRate*2+"k -vf scale=-1:"+videoHeight+" -threads 0 -pass 2 -acodec "+audioCodec+" -b:a "+audioBitRate+"k -f mp4 "+tempFile;
		String thirdPass  = "qt-faststart "+tempFile+" "+outFile;

		log.info(firstPass);
		log.info(secondPass);
		log.info(thirdPass);
		File encodedFile = new File(outFile);
		try {
			new File(tempDir).mkdirs();
			launchEncoding(firstPass);
			log.debug("About to delete buffer: "+buffer);
			new File(buffer).delete();
			launchEncoding(secondPass);
			launchEncoding(thirdPass);
			
			if(!new File(tempFile).renameTo(encodedFile)) {
				String message = "unable to move encoded file " + tempFile +" to target file "+outFile;
				log.error(message);
				throw new IllegalStateException(message);
			}
//			encodedFile.createNewFile();
			this.removeDirectory(new File(tempDir));
		} catch(IOException e){
			log.error(e);
		}
		return encodedFile;
	}

	private void scanFile(String inFile) {

		// Create a Xuggler container object
		IContainer container = IContainer.make();
		// Open up the container
		if (container.open(inFile, IContainer.Type.READ, null) < 0)
			throw new IllegalArgumentException("could not open file: " + inFile);

		// query how many streams the call to open found
		int numStreams = container.getNumStreams();
		videoBitRate = fixedVideoBitRate(container.getBitRate()/1000);

		// and iterate through the streams to print their meta data
		for(int i = 0; i < numStreams; i++)
		{
			// Find the stream object
			IStream stream = container.getStream(i);
			// Get the pre-configured decoder that can decode this stream;
			IStreamCoder coder = stream.getStreamCoder();
			if (coder.getCodecType() == ICodec.Type.CODEC_TYPE_AUDIO)
			{
				//"libf"+coder.getCodec().getName(); 
				audioBitRate = coder.getBitRate()/1024;
			} else if (coder.getCodecType() == ICodec.Type.CODEC_TYPE_VIDEO)
			{
				videoHeight = coder.getHeight();
			}
		}
		container.close();
	}

	private int fixedVideoBitRate(int i) {
		return i >= fixedVideoBitRate ? fixedVideoBitRate : i;
	}

	public void launchEncoding(String proc) throws IOException{

		log.debug("About to launch " + proc);
		Process process = Runtime.getRuntime().exec(proc, new String[0], new File(tempDir));
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

	public static void main(String[] args) {

		List<String> pelis = Lists.<String>newArrayList(
				"BEBES_BAILANDO.mp4.b",
				"Cutest_Cat_Ever_.mp4.b",
				"Cutest_Cat_Ever_Part_2.mp4.b"
				);

		String origin = "/home/matias/Escritorio/Luther_Season_2_Complete_720p/";
		String target = origin + "TARGET"+ File.separatorChar;
		for(String peli : pelis){

			H264Encoder encoder = new H264Encoder(peli, origin, target);
			encoder.encode();

		}
	}


	private boolean removeDirectory(File directory) {

		if (directory == null)
			return false;
		if (!directory.exists())
			return true;
		if (!directory.isDirectory())
			return false;

		String[] list = directory.list();

		// Some JVMs return null for File.list() when the
		// directory is empty.
		if (list != null) {
			for (int i = 0; i < list.length; i++) {
				File entry = new File(directory, list[i]);

				if (entry.isDirectory())
				{
					if (!removeDirectory(entry))
						return false;
				}
				else
				{
					if (!entry.delete())
						return false;
				}
			}
		}

		return directory.delete();
	}
}
