import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.util.ArrayList;

public class About {
    private JFrame winFrame;
    private JDesktopPane desktopPane;
    private ArrayList<String> memberList;
    
    //Attribute สำหรับ JTable ตรงนี้ ---
    private final String[] columnNames = {"Student ID", "Name"};
    private final Object[][] memberData = {
        {"68070023", "Chanawat Paenkhong"},
        {"68070061", "Thanawit Wanthong"},
        {"68070080", "Narawit Lueangprasoet"},
        {"68070091", "Bunyapol Mekcharoenviwattana"},
        {"68070113", "Phacharaphol Jaroen"},
        {"68070151", "Matawin Chortchuang"},
        {"68070164", "Wachirawit Anusunchanang"},
        {"68070175", "Weerachai Lorpa"},
        {"68070208", "Inthuch Thipwet"}
    };
    
    //Font color
    private Font mainFont;
    private Font titleFont;
    private final Color BG_COLOR = new Color(248, 249, 250);
    private final Color PANEL_BG = Color.WHITE;
    private final Color TEXT_DARK = new Color(44, 62, 80);
    private final Color ACCENT_COLOR = new Color(41, 128, 185);
    
    //Header
    private JLabel projTitle;
    private JLabel courseName;
    private JLabel teacherName;
    private JLabel lblPrepared;
    private JLabel lblLibraries;
    private JLabel lblFeatures;
    
    //number label
    private JLabel facultyName;
    private JLabel uniName;
    
    //Library
    private JLabel libFxHeader;
    private JLabel libFxUI;
    private JLabel libFxData;
    private JLabel libFxGraphic;
    private JLabel libFxMedia;
    private JLabel libSwingHeader;
    private JLabel libSwingDesc;
    private JLabel libStdHeader;
    private JLabel libStdDesc;
    
    //Feature
    private JLabel featDesc;
    private JLabel featHome;
    private JLabel featMySong;
    private JLabel featPlaylist;
    private JLabel featMix;
    private JLabel featOOP;
    
    //Thank You
    private JLabel thankYou;
    
    //Constructor
    public About() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        initFonts();
        initComponents();
        setupWindow();
    }
    
    private void initFonts() {
        try {
            mainFont = Font.createFont(Font.TRUETYPE_FONT, new java.io.File("Kanit-Regular.ttf"));
            GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(mainFont);
            titleFont = mainFont.deriveFont(Font.BOLD, 22f);
        } catch (Exception e) {
            mainFont = new Font("Tahoma", Font.PLAIN, 14);
            titleFont = new Font("Tahoma", Font.BOLD, 22);
        }
    }

    private void initComponents() {
        //Header Texts
        projTitle = createLabel("About Project \"FungJai\" Music media platform", titleFont, TEXT_DARK);
        courseName = createLabel("รายวิชา Object Oriented Programming", mainFont.deriveFont(16f), TEXT_DARK);
        teacherName = createLabel("เสนอ ผู้ช่วยศาสตราจารย์ ดร. ธราวิเชษฐ์ ธิติอรุณโรจน์", mainFont.deriveFont(16f), TEXT_DARK);

        //Header for Internal Frame
        Font sectionFont = mainFont.deriveFont(Font.BOLD, 18f);
        lblPrepared = createLabel("Prepared by", sectionFont, ACCENT_COLOR);
        lblLibraries = createLabel("Libraries", sectionFont, ACCENT_COLOR);
        lblFeatures = createLabel("Features", sectionFont, ACCENT_COLOR);

        //Member Bottom
        facultyName = createLabel("นักศึกษาคณะเทคโนโลยีสารสนเทศ", mainFont.deriveFont(Font.BOLD, 15f), ACCENT_COLOR);
        uniName = createLabel("สถาบันเทคโนโลยีพระจอมเกล้าเจ้าคุณทหารลาดกระบัง", mainFont.deriveFont(Font.BOLD, 15f), ACCENT_COLOR);

        //Library
        libFxHeader = createLabel("1. JavaFX (GUI สมัยใหม่ และมัลติมีเดีย)", mainFont.deriveFont(Font.BOLD, 14f), TEXT_DARK);
        libFxUI = createLabel("  - โครงสร้าง UI: javafx.application, stage, scene, layout", mainFont.deriveFont(13f), TEXT_DARK);
        libFxData = createLabel("  - จัดการข้อมูล: javafx.collections, beans.property", mainFont.deriveFont(13f), TEXT_DARK);
        libFxGraphic = createLabel("  - รูปร่าง/สี/กราฟิก: javafx.scene.paint, shape, canvas", mainFont.deriveFont(13f), TEXT_DARK);
        libFxMedia = createLabel("  - มัลติมีเดีย/แอนิเมชัน: javafx.scene.media, animation", mainFont.deriveFont(13f), TEXT_DARK);
        
        libSwingHeader = createLabel("2. Java Swing และ AWT (GUI สำหรับหน้า About)", mainFont.deriveFont(Font.BOLD, 14f), TEXT_DARK);
        libSwingDesc = createLabel("  - แพ็กเกจ: javax.swing.*, java.awt.*", mainFont.deriveFont(13f), TEXT_DARK);
        
        libStdHeader = createLabel("3. Java Standard Libraries (Backend & Logic)", mainFont.deriveFont(Font.BOLD, 14f), TEXT_DARK);
        libStdDesc = createLabel("  - Collections, UUID, LocalDateTime, I/O Stream", mainFont.deriveFont(13f), TEXT_DARK);

        //Feature
        featDesc = createLabel("แอปจัดการและฟังเพลงที่ออกแบบด้วยแนวคิด OOP", mainFont.deriveFont(Font.BOLD, 15f), TEXT_DARK);
        featHome = createLabel("- Home: ควบคุมการเล่นเพลง Loop, Shuffle, Volume", mainFont.deriveFont(14f), TEXT_DARK);
        featMySong = createLabel("- My Song: คลังเพลงส่วนตัว เพิ่มไฟล์เพลง (.mp4) ได้", mainFont.deriveFont(14f), TEXT_DARK);
        featPlaylist = createLabel("- Playlist: สร้าง แก้ไขชื่อ และจัดการรายการเพลง", mainFont.deriveFont(14f), TEXT_DARK);
        featMix = createLabel("- Mix For You: ระบบสุ่มเพลงเพื่อสร้าง Playlist", mainFont.deriveFont(14f), TEXT_DARK);
        featOOP = createLabel("- OOP: Encapsulation, Inheritance, Polymorphism", mainFont.deriveFont(14f), TEXT_DARK);

        //Thank you
        thankYou = new JLabel("Thank you");
        thankYou.setFont(mainFont.deriveFont(Font.ITALIC | Font.BOLD, 32f));
        thankYou.setForeground(ACCENT_COLOR);
    }

    private void setupWindow() {
        //Windows Frame
        winFrame = new JFrame("About us - FungJai");
        winFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        winFrame.setSize(1000, 820); 
        winFrame.setLayout(new BorderLayout());

        desktopPane = new JDesktopPane();
        desktopPane.setBackground(BG_COLOR);
        
        //Header for InternalFrame
        JPanel headerTextPanel = new JPanel(new GridLayout(3, 1, 0, 8));
        headerTextPanel.setOpaque(false);
        
        projTitle.setHorizontalAlignment(SwingConstants.CENTER);
        courseName.setHorizontalAlignment(SwingConstants.CENTER);
        teacherName.setHorizontalAlignment(SwingConstants.CENTER);
        
        headerTextPanel.add(projTitle);
        headerTextPanel.add(courseName);
        headerTextPanel.add(teacherName);
        
        headerTextPanel.setBounds(0, 30, 1000, 100);
        desktopPane.add(headerTextPanel, JLayeredPane.DEFAULT_LAYER);

        //JInternalFrame Prepared by
        JInternalFrame memberFrame = new JInternalFrame("Prepared by", true, true, true, true);
        JPanel memberPanel = new JPanel(new BorderLayout());
        memberPanel.setBackground(PANEL_BG);
        memberPanel.setBorder(new EmptyBorder(10, 20, 10, 20));
        JPanel topPrepPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        topPrepPanel.setBackground(PANEL_BG);
        topPrepPanel.add(lblPrepared);
        memberPanel.add(topPrepPanel, BorderLayout.NORTH);

        JPanel gridPanel = new JPanel(new GridLayout(0, 2, 20, 10)); 
        gridPanel.setBackground(PANEL_BG);
        gridPanel.setBorder(new EmptyBorder(10, 0, 0, 0));

        memberList = new ArrayList<>();
        memberList.add("(68070023) - Chanawat Paenkhong");
        memberList.add("(68070061) - Thanawit Wanthong");
        memberList.add("(68070080) - Narawit Lueangprasoet");
        memberList.add("(68070091) - Bunyapol Mekcharoenviwattana");
        memberList.add("(68070113) - Phacharaphol Jaroen");
        memberList.add("(68070151) - Matawin Chortchuang");
        memberList.add("(68070164) - Wachirawit Anusunchanang");
        memberList.add("(68070175) - Weerachai Lorpa");
        memberList.add("(68070208) - Inthuch Thipwet");

        for (String name : memberList) {
            gridPanel.add(createLabel("• " + name, mainFont.deriveFont(14f), TEXT_DARK));
        }

        JPanel instPanel = new JPanel();
        instPanel.setLayout(new BoxLayout(instPanel, BoxLayout.Y_AXIS));
        instPanel.setBackground(PANEL_BG);
        instPanel.setBorder(new EmptyBorder(20, 10, 10, 10));
        facultyName.setAlignmentX(Component.CENTER_ALIGNMENT);
        uniName.setAlignmentX(Component.CENTER_ALIGNMENT);
        instPanel.add(facultyName);
        instPanel.add(uniName);

        memberPanel.add(gridPanel, BorderLayout.CENTER);
        memberPanel.add(instPanel, BorderLayout.SOUTH);
        
        memberFrame.add(memberPanel);
        memberFrame.setBounds(130, 140, 720, 280); 
        memberFrame.setVisible(true);
        desktopPane.add(memberFrame);

        //JInternalFrame Library
        JInternalFrame libFrame = new JInternalFrame("Libraries", true, true, true, true);
        JPanel libInner = new JPanel();
        libInner.setLayout(new BoxLayout(libInner, BoxLayout.Y_AXIS));
        libInner.setBackground(PANEL_BG);
        libInner.setBorder(new EmptyBorder(15, 20, 15, 20));
        lblLibraries.setAlignmentX(Component.LEFT_ALIGNMENT);
        libInner.add(lblLibraries);
        libInner.add(Box.createRigidArea(new Dimension(0, 15)));
        
        libInner.add(libFxHeader);
        libInner.add(libFxUI);
        libInner.add(libFxData);
        libInner.add(libFxGraphic);
        libInner.add(libFxMedia);
        libInner.add(Box.createRigidArea(new Dimension(0, 10)));
        libInner.add(libSwingHeader);
        libInner.add(libSwingDesc);
        libInner.add(Box.createRigidArea(new Dimension(0, 10)));
        libInner.add(libStdHeader);
        libInner.add(libStdDesc);
        
        libFrame.add(libInner);
        libFrame.setBounds(30, 435, 450, 280);
        libFrame.setVisible(true);
        desktopPane.add(libFrame);

        //JInternalFrame Feature
        JInternalFrame featFrame = new JInternalFrame("Features", true, true, true, true);
        JPanel featInner = new JPanel();
        featInner.setLayout(new BoxLayout(featInner, BoxLayout.Y_AXIS));
        featInner.setBackground(PANEL_BG);
        featInner.setBorder(new EmptyBorder(15, 20, 15, 20));
        lblFeatures.setAlignmentX(Component.LEFT_ALIGNMENT);
        featInner.add(lblFeatures);
        featInner.add(Box.createRigidArea(new Dimension(0, 15)));
        
        featInner.add(featDesc);
        featInner.add(Box.createRigidArea(new Dimension(0, 15)));
        featInner.add(featHome);
        featInner.add(featMySong);
        featInner.add(featPlaylist);
        featInner.add(featMix);
        featInner.add(featOOP);

        featFrame.add(featInner);
        featFrame.setBounds(500, 435, 450, 280);
        featFrame.setVisible(true);
        desktopPane.add(featFrame);
        
        //Thankyou
        thankYou.setHorizontalAlignment(SwingConstants.CENTER);
        thankYou.setBounds(0, 720, 1000, 50); 
        desktopPane.add(thankYou, JLayeredPane.DEFAULT_LAYER);

        winFrame.add(desktopPane, BorderLayout.CENTER);
        winFrame.setLocationRelativeTo(null); 
        winFrame.setVisible(true);
    }

    private JLabel createLabel(String text, Font font, Color color) {
        JLabel label = new JLabel(text);
        label.setFont(font);
        label.setForeground(color);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new About());
    }
}