package test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import net.lingala.zip4j.ZipFile;

public class Test {

	public static void main(String[] args) throws InterruptedException {
		InputStream input = Test.class.getResourceAsStream("/server/server.zip");
		String destination = System.getProperty("java.io.tmpdir");
		copy(input, destination+"/server.zip");
		try {
			ZipFile zip = new ZipFile(destination+"/server.zip");
			zip.extractAll(destination);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static boolean copy(InputStream source , String destination) {
        boolean ok = true;

        try {
            Files.copy(source, Paths.get(destination), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ex) {
        	ok = false;
        }

        return ok;

    }
}
