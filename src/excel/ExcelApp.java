package excel;

import excel.main.LogWindow;
import excel.main.StatusBar;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.JToolBar;
import javax.swing.UIManager;

public class ExcelApp extends JFrame {

    protected LogWindow logWnd;
    protected StatusBar statusBar;
    protected JToolBar toolBar;

    /**
     * Entry point programu
     *
     * @param args
     */
    public static void main(final String[] args)
    {
        // próbujemy ustawić natywny wygląd
        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
        } catch (Exception e) {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e2) {

            }
        }

        // każemy odpalić główne okienko jak pętla eventów się odpali
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run()
            {
                ExcelApp app = new ExcelApp(args);
                app.setVisible(true);
            }
        });
    }

    /**
     * Konstruktor aplikacji
     *
     * @param args
     */
    public ExcelApp(String[] args)
    {
        setTitle("Arkusz kalkulacyjny");
        setIconImage(new ImageIcon(getClass().getResource("/excel/main/icons/new.png")).getImage());
        setSize(1600, 900);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        setupUserInterface();

        logWnd = new LogWindow();
        logWnd.setVisible(false);
    }

    /**
     * Tworzenie kontrolek głównych
     */
    private void setupUserInterface()
    {
        // ustawiamy layout
        Container c = getContentPane();
        c.setLayout(new BorderLayout(0, 0));

        // toolbar
        toolBar = new JToolBar();
        toolBar.setFloatable(false);
        toolBar.setRequestFocusEnabled(false);

        JButton btn = new JButton("Nowy");
        btn.setIcon(new ImageIcon(getClass().getResource("/excel/main/icons/new.png")));
        btn.addActionListener(new NewSheetAction());
        toolBar.add(btn);

        btn = new JButton("Otwórz");
        btn.setIcon(new ImageIcon(getClass().getResource("/excel/main/icons/open.png")));
        btn.addActionListener(new OpenSheetAction());
        toolBar.add(btn);

        btn = new JButton("Zapisz");
        btn.setIcon(new ImageIcon(getClass().getResource("/excel/main/icons/save.png")));
        btn.addActionListener(new SaveSheetAction());
        toolBar.add(btn);

        btn = new JButton("Zapisz jako");
        btn.setIcon(new ImageIcon(getClass().getResource("/excel/main/icons/save_as.png")));
        btn.addActionListener(new SaveAsSheetAction());
        toolBar.add(btn);

        btn = new JButton("Logi");
        btn.setIcon(new ImageIcon(getClass().getResource("/excel/main/icons/logs.png")));
        btn.addActionListener(new DebugAction());
        toolBar.add(btn);

        c.add(toolBar, BorderLayout.PAGE_START);

        // status bar
        statusBar = new StatusBar();
        c.add(statusBar, BorderLayout.PAGE_END);
    }

    class NewSheetAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent ae)
        {
            System.exit(0);
        }
    }

    class OpenSheetAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent ae)
        {
            System.exit(0);
        }
    }

    class SaveSheetAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent ae)
        {
            System.exit(0);
        }
    }

    class SaveAsSheetAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent ae)
        {
            System.exit(0);
        }
    }

    class DebugAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent ae)
        {
            if (!logWnd.isVisible()) {
                logWnd.setSize(600, 600);
                logWnd.setVisible(true);
            }
        }
    }

}
