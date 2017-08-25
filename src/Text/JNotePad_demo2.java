package Text;

import javax.swing.*;
import javax.xml.soap.Text;
import java.awt.*;
import java.awt.event.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by butter on 16-11-21.
 */


/**
 *
 * swing开发基本步骤：
 * （1）继承JFrame
 * （2）定义需要的组件
 * （3）创建组件
 * （4）添加组件
 * （5）对（顶层）窗体设置
 * （6）设置显示
 */

public class JNotePad_demo2 extends JFrame{


    private JMenuBar        menuBar;

    private JMenu fileMenu;
        private JMenuItem       menuOpen;
        private JMenuItem       menuSave;
        private JMenuItem       menuSaveAs;
        private JMenuItem       menuClose;

    private JMenu editMenu;
        private JMenuItem       menuCut;
        private JMenuItem       menuCopy;
        private JMenuItem       menuPast;

    private JMenu aboutMenu;
        private JMenuItem       menuAbout;

    private JTextArea textArea;//输入区域
    private JLabel    stateBar;//状态条

    private TextDAO textDAO;//保存
    private JFileChooser fileChooser;//文件选择器
    private JPopupMenu popUpMeue; //鼠标点击Menu事件



    public JNotePad_demo2(){
        initComponents();//初始组件外观
        initEventListeners();//初始化组件事件倾听器
    }

    private void initComponents(){
        setTitle("新增纯文本文档");
        setSize(400, 300);
        initMenu();
        initTextArea();
        initStateBar();
        popUpMeue = editMenu.getPopupMenu();
        fileChooser = new JFileChooser();
    }


/*----------------初始化Menu---------------------*/
    private void initMenu() {
    initFileMenu();
    initEditMenu();
    initAboutMenu();
    initMenuBar();
}
    private void initMenuBar() {
        //构造菜单列
        menuBar = new JMenuBar();
        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        menuBar.add(aboutMenu);

        //设置菜单列
        setJMenuBar(menuBar);
    }
    private void initAboutMenu() {
        aboutMenu = new JMenu("关于");
        menuAbout = new JMenuItem("关于JNotePad");

        aboutMenu.add(menuAbout);
    }
    private void initEditMenu() {
        editMenu = new JMenu("编辑");
        menuCut  = new JMenuItem("剪切");
        menuCopy = new JMenuItem("复制");
        menuPast = new JMenuItem("粘贴");

        editMenu.add(menuCut);
        editMenu.add(menuCopy);
        editMenu.add(menuPast);
    }
    private void initFileMenu() {
        fileMenu = new JMenu("文件");

        menuOpen   = new JMenuItem("打开");
        menuSave   = new JMenuItem("保存");
        menuSaveAs = new JMenuItem("另存为");
        menuClose  = new JMenuItem("关闭");

        fileMenu.add(menuOpen);
        fileMenu.addSeparator(); //分割线;
        fileMenu.add(menuSave);
        fileMenu.add(menuSaveAs);
        fileMenu.addSeparator(); //分割线;
        fileMenu.add(menuClose);
    }
/*-----------------------------------------------*/

    //初始化事件监视器
    private void initEventListeners(){
        //setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);设置点X关闭
        initAccelerator();
        //下面要设置,点X提示"文档已改变,是否保存"

        //按下窗口关闭按钮事件处理:
        addWindowListener(
                new WindowAdapter() {
                    public void windowClosing(WindowEvent event){
                        closeWindow(event);
                    }
                }
        );

        initMenuListener();//初始化菜单的点击事件

        //编辑区键盘事件:
        textArea.addKeyListener(
                new KeyAdapter() {
                    public void keyTyped(KeyEvent event){
                        jtexAreaActionPerformed(event);
                    }
                }
        );

        //编辑区鼠标事件:
        textArea.addMouseListener(
                new MouseAdapter() {
                    @Override
                    public void mouseReleased(MouseEvent mouseEvent) {
                        if(mouseEvent.getButton() == MouseEvent.BUTTON3){//3:右键
                            popUpMeue.show(editMenu, mouseEvent.getX(), mouseEvent.getY());
                        }
                    }
                    public void mouseClicked(MouseEvent e){
                        if(e.getButton() == MouseEvent.BUTTON1){
                            popUpMeue.setVisible(false);
                        }
                    }
                }
        );
    }


    private void initMenuListener() {
        menuOpen.addActionListener(this::openFile);
        menuSave.addActionListener(this::saveFile);
        menuSaveAs.addActionListener(this::saveFileAs);
        menuClose.addActionListener(this::closeFile);
        menuCut.addActionListener(this::cut);
        menuPast.addActionListener(this::past);
        menuAbout.addActionListener(event -> {//关于  弹窗
            JOptionPane.showOptionDialog(null,
                    "JNotePad 0.1\n来自 http://www.dubutter.com",
                    "关于JNotePad",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.INFORMATION_MESSAGE,
                    null, null, null);
        });
    }

    private void past(ActionEvent event) {

    }

    private void cut(ActionEvent event) {

    }


    private void saveFileAs(ActionEvent event) {
        int option = fileChooser.showDialog(null, null);
        if(option == JFileChooser.APPROVE_OPTION){
            //在标题栏设定文件名
            setTitle(fileChooser.getSelectedFile().toString());
            textDAO.create(fileChooser.getSelectedFile().toString());
            saveFile(event);
        }
    }

    private void saveFile(ActionEvent event) {
        Path path = Paths.get(getTitle());
        if(Files.notExists(path)){
            saveFileAs(event);
        }else{
            try{
                textDAO.save(path.toString(), textArea.getText());
                stateBar.setText("未修改");
            }catch(Throwable e){
                JOptionPane.showMessageDialog(null, e.toString(),
                        "写入失败", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    private void closeFile(ActionEvent event) {
        if(stateBar.getText().equals("未修改")){
            dispose();//释放窗口资源,关闭程序
        }else{
            int option = JOptionPane.showConfirmDialog(null,
                    "文档已修改,是否保存:",
                    "保存?",JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);
            switch (option){
                case JOptionPane.YES_OPTION:
                    saveFile(event);
                    break;
                case JOptionPane.NO_OPTION:
                    dispose();
            }
        }
    }


    private void openFile(ActionEvent event) {
        if(stateBar.getText().equals("未修改")){
            showFileDialog();
        } else{
            int option = JOptionPane.showConfirmDialog(
                    null, "已修改,是否保存?", "保存", JOptionPane.WARNING_MESSAGE, Integer.parseInt(null)
            );
            switch (option){
                case JOptionPane.YES_OPTION:
                    saveFile();
                    break;
                case JOptionPane.NO_OPTION:
                    showFileDialog();
                    break;
                default:
                    break;
            }
        }

    }

    private void jtexAreaActionPerformed(KeyEvent event) {
        stateBar.setText("已修改");
    }


    //关闭窗口,并提示是否表存
    private void closeWindow(WindowEvent event) {
        closeFile(new ActionEvent(
                event.getSource(), event.getID(), "windowClosing"));
    }

    //设置快捷键
    private void initAccelerator() {
        //设置快捷键,,略略略
        menuCopy.setAccelerator(
                KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_MASK)
        );
        menuPast.setAccelerator(
                KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_MASK)
        );
    }
    //设置文本区域
    private void initTextArea(){
        textArea = new JTextArea();
        textArea.setFont(new Font("细明体", Font.PLAIN, 16));
        textArea.setLineWrap(true);
        JScrollPane panel = new JScrollPane(textArea,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        getContentPane().add(panel, BorderLayout.CENTER);
    }

    //初始化状态栏(最下方)
    private void initStateBar() {
        stateBar = new JLabel("未修改");
        stateBar.setHorizontalAlignment(SwingConstants.LEFT);
        stateBar.setBorder(BorderFactory.createEtchedBorder());
        getContentPane().add(stateBar, BorderLayout.SOUTH);
    }

    public JNotePad_demo2(TextDAO textDAO){
        this();
        this.textDAO = textDAO;
    }

    //打开
    private void openFile(){
        if(stateBar.getText().equals("未修改")){
            showFileDialog();
        } else{
            int option = JOptionPane.showConfirmDialog(
                    null, "已修改,是否保存?", "保存", JOptionPane.WARNING_MESSAGE, Integer.parseInt(null)
            );
            switch (option){
                case JOptionPane.YES_OPTION:
                    saveFile();
                    break;
                case JOptionPane.NO_OPTION:
                    showFileDialog();
                    break;
                default:
                    break;
            }
        }
    }
    //保存
    private void saveFile() {
    }


    //输出问文件信息
    private void showFileDialog() {
        int option = fileChooser.showDialog(null, null);//文档选取对话框

        if(option == JFileChooser.APPROVE_OPTION){

            try {
                setTitle(fileChooser.getSelectedFile().toString());
                textArea.setText("");
                textArea.setText("未修改");
                String text = textDAO.read(fileChooser.getSelectedFile().toString());
                //乱码
                String textUtf8=new String( text.getBytes("GBK"),"gb2312");
                textArea.setText(textUtf8);
            } catch (Throwable e) {
                JOptionPane.showMessageDialog(null, e.toString(), "打开文档失败", JOptionPane.ERROR_MESSAGE);
            }
        }

    }
    //测试函数
    public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {
        //将建立的JNotePad实例与SetVisible()的动作排入事件队列
        //这玩意应该是一个Runnable接口的实现
        UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
        SwingUtilities.invokeLater( ()->{
            new JNotePad_demo2(new FileTextDAO()).setVisible(true);//true显示,false隐藏
        });
    }
}