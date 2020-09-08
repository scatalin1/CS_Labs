package sample;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.utils.IOUtils;

import java.io.*;
import java.util.zip.GZIPInputStream;

public class Reader {
    private String reader = new String();

    public String getReader(String folder) throws IOException{
        String COMPRESSED_FILE = folder + "\\audits.tar.gz";
        String DESTINATION_PATH = folder;
        File destFile = new File(DESTINATION_PATH);
        unTarFile(COMPRESSED_FILE, destFile);

        BufferedReader reader = new BufferedReader(new FileReader(folder + "\\portal_audits\\Windows\\MSCT_Windows_10_1903_v1.19.9.audit"));
        StringBuilder stringBuilder = new StringBuilder();
        String line = null;
        String ls = System.getProperty("line.separator");
        while ((line = reader.readLine()) != null) {
            stringBuilder.append(line);
            stringBuilder.append(ls);
        }
// delete the last new line separator
        stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        reader.close();

        this.reader = stringBuilder.toString();

//        System.out.println(this.reader);
        return this.reader;
    }


    private static void unTarFile(String tarFile, File destFile) {
        TarArchiveInputStream tis = null;
        try {
            FileInputStream fis = new FileInputStream(tarFile);
            // .gz
            GZIPInputStream gzipInputStream = new GZIPInputStream(new BufferedInputStream(fis));
            //.tar.gz
            tis = new TarArchiveInputStream(gzipInputStream);
            TarArchiveEntry tarEntry = null;
            while ((tarEntry = tis.getNextTarEntry()) != null) {
//                System.out.println(" tar entry- " + tarEntry.getName());
                if(tarEntry.isDirectory()){
                    continue;
                }else {
                    // In case entry is for file ensure parent directory is in place
                    // and write file content to Output Stream
                    File outputFile = new File(destFile + File.separator + tarEntry.getName());
                    outputFile.getParentFile().mkdirs();
                    IOUtils.copy(tis, new FileOutputStream(outputFile));
                }
            }
        }catch(IOException ex) {
            System.out.println("Error while untarring a file- " + ex.getMessage());
        }finally {
            if(tis != null) {
                try {
                    tis.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }
}