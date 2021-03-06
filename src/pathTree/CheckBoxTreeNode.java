package pathTree;
import javax.swing.JTextArea;
import javax.swing.tree.DefaultMutableTreeNode;

import main.MainJPanel;
  
public class CheckBoxTreeNode extends DefaultMutableTreeNode  
{   MainJPanel mainJPanel;
    protected boolean isSelected;  
      
    public CheckBoxTreeNode()  
    {  
        super(null);  
    }  
      
    public CheckBoxTreeNode(Object userObject, MainJPanel mainJPanel)  
    { 
        this(userObject, true, false);
        this.mainJPanel=mainJPanel;
    }  
      
    public CheckBoxTreeNode(Object userObject, boolean allowsChildren, boolean isSelected)  
    {  
        super(userObject, allowsChildren);  
        this.isSelected = isSelected;  
    }  
  
    public boolean isSelected()  
    {  
        return isSelected;  
    }  
      
    public void setSelected(boolean _isSelected)  
    {  
        this.isSelected = _isSelected;  
          
        if(_isSelected)  
        {  
            // 如果选中，则将其所有的子结点都选中  
            if(children != null)  
            {  
                for(Object obj : children)  
                {  
                    CheckBoxTreeNode node = (CheckBoxTreeNode)obj;  
                    if(_isSelected != node.isSelected())  
                        {node.setSelected(_isSelected);
                    
                        newPathArea(node);
                        
                        }  
                }  
            } 
            // 向上检查，如果父结点的所有子结点都被选中，那么将父结点也选中  
            CheckBoxTreeNode pNode = (CheckBoxTreeNode)parent;  
            // 开始检查pNode的所有子节点是否都被选中  
            if(pNode != null)  
            {  
                int index = 0;  
                for(; index < pNode.children.size(); ++ index)  
                {  
                    CheckBoxTreeNode pChildNode = (CheckBoxTreeNode)pNode.children.get(index);  
                    if(!pChildNode.isSelected())  
                        break;  
                }  
                /*  
                 * 表明pNode所有子结点都已经选中，则选中父结点， 
                 * 该方法是一个递归方法，因此在此不需要进行迭代，因为 
                 * 当选中父结点后，父结点本身会向上检查的。 
                 */  
                if(index == pNode.children.size())  
                {  
                    if(pNode.isSelected() != _isSelected)  
                        pNode.setSelected(_isSelected);  
                }  
            }  
        }  
        else   
        {  
            /* 
             * 如果是取消父结点导致子结点取消，那么此时所有的子结点都应该是选择上的； 
             * 否则就是子结点取消导致父结点取消，然后父结点取消导致需要取消子结点，但 
             * 是这时候是不需要取消子结点的。 
             */  
            if(children != null)  
            {  
                int index = 0;  
                for(; index < children.size(); ++ index)  
                {  
                    CheckBoxTreeNode childNode = (CheckBoxTreeNode)children.get(index);  
                    if(!childNode.isSelected())  
                        break;  
                }  
                // 从上向下取消的时候  
                if(index == children.size())  
                {  
                    for(int i = 0; i < children.size(); ++ i)  
                    {  
                        CheckBoxTreeNode node = (CheckBoxTreeNode)children.get(i);  
                        if(node.isSelected() != _isSelected)  
                            {node.setSelected(_isSelected);
                            deletePathArea(node);
                            }  
                    }  
                }  
            }  
              
            // 向上取消，只要存在一个子节点不是选上的，那么父节点就不应该被选上。  
            CheckBoxTreeNode pNode = (CheckBoxTreeNode)parent;  
            if(pNode != null && pNode.isSelected() != _isSelected)  
                pNode.setSelected(_isSelected);  
        }  
    }


	public void newPathArea(CheckBoxTreeNode node) {
		if(node.getChildCount()!=0)return;
		JTextArea oldPathNametextArea=new JTextArea(node.toString());
		oldPathNametextArea.setEnabled(false);
		 String areaString=  node.toString();
		String beforeFileNamePath= areaString.substring( 0,areaString.lastIndexOf("\\")+1);
		String fileName=areaString.substring(areaString.lastIndexOf("\\")+1);
		 String  oldfileName=mainJPanel.oldfileNametextArea.getText();
		String  newfileName=mainJPanel.newfileNametextArea.getText();

		if(oldfileName.trim().equals("")||newfileName.trim().equals("")){
			areaString= beforeFileNamePath+"copy_"+fileName;
		}
		else{
			fileName=	fileName.replaceAll(oldfileName, newfileName);
			areaString= beforeFileNamePath+fileName;
		}
		 JTextArea newPathNametextArea=new JTextArea(areaString);
		mainJPanel.putPathPaneBean(node.toString(),oldPathNametextArea,newPathNametextArea,node);
	} 
	public void deletePathArea(CheckBoxTreeNode node) {
		  mainJPanel.deletePathPaneBean(node.toString());
	} 
}  