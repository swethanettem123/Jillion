/*******************************************************************************
 * Copyright (c) 2009 - 2015 J. Craig Venter Institute.
 * 	This file is part of Jillion
 * 
 * 	Jillion is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 * 	
 * 	Jillion is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU General Public License
 * 	along with Jillion.  If not, see <http://www.gnu.org/licenses/>.
 * 	
 * 	
 * 	Contributors:
 *         Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.assembly.consed.phd;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.jcvi.jillion.core.io.IOUtil;
/**
 * {@code SinglePhdWriter} is a {@link PhdWriter}
 * that only allows one {@link Phd} to be written out.
 * Trying to write more than one {@link Phd} will throw an {@link IOException}. 
 * <p/>
 * This class is not thread-safe.
 * @author dkatzel
 *
 */
public final class SinglePhdWriter implements PhdWriter{

	private boolean alreadyWrittenRecord=false;
	
	private final PhdWriter delegateWriter;
	/**
	 * Create a new {@link SinglePhdWriter} instance
	 * that will write its contents to the given {@link OutputStream}.
	 * @param out the {@link OutputStream} to write to;
	 * can not be null.  
	 * @throws NullPointerException if outputFile is null.
	 */
	public SinglePhdWriter(OutputStream out) throws IOException{
		delegateWriter = new PhdBallWriter(out);
	}
	/**
	 * Create a new {@link SinglePhdWriter} instance
	 * that will write its contents to the given file.
	 * @param outputFile the {@link File} to write to;
	 * can not be null.  If this file already exists,
	 * then it will be overwritten.  If the File does
	 * not exist it will be created along with any non-existent
	 * parent directories.
	 * @throws IOException if there is a problem creating
	 * the file.  
	 * @throws NullPointerException if outputFile is null.
	 */
	public SinglePhdWriter(File outputFile) throws IOException{
		IOUtil.mkdirs(outputFile.getParentFile());
		delegateWriter = new PhdBallWriter(new FileOutputStream(outputFile));
	}

	
	@Override
	public void close() throws IOException {
		delegateWriter.close();
		
	}
	/**
	 * {@inheritDoc}
	 * @throws IOException if a {@link Phd}
	 * has already been written.
	 */
	@Override
	public void write(Phd phd) throws IOException {
		write(phd, null);
	}
	/**
	 * {@inheritDoc}
	 * @throws IOException if a {@link Phd}
	 * has already been written.
	 */
	@Override
	public void write(Phd phd, Integer version) throws IOException {
		if(alreadyWrittenRecord){
			throw new IOException("already wrote a phd to this file; only one allowed per file");
		}
		delegateWriter.write(phd, version);
		alreadyWrittenRecord = true;
		
	}

}
