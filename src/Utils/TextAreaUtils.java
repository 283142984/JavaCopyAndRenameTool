package Utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

import text.FileTextDAO;

public class TextAreaUtils {
	//readCharsetName="GBK" , OutCharsetName="gb2312" 即符合UTF8的输出
	   public String read(String file,String readCharsetName,String OutCharsetName) {
	        byte[] datas = null;
	        String resultText="";
	        try {
	            datas = Files.readAllBytes(Paths.get(file));
	            //乱码
	            String text = new String(datas);
	             resultText=new String( text.getBytes(readCharsetName),OutCharsetName);
	        } catch (IOException e) {
	            Logger.getLogger(FileTextDAO.class.getName()).log(Level.SEVERE, null, e);
	        }
	        return resultText;
	    }
	   
}