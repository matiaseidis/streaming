package org.test.streaming.xuggler;

/*******************************************************************************
 * Copyright (c) 2008, 2010 Xuggle Inc.  All rights reserved.
 *  
 * This file is part of Xuggle-Xuggler-Main.
 *
 * Xuggle-Xuggler-Main is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Xuggle-Xuggler-Main is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Xuggle-Xuggler-Main.  If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/


import org.test.streaming.Conf;

import com.xuggle.mediatool.IMediaReader;
import com.xuggle.mediatool.IMediaViewer.Mode;
import com.xuggle.mediatool.ToolFactory;

/**
 * Takes a media container, finds the first video stream,
 * decodes that stream, and then displays the video frames,
 * at the frame-rate specified by the container, on a 
 * window.
 * @author aclarke
 *
 */
public class Demo {
	
	public static void main(String[] args) {
		// create a media reader
		IMediaReader reader = ToolFactory.makeReader(Conf.getSharedDir()/**/);
		
		// add a viewer to the reader, to see the decoded media
		reader.addListener(ToolFactory.makeWriter("output.mp4", reader));
		
		
		// read and decode packets from the source file and
		// and dispatch decoded audio and video to the writer
		while (reader.readPacket() == null)
			;
		
		System.out.println("ok");
	}
}
