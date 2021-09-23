package com.example.demo;

import com.github.luben.zstd.Zstd;
import com.github.luben.zstd.ZstdInputStream;
import com.github.luben.zstd.ZstdOutputStream;
import org.apache.commons.io.IOUtils;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

@ApplicationPath("/api")
public class HelloApplication extends Application {
    public static void main(String[] args) throws IOException {


        // comparess way #1 with stream (mutli thread available)
        {
            InputStream is = HelloApplication.class.getResourceAsStream("/1sample.txt");
            String s = IOUtils.toString(is, Charset.defaultCharset());
//            ZstdInputStream zstdInputStream = new ZstdInputStream(is);
//            zstdInputStream.
            FileOutputStream fos = new FileOutputStream(new File("/tmp/outoo.csv"));
            ZstdOutputStream zd = new ZstdOutputStream(fos);
            zd.setWorkers(6);
            zd.write(s.getBytes(StandardCharsets.UTF_8));
            is.close();
            zd.close();
        }


        // compress #2 use utility
        System.out.println("reading......");
        String s = readFile();

        System.out.println("compressing......");
        compress(s);

        System.out.println("decompressing......");
        byte[] buffer = readCompressedFile();
        long decompressedSize = Zstd.decompressedSize(buffer);
        byte[] decompressedBytes = Zstd.decompress(buffer, (int) decompressedSize);
        String decompressedStr = new String(decompressedBytes, StandardCharsets.UTF_8);
        String path = HelloApplication.class.getResource("/").getPath() + File.separator + "decompressed.txt";
        File f = new File(path);
        System.out.println("decompressed path = " + f.getAbsolutePath());
        FileWriter fw = new FileWriter(f);
        IOUtils.write(decompressedStr, fw);
        fw.close();

        System.out.println("DONE");
    } // end main

    private static byte[] readCompressedFile(String compressedFilePath) throws IOException {
        int size = (int) Files.size(Paths.get(compressedFilePath));
        InputStream is2 = HelloApplication.class.getResourceAsStream("/compressed.bin");
        byte[] buffer = new byte[size];
        IOUtils.read(is2, buffer);
        is2.close();
        return buffer;
    }

    private static byte[] readCompressedFile() throws IOException {
        String compressedFilePath = HelloApplication.class.getResource("/compressed.bin").getPath();
        int size = (int) Files.size(Paths.get(compressedFilePath));
        InputStream is2 = HelloApplication.class.getResourceAsStream("/compressed.bin");
        byte[] buffer = new byte[size];
        IOUtils.read(is2, buffer);
        is2.close();
        return buffer;
    }

    private static String readFile() throws IOException {
        InputStream is = HelloApplication.class.getResourceAsStream("/sample.txt");
        String s = IOUtils.toString(is, Charset.defaultCharset());
        is.close();
        return s;
    }

    private static void compress(String s) throws IOException {
        byte[] b = Zstd.compress(s.getBytes(StandardCharsets.UTF_8));
        String outputPath = HelloApplication.class.getResource("/").getPath() + File.separator + "compressed.bin";
        File outputFile = new File(outputPath);
        System.out.println(outputFile.getAbsolutePath());
        FileOutputStream f = new FileOutputStream(outputFile);
        f.write(b);
        f.flush();
        f.close();
    }
} // end class
