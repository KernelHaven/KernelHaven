package net.ssehub.kernel_haven.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

/**
 * Tests the {@link ZipArchive} class. If the test archive "archive.zip" is destroyed, replace it with
 * "archive_original.zip".
 *
 * @author Adam
 */
public class ZipArchiveTest {


    private static final File TESTDATA = new File("testdata/zipArchive");
    
    /**
     * Tests whether a new zip archive is correctly created.
     * 
     * @throws IOException unwanted.
     */
    @Test
    public void testCreation() throws IOException {
        File zipFile = new File(TESTDATA, "testCreation.zip");
        zipFile.deleteOnExit();
        if (zipFile.exists()) {
            zipFile.delete();
        }
        Assert.assertFalse(zipFile.exists());
        
        ZipArchive archive = new ZipArchive(zipFile);
        Assert.assertTrue(zipFile.exists());
        archive.close();
        Assert.assertTrue(zipFile.exists());
    }
    
    /**
     * Tests whether a zip file with an invalid path is not created.
     * 
     * @throws IOException wanted.
     */
    @Test(expected = IOException.class)
    public void testInvalidCreation() throws IOException {
        File zipFile = TESTDATA; // this is a directory
        Assert.assertTrue(zipFile.isDirectory());
        
        new ZipArchive(zipFile).close();
    }
    
    /**
     * Tests whether the containsFile() method works on an existing archive.
     * 
     * @throws IOException unwanted.
     */
    @Test
    public void testContainsFile() throws IOException {
        File zipFile = new File(TESTDATA, "archive.zip");
        ZipArchive archive = new ZipArchive(zipFile);
        
        Assert.assertTrue(archive.containsFile(new File("test.txt")));
        Assert.assertTrue(archive.containsFile(new File("dir/test.txt")));
        
        Assert.assertFalse(archive.containsFile(new File("doesntExist.txt")));
        Assert.assertFalse(archive.containsFile(new File("doesntExist/doesnExist.txt")));
        Assert.assertFalse(archive.containsFile(new File("dir/doesntExist.txt")));
        
        archive.close();
    }
    
    /**
     * Tests whether containsFile() correctly works on directories in the archive.
     * 
     * @throws IOException unwanted.
     */
    @Test
    public void testContainsFileOnDirectory() throws IOException {
        File zipFile = new File(TESTDATA, "archive.zip");
        ZipArchive archive = new ZipArchive(zipFile);
        
        Assert.assertFalse(archive.containsFile(new File("dir")));
        Assert.assertFalse(archive.containsFile(new File("dir/")));
        
        archive.close();
    }
    
    /**
     * Tests whether the readFile() method works.
     * 
     * @throws IOException unwanted.
     */
    @Test
    public void testReadFile() throws IOException {
        File zipFile = new File(TESTDATA, "archive.zip");
        ZipArchive archive = new ZipArchive(zipFile);

        String read = archive.readFile(new File("test.txt"));
        
        Assert.assertEquals("Hello World!\n", read);
        
        archive.close();
    }
    
    /**
     * Tests whether the readFile() method works correctly on not existing files.
     * 
     * @throws FileNotFoundException wanted.
     * @throws IOException unwanted.
     */
    @Test(expected = FileNotFoundException.class)
    public void testReadNotExistingFile() throws IOException, FileNotFoundException {
        File zipFile = new File(TESTDATA, "archive.zip");
        ZipArchive archive = new ZipArchive(zipFile);
        
        try {
            archive.readFile(new File("doesntExist.txt"));
        } finally {
            archive.close();
        }
    }
    
    /**
     * Tests whether the getSize() method works correctly.
     * 
     * @throws IOException unwanted.
     */
    @Test
    public void testGetSize() throws IOException {
        File zipFile = new File(TESTDATA, "archive.zip");
        ZipArchive archive = new ZipArchive(zipFile);

        Assert.assertEquals(13, archive.getSize(new File("test.txt")));
        
        archive.close();
    }
    
    /**
     * Tests whether the getSize() method works correctly on not existinf files.
     * 
     * @throws FileNotFoundException wanted.
     * @throws IOException unwanted.
     */
    @Test(expected = FileNotFoundException.class)
    public void testGetSizeNotExisting() throws FileNotFoundException, IOException {
        File zipFile = new File(TESTDATA, "archive.zip");
        ZipArchive archive = new ZipArchive(zipFile);
        
        try {
            archive.getSize(new File("doesntExist.txt"));
        } finally {
            archive.close();
        }
    }
    
    /**
     * Tests the write and delete methods on an existing archive.
     * 
     * @throws IOException unwanted.
     */
    @Test
    public void testWriteAndDeleteFile() throws IOException {
        File zipFile = new File(TESTDATA, "archive.zip");
        ZipArchive archive = new ZipArchive(zipFile);
        
        File toWrite = new File("testWrite.txt");
        
        Assert.assertFalse(archive.containsFile(toWrite));
        
        String content = "This is a test text\n";
        archive.writeFile(toWrite, content);
        
        Assert.assertTrue(archive.containsFile(toWrite));
        Assert.assertEquals(archive.readFile(toWrite), content);
        
        archive.deleteFile(toWrite);

        Assert.assertFalse(archive.containsFile(toWrite));
        
        archive.close();
    }
    
    /**
     * Tests the delete method on a not existing file.
     * 
     * @throws FileNotFoundException wanted.
     * @throws IOException unwanted.
     */
    @Test(expected = FileNotFoundException.class)
    public void testDeleteNonExisting() throws FileNotFoundException, IOException {
        File zipFile = new File(TESTDATA, "archive.zip");
        ZipArchive archive = new ZipArchive(zipFile);
        
        Assert.assertFalse(archive.containsFile(new File("doesntExist.txt")));
        
        try {
            archive.deleteFile(new File("doesntExist.txt"));
        } finally {
            archive.close();
        }
    }
    
    /**
     * Tests whether overwriting files works correctly.
     * 
     * @throws IOException unwanted.
     */
    @Test
    public void testOverwriteFile() throws IOException {
        File zipFile = new File(TESTDATA, "archive.zip");
        ZipArchive archive = new ZipArchive(zipFile);
        
        File toWrite = new File("testOverwrite.txt");
        
        Assert.assertFalse(archive.containsFile(toWrite));
        
        String content1 = "This is a test text\n";
        archive.writeFile(toWrite, content1);
        
        Assert.assertTrue(archive.containsFile(toWrite));
        Assert.assertEquals(archive.readFile(toWrite), content1);
        
        String content2 = "This is another test text\n";
        archive.writeFile(toWrite, content2);
        
        Assert.assertTrue(archive.containsFile(toWrite));
        Assert.assertEquals(archive.readFile(toWrite), content2);
        
        archive.deleteFile(toWrite);

        Assert.assertFalse(archive.containsFile(toWrite));
        
        archive.close();
    }
    
    /**
     * Tests whether the copyFileToArchive() method works correctly.
     * 
     * @throws IOException unwanted.
     */
    @Test
    public void testCopyFileToArchive() throws IOException {
        File zipFile = new File(TESTDATA, "archive.zip");
        ZipArchive archive = new ZipArchive(zipFile);
        
        File insideFile = new File("testCopy.txt");
        File outsideFile = new File(TESTDATA, "testfile.txt");
        
        Assert.assertTrue(outsideFile.exists());
        
        Assert.assertFalse(archive.containsFile(insideFile));
        
        archive.copyFileToArchive(insideFile, outsideFile);
        
        Assert.assertTrue(outsideFile.exists());
        Assert.assertTrue(archive.containsFile(insideFile));
        
        FileInputStream in = new FileInputStream(outsideFile);
        Assert.assertEquals(Util.readStream(in), archive.readFile(insideFile));
        in.close();
        
        archive.deleteFile(insideFile);
        Assert.assertFalse(archive.containsFile(insideFile));
        
        archive.close();
    }
    
    /**
     * Tests whether the readFile() method works correctly.
     * 
     * @throws IOException unwanted.
     */
    @Test
    public void testExtract() throws IOException {
        File zipFile = new File(TESTDATA, "archive.zip");
        ZipArchive archive = new ZipArchive(zipFile);
        
        File insideFile = new File("test.txt");
        File outsideFile = new File(TESTDATA, "testExtract.txt");
        
        Assert.assertFalse(outsideFile.exists());
        Assert.assertTrue(archive.containsFile(insideFile));
        
        archive.extract(insideFile, outsideFile);

        Assert.assertTrue(outsideFile.exists());
        Assert.assertTrue(archive.containsFile(insideFile));

        FileInputStream in = new FileInputStream(outsideFile);
        Assert.assertEquals(archive.readFile(insideFile), Util.readStream(in));
        in.close();
        
        outsideFile.delete();
        Assert.assertFalse(outsideFile.exists());
        
        archive.close();
    }
    
    /**
     * Tests whether the listFiles() method works.
     * 
     * @throws IOException unwanted.
     */
    @Test
    public void testListFiles() throws IOException {
        File zipFile = new File(TESTDATA, "archive.zip");
        ZipArchive archive = new ZipArchive(zipFile);
        
        Set<File> files = archive.listFiles();
        
        Assert.assertEquals(2, files.size());
        Assert.assertTrue(files.contains(new File("test.txt")));
        Assert.assertTrue(files.contains(new File("dir/test.txt")));
        
        archive.close();
    }
    
}
