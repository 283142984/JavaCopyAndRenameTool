package Utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.channels.FileChannel;


public class FileUtils {
	//一般readCharsetName="UTF-8"
	   public static String read(String filePath,String readCharsetName) {
		   StringBuffer resultText = new StringBuffer();
	        FileInputStream fis = null;
	        InputStreamReader inputsr = null;
	        BufferedReader br = null;
			try {
				fis = new FileInputStream( new File(filePath));
				 inputsr = new InputStreamReader(fis,readCharsetName);   
			  
	         br = new BufferedReader(inputsr);   
	        String line = null;   
	        while ((line = br.readLine()) != null) {   
	        	resultText.append( line);   
	        	resultText.append("\r\n"); // 补上换行符   
	        } 
			} catch (Exception e) {
				e.printStackTrace();
			} finally{
				try {
					if(br!=null)
					 br.close();
					if(inputsr!=null)
					    inputsr.close();
					if(fis!=null)
					    fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
	        return resultText.toString();
	    }
	 //"UTF-8"
	   @SuppressWarnings("finally")
	public static boolean save(String fileContent,String outputFilePath,String charsetName) {
		   boolean isSuccess=false;
		   FileOutputStream fos  = null;
		   OutputStreamWriter osw = null;
			try {
				 fos = new FileOutputStream(new File(outputFilePath));   
			        osw = new OutputStreamWriter(fos, "UTF-8");   
			       osw.write(fileContent);   
			       osw.flush();   
			       isSuccess=true;
			} catch (Exception e) {
				e.printStackTrace();
			} finally{
				try {
					if(osw!=null)
						osw.close();
					if(fos!=null)
						fos.close();
					
				} catch (IOException e) {
					e.printStackTrace();
				}finally{
		        	   return isSuccess;
		        }
			}
	    }
	   @SuppressWarnings("finally")
	public static boolean fileChannelCopy(File s, File t) {  
		    boolean isSuccess=false;
		    FileInputStream fi = null;  
		    FileOutputStream fo = null;  
		    FileChannel in = null;  
		    FileChannel out = null;  
		    try {  
		        fi = new FileInputStream(s);  
		        fo = new FileOutputStream(t);  
		        in = fi.getChannel();//得到对应的文件通道  
		        out = fo.getChannel();//得到对应的文件通道  
		        in.transferTo(0, in.size(), out);//连接两个通道，并且从in通道读取，然后写入out通道  
		    } catch (IOException e) {  
		        e.printStackTrace();  
		    } finally {  
		        try {  
		        	if(fi!=null)
		            fi.close();  
		        	if(in!=null)
		            in.close();  
		        	if(fo!=null)
		            fo.close();  
		        	if(out!=null)
		            out.close();  
		        } catch (IOException e) {  
		            e.printStackTrace();  
		        } finally{
		        	   return isSuccess;
		        } 
		    }  
		} 
}
