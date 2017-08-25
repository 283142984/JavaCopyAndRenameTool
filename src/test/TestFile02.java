package test;
import java.io.File;

import Utils.PathUtils;  
  
/* 
 * 打印树状目录 
 * 核心方法：递归 
 * 用到的方法: 
 *   1)f.getName() ---获得该目录的名字 
 *   2)f.list() ------列出该目录下所有的一级目录及文件，返回值String[] 
 *     
 */  
public class TestFile02 {  
      
    public static void printFile(File f,int lever){  
        for(int i=0;i<lever;i++){  
            System.out.print("-");            
        }  
        System.out.println(f.getName());  
        if(f.isFile()){           
            return ;  
        }else{  
            String[] s=f.list();  
            for(int i=0;i<s.length;i++){  
                File f1=new File(f.getPath()+File.separator+s[i]);  
                printFile(f1,lever+1);  
            }  
        }  
        return ;  
    }  
  
    public static void main(String[] args) {  
        //String path="/home/soft01/QXL";  
          
        //可以用/也可以用File类提供的分隔符File.separator（建议用分隔符，不会有系统的问题）  
        String path="E:\\CoremailPlugin\\OutlookPlugin\\nk2view";   
//          System.out.println(PathUtils.getClassPath(this.class));
        File f=new File(path);  
        printFile(f,0);  
    }  
  
}  