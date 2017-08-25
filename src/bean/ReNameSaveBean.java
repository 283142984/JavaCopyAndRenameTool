package bean;

import java.io.Serializable;
/**
 * 保存要替换的字段和新字段  序列化类 用于恢复现场
 * 
 * */
public class ReNameSaveBean implements Serializable {
	private String oldName;
	private String newName;
	public String getOldName() {
		return oldName;
	}
	public void setOldName(String oldName) {
		this.oldName = oldName;
	}
	public String getNewName() {
		return newName;
	}
	public void setNewName(String newName) {
		this.newName = newName;
	}

}
