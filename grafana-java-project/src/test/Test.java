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
		
		try {
            Files.copy(input, Paths.get(destination+"/server.zip"), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ex) {
        	return;
        }
		
		try {
			ZipFile zip = new ZipFile(destination+"/server.zip");
			zip.extractAll(destination);
			Files.deleteIfExists(Paths.get(destination+"/server.zip"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return;
	}
}
