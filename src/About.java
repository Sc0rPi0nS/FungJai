import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.*;

public class About {
    private JFrame winFrame;
    private JDesktopPane desktopPane;
    
    //Name List For JTable
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
    private JInternalFrame InternalFrameMember;
    private JPanel PanelForTable;
    private JPanel PanelForFacandUniver;
    private JPanel PanelPrepare;
    private JPanel PanelMember;
    private JTable TableMember;
    private JTableHeader TableHaeder;
    private DefaultTableCellRenderer RenderTableHeader;
    private DefaultTableCellRenderer RenderTableCell;
    private DefaultTableModel TableModel;
    
    //Font color
    private Font mainFont;
    private Font titleFont;
    
    //Header    
    private JPanel PanelheaderText;
    private JLabel LabelProjectTitle;
    private JLabel LabelSubjectName;
    private JLabel LabelTeacherName;
    private JLabel LabelPrepared;
    private JLabel LabelLibrary;
    private JLabel LabelFeature;
    
    //number label
    private JLabel LabelFacultyName;
    private JLabel LabelUniversityName;
    
    //Library
    private JInternalFrame InternalFrameLibrary;
    private JPanel PanelForLibrary;
    private JLabel LabelLibrary_Header_FxHeader;
    private JLabel LabelLibraryFxUI;
    private JLabel LabelLibraryFxData;
    private JLabel LabelLibraryFxGraphic;
    private JLabel LabelLibraryFxMedia;
    private JLabel LabelLibrary_Header_SwingHeader;
    private JLabel LabelLibrarySwingDesc;
    private JLabel LabelLibrary_Header_StdHeader;
    private JLabel LabelLibraryStdDesc;
    
    //Feature
    private JInternalFrame InternalFrameFeature;
    private JPanel PanelForFeature;
    private JLabel LabelFeatureDesc;
    private JLabel LabelFeatureHome;
    private JLabel LabelFeatureMySong;
    private JLabel LabelFeaturePlaylist;
    private JLabel LabelFeatureMix;
    private JLabel LabelFeatureOOP;
    private JLabel LabelFeatureLyrics; 
    private JLabel LabelFeatureSaveData;
    
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
            titleFont = mainFont.deriveFont(Font.BOLD, 22f);
        } catch (Exception e) {
            mainFont = new Font("Tahoma", Font.PLAIN, 14);
            titleFont = new Font("Tahoma", Font.BOLD, 22);
        }
    }
    
    private JLabel createLabel(String text, Font font, Color color) {
        JLabel label = new JLabel(text);
        label.setFont(font);
        label.setForeground(color);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }
    
    private void initComponents() {
        //Header Texts
        LabelProjectTitle = createLabel("About Project \"FungJai\" Music media platform", titleFont, Color.BLACK);
        LabelSubjectName = createLabel("รายวิชา Object Oriented Programming", mainFont.deriveFont(16f), Color.BLACK);
        LabelTeacherName = createLabel("เสนอ ผู้ช่วยศาสตราจารย์ ดร. ธราวิเชษฐ์ ธิติอรุณโรจน์", mainFont.deriveFont(16f), Color.BLACK);

        //Header for Internal Frame
        Font sectionFont = mainFont.deriveFont(Font.BOLD, 18f);
        LabelPrepared = createLabel("Prepared by", sectionFont, new Color(41, 128, 185));
        LabelLibrary = createLabel("Libraries", sectionFont, new Color(41, 128, 185));
        LabelFeature = createLabel("Features", sectionFont, new Color(41, 128, 185));

        //Member Bottom
        LabelFacultyName = createLabel("นักศึกษาคณะเทคโนโลยีสารสนเทศ", mainFont.deriveFont(Font.BOLD, 15f), new Color(41, 128, 185));
        LabelUniversityName = createLabel("สถาบันเทคโนโลยีพระจอมเกล้าเจ้าคุณทหารลาดกระบัง", mainFont.deriveFont(Font.BOLD, 15f), new Color(41, 128, 185));

        //Library
        LabelLibrary_Header_FxHeader = createLabel("1. JavaFX (GUI สมัยใหม่ และมัลติมีเดีย)", mainFont.deriveFont(Font.BOLD, 14f), Color.BLACK);
        LabelLibraryFxUI = createLabel("  - โครงสร้าง UI: javafx.application, stage, scene, layout", mainFont.deriveFont(13f), Color.BLACK);
        LabelLibraryFxData = createLabel("  - จัดการข้อมูล: javafx.collections, beans.property", mainFont.deriveFont(13f), Color.BLACK);
        LabelLibraryFxGraphic = createLabel("  - รูปร่าง/สี/กราฟิก: javafx.scene.paint, shape, canvas", mainFont.deriveFont(13f), Color.BLACK);
        LabelLibraryFxMedia = createLabel("  - มัลติมีเดีย/แอนิเมชัน: javafx.scene.media, animation", mainFont.deriveFont(13f), Color.BLACK);
        
        LabelLibrary_Header_SwingHeader = createLabel("2. Java Swing และ AWT (GUI สำหรับหน้า About)", mainFont.deriveFont(Font.BOLD, 14f), Color.BLACK);
        LabelLibrarySwingDesc = createLabel("  - แพ็กเกจ: javax.swing.*, java.awt.*", mainFont.deriveFont(13f), Color.BLACK);
        
        LabelLibrary_Header_StdHeader = createLabel("3. Java Standard Libraries (Backend & Logic)", mainFont.deriveFont(Font.BOLD, 14f), Color.BLACK);
        LabelLibraryStdDesc = createLabel("  - Collections, UUID, LocalDateTime, I/O Stream", mainFont.deriveFont(13f), Color.BLACK);

        //Feature
        LabelFeatureDesc = createLabel("แอปจัดการและฟังเพลงที่ออกแบบด้วยแนวคิด OOP", mainFont.deriveFont(Font.BOLD, 15f), Color.BLACK);
        LabelFeatureHome = createLabel("- Home: ควบคุมการเล่นเพลง Loop, Shuffle, Volume", mainFont.deriveFont(14f), Color.BLACK);
        LabelFeatureMySong = createLabel("- My Song: คลังเพลงส่วนตัว เพิ่มไฟล์เพลง (.mp3) ได้", mainFont.deriveFont(14f), Color.BLACK);
        LabelFeaturePlaylist = createLabel("- Playlist: สร้าง แก้ไขชื่อ และจัดการรายการเพลง", mainFont.deriveFont(14f), Color.BLACK);
        LabelFeatureMix = createLabel("- Mix For You: ระบบสุ่มเพลงเพื่อสร้าง Playlist", mainFont.deriveFont(14f), Color.BLACK);
        LabelFeatureOOP = createLabel("- OOP: Encapsulation, Inheritance, Polymorphism", mainFont.deriveFont(14f), Color.BLACK);
        LabelFeatureLyrics = createLabel("- Lyrics: ระบบแสดงเนื้อเพลง", mainFont.deriveFont(14f), Color.BLACK); 
        LabelFeatureSaveData = createLabel("- Data: บันทึกคลังเพลงด้วย Object Stream", mainFont.deriveFont(14f), Color.BLACK); 
        LabelFeatureOOP = createLabel("- OOP: Encapsulation, Inheritance, Polymorphism, Abstract, Interface", mainFont.deriveFont(14f), Color.BLACK);

        //Thank you
        thankYou = new JLabel("Thank you");
        thankYou.setFont(mainFont.deriveFont(Font.ITALIC | Font.BOLD, 32f));
        thankYou.setForeground(new Color(41, 128, 185));
    }

    private void setupWindow() {
        //Windows Frame
        winFrame = new JFrame("About us - FungJai");
        winFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        winFrame.setSize(1000, 820); 
        winFrame.setLayout(new BorderLayout());

        desktopPane = new JDesktopPane();
        desktopPane.setBackground(Color.WHITE);
        
        //Header Big Text
        PanelheaderText = new JPanel(new GridLayout(3, 1));
        PanelheaderText.setOpaque(false);
        
        LabelProjectTitle.setHorizontalAlignment(SwingConstants.CENTER);
        LabelSubjectName.setHorizontalAlignment(SwingConstants.CENTER);
        LabelTeacherName.setHorizontalAlignment(SwingConstants.CENTER);
        
        PanelheaderText.add(LabelProjectTitle);
        PanelheaderText.add(LabelSubjectName);
        PanelheaderText.add(LabelTeacherName);
        
        PanelheaderText.setBounds(0, 30, 1000, 100);
        desktopPane.add(PanelheaderText, JLayeredPane.DEFAULT_LAYER);

        //JInternalFrame Prepared by
        InternalFrameMember = new JInternalFrame("Prepared by", false, false, false, false);
        PanelMember = new JPanel(new BorderLayout(0, 10));
        PanelMember.setBackground(Color.WHITE);
        PanelMember.setBorder(new EmptyBorder(10, 20, 10, 20));
        
        PanelPrepare = new JPanel(new FlowLayout(FlowLayout.CENTER));
        PanelPrepare.setBackground(Color.WHITE);
        PanelPrepare.add(LabelPrepared);
        PanelMember.add(PanelPrepare, BorderLayout.NORTH);

                //JTable Attribute
        TableModel = new DefaultTableModel(memberData, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; 
            }
        };

        TableMember = new JTable(TableModel);
        TableMember.setFont(mainFont.deriveFont(14f));
        TableMember.setForeground(Color.BLACK);
        TableMember.setBackground(Color.WHITE);
        TableMember.setRowHeight(30);
        TableMember.setGridColor(new Color(230, 230, 230));
        TableMember.setSelectionBackground(new Color(41, 128, 185));
        TableMember.setSelectionForeground(Color.WHITE);

        TableHaeder = TableMember.getTableHeader();
        TableHaeder.setFont(mainFont.deriveFont(Font.BOLD, 15f));
        TableHaeder.setBackground(new Color(240, 240, 240));
        TableHaeder.setForeground(new Color(41, 128, 185));
        
        RenderTableHeader = (DefaultTableCellRenderer) TableHaeder.getDefaultRenderer();
        RenderTableHeader.setHorizontalAlignment(JLabel.CENTER);

        RenderTableCell = new DefaultTableCellRenderer();
        RenderTableCell.setHorizontalAlignment(JLabel.CENTER);
        
        TableMember.getColumnModel().getColumn(0).setCellRenderer(RenderTableCell);
        TableMember.getColumnModel().getColumn(0).setPreferredWidth(150);
        TableMember.getColumnModel().getColumn(0).setMaxWidth(200);

        PanelForTable = new JPanel(new BorderLayout());
        PanelForTable.setBackground(Color.WHITE);
        PanelForTable.add(TableMember.getTableHeader(), BorderLayout.NORTH);
        PanelForTable.add(TableMember, BorderLayout.CENTER);
        PanelForTable.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));

        PanelForFacandUniver = new JPanel();
        PanelForFacandUniver.setLayout(new BoxLayout(PanelForFacandUniver, BoxLayout.Y_AXIS));
        PanelForFacandUniver.setBackground(Color.WHITE);
        PanelForFacandUniver.setBorder(new EmptyBorder(10, 10, 0, 10));
        LabelFacultyName.setAlignmentX(Component.CENTER_ALIGNMENT);
        LabelUniversityName.setAlignmentX(Component.CENTER_ALIGNMENT);
        PanelForFacandUniver.add(LabelFacultyName);
        PanelForFacandUniver.add(LabelUniversityName);

        PanelMember.add(PanelForTable, BorderLayout.CENTER);
        PanelMember.add(PanelForFacandUniver, BorderLayout.SOUTH);
        
        InternalFrameMember.add(PanelMember);
        InternalFrameMember.setBounds(30, 140, 450, 575);
        InternalFrameMember.setVisible(true);
        desktopPane.add(InternalFrameMember);

        //JInternalFrame Library
        InternalFrameLibrary = new JInternalFrame("Libraries", false, false, false, false);
        PanelForLibrary = new JPanel();
        PanelForLibrary.setLayout(new BoxLayout(PanelForLibrary, BoxLayout.Y_AXIS));
        PanelForLibrary.setBackground(Color.WHITE);
        PanelForLibrary.setBorder(new EmptyBorder(15, 20, 15, 20));
        PanelForLibrary.add(LabelLibrary);
        PanelForLibrary.add(Box.createRigidArea(new Dimension(0, 15)));
        
        PanelForLibrary.add(LabelLibrary_Header_FxHeader);
        PanelForLibrary.add(LabelLibraryFxUI);
        PanelForLibrary.add(LabelLibraryFxData);
        PanelForLibrary.add(LabelLibraryFxGraphic);
        PanelForLibrary.add(LabelLibraryFxMedia);
        PanelForLibrary.add(Box.createRigidArea(new Dimension(0, 10)));
        PanelForLibrary.add(LabelLibrary_Header_SwingHeader);
        PanelForLibrary.add(LabelLibrarySwingDesc);
        PanelForLibrary.add(Box.createRigidArea(new Dimension(0, 10)));
        PanelForLibrary.add(LabelLibrary_Header_StdHeader);
        PanelForLibrary.add(LabelLibraryStdDesc);
        
        InternalFrameLibrary.add(PanelForLibrary);
        InternalFrameLibrary.setBounds(500, 140, 450, 280);
        InternalFrameLibrary.setVisible(true);
        desktopPane.add(InternalFrameLibrary);

        //JInternalFrame Feature
        InternalFrameFeature = new JInternalFrame("Features", false, false, false, false);
        PanelForFeature = new JPanel();
        PanelForFeature.setLayout(new BoxLayout(PanelForFeature, BoxLayout.Y_AXIS));
        PanelForFeature.setBackground(Color.WHITE);
        PanelForFeature.setBorder(new EmptyBorder(15, 20, 15, 20));
        PanelForFeature.add(LabelFeature);
        PanelForFeature.add(Box.createRigidArea(new Dimension(0, 15)));
        
        PanelForFeature.add(LabelFeatureDesc);
        PanelForFeature.add(Box.createRigidArea(new Dimension(0, 15)));
        PanelForFeature.add(LabelFeatureHome);
        PanelForFeature.add(LabelFeatureMySong);
        PanelForFeature.add(LabelFeaturePlaylist);
        PanelForFeature.add(LabelFeatureMix);
        PanelForFeature.add(LabelFeatureLyrics); 
        PanelForFeature.add(LabelFeatureSaveData);
        PanelForFeature.add(LabelFeatureOOP);

        InternalFrameFeature.add(PanelForFeature);
        InternalFrameFeature.setBounds(500, 435, 450, 280);
        InternalFrameFeature.setVisible(true);
        desktopPane.add(InternalFrameFeature);
        
        //Thankyou
        thankYou.setHorizontalAlignment(SwingConstants.CENTER);
        thankYou.setBounds(0, 720, 1000, 50); 
        desktopPane.add(thankYou, JLayeredPane.DEFAULT_LAYER);

        winFrame.add(desktopPane, BorderLayout.CENTER);
    }

    public void show(double ownerX, double ownerY, double ownerW) {
        if (winFrame == null) {
            initFonts();
            initComponents();
            setupWindow();
        }

        if (winFrame.isVisible()) {
            winFrame.toFront();
            winFrame.requestFocus();
            return;
        }
    winFrame.setLocationRelativeTo(null);
    winFrame.setVisible(true);
    }
}
