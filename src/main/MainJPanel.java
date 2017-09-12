package main;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileFilter;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.ScrollPaneConstants;
import javax.swing.tree.DefaultTreeModel;

import Utils.FileUtils;
import bean.PathPaneBean;
import bean.ReNamePaneBean;
import pathTree.CheckBoxTreeCellRenderer;
import pathTree.CheckBoxTreeNode;
import pathTree.CheckBoxTreeNodeSelectionListener;

public class MainJPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    private JButton stopButton = new JButton("停止扫描（未开始）");
    private JButton browseButton = new JButton("选择文件夹");
    private JButton addReNameButton = new JButton("添加替换字段");
    private JCheckBox showHiddenFilesCheckbox = new JCheckBox("显示隐藏文件", false);
    private  JPanel northPanel = new JPanel();
    private  JPanel centerPanel = new JPanel();
    private  JPanel centerPathPanel = new JPanel();//中间右方路径设置区域
    private  JPanel centerButtonPanel = new JPanel();//中间按钮区域
    
//    private Map<Integer, String> pathIndexes = new HashMap<Integer, String>();
    private FileFilter docFilter = new DocFilter(); // 文档过滤器
    private FileFilter dirFilter = new DirFilter(); // 文件夹过滤器
    private boolean stopped = false; // 是否停止扫描的标志
    public JTextArea oldfileNametextArea=new JTextArea();//旧文件名Po
    public JTextArea newfileNametextArea=new JTextArea();//新文件名Po
    private  JLabel oldfileNameLabel =new JLabel("旧文件Po名:");  
    private  JLabel newfileNameLabel =new JLabel("新文件Po名:");  
    public JScrollPane jTreescroll;//左方树形区域
    public JTextArea textArea;//底部文本显示
    
    public Map<Integer,ReNamePaneBean> reNamePaneBeanMap=new LinkedHashMap<>();//保存reName对象 Map
    public Map<String,PathPaneBean> pathPaneBeanMap=new ConcurrentHashMap<>();//保存path文件路径对象 Map
    private JButton reChooseFileButton = new JButton("重新选择文件");
    private JButton reloadFileNameOldButton = new JButton("重写文件名");
    private JButton replaceOldButton = new JButton("原文替换");
    private JButton copyAndReplaceButton = new JButton("复制并且替换");
    private String CharsetName="UTF-8";
    JTree tree = new JTree();  
    CheckBoxTreeNode rootNode ;
    private MainJPanel mainJPanel;//自身引用
    public MainJPanel() {
        initGui();
        mainJPanel=this;
    }

    // 初始化界面
    private void initGui() {
        this.setLayout(new BorderLayout());

        
//        JScrollPane northPane = new JScrollPane(northPanel);
//        northPane.setHorizontalScrollBarPolicy (JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
//        northPane.setVerticalScrollBarPolicy (JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
//        northPane.setPreferredSize(new Dimension(800, 100));
        loadNorthPanel();
        this.add(northPanel, BorderLayout.NORTH);

        
        centerPanel.setLayout(new BorderLayout());
        centerPanel.add(centerButtonPanel,BorderLayout.SOUTH);
        centerButtonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));  
        centerButtonPanel.add(reChooseFileButton);
        centerButtonPanel.add(reloadFileNameOldButton);
        centerButtonPanel.add(replaceOldButton);
        centerButtonPanel.add(copyAndReplaceButton);
        
        
        JScrollPane pane = new JScrollPane(centerPathPanel);
        pane.setHorizontalScrollBarPolicy (JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        pane.setVerticalScrollBarPolicy (JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        
        centerPanel.add(pane,BorderLayout.CENTER);
        loadCenterPathPanel();
        this.add(centerPanel, BorderLayout.CENTER);

        
        //西边
        tree.addMouseListener(new CheckBoxTreeNodeSelectionListener(this));  
       jTreescroll = new JScrollPane(tree);  
      this.add(jTreescroll, BorderLayout.WEST);
      
      //南边
      textArea = new JTextArea();
      textArea.setFont(new Font("细明体", Font.PLAIN, 16));
      textArea.setLineWrap(true);
      JScrollPane panel = new JScrollPane(textArea,
              ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
              ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
      panel.setPreferredSize(new Dimension(0, 300));
      this.add(panel, BorderLayout.SOUTH);
        
        browseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 选择文件夹
                final JFileChooser chooser = new JFileChooser();
                chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int result = chooser.showOpenDialog(MainJPanel.this);

                if (result == JFileChooser.APPROVE_OPTION) {
                    Thread t = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            File dir = chooser.getSelectedFile();
                           rootNode = new CheckBoxTreeNode(dir.toString(),mainJPanel); 
                          
                          pathPaneBeanMap=new ConcurrentHashMap<>();
                          loadCenterPathPanel();
                          DefaultTreeModel model = new DefaultTreeModel(rootNode);  
                          tree.setModel(model);  
                          tree.setCellRenderer(new CheckBoxTreeCellRenderer());  
                            stopped = false;
                            stopButton.setText("停止扫描（扫描中）");
                            walkTree(dir, 0,rootNode);
                            jTreescroll.updateUI();
                            if( stopped == false)
                            stopButton.setText("停止扫描（扫描完成）");
                            else
                            stopButton.setText("停止扫描（扫描中断）");
                        }
                    });
                    t.start();
                }
            }
        });

        stopButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                stopped = true;
                
            }
        });
        
        addReNameButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	Integer MaxKey=0;
            	  for(Integer key:reNamePaneBeanMap.keySet()){
            		  MaxKey=key;
            	  }
            		reNamePaneBeanMap.put(MaxKey+1,new ReNamePaneBean());
            		loadNorthPanel();
            }
        });
        reChooseFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	for(String key:pathPaneBeanMap.keySet()){
            		PathPaneBean pathPaneBean=pathPaneBeanMap.get(key);
            		if(pathPaneBean!=null&&pathPaneBean.getCheckBoxTreeNode()!=null){
            		pathPaneBean.getCheckBoxTreeNode().setSelected(false);
            		 ((DefaultTreeModel)tree.getModel()).nodeStructureChanged(pathPaneBean.getCheckBoxTreeNode());
            		 } 
            	}
            	pathPaneBeanMap=new ConcurrentHashMap<>();
            	loadCenterPathPanel();
            }
        });
        
        reloadFileNameOldButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	
            	for(String key:pathPaneBeanMap.keySet()){
            		PathPaneBean pathPaneBean=pathPaneBeanMap.get(key);
            		String oldfilePath=pathPaneBean.getOldPathNametextArea().getText();
            		
            		String  oldfileName=oldfileNametextArea.getText();
            		String  newfileName=newfileNametextArea.getText();
            		
            		String newFilePath=  oldfilePath;
            		
            		String beforeFileNamePath= newFilePath.substring( 0,newFilePath.lastIndexOf("\\")+1);
            		String fileName=newFilePath.substring(newFilePath.lastIndexOf("\\")+1);
            			if(oldfileName.trim().equals("")||newfileName.trim().equals("")){
            				newFilePath= beforeFileNamePath+"copy_"+fileName;
            			}
            			else{
            				fileName=	fileName.replaceAll(oldfileName, newfileName);
            				newFilePath= beforeFileNamePath+fileName;
            			}
            			pathPaneBean.getNewPathNametextArea().setText((newFilePath));
            		
            	}
            	
            	JOptionPane.showMessageDialog(null, "成功刷新！", "成功", JOptionPane.OK_OPTION); 	
            }
        });
        replaceOldButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	if(reNamePaneBeanMap.size()==0){
            		JOptionPane.showMessageDialog(null, "没有添加设置要替换的字段！", "错误", JOptionPane.ERROR_MESSAGE); 
            		return;
            	}
            	if(pathPaneBeanMap.size()==0){
            		JOptionPane.showMessageDialog(null, "没有选定文件！", "错误", JOptionPane.ERROR_MESSAGE); 
            		return;
            	}
            	for(String key:pathPaneBeanMap.keySet()){
            		PathPaneBean pathPaneBean=pathPaneBeanMap.get(key);
            		String filePath=pathPaneBean.getOldPathNametextArea().getText();
            		String fileContent=FileUtils.read(filePath, CharsetName);
            	   	for(Integer k:reNamePaneBeanMap.keySet()){
            	   		ReNamePaneBean reNamePaneBean=reNamePaneBeanMap.get(k);
            		fileContent=fileContent.replaceAll(reNamePaneBean.getOldNametextArea().getText(),
            				reNamePaneBean.getNewNametextArea().getText());
            	   	
            	   	}
            		FileUtils.save(fileContent, filePath, CharsetName);
            	}
            	
            	JOptionPane.showMessageDialog(null, "成功替换！", "成功", JOptionPane.OK_OPTION); 	
            	
            }
        });
        copyAndReplaceButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	if(reNamePaneBeanMap.size()==0){
            		JOptionPane.showMessageDialog(null, "没有添加设置要替换的字段！", "错误", JOptionPane.ERROR_MESSAGE); 
            		return;
            	}
            	if(pathPaneBeanMap.size()==0){
            		JOptionPane.showMessageDialog(null, "没有选定文件！", "错误", JOptionPane.ERROR_MESSAGE); 
            		return;
            	}
            	for(String key:pathPaneBeanMap.keySet()){
            		PathPaneBean pathPaneBean=pathPaneBeanMap.get(key);
            		String oldfilePath=pathPaneBean.getOldPathNametextArea().getText();
            		String newfilePath=pathPaneBean.getNewPathNametextArea().getText().trim();
            		
            		FileUtils.fileChannelCopy(new File(oldfilePath), new File(newfilePath));
            		
            		String fileContent=FileUtils.read(newfilePath, CharsetName);
            	   	for(Integer k:reNamePaneBeanMap.keySet()){
            	   		ReNamePaneBean reNamePaneBean=reNamePaneBeanMap.get(k);
            		fileContent=fileContent.replaceAll(reNamePaneBean.getOldNametextArea().getText(),
            				reNamePaneBean.getNewNametextArea().getText());
            	   	
            	   	}
            		FileUtils.save(fileContent, newfilePath, CharsetName);
            	}
            	
            	JOptionPane.showMessageDialog(null, "成功复制并且替换！", "成功", JOptionPane.OK_OPTION); 	
            	
            
            }
        });
    }

    public void putPathPaneBean(String oldFilePathName,JTextArea oldPathNametextArea,
			JTextArea newPathNametextArea,CheckBoxTreeNode checkBoxTreeNode) {

    	  pathPaneBeanMap.put(oldFilePathName,new PathPaneBean(oldPathNametextArea,newPathNametextArea,checkBoxTreeNode));
    		loadCenterPathPanel();
	}
	private void loadNorthPanel() {
		northPanel.removeAll();
		northPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.GRAY));
		GridBagLayout layout = new GridBagLayout();
		GridBagConstraints s = new GridBagConstraints();
		s.fill = GridBagConstraints.BOTH;
		
		showHiddenFilesCheckbox.setPreferredSize(new Dimension(150, 25));
		s.gridwidth = 1;// 该方法是设置组件水平所占用的格子数，如果为0，就说明该组件是该行的最后一个
    	s.weightx = 0;// 该方法设置组件水平的拉伸幅度，如果为0就说明不拉伸，不为0就随着窗口增大进行拉伸，0到1之间
    	s.weighty = 0;// 该方法设置组件垂直的拉伸幅度，如果为0就说明不拉伸，不为0就随着窗口增大进行拉伸，0到1之间
    	layout.setConstraints(showHiddenFilesCheckbox, s);// 设置组件
        northPanel.add(showHiddenFilesCheckbox);
        
        browseButton.setPreferredSize(new Dimension(150, 25));
		s.gridwidth = 1;// 该方法是设置组件水平所占用的格子数，如果为0，就说明该组件是该行的最后一个
    	s.weightx = 0;// 该方法设置组件水平的拉伸幅度，如果为0就说明不拉伸，不为0就随着窗口增大进行拉伸，0到1之间
    	s.weighty = 0;// 该方法设置组件垂直的拉伸幅度，如果为0就说明不拉伸，不为0就随着窗口增大进行拉伸，0到1之间
    	layout.setConstraints(browseButton, s);// 设置组件
        northPanel.add(browseButton);
        
        stopButton.setPreferredSize(new Dimension(150, 25));
		s.gridwidth = 1;// 该方法是设置组件水平所占用的格子数，如果为0，就说明该组件是该行的最后一个
    	s.weightx = 0;// 该方法设置组件水平的拉伸幅度，如果为0就说明不拉伸，不为0就随着窗口增大进行拉伸，0到1之间
    	s.weighty = 0;// 该方法设置组件垂直的拉伸幅度，如果为0就说明不拉伸，不为0就随着窗口增大进行拉伸，0到1之间
    	layout.setConstraints(stopButton, s);// 设置组件
        northPanel.add(stopButton);
        
        addReNameButton.setPreferredSize(new Dimension(150, 25));
     		s.gridwidth = 0;// 该方法是设置组件水平所占用的格子数，如果为0，就说明该组件是该行的最后一个
         	s.weightx = 0;// 该方法设置组件水平的拉伸幅度，如果为0就说明不拉伸，不为0就随着窗口增大进行拉伸，0到1之间
         	s.weighty = 0;// 该方法设置组件垂直的拉伸幅度，如果为0就说明不拉伸，不为0就随着窗口增大进行拉伸，0到1之间
         	layout.setConstraints(addReNameButton, s);// 设置组件
        northPanel.add(addReNameButton);
        
        oldfileNameLabel.setPreferredSize(new Dimension(150, 25));
 		s.gridwidth = 1;// 该方法是设置组件水平所占用的格子数，如果为0，就说明该组件是该行的最后一个
     	s.weightx = 0;// 该方法设置组件水平的拉伸幅度，如果为0就说明不拉伸，不为0就随着窗口增大进行拉伸，0到1之间
     	s.weighty = 0;// 该方法设置组件垂直的拉伸幅度，如果为0就说明不拉伸，不为0就随着窗口增大进行拉伸，0到1之间
     	layout.setConstraints(oldfileNameLabel, s);// 设置组件
        northPanel.add(oldfileNameLabel);
        
        oldfileNametextArea.setPreferredSize(new Dimension(150, 25));
 		s.gridwidth = 1;// 该方法是设置组件水平所占用的格子数，如果为0，就说明该组件是该行的最后一个
     	s.weightx = 0;// 该方法设置组件水平的拉伸幅度，如果为0就说明不拉伸，不为0就随着窗口增大进行拉伸，0到1之间
     	s.weighty = 0;// 该方法设置组件垂直的拉伸幅度，如果为0就说明不拉伸，不为0就随着窗口增大进行拉伸，0到1之间
     	layout.setConstraints(oldfileNametextArea, s);// 设置组件
        northPanel.add(oldfileNametextArea);
        oldfileNametextArea.setLineWrap(true);
        
        newfileNameLabel.setPreferredSize(new Dimension(150, 25));
 		s.gridwidth = 1;// 该方法是设置组件水平所占用的格子数，如果为0，就说明该组件是该行的最后一个
     	s.weightx = 0;// 该方法设置组件水平的拉伸幅度，如果为0就说明不拉伸，不为0就随着窗口增大进行拉伸，0到1之间
     	s.weighty = 0;// 该方法设置组件垂直的拉伸幅度，如果为0就说明不拉伸，不为0就随着窗口增大进行拉伸，0到1之间
     	layout.setConstraints(newfileNameLabel, s);// 设置组件
        northPanel.add(newfileNameLabel);
        
        newfileNametextArea.setPreferredSize(new Dimension(150, 25));
 		s.gridwidth = 0;// 该方法是设置组件水平所占用的格子数，如果为0，就说明该组件是该行的最后一个
     	s.weightx = 0;// 该方法设置组件水平的拉伸幅度，如果为0就说明不拉伸，不为0就随着窗口增大进行拉伸，0到1之间
     	s.weighty = 0;// 该方法设置组件垂直的拉伸幅度，如果为0就说明不拉伸，不为0就随着窗口增大进行拉伸，0到1之间
     	layout.setConstraints(newfileNametextArea, s);// 设置组件
        northPanel.add(newfileNametextArea);
        newfileNametextArea.setLineWrap(true);
        
        for(Integer key:reNamePaneBeanMap.keySet()){
//        	System.out.println(key);
        	ReNamePaneBean reNamePaneBean=reNamePaneBeanMap.get(key);
        	
        	JLabel oldJLabel=new JLabel("原字段:");
        	oldJLabel.setPreferredSize(new Dimension(150, 25));
       		s.gridwidth = 1;// 该方法是设置组件水平所占用的格子数，如果为0，就说明该组件是该行的最后一个
           	s.weightx = 0;// 该方法设置组件水平的拉伸幅度，如果为0就说明不拉伸，不为0就随着窗口增大进行拉伸，0到1之间
           	s.weighty = 0;// 该方法设置组件垂直的拉伸幅度，如果为0就说明不拉伸，不为0就随着窗口增大进行拉伸，0到1之间
           	layout.setConstraints(oldJLabel, s);// 设置组件
        	northPanel.add(oldJLabel);
        
        	JTextArea oldNametextArea=reNamePaneBean.getOldNametextArea();
        	oldNametextArea.setPreferredSize(new Dimension(150, 25));
       		s.gridwidth = 1;// 该方法是设置组件水平所占用的格子数，如果为0，就说明该组件是该行的最后一个
           	s.weightx = 0;// 该方法设置组件水平的拉伸幅度，如果为0就说明不拉伸，不为0就随着窗口增大进行拉伸，0到1之间
           	s.weighty = 0;// 该方法设置组件垂直的拉伸幅度，如果为0就说明不拉伸，不为0就随着窗口增大进行拉伸，0到1之间
           	layout.setConstraints(oldNametextArea, s);// 设置组件
            northPanel.add(oldNametextArea);
            oldNametextArea.setLineWrap(true);
            
            JLabel newJLabel=new JLabel("该更为:");
            newJLabel.setPreferredSize(new Dimension(150, 25));
       		s.gridwidth = 1;// 该方法是设置组件水平所占用的格子数，如果为0，就说明该组件是该行的最后一个
           	s.weightx = 0;// 该方法设置组件水平的拉伸幅度，如果为0就说明不拉伸，不为0就随着窗口增大进行拉伸，0到1之间
           	s.weighty = 0;// 该方法设置组件垂直的拉伸幅度，如果为0就说明不拉伸，不为0就随着窗口增大进行拉伸，0到1之间
           	layout.setConstraints(newJLabel, s);// 设置组件
            northPanel.add(newJLabel);
            
            JTextArea newNametextArea=reNamePaneBean.getNewNametextArea();
            newNametextArea.setPreferredSize(new Dimension(150, 25));
       		s.gridwidth = 1;// 该方法是设置组件水平所占用的格子数，如果为0，就说明该组件是该行的最后一个
           	s.weightx = 0;// 该方法设置组件水平的拉伸幅度，如果为0就说明不拉伸，不为0就随着窗口增大进行拉伸，0到1之间
           	s.weighty = 0;// 该方法设置组件垂直的拉伸幅度，如果为0就说明不拉伸，不为0就随着窗口增大进行拉伸，0到1之间
           	layout.setConstraints(newNametextArea, s);// 设置组件
            northPanel.add(newNametextArea);
            newNametextArea.setLineWrap(true);
            
            JButton deleteButton=new JButton("删除");
            deleteButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                	deleteReNamePaneBean(key);
                }
				
            });
            deleteButton.setPreferredSize(new Dimension(150, 25));
       		s.gridwidth = 0;// 该方法是设置组件水平所占用的格子数，如果为0，就说明该组件是该行的最后一个
           	s.weightx = 0;// 该方法设置组件水平的拉伸幅度，如果为0就说明不拉伸，不为0就随着窗口增大进行拉伸，0到1之间
           	s.weighty = 0;// 该方法设置组件垂直的拉伸幅度，如果为0就说明不拉伸，不为0就随着窗口增大进行拉伸，0到1之间
           	layout.setConstraints(deleteButton, s);// 设置组件
            northPanel.add(deleteButton);
        }
        northPanel.setLayout(layout);
        northPanel.updateUI();
	}
	
	private void loadCenterPathPanel() {
		centerPathPanel.removeAll();
		GridBagLayout layout = new GridBagLayout();
		GridBagConstraints s = new GridBagConstraints();
		s.fill = GridBagConstraints.HORIZONTAL;
		int ordeyNumber=0;//第几个
        for(String key:pathPaneBeanMap.keySet()){
        	
        	PathPaneBean pathPaneBean=pathPaneBeanMap.get(key);
        	ordeyNumber++;
        	
        	JLabel oldPathLabel=new JLabel("第"+ordeyNumber+"个原路径:");
        	oldPathLabel.setPreferredSize(new Dimension(150, 20));
        	oldPathLabel.setForeground(Color.red);
        	centerPathPanel.add(oldPathLabel);
        	
        	s.gridwidth = 1;// 该方法是设置组件水平所占用的格子数，如果为0，就说明该组件是该行的最后一个
        	s.weightx = 0;// 该方法设置组件水平的拉伸幅度，如果为0就说明不拉伸，不为0就随着窗口增大进行拉伸，0到1之间
        	s.weighty = 0;// 该方法设置组件垂直的拉伸幅度，如果为0就说明不拉伸，不为0就随着窗口增大进行拉伸，0到1之间
        	layout.setConstraints(oldPathLabel, s);// 设置组件
        	
        	centerPathPanel.add(pathPaneBean.getOldPathNametextArea());
        	pathPaneBean.getOldPathNametextArea().setLineWrap(true);
        	s.gridwidth = 0;// 该方法是设置组件水平所占用的格子数，如果为0，就说明该组件是该行的最后一个
        	s.weightx = 1;// 该方法设置组件水平的拉伸幅度，如果为0就说明不拉伸，不为0就随着窗口增大进行拉伸，0到1之间
        	s.weighty = 0;// 该方法设置组件垂直的拉伸幅度，如果为0就说明不拉伸，不为0就随着窗口增大进行拉伸，0到1之间
        	layout.setConstraints(pathPaneBean.getOldPathNametextArea(), s);// 设置组件
        	
        	JLabel newPathLabel=new JLabel("复制到:");
        	newPathLabel.setPreferredSize(new Dimension(150, 20));
        	s.gridwidth = 1;// 该方法是设置组件水平所占用的格子数，如果为0，就说明该组件是该行的最后一个
        	s.weightx = 0;// 该方法设置组件水平的拉伸幅度，如果为0就说明不拉伸，不为0就随着窗口增大进行拉伸，0到1之间
        	s.weighty = 0;// 该方法设置组件垂直的拉伸幅度，如果为0就说明不拉伸，不为0就随着窗口增大进行拉伸，0到1之间
        	layout.setConstraints(newPathLabel, s);// 设置组件
        	
            centerPathPanel.add(newPathLabel);
            centerPathPanel.add(pathPaneBean.getNewPathNametextArea());
            pathPaneBean.getNewPathNametextArea().setLineWrap(true);
            s.gridwidth = 0;// 该方法是设置组件水平所占用的格子数，如果为0，就说明该组件是该行的最后一个
        	s.weightx = 1;// 该方法设置组件水平的拉伸幅度，如果为0就说明不拉伸，不为0就随着窗口增大进行拉伸，0到1之间
        	s.weighty = 0;// 该方法设置组件垂直的拉伸幅度，如果为0就说明不拉伸，不为0就随着窗口增大进行拉伸，0到1之间
        	layout.setConstraints(pathPaneBean.getNewPathNametextArea(), s);// 设置组件
        	//分割线
        	JPanel tmpPanel=	new JPanel();
        	tmpPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.GRAY));
        	centerPathPanel.add(tmpPanel);
        	s.gridwidth = 0;// 该方法是设置组件水平所占用的格子数，如果为0，就说明该组件是该行的最后一个
        	s.weightx = 1;// 该方法设置组件水平的拉伸幅度，如果为0就说明不拉伸，不为0就随着窗口增大进行拉伸，0到1之间
        	s.weighty = 0;// 该方法设置组件垂直的拉伸幅度，如果为0就说明不拉伸，不为0就随着窗口增大进行拉伸，0到1之间
        	layout.setConstraints(tmpPanel, s);// 设置组件
            
        }
        centerPathPanel.setLayout(layout);
//        centerPathPanel.setLayout(new GridLayout(pathPaneBeanMap.size()*2,2));
        centerPathPanel.updateUI();
	}
	//删除替换字段方法
	private void deleteReNamePaneBean(Integer key) {
		reNamePaneBeanMap.remove(key);
		loadNorthPanel();
		
	}
	//删除替换字段方法
		public void deletePathPaneBean(String key) {
			pathPaneBeanMap.remove(key);
			loadCenterPathPanel();
			
		}
    // 递归遍历目录树
    private void walkTree(File dir, int level,CheckBoxTreeNode parentNode) {
        // 1. current dir path
        // 2. docs path that located in this dir
        // 3. sub dirs path
    	if(parentNode==null){return;}
    	if (stopped) { return; }
    	//如果文件夹其下有且只有一个文件夹，就合并名，用.连接（仿idea等IDE）
    	 File[] dirFile= dir.listFiles(dirFilter);
    	 File[] docFile= dir.listFiles(docFilter);
    	if(dirFile.length==1&&docFile.length==0){
    		CheckBoxTreeNode parentFatherNode=	(pathTree.CheckBoxTreeNode) parentNode.getParent();
    		File thisFile=	dirFile[0];
    		CheckBoxTreeNode CheckBoxTreeNode=new CheckBoxTreeNode(
    				new File(parentNode.toString()+"."+	thisFile.getName()),mainJPanel);
    		parentFatherNode.add(CheckBoxTreeNode);
    		parentFatherNode.remove(parentNode);
    		 walkTree(thisFile, level + 1,CheckBoxTreeNode);
    		 return;
    	}
    	
    	
    	CheckBoxTreeNode childrenNode=null;

        // 如果不显示隐藏文件，删除本身并 返回
        if (dir.isHidden() && !showHiddenFilesCheckbox.isSelected()) { 
        	CheckBoxTreeNode parentFatherNode=	(pathTree.CheckBoxTreeNode) parentNode.getParent();
        	parentFatherNode.remove(parentNode);
        	return; }


        // 访问文档
        for (File doc : docFile) {
            if (doc.isHidden() && !showHiddenFilesCheckbox.isSelected()) {
                continue;
            }
            
            childrenNode = new CheckBoxTreeNode(doc,mainJPanel); 
            parentNode.add(childrenNode);
        }

       
      
        // 递归遍历子目录
        for (File subDir :dirFile) {
        	childrenNode = new CheckBoxTreeNode(subDir,mainJPanel); 
            parentNode.add(childrenNode);
            walkTree(subDir, level + 1,childrenNode);
        }
    }

    // 创建主窗口
    public static void createGUIAndShow() {
        JFrame frame = new JFrame("目录结构树");
        Dimension ss = Toolkit.getDefaultToolkit().getScreenSize();
        int w = 1000;
        int h = 700;
        int x = (ss.width - w) / 2;
        int y = (ss.height - h) / 2 - 40;
        x = x > 0 ? x : 0;
        y = y > 0 ? y : 0;
        frame.setBounds(x, y, w, h);
        frame.setContentPane(new MainJPanel());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
        frame.setVisible(true);
    }

    public void readFileToTextArea(String file){
    	textArea.setText(FileUtils.read(file,CharsetName)); 
    }
 
}

class DocFilter implements FileFilter {
    @Override
    public boolean accept(File file) {
        return file.isFile();
    }
}

class DirFilter implements FileFilter {
    @Override
    public boolean accept(File file) {
        return file.isDirectory();
    }
}