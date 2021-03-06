/*
 * The MIT License
 *
 * Copyright 2015 eplightning <eplightning at outlook dot com>.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package excel;

import excel.calc.*;
import excel.exportimport.*;
import excel.exportimport.formats.*;
import excel.main.*;
import excel.sheet.Sheet;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.JToolBar;

/**
 * Główne okno aplikacji
 *
 * @author eplightning <eplightning at outlook dot com>
 */
public class ExcelApp extends JFrame {

    /**
     * Instancja kalkulatora
     */
    private Calculator calc;

    /**
     * Okienko logowania
     */
    private LogWindow logWnd;

    /**
     * Obecnie otwarty plik
     */
    private File openedFile;

    /**
     * Arkusz
     */
    private Sheet sheet;

    /**
     * Pasek dolny
     */
    private StatusBar statusBar;

    /**
     * Toolbar górny
     */
    private JToolBar toolBar;

    /**
     * Entry point programu
     *
     * @param args
     */
    public static void main(final String[] args)
    {
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
        // parametry okienka
        setTitle("Arkusz kalkulacyjny");
        setIconImage(new ImageIcon(getClass().getResource("/excel/main/icons/new.png")).getImage());
        setSize(1600, 900);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // tworzymy elementy interfejsu
        setupUserInterface();

        // okienko logowania
        logWnd = new LogWindow();
        logWnd.setVisible(false);

        // próbujemy utworzyć kalkulator
        // jak nie da rady to kończymy bo to sensu nie ma
        try {
            calc = new CppCalculator(logWnd);
        } catch (IOException ex) {
            System.exit(1);
        }

        // pusty arkusz
        sheet = new Sheet(calc, logWnd, statusBar);
        getContentPane().add(sheet, BorderLayout.CENTER);
    }

    /**
     * Zastąpienie starego arkusza
     *
     * @param newSheet Nowy arkusz
     */
    private void replaceSheet(Sheet newSheet)
    {
        Container c = getContentPane();

        c.remove(sheet);

        sheet = newSheet;
        c.add(sheet, BorderLayout.CENTER);
        validate();
    }

    /**
     * Zapis arkusza do pliku
     *
     * @param output  Plik
     * @param factory Fabryka formatów
     */
    private void saveSheet(File output, SheetFormatFactory factory)
    {
        ExportImportData data = sheet.export();

        logWnd.addLine("Zapisywanie pliku " + output.getName());
        statusBar.setState("Zapisywanie ...");

        // wczytywanie danych z pliku
        try {
            SheetFormat format = factory.makeFormat(output);

            format.saveFile(output, data);
        } catch (IOException e) {
            logWnd.addLine(e.getMessage());
            statusBar.setState("Błąd podczas zapisu pliku, szczegóły w logu");
            JOptionPane.showMessageDialog(null, e.getMessage(), "Błąd zapisywania pliku", JOptionPane.ERROR_MESSAGE);
            return;
        }

        statusBar.setState("Gotowe");

        sheet.setModified(false);
    }

    /**
     * Tworzenie kontrolek głównych
     */
    private void setupUserInterface()
    {
        // ustawiamy layout
        Container c = getContentPane();
        c.setLayout(new BorderLayout(5, 5));

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

        // pusta przestrzeń lewo prawo
        c.add(Box.createHorizontalStrut(1), BorderLayout.LINE_START);
        c.add(Box.createHorizontalStrut(1), BorderLayout.LINE_END);
    }

    /**
     * Nowy arkusz
     */
    private class NewSheetAction implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent ae)
        {
            if (sheet.isModified()) {
                int option = JOptionPane.showConfirmDialog(null, "Zmiany nie zostały zapisane, czy na pewno chcesz utworzyć nowy arkusz?", "Potwierdzenie", JOptionPane.YES_NO_OPTION);

                if (option == JOptionPane.NO_OPTION) {
                    return;
                }
            }

            openedFile = null;
            replaceSheet(new Sheet(calc, logWnd, statusBar));
        }

    }

    /**
     * Otwórz arkusz
     */
    private class OpenSheetAction implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent ae)
        {
            if (sheet.isModified()) {
                int option = JOptionPane.showConfirmDialog(null, "Zmiany nie zostały zapisane, czy na pewno chcesz otworzyć inny plik?", "Potwierdzenie", JOptionPane.YES_NO_OPTION);

                if (option == JOptionPane.NO_OPTION) {
                    return;
                }
            }

            // fabryka formatów
            SheetFormatFactory factory = new SheetFormatFactory();

            // dialog wyboru pliku
            JFileChooser picker = new JFileChooser();
            factory.addFilters(picker);

            int result = picker.showOpenDialog(ExcelApp.this);

            // zatwierdzone
            if (result == JFileChooser.APPROVE_OPTION) {
                ExportImportData data;

                logWnd.addLine("Wczytywanie pliku " + picker.getSelectedFile().getName());
                statusBar.setState("Wczytywanie ...");

                // wczytywanie danych z pliku
                try {
                    SheetFormat format = factory.makeFormat(picker.getSelectedFile());

                    data = format.loadFile(picker.getSelectedFile());
                } catch (IOException e) {
                    logWnd.addLine(e.getMessage());
                    statusBar.setState("Błąd podczas wczytywania pliku, szczegóły w logu");
                    JOptionPane.showMessageDialog(null, e.getMessage(), "Błąd wczytywania pliku", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                statusBar.setState("Gotowe");

                Sheet newSheet = new Sheet(calc, logWnd, statusBar, data);
                replaceSheet(newSheet);
                openedFile = picker.getSelectedFile();
            }
        }

    }

    /**
     * Zapisz arkusz
     */
    private class SaveSheetAction implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent ae)
        {
            // jeśli plik nie otwarty to przekierowujemy na akcje zapisz jako
            if (openedFile == null) {
                SaveAsSheetAction passingTheEvent = new SaveAsSheetAction();

                passingTheEvent.actionPerformed(ae);
                return;
            }

            // fabryka formatów
            SheetFormatFactory factory = new SheetFormatFactory();

            // zapis
            saveSheet(openedFile, factory);
        }

    }

    /**
     * Zapisz arkusz jako
     */
    private class SaveAsSheetAction implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent ae)
        {
            // fabryka formatów
            SheetFormatFactory factory = new SheetFormatFactory();

            // dialog wyboru pliku
            JFileChooser picker = new JFileChooser();
            factory.addFilters(picker);

            int result = picker.showSaveDialog(ExcelApp.this);

            // zatwierdzone
            if (result == JFileChooser.APPROVE_OPTION)
                saveSheet(picker.getSelectedFile(), factory);
        }

    }

    /**
     * Konsola logowania
     */
    private class DebugAction implements ActionListener {

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
