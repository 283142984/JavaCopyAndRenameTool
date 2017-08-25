package test;
import java.io.File;

import Utils.PathUtils;  
  
/* 
 * ��ӡ��״Ŀ¼ 
 * ���ķ������ݹ� 
 * �õ��ķ���: 
 *   1)f.getName() ---��ø�Ŀ¼������ 
 *   2)f.list() ------�г���Ŀ¼�����е�һ��Ŀ¼���ļ�������ֵString[] 
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
          
        //������/Ҳ������File���ṩ�ķָ���File.separator�������÷ָ�����������ϵͳ�����⣩  
        String path="E:\\CoremailPlugin\\OutlookPlugin\\nk2view";   
//          System.out.println(PathUtils.getClassPath(this.class));
        File f=new File(path);  
        printFile(f,0);  
    }  
  
}  