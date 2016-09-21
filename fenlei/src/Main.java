
import com.sun.awt.AWTUtilities;
import com.sun.deploy.util.StringUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Iterator;

public class Main {
    public static JFrame jf=new JFrame();
    public static JTextArea ja=new JTextArea();
    public static JScrollPane jscrollPane= new JScrollPane(ja);//使文本域内容超出时显示滚动条
    public static SimpleDateFormat sdf;
    public static Calendar cal = Calendar.getInstance();
    public static String begin;//
    public static String rootName;
    public static String f_type;
    public int c_wjjs=0;//创建文件夹数量
    public int c_wjs=0;//复制文件数量

    public static JTextField begin_v;
    public static JTextField over_v;
    public static JComboBox sel_v;


    public static ImageIcon close,close2;
    public static JLabel jl_c = new JLabel();

    //设置窗体
    public void setFrame() {
        int m_l=25;
        int m_t=15;
        int h=25;
        int t_w=80;
        int t_w2=80*4+50;
        Font font = new Font("黑体",Font.BOLD,13);

        int x=320,x1=x+m_l+t_w-30;
        int y=120;

        //第一行
        JLabel begin_k = new JLabel("需求目录：");
        begin_k.setBounds(x,y,t_w,h);
        begin_k.setFont(font);

        begin_v = new JTextField("");
        begin_v.setBounds(x1,y,t_w2,h);

        //第二行
        y+=m_t+h;

        JLabel over_k = new JLabel("结果目录：");
        over_k.setBounds(x,y,t_w,h);
        over_k.setFont(font);

        over_v = new JTextField("");
        over_v.setBounds(x1,y,t_w2,h);

        //第三行
        y+=m_t+m_l;

        JLabel sel_k = new JLabel("分类类型：");
        sel_k.setFont(font);
        sel_k.setBounds(x,y,t_w,h);

        sel_v=new JComboBox();
        sel_v.addItem("按天分类");
        sel_v.addItem("按月分类");
        sel_v.addItem("按年分类");
        sel_v.setBounds(x1,y,t_w,h);

        //按钮
        x1+=m_l*2+t_w*3;

        JButton jb = new JButton("分类");
        jb.setBounds(x1,y,t_w,h);
        jb.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jb_click();
            }
        });

        y+=m_t+h+30;
        jscrollPane.setBounds(x,y,t_w2-5+t_w,h*8);

        ja.setBackground(Color.lightGray);
        ja.setLineWrap(true);//自动换行
        ja.setEnabled(false);//文本框只读

        try{

            set_close();

            Image img= ImageIO.read(this.getClass().getResourceAsStream("/public/logo.png"));
            jf.setIconImage(img);

            ImageIcon background=new ImageIcon(ImageIO.read(this.getClass().getResourceAsStream("/public/b1.png")));
            JLabel label = new JLabel(background);
            label.setBounds(0, 0, background.getIconWidth(),background.getIconHeight());
            JPanel imagePanel = (JPanel) jf.getContentPane();
            imagePanel.setOpaque(false);
            imagePanel.setLayout(null);
            imagePanel.add(jl_c);
            imagePanel.add(begin_k);
            imagePanel.add(begin_v);
            imagePanel.add(over_k);
            imagePanel.add(over_v);
            imagePanel.add(sel_k);
            imagePanel.add(sel_v);
            imagePanel.add(jb);
            imagePanel.add(jscrollPane);


            jf.getLayeredPane().setLayout(null);
            jf.getLayeredPane().add(label, new Integer(Integer.MIN_VALUE));
            jf.setSize(background.getIconWidth(), background.getIconHeight());
            jf.setResizable(false);
        }catch (Exception e){

        }
        jf.setSize(new Dimension(800, 500));//窗体大小
        jf.setLocationRelativeTo(null);//窗体在屏幕居中
        jf.setUndecorated(true);//窗体标题栏是否隐藏
        jf.setResizable(false);//窗体大小是否可变
        jf.setVisible(true);//窗体是否显示
    }
    //设置关闭按钮
    public void set_close(){
        try {
            close=new ImageIcon(ImageIO.read(this.getClass().getResourceAsStream("/public/c.png")));
            close2=new ImageIcon(ImageIO.read(this.getClass().getResourceAsStream("/public/c2.png")));

            jl_c.setIcon(close);
            jl_c.setBounds(740,10,36,35);
            jl_c.addMouseListener(new MouseListener(){
                public void mouseClicked(MouseEvent e) {
                    // 处理鼠标点击
                }
                public void mouseEntered(MouseEvent e) {
                    // 处理鼠标移入
                    jl_c.setIcon(close2);
                }
                public void mouseExited(MouseEvent e) {
                    // 处理鼠标离开
                    jl_c.setIcon(close);
                }
                public void mousePressed(MouseEvent e) {
                    // 处理鼠标按下
                }
                public void mouseReleased(MouseEvent e) {
                    // 处理鼠标释放
                    gb_click();
                }
            });
        }catch (Exception e){

        }

    }

    //关闭窗体
    public void gb_click(){
        System.exit(0);
    }
    public static void main(String[] args) {
        JFrame.setDefaultLookAndFeelDecorated(true);
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                Main m = new Main();
                m.setFrame();
                boolean re=m.getPublic();
                if(!re)
                    return;
            }
        });

    }

    //按钮单击
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
        c_wjjs=0;
        c_wjs=0;
        begin_fl(selV);
    }

    //开始分类
    public void begin_fl(String type){
        setText_ja("当前时间："+cal.getTime().toLocaleString());

        setText_ja("\n\n 从 “"+begin+"” 到 “"+rootName+"” , "+type+" ！");
        setText_ja("\n开始对分类。请稍候！！！");

        sdf=new SimpleDateFormat(f_type);

        File f = new File(begin);
        getFile(f.listFiles());

        if(c_wjs>0)
            setText_ja("\n\n分类完成！请到“"+rootName+"”目录下查看分类结果！\n创建文件夹："+c_wjjs+" 个,分类文件："+c_wjs+" 个！\n\n\n");

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
                    setText_ja("\n复制成功："+name);
                    c_wjs++;
                }
            }
        } catch (IOException e) {
            setText_ja("\n\n分类出错");
            e.printStackTrace();
        } finally {
            try {
                if(fi!=null){
                    fo.flush();
                    fi.close();
                }if(fo!=null){
                    fo.flush();
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

        //if(f_name.contains("pic_")){
        //    wjjm=f_name.replaceAll("pic_","").substring(0,8).toString();
        //}else{
            cal.setTimeInMillis(f.lastModified());//文件修改日期
            wjjm=sdf.format(cal.getTime()).toString();
        //}
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
