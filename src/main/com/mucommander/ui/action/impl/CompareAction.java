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

package com.mucommander.ui.action.impl;

import io.atlassian.fugue.Either;
import io.atlassian.fugue.Eithers;
import java.io.IOException;
import java.util.Map;
import javax.swing.KeyStroke;
import org.apache.lucene.util.StringHelper;
import scala.util.Left;
import net.sf.jftp.system.logging.Log;
import com.google.common.base.Optional;
import com.mucommander.commons.file.AbstractFile;
import com.mucommander.commons.file.util.FileSet;
import com.mucommander.process.ProcessListener;
import com.mucommander.shell.Shell;
import com.mucommander.ui.action.AbstractActionDescriptor;
import com.mucommander.ui.action.ActionCategory;
import com.mucommander.ui.action.ActionDescriptor;
import com.mucommander.ui.action.ActionFactory;
import com.mucommander.ui.action.ActionManager;
import com.mucommander.ui.action.MuAction;
import com.mucommander.ui.main.MainFrame;

/**
 * This action compares two files: - either two selected ones in current panel - either the one on the right and/or the
 * one on the left
 *
 * @author raisercostin
 */
public enum CompareAction {
	$;
	private final static org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(CompareAction.class);
	
	static{
		ActionManager.registerAction(new Descriptor(), new Factory());
	}

	public final String ACTION_ID = "CompareByContent";

	public static class Action extends SelectedFilesAction {
		private static final long serialVersionUID = -7259700737932009568L;

		public Action(MainFrame mainFrame, Map<String, Object> properties) {
			super(mainFrame, properties);
		}
		//
		// @Override
		// public void performAction(FileSet files) {
		// new CopyDialog(mainFrame, files).showDialog();
		// }

		@Override
		public ActionDescriptor getDescriptor() {
			return new Descriptor();
		}
		private static class TwoFilesOperation{
			public final String reason;
			public final AbstractFile file1;
			public final AbstractFile file2;
			public TwoFilesOperation(String reason, AbstractFile file1, AbstractFile file2) {
				super();
				this.reason = reason;
				this.file1 = file1;
				this.file2 = file2;
			}
		}

		@Override
		public void performAction(FileSet filesIgnored) {
			MainFrame mainFrame = this.mainFrame;
			Either<String,TwoFilesOperation> result = extractTwoFilesOperation(mainFrame);
			result.right().foreach(r ->{
				System.out.println("Compare ["+r.reason+"] files "+r.file1+"    "+r.file2);
				try{
					Shell.execute("/opt/homebrew-cask/Caskroom/p4merge/2014.3-1007540/p4merge.app/Contents/MacOS/p4merge "+r.file1+" "+r.file2, mainFrame.getActivePanel().getCurrentFolder(),new ProcessListener() {
						@Override
						public void processOutput(byte[] buffer, int offset, int length) {
						}
						@Override
						public void processOutput(String output) {
							System.out.println(output);
						}
						@Override
						public void processDied(int returnValue) {
							System.out.println("died"+returnValue);
						}
					});
				}catch(IOException e){
					throw new RuntimeException(e);
				}
			});
			result.left().forEach(l->
				System.out.println(l)
			);
		}
		private Either<String,TwoFilesOperation> extractTwoFilesOperation(MainFrame mainFrame) {
			TwoFilesOperation result = null;
			FileSet files = mainFrame.getActiveTable().getSelectedFiles();
			if (files.size() == 2) {
				result = new TwoFilesOperation("two marked files",files.get(0),files.get(1));
			} else if (files.size() > 2) {
				result = new TwoFilesOperation("first two marked files",files.get(0),files.get(1));
			} else {
				Optional<AbstractFile> markedFile1 = oneOrNull(mainFrame.getActiveTable().getMarkedFiles());
				Optional<AbstractFile> markedFile2 = oneOrNull(mainFrame.getInactiveTable().getMarkedFiles());
				if (markedFile1.isPresent() && markedFile2.isPresent()) {
					result = new TwoFilesOperation("current/marked files from both panels",markedFile1.get(),markedFile2.get());
				}else{
					AbstractFile file1 = mainFrame.getActiveTable().getSelectedFile(true);
					AbstractFile file2 = mainFrame.getInactiveTable().getSelectedFile(true);
					AbstractFile f1 = markedFile1.or(file1);
					AbstractFile f2 = markedFile2.or(file2);
					if (markedFile1.isPresent() && !markedFile1.equals(Optional.fromNullable(file1))) {
						result = new TwoFilesOperation("marked file with current one",markedFile1.get(),file1);
					} else if (f1 != null && f2 != null) {
						result = new TwoFilesOperation("current/marked files from both panels",f1,f2);
					} else {
						FileSet files2 = mainFrame.getInactiveTable().getSelectedFiles();
						logger.warn(
								"Cannot compare selected files {} with {}, neither marked files {} with {}, neither current {} with {}. The command should be disabled?",
								new Object[] { files, files2, markedFile1, markedFile2, file1, file2 });
						return Either.left(String.format("Cannot compare selected files {} with {}, neither marked files {} with {}, neither current {} with {}. The command should be disabled?",
								files, files2, markedFile1, markedFile2, file1, file2 ));
					}
				}
			}
			return Either.right(result);
		}
		private Optional<AbstractFile> oneOrNull(FileSet files) {
			if(files.size()==1)
				return Optional.of(files.get(0));
			return Optional.absent();
		}
	}

	private static class Factory implements ActionFactory {

		public MuAction createAction(MainFrame mainFrame, Map<String, Object> properties) {
			return new Action(mainFrame, properties);
		}
	}

	private static class Descriptor extends AbstractActionDescriptor {
		public String getId() {
			return $.ACTION_ID;
		}

		public ActionCategory getCategory() {
			return ActionCategory.FILES;
		}

		public KeyStroke getDefaultAltKeyStroke() {
			return null;
		}

		public KeyStroke getDefaultKeyStroke() {
			return null;
		}
	}
}