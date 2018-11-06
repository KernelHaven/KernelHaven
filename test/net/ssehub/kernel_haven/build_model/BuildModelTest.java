package net.ssehub.kernel_haven.build_model;

import static net.ssehub.kernel_haven.util.logic.FormulaBuilder.and;
import static net.ssehub.kernel_haven.util.logic.FormulaBuilder.or;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.io.File;

import org.junit.Test;

import net.ssehub.kernel_haven.build_model.BuildModelDescriptor.KeyType;
import net.ssehub.kernel_haven.util.logic.Formula;

/**
 * Tests the {@link BuildModel}.
 * 
 * @author Adam
 */
@SuppressWarnings("null")
public class BuildModelTest {

    /**
     * Tests the {@link KeyType#FILE}.
     */
    @Test
    public void testKeyTypeFile() {
        BuildModel bm = new BuildModel();
        assertThat(bm.getDescriptor().getKeyType(), is(KeyType.FILE)); // default value
        
        File dir = new File("dir");
        File file = new File(dir, "test.c");
        
        Formula pc1 = or("A", "B");
        Formula pc2 = and("A", "C");
        
        bm.add(dir, pc1);
        
        assertThat(bm.getPc(file), nullValue());
        assertThat(bm.getPc(dir), is(pc1));
        
        assertThat(bm.containsFile(dir), is(true));
        assertThat(bm.containsFile(file), is(false));
        assertThat(bm.containsKey(dir), is(true));
        assertThat(bm.containsKey(file), is(false));
        
        bm.add(file, pc2);
        
        assertThat(bm.getPc(file), is(pc2));
        assertThat(bm.getPc(dir), is(pc1));
        
        assertThat(bm.containsFile(dir), is(true));
        assertThat(bm.containsFile(file), is(true));
        assertThat(bm.containsKey(dir), is(true));
        assertThat(bm.containsKey(file), is(true));
    }
    
    /**
     * Tests the {@link KeyType#DIRECTORY}.
     */
    @Test
    public void testKeyTypeDirectory() {
        BuildModel bm = new BuildModel();
        bm.getDescriptor().setKeyType(KeyType.DIRECTORY);
        
        assertThat(bm.getDescriptor().getKeyType(), is(KeyType.DIRECTORY));
        
        File dir = new File("dir");
        File file = new File(dir, "test.c");
        
        Formula pc1 = or("A", "B");
        Formula pc2 = and("A", "C");
        
        bm.add(dir, pc1);
        
        assertThat(bm.getPc(file), is(pc1));
        assertThat(bm.getPc(dir), nullValue()); // null, because ONLY parent directories are checked
        
        assertThat(bm.containsFile(dir), is(false));
        assertThat(bm.containsFile(file), is(true));
        assertThat(bm.containsKey(dir), is(true));
        assertThat(bm.containsKey(file), is(false));
        
        bm.add(file, pc2);
        
        assertThat(bm.getPc(file), is(pc1)); // still pc1, because ONLY parent directories are checked
        assertThat(bm.getPc(dir), nullValue()); // null, because ONLY parent directories are checked
        
        assertThat(bm.containsFile(dir), is(false));
        assertThat(bm.containsFile(file), is(true));
        assertThat(bm.containsKey(dir), is(true));
        assertThat(bm.containsKey(file), is(true));
    }
    
    /**
     * Tests the {@link KeyType#DIRECTORY}.
     */
    @Test
    public void testKeyTypeDirectoryDeepNesting() {
        BuildModel bm = new BuildModel();
        bm.getDescriptor().setKeyType(KeyType.DIRECTORY);
        
        assertThat(bm.getDescriptor().getKeyType(), is(KeyType.DIRECTORY));
        
        Formula pc1 = or("A", "B");
        Formula pc2 = and("A", "C");
        
        File topdir = new File("topdir");
        File middledir = new File(topdir, "middledir");
        File bottomdir = new File(middledir, "bottomdir");
        File file = new File(bottomdir, "test.c");
        
        assertThat(bm.containsFile(file), is(false));
        
        bm.add(topdir, pc1);
        
        assertThat(bm.containsFile(file), is(true));
        assertThat(bm.getPc(file), is(pc1));
        
        bm.add(middledir, pc2);
        
        assertThat(bm.containsFile(file), is(true));
        assertThat(bm.getPc(file), is(pc2));
    }
    
    /**
     * Tests the {@link KeyType#FILE_AND_DIRECTORY}.
     */
    @Test
    public void testKeyTypeFileAndDirectory() {
        BuildModel bm = new BuildModel();
        bm.getDescriptor().setKeyType(KeyType.FILE_AND_DIRECTORY);
        
        assertThat(bm.getDescriptor().getKeyType(), is(KeyType.FILE_AND_DIRECTORY));
        
        File dir = new File("dir");
        File file = new File(dir, "test.c");
        
        Formula pc1 = or("A", "B");
        Formula pc2 = and("A", "C");
        
        bm.add(dir, pc1);
        
        assertThat(bm.getPc(file), is(pc1));
        assertThat(bm.getPc(dir), is(pc1));
        
        assertThat(bm.containsFile(dir), is(true));
        assertThat(bm.containsFile(file), is(true));
        assertThat(bm.containsKey(dir), is(true));
        assertThat(bm.containsKey(file), is(false));
        
        bm.add(file, pc2);
        
        assertThat(bm.getPc(file), is(pc2));
        assertThat(bm.getPc(dir), is(pc1));
        
        assertThat(bm.containsFile(dir), is(true));
        assertThat(bm.containsFile(file), is(true));
        assertThat(bm.containsKey(dir), is(true));
        assertThat(bm.containsKey(file), is(true));
    }
    
    /**
     * Tests the {@link KeyType#DIRECTORY}.
     */
    @Test
    public void testKeyTypeFileAndDirectoryDeepNesting() {
        BuildModel bm = new BuildModel();
        bm.getDescriptor().setKeyType(KeyType.FILE_AND_DIRECTORY);
        
        assertThat(bm.getDescriptor().getKeyType(), is(KeyType.FILE_AND_DIRECTORY));
        
        Formula pc1 = or("A", "B");
        Formula pc2 = and("A", "C");
        
        File topdir = new File("topdir");
        File middledir = new File(topdir, "middledir");
        File bottomdir = new File(middledir, "bottomdir");
        File file = new File(bottomdir, "test.c");
        
        assertThat(bm.containsFile(file), is(false));
        
        bm.add(topdir, pc1);
        
        assertThat(bm.containsFile(file), is(true));
        assertThat(bm.getPc(file), is(pc1));
        
        bm.add(middledir, pc2);
        
        assertThat(bm.containsFile(file), is(true));
        assertThat(bm.getPc(file), is(pc2));
    }
    
    /**
     * Tests case-sensitive matching of filenames.
     */
    @Test
    public void testCaseSensitive() {
        BuildModel bm = new BuildModel();
        
        assertThat(bm.getDescriptor().isCaseSensitive(), is(true)); // default value
        
        Formula pc1 = or("A", "B");
        Formula pc2 = and("A", "C");
        
        File f1 = new File("dir/test.c");
        File f2 = new File("dir/Test.c");
        File f3 = new File("Dir/test.c");
        
        bm.add(f1, pc1);
        
        assertThat(bm.getPc(f1), is(pc1));
        assertThat(bm.getPc(f2), nullValue());
        assertThat(bm.getPc(f3), nullValue());
        
        bm.add(f3, pc2);
        
        assertThat(bm.getPc(f1), is(pc1));
        assertThat(bm.getPc(f2), nullValue());
        assertThat(bm.getPc(f3), is(pc2));
    }
    
    /**
     * Tests case-insensitive matching of filenames.
     */
    @Test
    public void testCaseInsensitive() {
        BuildModel bm = new BuildModel();
        bm.getDescriptor().setCaseSensitive(false);
        
        assertThat(bm.getDescriptor().isCaseSensitive(), is(false));
        
        Formula pc1 = or("A", "B");
        Formula pc2 = and("A", "C");
        
        File f1 = new File("dir/test.c");
        File f2 = new File("dir/Test.c");
        File f3 = new File("Dir/test.c");
        
        bm.add(f1, pc1);
        
        assertThat(bm.getPc(f1), is(pc1));
        assertThat(bm.getPc(f2), is(pc1));
        assertThat(bm.getPc(f3), is(pc1));
        
        bm.add(f3, pc2);
        
        assertThat(bm.getPc(f1), is(pc2)); // overwrite by f3
        assertThat(bm.getPc(f2), is(pc2));
        assertThat(bm.getPc(f3), is(pc2));
    }
    
    /**
     * Tests {@link BuildModel#filesEqual(File, File)}.
     */
    @Test
    public void testFilesEqual() {
        File f1 = new File("some/dir/test.c");
        File f2 = new File("some/dir/test.c");
        File f3 = new File("some/dIr/test.c");
        File f4 = new File("some/test.c");
        File f5 = new File("");
        File f6 = new File("test.c");
        
        BuildModel bm = new BuildModel();
        assertThat(bm.getDescriptor().isCaseSensitive(), is(true)); // default value
        
        assertThat(bm.filesEqual(f1, f2), is(true));
        assertThat(bm.filesEqual(f1, f3), is(false));
        assertThat(bm.filesEqual(f1, f4), is(false));
        assertThat(bm.filesEqual(f4, f1), is(false));
        assertThat(bm.filesEqual(f1, f5), is(false));
        assertThat(bm.filesEqual(f1, f6), is(false));
        assertThat(bm.filesEqual(f6, f1), is(false));
        
        bm.getDescriptor().setCaseSensitive(false);
        
        assertThat(bm.filesEqual(f1, f2), is(true));
        assertThat(bm.filesEqual(f1, f3), is(true));
        assertThat(bm.filesEqual(f4, f1), is(false));
        assertThat(bm.filesEqual(f1, f5), is(false));
        assertThat(bm.filesEqual(f1, f6), is(false));
        assertThat(bm.filesEqual(f6, f1), is(false));
    }
    
}
