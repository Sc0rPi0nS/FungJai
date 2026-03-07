import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.util.ArrayList;

public class About {
    private JFrame winFrame;
    private JPanel iconPanel;
    private JPanel txtPanel;
    private JScrollPane scrollPane;
    private ArrayList<String> memberList;
    private Font mainFont;
    
    //Icon
    private JLabel itLogo;
    private JLabel kmitlLogo;
    
    //Header
    private JLabel projTitle;
    private JLabel courseName;
    private JLabel teacherName;
    
    //Txt Prepared by
    private JLabel preparedByHeader;
    
    //KMITL
    private JLabel facultyName;
    private JLabel uniName;
    
    //Library
    private JLabel libMainHeader;
    
            //JavaFX
    private JLabel libFxHeader;
    private JLabel libFxUI;
    private JLabel libFxData;
    private JLabel libFxGraphic;
    private JLabel libFxMedia;
    private JLabel libFxAnim;

            //Java Swing
    private JLabel libSwingHeader;
    private JLabel libSwingDesc;

            //Java Standard Libraries
    private JLabel libStdHeader;
    private JLabel libStdCollection;
    private JLabel libStdUUID;
    private JLabel libStdTime;
    private JLabel libStdNetwork;
    private JLabel libStdIO;
    private JLabel libStdUtil;
    
    //Feature
    private JLabel featMainHeader, featDesc;
    private JLabel featHomeTitle, featHome1, featHome2, featHome3, featHome4;
    private JLabel featSongTitle, featSong1, featSong2, featSong3;
    private JLabel featPlayTitle, featPlay1, featPlay2;
    private JLabel featMixTitle, featMix1, featMix2;
    private JLabel featDataTitle, featData1, featData2;
    private JLabel featOopTitle, featOop1, featOop2, featOop3, featOop4;
    
    //Think You
    private JLabel thankYou;
    
    //Constructor
    public About() {
        try {
            mainFont = Font.createFont(Font.TRUETYPE_FONT, new java.io.File("Kanit-Regular.ttf"));
            GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(mainFont);
        } catch (Exception e) {
            mainFont = new Font("Tahoma", Font.PLAIN, 14); 
        }
        //members list
        memberList = new ArrayList<>();
        memberList.add("Chanawat Paenkhong (68070xxx) Group x");
        memberList.add("Phacharaphol Jaroen (68070xxx) Group x");
        memberList.add("Inthuch Thipwet (68070xxx) Group x");
        memberList.add("Thanawit Wanthong (68070xxx) Group x");
        memberList.add("Bunyapol Mekcharoenviwattana (68070xxx) Group x");
        memberList.add("Weerachai Lorpa (68070xxx) Group x");
        memberList.add("Wachirawit Anusunchanang (68070164) Group x");
        memberList.add("Narawit Lueangprasoet (68070xxx) Group x");
        memberList.add("Matawin Chortchuang (68070xxx) Group x");
        
        //window frame
        winFrame = new JFrame("About us - FungJai");
        winFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        winFrame.setSize(478, 500);
        winFrame.setResizable(false);
        winFrame.setLayout(new BorderLayout());
        
        //Icon
        iconPanel = new JPanel();
        iconPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 30, 10));
        
        itLogo = new JLabel("[ IT LOGO ]");
        itLogo.setFont(new Font("Kanit", Font.BOLD, 18));
        
        kmitlLogo = new JLabel("[ KMITL LOGO ]");
        kmitlLogo.setFont(new Font("Kanit", Font.BOLD, 18));
        
        iconPanel.add(itLogo);
        iconPanel.add(kmitlLogo);
        
        //Text Panel Middle
        txtPanel = new JPanel();
        txtPanel.setLayout(new BoxLayout(txtPanel, BoxLayout.Y_AXIS));
        txtPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        //Header
        projTitle = new JLabel("About Project \"FungJai\" Music media platform");
        projTitle.setFont(new Font("Kanit", Font.BOLD, 14));
        projTitle.setAlignmentX(Component.LEFT_ALIGNMENT); // ชิดซ้าย

        courseName = new JLabel("รายวิชา Object Oriented Programming");
        courseName.setFont(new Font("Tahoma", Font.PLAIN, 14));
        courseName.setAlignmentX(Component.LEFT_ALIGNMENT);

        teacherName = new JLabel("เสนอ ผู้ช่วยศาสตราจารย์ ดร. ธราวิเชษฐ์ ธิติอรุณโรจน์");
        teacherName.setFont(new Font("Tahoma", Font.PLAIN, 14));
        teacherName.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        //put Header in Panel
        txtPanel.add(projTitle);
        txtPanel.add(courseName);
        txtPanel.add(teacherName);
        txtPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        
        //Prepared
        preparedByHeader = new JLabel("จัดทำโดย:");
        preparedByHeader.setFont(new Font("Tahoma", Font.BOLD, 14));
        preparedByHeader.setAlignmentX(Component.LEFT_ALIGNMENT);
        txtPanel.add(preparedByHeader);
        
        //Members Panel : build and put in Panel
        for (String name : memberList) {
            JLabel memberLabel = new JLabel("    " + name);
            memberLabel.setFont(new Font("Kanit", Font.PLAIN, 14));
            memberLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            txtPanel.add(memberLabel);
        }
        txtPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        
        //institude
        facultyName = new JLabel("เป็นนักศึกษาคณะเทคโนโลยีสารสนเทศ");
        facultyName.setFont(new Font("Tahoma", Font.PLAIN, 14));
        facultyName.setAlignmentX(Component.LEFT_ALIGNMENT);

        uniName = new JLabel("สถาบันเทคโนโลยีพระจอมเกล้าเจ้าคุณทหารลาดกระบัง");
        uniName.setFont(new Font("Tahoma", Font.PLAIN, 14));
        uniName.setAlignmentX(Component.LEFT_ALIGNMENT);

        txtPanel.add(facultyName);
        txtPanel.add(uniName);
        txtPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        
        //Library
        libMainHeader = createLabel("Libraries :", true, 18f);
        txtPanel.add(libMainHeader);
        txtPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        
                //JavaFX
        libFxHeader = createLabel("1. JavaFX (GUI สมัยใหม่ และมัลติมีเดีย)", true, 14f);
        libFxUI = createLabel("    - โครงสร้าง UI: javafx.application, stage, scene, layout, control", false, 13f);
        libFxData = createLabel("    - จัดการข้อมูล: javafx.collections, beans.property", false, 13f);
        libFxGraphic = createLabel("    - รูปร่าง/สี/กราฟิก: javafx.scene.paint, shape, text, canvas, geometry", false, 13f);
        libFxMedia = createLabel("    - มัลติมีเดีย: javafx.scene.media, image, web", false, 13f);
        libFxAnim = createLabel("    - แอนิเมชัน: javafx.animation, util.Duration", false, 13f);
        
        txtPanel.add(libFxHeader); txtPanel.add(libFxUI); txtPanel.add(libFxData); 
        txtPanel.add(libFxGraphic); txtPanel.add(libFxMedia); txtPanel.add(libFxAnim);
        txtPanel.add(Box.createRigidArea(new Dimension(0, 10)));

                //Java Swing & AWT
        libSwingHeader = createLabel("2. Java Swing และ AWT (GUI สำหรับหน้า About)", true, 14f);
        libSwingDesc = createLabel("    - แพ็กเกจ: javax.swing.*, java.awt.*, javax.swing.border.*", false, 13f);
        
        txtPanel.add(libSwingHeader); txtPanel.add(libSwingDesc);
        txtPanel.add(Box.createRigidArea(new Dimension(0, 10)));

                //Java Standard Libraries
        libStdHeader = createLabel("3. Java Standard Libraries (Backend & Logic)", true, 14f);
        libStdCollection = createLabel("    - Collections: java.util.List, ArrayList, Collections", false, 13f);
        libStdUUID = createLabel("    - สร้างรหัสเฉพาะ (ID): java.util.UUID", false, 13f);
        libStdTime = createLabel("    - จัดการเวลา: java.time.LocalDateTime", false, 13f);
        libStdNetwork = createLabel("    - เครือข่าย/สื่อ: java.net.URL", false, 13f);
        libStdIO = createLabel("    - จัดการไฟล์ (Stream/Serializable): java.io.*", false, 13f);
        libStdUtil = createLabel("    - จัดการข้อผิดพลาด: java.util.Objects", false, 13f);

        txtPanel.add(libStdHeader); txtPanel.add(libStdCollection); txtPanel.add(libStdUUID);
        txtPanel.add(libStdTime); txtPanel.add(libStdNetwork); txtPanel.add(libStdIO); txtPanel.add(libStdUtil);
        txtPanel.add(Box.createRigidArea(new Dimension(0, 30)));
        
        //Feature
        featMainHeader = createLabel("Features (ความสามารถหลักของระบบ) :", true, 18f);
        featDesc = createLabel("แอปจัดการและฟังเพลงที่ออกแบบด้วยแนวคิด OOP", false, 14f);
        txtPanel.add(featMainHeader); txtPanel.add(featDesc);
        txtPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        featHomeTitle = createLabel("🎵 Home (Music Player)", true, 15f);
        featHome1 = createLabel("- ควบคุมการเล่นเพลง Play/Pause, Next, Previous", false, 14f);
        featHome2 = createLabel("- เปิด/ปิดโหมด Loop, Shuffle และปรับ Volume", false, 14f);
        featHome3 = createLabel("- แสดงเนื้อเพลง (Lyrics) ตามช่วงเวลาของเพลง", false, 14f);
        featHome4 = createLabel("- เปิดหน้าต่างอื่นซ้อนได้โดยเพลงไม่สะดุด", false, 14f);
        txtPanel.add(featHomeTitle); txtPanel.add(featHome1); txtPanel.add(featHome2); txtPanel.add(featHome3); txtPanel.add(featHome4);
        txtPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        featSongTitle = createLabel("🎧 My Song (Music Library)", true, 15f);
        featSong1 = createLabel("- คลังเพลงส่วนตัว เพิ่มไฟล์เพลง (.mp4) เข้าสู่ระบบ", false, 14f);
        featSong2 = createLabel("- แก้ไขข้อมูล (ชื่อเพลง, ชื่อศิลปิน) และลบเพลงได้", false, 14f);
        featSong3 = createLabel("- เพิ่มเพลงไปยัง Playlist ที่ต้องการได้", false, 14f);
        txtPanel.add(featSongTitle); txtPanel.add(featSong1); txtPanel.add(featSong2); txtPanel.add(featSong3);
        txtPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        featPlayTitle = createLabel("📁 Playlist Management", true, 15f);
        featPlay1 = createLabel("- สร้าง แก้ไขชื่อ หรือรายละเอียดของ Playlist", false, 14f);
        featPlay2 = createLabel("- เพิ่ม/ลบ และเปิดดูรายการเพลงทั้งหมดใน Playlist ได้", false, 14f);
        txtPanel.add(featPlayTitle); txtPanel.add(featPlay1); txtPanel.add(featPlay2);
        txtPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        featMixTitle = createLabel("🎶 Mix For You", true, 15f);
        featMix1 = createLabel("- ระบบสุ่มเพลงจากคลังเพื่อสร้าง Playlist อัตโนมัติ", false, 14f);
        featMix2 = createLabel("- เพิ่ม Mix ที่ระบบสร้างไปยัง Playlist ส่วนตัวได้", false, 14f);
        txtPanel.add(featMixTitle); txtPanel.add(featMix1); txtPanel.add(featMix2);
        txtPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        featDataTitle = createLabel("💾 Data Storage", true, 15f);
        featData1 = createLabel("- บันทึกข้อมูลทั้งหมดลงไฟล์ด้วย Object Stream", false, 14f);
        featData2 = createLabel("- โหลดข้อมูลกลับมาอัตโนมัติเมื่อเปิดโปรแกรมใหม่", false, 14f);
        txtPanel.add(featDataTitle); txtPanel.add(featData1); txtPanel.add(featData2);
        txtPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        featOopTitle = createLabel("🧩 Object-Oriented Design", true, 15f);
        featOop1 = createLabel("- Class & Object, Encapsulation", false, 14f);
        featOop2 = createLabel("- Inheritance & Polymorphism", false, 14f);
        featOop3 = createLabel("- Interface & Abstract Class", false, 14f);
        featOop4 = createLabel("- Event Handling สำหรับการควบคุมปุ่มในระบบ", false, 14f);
        txtPanel.add(featOopTitle); txtPanel.add(featOop1); txtPanel.add(featOop2); txtPanel.add(featOop3); txtPanel.add(featOop4);
        txtPanel.add(Box.createRigidArea(new Dimension(0, 30)));
        
        //Thankyou
        JPanel thankYouPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        thankYouPanel.setBackground(Color.WHITE); 
        thankYouPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        thankYouPanel.setMaximumSize(new Dimension(500, 100));

        thankYou = new JLabel("\" Thank you \"");
        thankYou.setFont(new Font("Kanit", Font.ITALIC | Font.BOLD, 28));
        
        thankYouPanel.add(thankYou);
        txtPanel.add(thankYouPanel);
        
        //add to Windows Frame
        winFrame.add(iconPanel, BorderLayout.NORTH);
        
        scrollPane = new JScrollPane(txtPanel);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        winFrame.add(scrollPane, BorderLayout.CENTER);
        
        winFrame.setLocationRelativeTo(null);
        winFrame.setVisible(true);
        
    }
    private JLabel createLabel(String text, boolean isBold, float size) {
        JLabel label = new JLabel(text);
        int style = isBold ? Font.BOLD : Font.PLAIN;
        label.setFont(mainFont.deriveFont(style, size));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        label.setForeground(new Color(50, 50, 50)); 
        return label;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new About();
        });
    }
}