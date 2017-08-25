package Utils;

import java.net.URL;

public class PathUtils {
	  /**
	     * �õ����·��������E:/workspace/JavaGUI/bin/com/util
	     * @return
	     * @throws java.lang.Exception
	     */
	    public static String getClassPath(Class claz) throws Exception {
	        try {
	            String strClassName =claz.getClass().getName();
	            String strPackageName = "";
	            if (claz.getClass().getPackage() != null) {
	                strPackageName = claz.getClass().getPackage().getName();
	            }
	            String strClassFileName = "";
	            if (!"".equals(strPackageName)) {
	                strClassFileName = strClassName.substring(strPackageName.length() + 1,
	                        strClassName.length());
	            } else {
	                strClassFileName = strClassName;
	            }
	            URL url = null;
	            url =claz. getClass().getResource(strClassFileName + ".class");
	            String strURL = url.toString();
	            strURL = strURL.substring(strURL.indexOf('/') + 1, strURL
	                    .lastIndexOf('/'));
	            //���ص�ǰ���·�������Ҵ���·���еĿո���Ϊ��·���г��ֵĿո����������Ļ���
	            //�ڷ���ʱ�ͻ�ӿո񴦶Ͽ�����ôҲ��ȡ������������Ϣ�ˣ����������web����������Ҫע��
	            return strURL.replaceAll("%20", " ");
	        } catch (Exception ex) {
	            ex.printStackTrace();
	            throw ex;
	        }
	    }
}

