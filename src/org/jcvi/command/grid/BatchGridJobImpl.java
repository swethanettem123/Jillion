/*******************************************************************************
 * Copyright 2010 J. Craig Venter Institute
 * 
 * 	This file is part of JCVI Java Common
 * 
 *     JCVI Java Common is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     JCVI Java Common is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with JCVI Java Common.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

package org.jcvi.command.grid;


import org.ggf.drmaa.*;
import org.jcvi.command.Command;

import java.io.File;
import java.util.*;

/**
 * A <code>GridJobImpl</code> is an abstractions for a DRMAA-supported distributed execution
 * process.
 *
 * @author jsitz@jcvi.org
 * @author dkatzel
 */
public class BatchGridJobImpl extends GridJobImpl implements BatchGridJob {

    protected BatchGridJobImpl(Session gridSession,
                               Command command,
                               Map<NativeSpec, String> nativeSpecs,
                               Set<String> otherNativeSpecs,
                               Properties env,
                               Set<String> emailRecipients,
                               String jobName,
                               File workingDirectory,
                               File inputFile,
                               File outputFile,
                               File errorFile,
                               long timeout) {
        super(gridSession,
              command,
              nativeSpecs,
              otherNativeSpecs,
              env,
              emailRecipients,
              jobName,
              workingDirectory,
              inputFile,
              outputFile,
              errorFile,
              timeout);
    }

    /**
     * Creates a new <code>GridJobImpl</code>.
     */
    public BatchGridJobImpl(BatchGridJobImpl copy) throws DrmaaException
    {
        super(copy);
    }

    /*
        BatchGridJob interface methods
     */
    @Override
    public String getJobID()
    {
        String jobID = null;
        if ( jobIDList != null ) {
            jobID = this.jobIDList.get(0);
        }
        return jobID;
    }

    @Override
    public JobInfo getJobInfo()
    {
        JobInfo jobInfo = null;
        if ( jobInfoMap != null ) {
            jobInfo = jobInfoMap.get(jobIDList.get(0));
        }
        return jobInfo;
    }

    /*
        GridJobImpl abstract method implementations
     */

    @Override
    // by default, return the job's exit status
    protected int postExecution() throws DrmaaException
    {
        return getJobInfo().getExitStatus();
    }

    @Override
    public void runGridCommand() throws DrmaaException
    {
        try
        {
            this.buildJobTemplate();
            this.jobIDList = Collections.singletonList(this.gridSession.runJob(this.jobTemplate));
            this.jobInfoMap = new Hashtable<String,JobInfo>();
        }
        finally
        {
            this.releaseJobTemplate();
        }
    }

    @Override
    public void waitForCompletion() throws DrmaaException
    {
        JobInfo jobInfo = this.gridSession.wait(getJobID(), this.timeout);
        System.out.println("completed has exited= "+ jobInfo.hasExited());
        jobInfoMap.put(getJobID(),jobInfo);
    }

    @Override
    public void terminate() throws DrmaaException{
        this.gridSession.control(getJobID(), Session.TERMINATE);
        jobInfoMap.put(getJobID(),new StatusJobInfo(getJobID(), Session.TERMINATE));
    }

    @Override
    public GridException getGridTimeoutException() {
        return new GridException("The grid job (" + this.getJobID()
            + ") failed to complete in the scheduled time.");
    }

    public static class Builder extends GridJobImpl.Builder implements org.jcvi.Builder<BatchGridJob>{
        public Builder(Session gridSession, Command command, String projectCode) {
            super(gridSession, command, projectCode);
        }

        @Override
        public BatchGridJob build() {
            return new BatchGridJobImpl(gridSession,
                                        command,
                                        nativeSpecs,
                                        otherNativeSpecs,
                                        env,
                                        emailRecipients,
                                        jobName,
                                        workingDirectory,
                                        inputFile,
                                        outputFile,
                                        errorFile,
                                        timeout);
        }
    }
}