/*
 * Created on Aug 12, 2009
 *
 * @author dkatzel
 */
package org.jcvi.io;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import static org.easymock.classextension.EasyMock.*;
import static org.junit.Assert.*;
public class TestIOUtil_deleteDir {

    File rootDir;
    @Test(expected = NullPointerException.class)
    public void nullDirThrowsNullPointerException()throws IOException{
        IOUtil.recursiveDelete(null);
    }
    private File createMockFileThatIsNeverAccessed(){
        File singleFile= createMock(File.class);       
        return singleFile;
    }
    private File createMockFile(){
        File singleFile= createMock(File.class);
        expect(singleFile.exists()).andReturn(true);
        expect(singleFile.isDirectory()).andReturn(false);
        expect(singleFile.delete()).andReturn(true);
        return singleFile;
    }
    private File createMockDir(File...subFiles){
        File dir=createMockDirThatIsNotDeleted(subFiles);
        expect(dir.delete()).andReturn(true);
        return dir;
    }
    private File createMockDirThatIsNotDeleted(File...subFiles){
        File dir= createMock(File.class);
        expect(dir.exists()).andReturn(true);
        expect(dir.isDirectory()).andReturn(true);
        expect(dir.listFiles()).andReturn(subFiles);
        return dir;
    }
    
    private File createMockFileThatCantBeDeleted(){
        File singleFile= createMock(File.class);
        expect(singleFile.exists()).andReturn(true);
        expect(singleFile.isDirectory()).andReturn(false);
        expect(singleFile.delete()).andReturn(false);
        return singleFile;
    }
    private File createNonExistentMockFile(){
        File singleFile= createMock(File.class);
        expect(singleFile.exists()).andReturn(false);
        return singleFile;
    }
    @Test
    public void singleFileGetsDeleted() throws IOException{
        File singleFile = createMockFile();
        replay(singleFile);
        IOUtil.recursiveDelete(singleFile);
        verify(singleFile);
    }
    @Test
    public void fileThatDoesNotExistShouldDoNothing() throws IOException{
        File singleFile = createNonExistentMockFile();
        replay(singleFile);
        IOUtil.recursiveDelete(singleFile);
        verify(singleFile);
    }
    
    @Test
    public void errorOnDeleteShouldThrowIOException(){
        File singleFile = createMockFileThatCantBeDeleted();
        replay(singleFile);
        try {
            IOUtil.recursiveDelete(singleFile);
            fail("if can't be deleted should throw IOException");
        } catch (IOException e) {
            assertEquals("unable to delete "+ singleFile, e.getMessage());
        }
        verify(singleFile);
    }
    public static <T> void replayAll(T[] mocks){
        for(T mock : mocks){
            replay(mock);
        }
    }
    public static <T> void verifyAll(T[] mocks){
        for(T mock : mocks){
            verify(mock);
        }
    }
    @Test
    public void deleteDir() throws IOException{
        File[] subFiles = new File[]{
                createMockFile(),createMockFile(),createMockFile()};
        File dir = createMockDir(subFiles);
        replay(dir);
        replayAll(subFiles);
        IOUtil.recursiveDelete(dir);
        verify(dir);
        verifyAll(subFiles);
    }
    
    @Test
    public void nestedDirs() throws IOException{
        File[] subSubFiles = new File[]{
                createMockFile(),createMockFile(),createMockFile()};
        File subDir = createMockDir(subSubFiles);
        File[] subFiles = new File[]{
                createMockFile(),createMockFile()};
        List<File> fileList = new ArrayList<File>();
        fileList.add(subDir);
        for(File subFile : subFiles){
            fileList.add(subFile);
        }
        final File[] subFilesAndSubDirs = fileList.toArray(new File[]{});
        File dir = createMockDir(subFilesAndSubDirs);
        replay(dir,subDir);
        replayAll(subFiles);
        replayAll(subSubFiles);
        IOUtil.recursiveDelete(dir);
        verify(dir,subDir);
        verifyAll(subFiles);
        verifyAll(subSubFiles);
    }
    
    @Test
    public void nestedDeleteFailsShouldThrowIOExceptionAndStopDeleting(){
        File[] subSubFiles = new File[]{
                createMockFile(),createMockFileThatCantBeDeleted(),createMockFileThatIsNeverAccessed()};
        File subDir = createMockDirThatIsNotDeleted(subSubFiles);
        File[] subFiles = new File[]{
                createMockFileThatIsNeverAccessed(),createMockFileThatIsNeverAccessed()};
        List<File> fileList = new ArrayList<File>();
        fileList.add(subDir);
        for(File subFile : subFiles){
            fileList.add(subFile);
        }
        final File[] subFilesAndSubDirs = fileList.toArray(new File[]{});
        File dir = createMockDirThatIsNotDeleted(subFilesAndSubDirs);
        replay(dir,subDir);
        replayAll(subFiles);
        replayAll(subSubFiles);
        try {
            IOUtil.recursiveDelete(dir);
            fail("failure to delete file should throw IOException");
        } catch (IOException e) {
            verify(dir,subDir);
            verifyAll(subFiles);
            verifyAll(subSubFiles);
        }
        
    }
    
}


