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
package excel.sheet;

import excel.calc.Calculator;
import excel.Logger;
import excel.exportimport.*;
import excel.main.StatusBar;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JViewport;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * Widżet skoroszytu
 * 
 * @author eplightning <eplightning at outlook dot com>
 */
public class Sheet extends JPanel {
    
    // komponenty graficzne
    protected JTextField formulaField;
    protected Table table;
    protected JScrollPane tableScroll;
    protected ColumnTable colTable;
    protected JViewport colScroll;
    
    // stany
    protected boolean modified;
    
    // inne obiekty
    protected Model model;
    
    // obiekty z maina
    protected Calculator calc;
    protected Logger logger;
    protected StatusBar statusBar;
    
    public Sheet(Calculator calc, Logger logger, StatusBar statusBar)
    {
        this.calc = calc;
        this.logger = logger;
        this.statusBar = statusBar;
        this.modified = false;
        
        setupUserInterface();
        setupTable();
    }
    
    public Sheet(Calculator calc, Logger logger, StatusBar statusBar, ExportImportData data)
    {
        this.calc = calc;
        this.logger = logger;
        this.statusBar = statusBar;
        this.modified = false;
        
        setupUserInterface();
        setupTable();
        // TODO: Import
    }
    
    private void setupTable()
    {
        model = new Model(logger, statusBar, calc);
        
        table.setModel(model);
    }
    
    private void setupUserInterface()
    {
        setPreferredSize(new Dimension(-1, -1));
        setLayout(new BorderLayout(5, 5));
        
        formulaField = new JTextField();
        formulaField.setEditable(false);
        formulaField.setPreferredSize(new Dimension(-1, 30));
        formulaField.setMargin(new Insets(6, 6, 6, 6));
        add(formulaField, BorderLayout.PAGE_START);
        
        // tytuły dla wierszy
        colTable = new ColumnTable();
        colScroll = new JViewport();
        colScroll.setPreferredSize(new Dimension(40, -1));
        colScroll.setView(colTable);
        
        // tabela
        table = new Table();
        tableScroll = new JScrollPane(table);
        tableScroll.setPreferredSize(new Dimension(-1, -1));
        
        // tytuły wierszy wrzucamy jako row header i synchronizujemy ze scrollem
        tableScroll.setRowHeaderView(colScroll);
        tableScroll.setCorner(JScrollPane.UPPER_LEFT_CORNER, colTable.getTableHeader());

        add(tableScroll, BorderLayout.CENTER);
        
        table.getSelectionModel().addListSelectionListener(new SelectionListener());
        table.getColumnModel().getSelectionModel().addListSelectionListener(new SelectionListener());
        table.getDefaultEditor(Cell.class).addCellEditorListener(new EditorListener());
        tableScroll.getVerticalScrollBar().addAdjustmentListener(new VerticalScrollListener());
        tableScroll.getHorizontalScrollBar().addAdjustmentListener(new HorizontalScrollListener());
    }

    public ExportImportData export()
    {
        // TODO
        return null;
    }

    public boolean isModified()
    {
        return modified;
    }

    public void setModified(boolean modified)
    {
        this.modified = modified;
    }
    
    protected class SelectionListener implements ListSelectionListener {
        @Override
        public void valueChanged(ListSelectionEvent lse)
        {
            if (lse.getValueIsAdjusting())
                return;
            
            int col = table.getSelectedColumn();
            int row = table.getSelectedRow();
            
            if (col == -1 || row == -1) {
                formulaField.setText("");
            } else {
                Object o = model.getValueAt(row, col);

                if (o == null) {
                    formulaField.setText("");
                } else {
                    Cell cell = (Cell) o;
                    formulaField.setText(cell.getFormula());
                }
            }
        }
    }
    
    protected class VerticalScrollListener implements AdjustmentListener {
        @Override
        public void adjustmentValueChanged(AdjustmentEvent ae)
        {
            // overscroll
            if (!ae.getValueIsAdjusting()) {
                JScrollBar bar = (JScrollBar) ae.getAdjustable();

                int cur = bar.getValue() + bar.getModel().getExtent();
                int max = bar.getMaximum();

                if (cur == max) {
                    model.setRows(model.getRowCount() + 10);
                    colTable.setRows(model.getRowCount());
                }
            }
            
            // nagłówki wierszy mają też być scrollowane tym
            colScroll.setViewPosition(new Point(0, ae.getValue()));
        }
    }
    
    protected class HorizontalScrollListener implements AdjustmentListener {
        @Override
        public void adjustmentValueChanged(AdjustmentEvent ae)
        {
            // overscroll
            if (!ae.getValueIsAdjusting()) {
                JScrollBar bar = (JScrollBar) ae.getAdjustable();

                int cur = bar.getValue() + bar.getModel().getExtent();
                int max = bar.getMaximum();

                if (cur == max) {
                    model.setColumns(model.getColumnCount() + 10);
                }
            }
        }
    }
    
    protected class EditorListener implements CellEditorListener {
        @Override
        public void editingStopped(ChangeEvent ce)
        {
            int col = table.getSelectedColumn();
            int row = table.getSelectedRow();
            
            if (col == -1 || row == -1) {
                formulaField.setText("");
            } else {
                Object o = model.getValueAt(row, col);

                if (o == null) {
                    formulaField.setText("");
                } else {
                    Cell cell = (Cell) o;
                    formulaField.setText(cell.getFormula());
                }
            }
            
            modified = true;
        }

        @Override
        public void editingCanceled(ChangeEvent ce)
        {
            
        }
    }
}
