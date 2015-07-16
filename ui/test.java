package ui;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Base64;

public class test {

	public static void main(String[] args) {
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader("./test/temp.txt"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		try {
			StringBuilder sb = new StringBuilder();
			String line = null;
			try {
				line = br.readLine();
			} catch (IOException e) {
				e.printStackTrace();
			}

			while (line != null) {
				sb.append(line);
				line = br.readLine();
			}
			
			String[] data = sb.toString().split(",");
			
			final byte[] decode = Base64.getDecoder().decode(data[1]);
			File file = new File("./test/temp.mov");
			file.createNewFile();
			FileOutputStream out = new FileOutputStream(file);
			out.write(decode);
			out.flush();
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

}
