/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mucommander.commons.file.archiver;

import com.github.stephenc.javaisotools.iso9660.ISO9660Directory;
import com.github.stephenc.javaisotools.iso9660.ISO9660RootDirectory;
import com.google.common.io.Files;
import com.mucommander.commons.file.AbstractFile;
import com.mucommander.commons.file.FileFactory;
import java.io.File;
import java.lang.reflect.Method;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 *
 * @author jeppe
 */
public class ISOArchiverNGTest {
    
    public ISOArchiverNGTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @BeforeMethod
    public void setUpMethod() throws Exception {
    }

    @AfterMethod
    public void tearDownMethod() throws Exception {
    }

    /**
     * Test of createEntry method, of class ISOArchiver.
     */
    @Test
    public void testCreateEntry() throws Exception {
        File createTempFile = File.createTempFile("ISOArchiverNGTest", "iso");
        AbstractFile aFile = FileFactory.getFile(createTempFile.getPath());
        ISOArchiver instance = new ISOArchiver(aFile);
        
        File tempFile1 = File.createTempFile("ISOArchiverNGTest1", "txt");
        AbstractFile aTempFile1 = FileFactory.getFile(tempFile1.getPath());
        tempFile1.deleteOnExit();
        instance.createEntry(aTempFile1.getName(), aTempFile1);
        
        File tempDir1 = Files.createTempDir();
        AbstractFile aTempDir1 = FileFactory.getFile(tempDir1.getPath());
        tempDir1.deleteOnExit();
        instance.createEntry(aTempDir1.getName(), aTempDir1);
        
        File tempFile2 = File.createTempFile("ISOArchiverNGTest2", "txt", tempDir1);
        AbstractFile aTempFile2 = FileFactory.getFile(tempFile2.getPath());
        tempFile2.deleteOnExit();
        instance.createEntry(aTempDir1.getName() + "\\" +aTempFile2.getName(), aTempFile2);
        
        
        Method method = instance.getClass().getDeclaredMethod("getParentDirectory", String.class);
        method.setAccessible(true);
        
        Object invoke = method.invoke(instance, tempFile1.getName());
        //tempFile1 is placed in the root directory of the iso archive
        assert method.invoke(instance, tempFile1.getName()) instanceof ISO9660RootDirectory;
        
        invoke = method.invoke(instance, aTempDir1.getName() + "\\" + tempFile2.getName());
        //tempFile2 is placed in the aTempDir1 directory of the iso archive and not the root directory
        assert invoke instanceof ISO9660Directory && !(invoke instanceof ISO9660RootDirectory);
    }

    /**
     * Test of getProcessingFile method, of class ISOArchiver.
     */
//    @Test
    public void testGetProcessingFile() {
        //Tested in MyCreateISOTest.java
    }

    /**
     * Test of totalWrittenBytes method, of class ISOArchiver.
     */
//    @Test
    public void testTotalWrittenBytes() {
        //Tested in MyCreateISOTest.java
    }

    /**
     * Test of writtenBytesCurrentFile method, of class ISOArchiver.
     */
//    @Test
    public void testWrittenBytesCurrentFile() {
        //Tested in MyCreateISOTest.java
    }

    /**
     * Test of currentFileLength method, of class ISOArchiver.
     */
    //@Test
    public void testCurrentFileLength() {
        //Tested in MyCreateISOTest.java
    }

    /**
     * Test of postProcess method, of class ISOArchiver.
     */
//    @Test
    public void testPostProcess() throws Exception {
        //Tested in MyCreateISOTest.java
    }

    /**
     * Test of close method, of class ISOArchiver.
     */
//    @Test
    public void testClose() throws Exception {
    }
    
}
