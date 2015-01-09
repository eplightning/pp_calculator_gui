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

import java.awt.Component;
import javax.swing.DefaultCellEditor;
import javax.swing.JTable;
import javax.swing.JTextField;

/**
 * Rozszerzenie JTable dla arkusza
 * 
 * @author eplightning <eplightning at outlook dot com>
 */
public class Table extends JTable {
    
    protected JTextField editorField;
    
    public Table()
    {
        setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        setColumnSelectionAllowed(false);
        setFillsViewportHeight(true);
        setRowSelectionAllowed(false);
        setColumnSelectionAllowed(false);
        setRowHeight(25);
        setAutoCreateRowSorter(false);
        getTableHeader().setReorderingAllowed(false);
        
        editorField = new JTextField();
        setDefaultEditor(Cell.class, new CellEditor(editorField));
    }
    
    protected class CellEditor extends DefaultCellEditor {

        public CellEditor(JTextField jtf)
        {
            super(jtf);
        }

        @Override
        public Component getTableCellEditorComponent(JTable jtable, Object o, boolean bln, int i, int i1)
        {
            if (o instanceof Cell) {
                Cell c = (Cell) o;
                return super.getTableCellEditorComponent(jtable, c.getFormula(), bln, i, i1);
            }
            
            return super.getTableCellEditorComponent(jtable, o, bln, i, i1); //To change body of generated methods, choose Tools | Templates.
        }
    }
}
