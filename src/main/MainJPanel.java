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
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultTreeModel;

import pathTree.CheckBoxTreeCellRenderer;
import pathTree.CheckBoxTreeNode;
import pathTree.CheckBoxTreeNodeSelectionListener;
import Utils.FileUtils;
import bean.PathPaneBean;
import bean.ReNamePaneBean;

public class MainJPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    private JButton stopButton = new JButton("停止扫描（未开始）");
    private JButton browseButton = new JButton("选择文件夹");
    private JButton addReNameButton = new JButton("添加替换");
    private JCheckBox showHiddenFilesCheckbox = new JCheckBox("显示隐藏文件", false);
    private  JPanel northPanel = new JPanel();
    private  JPanel centerPanel = new JPanel();
    private  JPanel centerPathPanel = new JPanel();
    private  JPanel centerButtonPanel = new JPanel();
//    private Map<Integer, String> pathIndexes = new HashMap<Integer, String>();
    private FileFilter docFilter = new DocFilter(); // 文档过滤器
    private FileFilter dirFilter = new DirFilter(); // 文件夹过滤器
    private boolean stopped = false; // 是否停止扫描的标志
    private JTextArea oldfileNametextArea=new JTextArea();//旧文件名Po
    private JTextArea newfileNametextArea=new JTextArea();//新文件名Po
    public  JLabel oldfileNameLabel =new JLabel("旧文件Po名:");  
    public  JLabel newfileNameLabel =new JLabel("新文件Po名:");  
    public JScrollPane jTreescroll;//树形区域
    public JTextArea textArea;//输入区域
    public Map<Integer,ReNamePaneBean> reNameSaveMap=new LinkedHashMap<>();//保存reName对象 Map
    public Map<String,PathPaneBean> pathPaneBeanMap=new LinkedHashMap<>();//保存path文件路径对象 Map
    private JButton replaceOldButton = new JButton("原文替换");
    private JButton copyAndReplaceButton = new JButton("复制并且替换");
    
    
    private MainJPanel mainJPanel;
    public MainJPanel() {
        initGui();
        mainJPanel=this;
    }

    // 初始化界面
    private void initGui() {
        this.setLayout(new BorderLayout());

        loadNorthPanel();
        this.add(northPanel, BorderLayout.NORTH);

        
        centerPanel.setLayout(new BorderLayout());
        centerPanel.add(centerButtonPanel,BorderLayout.SOUTH);
        centerButtonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));  
        centerButtonPanel.add(replaceOldButton);
        centerButtonPanel.add(copyAndReplaceButton);
        
        
        JScrollPane pane = new JScrollPane(centerPathPanel);
        pane.setHorizontalScrollBarPolicy (JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        pane.setVerticalScrollBarPolicy (JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        
        centerPanel.add(pane,BorderLayout.CENTER);
        loadCenterPathPanel();
        this.add(centerPanel, BorderLayout.CENTER);

        
        //西边
        JTree tree = new JTree();  
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
                          CheckBoxTreeNode rootNode = new CheckBoxTreeNode(dir.toString(),mainJPanel);  
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
            	  for(Integer key:reNameSaveMap.keySet()){
            		  MaxKey=key;
            	  }
            		reNameSaveMap.put(MaxKey+1,new ReNamePaneBean());
            		loadNorthPanel();
            }
        });
    }

    public void putPathPaneBean(String oldFilePathName,JTextArea oldPathNametextArea,
			JTextArea newPathNametextArea) {

    	  pathPaneBeanMap.put(oldFilePathName,new PathPaneBean(oldPathNametextArea,newPathNametextArea));
    		loadCenterPathPanel();
	}
	private void loadNorthPanel() {
		northPanel.removeAll();
		northPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.GRAY));
        northPanel.add(showHiddenFilesCheckbox);
        northPanel.add(browseButton);
        northPanel.add(stopButton);
        northPanel.add(addReNameButton);
        northPanel.add(new JLabel());
        
        northPanel.add(oldfileNameLabel);
        northPanel.add(oldfileNametextArea);
        oldfileNametextArea.setLineWrap(true);
        northPanel.add(newfileNameLabel);
        northPanel.add(newfileNametextArea);
        northPanel.add(new JLabel());
        newfileNametextArea.setLineWrap(true);
        for(Integer key:reNameSaveMap.keySet()){
        	System.out.println(key);
        	ReNamePaneBean reNamePaneBean=reNameSaveMap.get(key);
        	northPanel.add(new JLabel("原字段:"));
            northPanel.add(reNamePaneBean.getOldNametextArea());
            reNamePaneBean.getOldNametextArea().setLineWrap(true);
            northPanel.add(new JLabel("该更为:"));
            northPanel.add(reNamePaneBean.getNewNametextArea());
            reNamePaneBean.getNewNametextArea().setLineWrap(true);
            JButton deleteButton=new JButton("删除");
            deleteButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                	deleteReNamePaneBean(key);
                }

				
            });
            northPanel.add(deleteButton);
        }
        northPanel.setLayout(new GridLayout(2+reNameSaveMap.size(),5));
        northPanel.updateUI();
	}
	
	private void loadCenterPathPanel() {
		centerPathPanel.removeAll();
		GridBagLayout layout = new GridBagLayout();
		GridBagConstraints s = new GridBagConstraints();
		s.fill = GridBagConstraints.HORIZONTAL;
		
        for(String key:pathPaneBeanMap.keySet()){
        	
        	PathPaneBean pathPaneBean=pathPaneBeanMap.get(key);
        	
        	
        	JLabel oldPathLabel=new JLabel("原路径:");
        	oldPathLabel.setPreferredSize(new Dimension(50, 20));
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
        	newPathLabel.setPreferredSize(new Dimension(50, 20));
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
		reNameSaveMap.remove(key);
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
    	CheckBoxTreeNode childrenNode=null;
        if (stopped) { return; }

        // 如果不显示隐藏文件，则返回
        if (dir.isHidden() && !showHiddenFilesCheckbox.isSelected()) { return; }

//        final StringBuilder pathBuffer = new StringBuilder(1024);

        // 访问当前目录
//        pathBuffer.append(createPath(dir, level));
//         childrenNode = new CheckBoxTreeNode(); 

        // 访问文档
        for (File doc : dir.listFiles(docFilter)) {
            if (doc.isHidden() && !showHiddenFilesCheckbox.isSelected()) {
                continue;
            }
//            pathBuffer.append(createPath(doc, level + 1));
            
            childrenNode = new CheckBoxTreeNode(doc,mainJPanel); 
            parentNode.add(childrenNode);
        }

       

        // 递归遍历子目录
        for (File subDir : dir.listFiles(dirFilter)) {
        	childrenNode = new CheckBoxTreeNode(subDir,mainJPanel); 
            parentNode.add(childrenNode);
            walkTree(subDir, level + 1,childrenNode);
        }
    }

   /* // 创建文件的路径
    public String createPath(File file, int level) {
        StringBuilder pathBuffer = new StringBuilder(128);
        pathBuffer.append(getPathIndex(level)).append(file.getName()).append("\n");

        return pathBuffer.toString();
    }*/

   /* // 创建目录的缩进
    private String getPathIndex(int level) {
        // 如果不存在，则创建
        if (pathIndexes.get(level) == null) {
            StringBuilder indexBuffer = new StringBuilder(128);
            for (int i = 0; i < level; ++i) {
                indexBuffer.append("|        ");
            }

            indexBuffer.append("|----");
            pathIndexes.put(Integer.valueOf(level), indexBuffer.toString());
            return indexBuffer.toString();
        } else {
            return pathIndexes.get(level);
        }
    }*/

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
    	textArea.setText(FileUtils.read(file,"UTF-8")); //"GBK", "gb2312"));
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