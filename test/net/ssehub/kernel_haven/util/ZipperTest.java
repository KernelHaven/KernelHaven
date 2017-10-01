package net.ssehub.kernel_haven.util;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import org.hamcrest.CoreMatchers;
import org.junit.Test;

/**
 * Tests the utility functions from the Zipper class.
 * 
 * @author Moritz
 * @author Malek
 */
public class ZipperTest {

    /**
     * Tests whether zipping of files is successful.
     * 
     * @throws IOException
     *             unwanted.
     */
    @Test
    public void testSingleFiles() throws IOException {
        File testfile1 = new File("testdata/ziptest/testfile1.txt");
        File testfile2 = new File("testdata/ziptest/dir/testfile2.txt");
        File tmpZip = File.createTempFile("tempZip", ".zip");
        tmpZip.delete();

        Zipper zipper = new Zipper(tmpZip);

        zipper.copyFileToZip(testfile1, "/testfile1.txt");
        zipper.copyFileToZip(testfile2, "/directory/testfile2.txt");

        Collection<String> zipEntries = getZipEntriesForFile(tmpZip);

        assertThat(zipEntries, hasItem(CoreMatchers.equalTo("directory/")));
        assertThat(zipEntries, hasItem(CoreMatchers.equalTo("directory/testfile2.txt")));
        assertThat(zipEntries, hasItem(CoreMatchers.equalTo("testfile1.txt")));
    }

    /**
     * Tests whether zipping of files is successful.
     * 
     * @throws IOException
     *             unwanted.
     */
    @Test
    public void testFolder() throws IOException {
        File testFolder = new File("testdata/ziptest/");
        File tmpZip = File.createTempFile("tempZip", ".zip");
        tmpZip.delete();

        Zipper zipper = new Zipper(tmpZip);

        zipper.copyFileToZip(testFolder, "/directory/");

        Collection<String> zipEntries = getZipEntriesForFile(tmpZip);

        assertThat(zipEntries, hasItem(CoreMatchers.equalTo("directory/")));
        assertThat(zipEntries, hasItem(CoreMatchers.equalTo("directory/dir/")));
        assertThat(zipEntries, hasItem(CoreMatchers.equalTo("directory/dir/testfile2.txt")));
        assertThat(zipEntries, hasItem(CoreMatchers.equalTo("directory/testfile1.txt")));

    }

    /**
     * 
     * Tests whether the Constructor creates an empty file if the file did not
     * exist before.
     * 
     * 
     * @throws IOException wanted.
     */
    @Test(expected = NoSuchFileException.class)
    public void testCopyNonExistantFile() throws IOException {
        File tmpZip = File.createTempFile("tempZip", ".zip");
        tmpZip.delete();

        // Constructor should create file
        Zipper zipper = new Zipper(tmpZip);

        zipper.copyFileToZip(new File("thisfiledoessurelynotexist.tmp"), "target/in/zip/placeholder.tmp");

    }

    /**
     * 
     * Tests whether the Constructor creates an empty file if the file did not
     * exist before.
     * 
     * 
     * @throws IOException
     *             unwanted.
     */
    @Test
    public void testInitFileCreation() throws IOException {
        File tmpZip = File.createTempFile("tempZip", ".zip");
        tmpZip.delete();

        assertThat(tmpZip.exists(), equalTo(false));

        // Constructor should create file
        new Zipper(tmpZip);

        assertThat(tmpZip.exists(), equalTo(true));

    }

    /**
     * 
     * Reads all entries from a zip-file and returns a list of them.
     * 
     * @param file
     *            the *.zip-file to look into
     * @return collection of zip-entries. Must not be null.
     * @throws IOException
     *             thrown if error on fileaccess.
     * @throws ZipException
     *             thrown if not a zip archive.
     */
    public static Collection<String> getZipEntriesForFile(File file) throws IOException, ZipException {
        ZipFile zipFile = new ZipFile(file);
        @SuppressWarnings("unchecked")
        Enumeration<ZipEntry> zipEntries = (Enumeration<ZipEntry>) zipFile.entries();
        ArrayList<String> entries = new ArrayList<String>();
        while (zipEntries.hasMoreElements()) {
            entries.add(zipEntries.nextElement().getName());
        }
        zipFile.close();
        return entries;
    }
    
    /**
     * Tests whether zipping of files is successful.
     * 
     * @throws IOException
     *             unwanted.
     */
    @Test
    public void testExisting() throws IOException {
        File testfile1 = new File("testdata/ziptest/testfile1.txt");
        File tmpZip = File.createTempFile("tempZip", ".zip");
        tmpZip.delete();
        Util.copyFile(new File("testdata/ziptest/test.zip"), tmpZip);

        Zipper zipper = new Zipper(tmpZip);

        zipper.copyFileToZip(testfile1, "/testfile1.txt");

        Collection<String> zipEntries = getZipEntriesForFile(tmpZip);

        assertThat(zipEntries, hasItem(CoreMatchers.equalTo("testfile1.txt")));
        assertThat(zipEntries, hasItem(CoreMatchers.equalTo("testfile2.txt"))); // this was previously in the zip
    }

}
