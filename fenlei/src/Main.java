
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Iterator;

public class Main extends JFrame {
    public static JTextArea ja=new JTextArea(14,41);
    public static JScrollPane jscrollPane= new JScrollPane(ja);;
    public static SimpleDateFormat sdf;
    public static Calendar cal = Calendar.getInstance();
    public static String begin;
    public static String rootName;
    public int c_wjjs=0;//创建文件夹数量
    public int c_wjs=0;//复制文件数量

    public Main(boolean b) {
        super("文件分类");
        if(!b) return ;
        this.setLayout(new FlowLayout());

        ja.setLineWrap(true);
        this.add(jscrollPane);

        this.setBackground(Color.BLACK);
        this.setSize(new Dimension(500, 300));
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public static void main(String[] args) {
        JFrame.setDefaultLookAndFeelDecorated(true);
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                Window w = new Main(true);
                w.setVisible(true);
            }
        });

        Main m=new Main(false);

        ja.setText(ja.getText()+"时间："+cal.getTime().toLocaleString());

        boolean re=m.getPublic();
        if(!re)
            return;

        ja.setText(ja.getText()+"\n\n开始对 “"+begin+"” 目录下的文件进行分类。请稍候！！！");

        File f = new File(begin);
        m.getFile(f.listFiles());

        ja.setText(ja.getText()+"\n\n\n\n分类完成,创建文件夹："+m.c_wjjs+" 个,分类文件："+m.c_wjs+" 个.\n\n\n\n\n\n\n");

    }


    //读取配置文件xml
    public boolean getPublic(){
        boolean re=false;
        ja.setText(ja.getText()+"\n\n开始解析xml配置文件");
        try {
            InputStream is=this.getClass().getResourceAsStream("/public/pu.xml");
            SAXReader sr = new SAXReader();
            Document doc  =  sr.read(is);
            Element root = doc.getRootElement();
            Element a_e=root.element("a");
            for (Iterator nodes = a_e.elementIterator(); nodes.hasNext();) {
                Element node = (Element) nodes.next();
                if("type".equals(node.getName().toString())){
                    String type="yyyyMMdd";
                    if("年".equals(node.getText().toString())){
                        type="yyyy";
                    }else if("月".equals(node.getText().toString())){
                        type="yyyyMM";
                    }
                    sdf=new SimpleDateFormat(type);

                }else if("begin".equals(node.getName().toString())){
                    begin=node.getText().toString();
                }else if("root".equals(node.getName().toString())){
                    rootName=node.getText().toString();
                }
            }
            re=true;
            ja.setText(ja.getText()+"\n\n解析xml成功");
        } catch (Exception e) {
            ja.setText(ja.getText()+"\n\n解析xml失败");
            e.printStackTrace();
        }
        return re;
    }

    //读取并复制照片
    public void getFile(File[] fs){
        if(fs==null || fs.length<=0) {
            ja.setText(ja.getText()+"\n\n“"+begin+"” 目录不存在或该目录下没有找到需要分类的文件");
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
                    ja.setText(ja.getText()+"\n复制文件到："+name);
                    int height=20;
                    Point p = new Point();
                    p.setLocation(0,this.ja.getLineCount()*height);
                    jscrollPane.getViewport().setViewPosition(p);
                    c_wjs++;
                }
            }
        } catch (IOException e) {
            ja.setText(ja.getText()+"\n\n分类出错");
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
                ja.setText(ja.getText()+"\n\n创建跟目录："+rootName);
                c_wjjs++;
            }
            File fl2=new File(name);
            if(!fl2.exists()){//判断文件夹是否存在
                fl2.mkdirs();
                ja.setText(ja.getText()+"\n\n创建目录："+name+"\n");
                c_wjjs++;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

}
