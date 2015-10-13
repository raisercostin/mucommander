/*
 * This file is part of muCommander, http://www.mucommander.com
 * Copyright (C) 2002-2012 Maxence Bernard
 *
 * muCommander is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * muCommander is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.mucommander.job.progress;

/**
 *
 * @author jeppe
 */
public interface IJobProgress {

    /**
     * Calculates the job progress status. This method calculates variables used
     * to show job progress information. It can update information only on a
     * processed file (when <code>labelOnly</code> is <code>true</code>). If
     * <code>labelOnly</code> is false it will try to update full information on
     * a job progress (e.g. percent completed, bytes per second, etc.).
     *
     * @param fullUpdate
     * 			 <code>true</code> update all information about processed file.<br/>
     * 			 <code>false</code> update only label of a processed file.<br/>
     * 		     Note that if a job has just finished this flag is ignored
     * 			 and all variables are recalulated.
     * @return <code>true</code> if full job progress has been updated,
     *         <code>false</code> if only label has been updated.
     */
    boolean calcJobProgress(boolean fullUpdate);

    long getBytesTotal();

    long getCurrentBps();

    long getEffectiveJobTime();

    int getFilePercentInt();

    String getFileProgressText();

    long getJobPauseStartDate();

    String getJobStatusString();

    long getLastTime();

    long getTotalBps();

    int getTotalPercentInt();

    String getTotalProgressText();

    boolean isTransferFileJob();
    
}
