
import com.sun.awt.AWTUtilities;
import com.sun.deploy.util.StringUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Iterator;

public class Main extends JFrame {
    public static JTextArea ja=new JTextArea(13,42);
    public static JScrollPane jscrollPane= new JScrollPane(ja);//使文本域内容超出时显示滚动条
    public static SimpleDateFormat sdf;
    public static Calendar cal = Calendar.getInstance();
    public static String begin;//
    public static String rootName;
    public static String f_type;
    public int c_wjjs=0;//创建文件夹数量
    public int c_wjs=0;//复制文件数量

    public static JTextField begin_v = new JTextField("sfsd",36);
    public static JTextField over_v = new JTextField("werwe",36);
    public static JComboBox sel_v=new JComboBox();

    public Main(boolean b) {
        super("文件分类");
        if(!b) return ;
        FlowLayout layout = new FlowLayout();
        //
        Font font = new Font("宋体",Font.PLAIN,12);

        //第一行
        JLabel begin_k = new JLabel("需求目录：",SwingUtilities.RIGHT);
        begin_k.setFont(font);

        JPanel jp_one = new JPanel();
        jp_one.setLayout(layout);
        jp_one.setBorder(new EmptyBorder(0,5,0,5));//上、左、下、右。创建具有指定插入一个空的边界。
        jp_one.add(begin_k);
        jp_one.add(begin_v);
        //jp_one.setBorder(BorderFactory.createEmptyBorder(20,20,10,20));

        //第二行
        JLabel over_k = new JLabel("结果目录：",SwingUtilities.RIGHT);
        over_k.setFont(font);

        JPanel jp_two = new JPanel();
        jp_two.setLayout(layout);
        jp_two.setBorder(new EmptyBorder(0,5,0,5));
        jp_two.add(over_k);
        jp_two.add(over_v);


        //第三行
        JLabel sel_k = new JLabel("分类类型：",SwingUtilities.RIGHT);
        sel_k.setFont(font);
        sel_v.addItem("按天分类");
        sel_v.addItem("按月分类");
        sel_v.addItem("按年分类");

        //按钮
        JButton jb = new JButton("分类");
        jb.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jb_click();
            }
        });

        JLabel kongge = new JLabel(" ");
        kongge.setBorder(new EmptyBorder(0,242,0,5));

        JPanel jp_three = new JPanel();
        jp_three.setLayout(layout);
        jp_two.setBorder(new EmptyBorder(0,5,0,5));
        jp_three.add(sel_k);
        jp_three.add(sel_v);
        jp_three.add(kongge);
        jp_three.add(jb);

        JPanel jp = new JPanel();
        jp.setLayout(new GridLayout(3,1));
        jp.add(jp_one);
        jp.add(jp_two);
        jp.add(jp_three);

        JPanel j = new JPanel();
        j.setLayout(layout);
        j.setBorder(new EmptyBorder(5,5,5,5));
        j.add(jp);
        j.add(jscrollPane);



        this.setLayout(new GridLayout(1,1));

        ja.setLineWrap(true);//自动换行
        ja.setEnabled(false);//文本框只读
        this.add(j);

        //背景色，不能直接设置在jFrame,因为jFrame存在内容面板，应该将颜色设置在内容面板上
        //this.getContentPane()就是获取内容面板
        //this.getContentPane().setBackground(Color.LIGHT_GRAY);
        //this.setResizable(false);
        this.setSize(new Dimension(500, 400));//窗体大小
        this.setLocationRelativeTo(null);//窗体在屏幕居中
        this.setUndecorated(true);//窗体标题栏是否显示
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//窗体是否显示关闭按钮
        this.setResizable(false);//窗体大小是否可变
        //AWTUtilities.setWindowOpaque(this, false);//窗体完全透明，其中的控件不透明
        //AWTUtilities.setWindowOpacity(this, Float.parseFloat("0.8"));//全体按比例透明
    }

    public static void main(String[] args) {
        JFrame.setDefaultLookAndFeelDecorated(true);
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                Window w = new Main(true);
                w.setVisible(true);
                Main m = new Main(false);

                boolean re=m.getPublic();
                if(!re)
                    return;
            }
        });

    }

    //按钮单击时间
    public void jb_click(){
        ja.setText("");
        String beginV=begin_v.getText().toString();
        String overV=over_v.getText().toString();
        String selV=sel_v.getSelectedItem().toString();
        if("".equals(beginV) || beginV==null){
            setText_ja("\n请输入需求目录");
            return;
        }else if("".equals(overV) || overV==null){
            setText_ja("\n请输入结果目录");
            return;
        }
        begin=beginV;
        rootName=overV;
        if(selV.contains("天")){
            f_type="yyyyMMdd";
        }else if(selV.contains("月")){
            f_type="yyyyMM";
        }else if(selV.contains("年")){
            f_type="yyyy";
        }
        begin_fl();
    }

    //开始分类
    public void begin_fl(){
        setText_ja("\n\n当前时间："+cal.getTime().toLocaleString());

        setText_ja("\n\n开始对 “"+begin+"” 目录下的文件进行分类。请稍候！！！");

        sdf=new SimpleDateFormat(f_type);

        File f = new File(begin);
        getFile(f.listFiles());

        if(c_wjs>0)
            setText_ja("\n\n分类完成请到“"+rootName+"”目录下查看分类结果！\n创建文件夹："+c_wjjs+" 个,分类文件："+c_wjs+" 个！\n\n\n");

    }

    //读取配置文件xml
    public boolean getPublic(){
        boolean re=false;
        setText_ja("开始解析配置文件！");
        try {
            InputStream is=this.getClass().getResourceAsStream("/public/pu.xml");
            SAXReader sr = new SAXReader();
            Document doc  =  sr.read(is);
            Element root = doc.getRootElement();
            Element a_e=root.element("a");
            for (Iterator nodes = a_e.elementIterator(); nodes.hasNext();) {
                Element node = (Element) nodes.next();
                if("type".equals(node.getName().toString())){
                    f_type="yyyyMMdd";
                    if("年".equals(node.getText().toString())){
                        f_type="yyyy";
                    }else if("月".equals(node.getText().toString())){
                        f_type="yyyyMM";
                    }
                }else if("begin".equals(node.getName().toString())){
                    begin=node.getText().toString();
                }else if("root".equals(node.getName().toString())){
                    rootName=node.getText().toString();
                }
            }
            re=true;
            setText_ja("\n解析成功！请点击“分类”按钮进行分类！");
        } catch (Exception e) {
            setText_ja("\n解析失败,请手动填入相应信息！");
            e.printStackTrace();
        }
        setTxt();
        return re;
    }

    //设置配置信息到窗体
    public void setTxt(){
        begin_v.setText(begin);
        over_v.setText(rootName);
    }

    //读取并复制照片
    public void getFile(File[] fs){
        if(fs==null || fs.length<=0) {
            setText_ja("\n\n“"+begin+"” 目录不存在或该目录下没有找到需要分类的文件");
            return;
        }

        InputStream fi = null;
        FileOutputStream fo = null;

        try {
            for(File file : fs){
                if(file.isDirectory()){//文件夹
                    getFile(file.listFiles());
                }else{
                    fi = new FileInputStream(file);
                    String name=getName(file)+"/"+file.getName().toString();
                    fo = new FileOutputStream(new File(name));

                    byte[] buffer = new byte[1444];
                    int byteread = 0;
                    while ( (byteread = fi.read(buffer)) != -1) {
                        fo.write(buffer, 0, byteread);
                    }
                    setText_ja("\n复制文件到："+name);
                    c_wjs++;
                }
            }
        } catch (IOException e) {
            setText_ja("\n\n分类出错");
            e.printStackTrace();
        } finally {
            try {
                if(fi!=null){
                    fi.close();
                }if(fo!=null){
                    fo.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //根据条件获取文件夹名称
    public String getName(File f){
        String wjjm="";
        String f_name=f.getName().toString().toLowerCase();

        if(f_name.contains("pic_")){
            wjjm=f_name.replaceAll("pic_","").substring(0,8).toString();
        }else{
            cal.setTimeInMillis(f.lastModified());//文件修改日期
            wjjm=sdf.format(cal.getTime()).toString();
        }
        wjjm=rootName+"/"+wjjm;
        boolean re=createFile(wjjm);
        return wjjm;
    }

    //创建文件夹
    public boolean createFile(String name){
        File fl=new File(rootName);
        try {
            if(!fl.exists()){//判断文件夹是否存在
                fl.mkdirs();
                setText_ja("\n\n创建跟目录："+rootName);
                c_wjjs++;
            }
            File fl2=new File(name);
            if(!fl2.exists()){//判断文件夹是否存在
                fl2.mkdirs();
                setText_ja("\n\n创建目录："+name);
                c_wjjs++;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    //打印
    public void setText_ja(String text){
        ja.append(text);//在文本框最后追加文本
        ja.setDisabledTextColor(Color.BLACK);//字体颜色，黑
        ja.setCaretPosition(ja.getText().length());//自动滚动到最底行
    }
}
