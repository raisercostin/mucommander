/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mucommander.commons.file.impl.iso;

import com.github.stephenc.javaisotools.eltorito.impl.ElToritoConfig;
import com.github.stephenc.javaisotools.iso9660.ConfigException;
import com.github.stephenc.javaisotools.iso9660.ISO9660RootDirectory;
import com.github.stephenc.javaisotools.iso9660.impl.ISO9660Config;
import com.github.stephenc.javaisotools.iso9660.impl.ISOImageFileHandler;
import com.github.stephenc.javaisotools.joliet.impl.JolietConfig;
import com.github.stephenc.javaisotools.rockridge.impl.RockRidgeConfig;
import com.mucommander.commons.file.archiver.ISOArchiver;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;


@Test
public class MuCreateISOTest {
    private File tempFile = null;
    private File archiveFile = null;
    private MuCreateISO instance = null;
    
    public MuCreateISOTest(){
        
    }
    
    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @BeforeMethod
    public void setUpMethod() throws Exception {
        ISO9660RootDirectory root = new ISO9660RootDirectory();
        tempFile = createTempFile("MuCreateISOTest",10000);
        tempFile.deleteOnExit();
        root.addFile(tempFile);
        
        archiveFile = File.createTempFile("MuCreateISOTestArchive", "test");
        archiveFile.deleteOnExit();
        
        ISO9660Config iso9660Config = new ISO9660Config();
        try {
            iso9660Config.allowASCII(false);
            iso9660Config.setInterchangeLevel(1);
            iso9660Config.restrictDirDepthTo8(false);
            iso9660Config.setPublisher(System.getProperty("user.name"));
            iso9660Config.setVolumeID(tempFile.getName());
            iso9660Config.setDataPreparer(System.getProperty("user.name"));
            iso9660Config.forceDotDelimiter(true);
        } catch (ConfigException ex) {
            Logger.getLogger(ISOArchiver.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        RockRidgeConfig rrConfig = new RockRidgeConfig();
        rrConfig.setMkisofsCompatibility(false);
        
        JolietConfig jolietConfig = new JolietConfig();
        jolietConfig.forceDotDelimiter(true);
        
        ElToritoConfig elToritoConfig = null;
        
        instance = new MuCreateISO(new ISOImageFileHandler(archiveFile), root);
        instance.process(iso9660Config, rrConfig, jolietConfig, elToritoConfig);
    }

    @AfterMethod
    public void tearDownMethod() throws Exception {
    }
    
    
    public File createTempFile(String name, int fileSize){
        File file = null;
        try {
            file = File.createTempFile(name, "test");
            Random random = new Random(123456789);
            byte[] chars = new byte[fileSize];
            random.nextBytes(chars);
            
            PrintWriter pw = new PrintWriter(file);
            
            for(int i = 0; i < chars.length; i++){
                pw.write(chars[i] + 128);
            }
            pw.flush();
            pw.close();
            
        } catch (IOException ex) {
            Logger.getLogger(MuCreateISOTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        return file;
    }
    
    

    /**
     * Test of process method, of class MuCreateISO.
     */
    @Test
    public void testProcess() throws Exception {
        System.out.println("process");
        //Archive size if archived correctly
        assert archiveFile.length() == 63488;
    }

    /**
     * Test of getProcessingFile method, of class MuCreateISO.
     */
    @Test
    public void testGetProcessingFile() throws Exception {
        System.out.println("getProcessingFile");
        
        assert instance.getProcessingFile().equals(tempFile.getName());
    }

    /**
     * Test of totalWrittenBytes method, of class MuCreateISO.
     */
    @Test
    public void testTotalWrittenBytes() throws Exception {
        System.out.println("totalWrittenBytes");
        
        ISO9660RootDirectory root = new ISO9660RootDirectory();
        File tempFile = createTempFile("totalWrittenBytes",10000);
        root.addFile(tempFile);
        tempFile.deleteOnExit();
        File tempFile2 = createTempFile("totalWrittenBytes2",20000);
        root.addFile(tempFile2);
        tempFile2.deleteOnExit();
        File tempFile3 = createTempFile("totalWrittenBytes3",40000);
        root.addFile(tempFile3);
        tempFile3.deleteOnExit();
        
        File archiveFile = File.createTempFile("totalWrittenBytesArchive", "test");
        archiveFile.deleteOnExit();
        
        ISO9660Config iso9660Config = new ISO9660Config();
        try {
            iso9660Config.allowASCII(false);
            iso9660Config.setInterchangeLevel(1);
            iso9660Config.restrictDirDepthTo8(false);
            iso9660Config.setPublisher(System.getProperty("user.name"));
            iso9660Config.setVolumeID(tempFile.getName());
            iso9660Config.setDataPreparer(System.getProperty("user.name"));
            iso9660Config.forceDotDelimiter(true);
        } catch (ConfigException ex) {
            Logger.getLogger(ISOArchiver.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        RockRidgeConfig rrConfig = new RockRidgeConfig();
        rrConfig.setMkisofsCompatibility(false);
        
        JolietConfig jolietConfig = new JolietConfig();
        jolietConfig.forceDotDelimiter(true);
        
        ElToritoConfig elToritoConfig = null;
        
        MuCreateISO instance = new MuCreateISO(new ISOImageFileHandler(archiveFile), root);
        instance.process(iso9660Config, rrConfig, jolietConfig, elToritoConfig);
        
        assert instance.totalWrittenBytes() == 70000;
    }

    @Test
    public void testWrittenBytesCurrentFile() throws Exception {
        System.out.println("writtenBytesCurrentFile");
        
        assert instance.writtenBytesCurrentFile() == 10000;
    }

    /**
     * Test of currentFileLength method, of class MuCreateISO.
     */
    @Test
    public void testCurrentFileLength() {
        System.out.println("currentFileLength");
        
        assert instance.currentFileLength() == tempFile.length();
    }
    
}
