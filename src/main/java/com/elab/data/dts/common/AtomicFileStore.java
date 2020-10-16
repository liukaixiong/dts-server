package com.elab.data.dts.common;

import java.io.*;
import java.util.LinkedList;
import java.util.List;


public class AtomicFileStore {
    private final String fileName;

    public AtomicFileStore(String fileName) {
        this.fileName = fileName;
    }

    public List<String> getContent() {
        List<String> ret = new LinkedList<>();
        if (!Util.checkFileExists(fileName)) {
            return ret;
        }
        FileReader readFile = null;
        BufferedReader bufferedReader = null;
        try {
            readFile = new FileReader(fileName);
            bufferedReader = new BufferedReader(readFile);
            String s = null;
            while ((s = bufferedReader.readLine()) != null) {
                ret.add(s);
            }
        } catch (Exception e) {
        } finally {
            Util.swallowErrorClose(readFile);
            Util.swallowErrorClose(bufferedReader);
        }
        return ret;

    }

    public boolean updateContent(List<String> newContent)  {
        synchronized (this) {
            String tmpFileName = fileName + ".tmp";
            if (Util.checkFileExists(tmpFileName)) {
                Util.deleteFile(tmpFileName);
            }
            boolean writeSuccess = true;
            FileWriter fileWriter = null;
            BufferedWriter bufferedWriter = null;
            try {
                fileWriter = new FileWriter(tmpFileName);
                bufferedWriter = new BufferedWriter(fileWriter);
                for (String content : newContent) {
                    bufferedWriter.write(content);
                    bufferedWriter.newLine();
                }
                bufferedWriter.flush();
            } catch (Exception e) {
                writeSuccess = false;
            } finally {
                Util.swallowErrorClose(fileWriter);
                Util.swallowErrorClose(bufferedWriter);
            }
            // BugFix: windows can't rename file to existing file, remove old file first
            remove();
            return writeSuccess ? (new File(tmpFileName).renameTo(new File(fileName))) : false;
        }
    }

    public void remove()  {
        Util.deleteFile(fileName);
    }

}
