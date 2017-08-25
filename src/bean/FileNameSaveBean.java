package bean;

import java.io.Serializable;
/**
 * 保存文件名 和新文件名  序列化类 用于恢复现场
 * 
 * */
public class FileNameSaveBean implements Serializable {

	private String oldfileName;
	private String newfileName;

	public String getOldfileName() {
		return oldfileName;
	}

	public void setOldfileName(String oldfileName) {
		this.oldfileName = oldfileName;
	}

	public String getNewfileName() {
		return newfileName;
	}

	public void setNewfileName(String newfileName) {
		this.newfileName = newfileName;
	}

}
