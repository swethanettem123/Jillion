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

import java.util.Date;

import org.jcvi.jillion.core.Range;
/**
 * {@code AbstractPhdReadTagVisitor} is a {@link PhdReadTagVisitor}
 * that collects the information about a single read tag
 * and then calls {@link #visitPhdReadTag(String, String, Range, Date, String, String)}
 * when the entire tag has been visited (this is known because {@link #visitEnd()}
 * as been called).
 * Subclasses are required to implement the abstract class 
 * {@link #visitPhdReadTag(String, String, Range, Date, String, String)}
 * to handle the completely visited read tag.
 * 
 * @author dkatzel
 *
 */
public abstract class AbstractPhdReadTagVisitor implements PhdReadTagVisitor{

	private String type;
	private String source;
	private Range ungappedRange;
	private Date date;
	private String comment;
	private final StringBuilder freeFormDataBuilder = new StringBuilder();
	
	@Override
	public final void visitType(String type) {
		this.type = type;
	}

	@Override
	public final void visitSource(String source) {
		this.source = source;
		
	}

	@Override
	public final void visitUngappedRange(Range ungappedRange) {
		this.ungappedRange = ungappedRange;
	}

	@Override
	public final void visitDate(Date date) {
		this.date = new Date(date.getTime());		
	}

	@Override
	public final void visitComment(String comment) {
		this.comment=comment;
		
	}

	@Override
	public final void visitFreeFormData(String data) {
		this.freeFormDataBuilder.append(data);
		
	}

	@Override
	public final void visitEnd() {
		final String freeFormData;
		if(freeFormDataBuilder.length() ==0){
			//no free form data
			freeFormData =null;
		}else{
			freeFormData= freeFormDataBuilder.toString().trim();
		}
		visitPhdReadTag(type, source,ungappedRange, date, comment, freeFormData);
		
	}
	/**
	 * 
	 * @param type
	 * @param source
	 * @param ungappedRange
	 * @param date
	 * @param comment
	 * @param freeFormData
	 */
	protected abstract void visitPhdReadTag(String type, String source,
			Range ungappedRange, Date date, String comment, String freeFormData);
	/**
	 * Ignored by default, please
	 * override to get halted notification.
	 * {@inheritDoc}
	 */
	@Override
	public void halted() {
		//no-op		
	}

}
