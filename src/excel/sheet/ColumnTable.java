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

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.TableModelEvent;
import javax.swing.table.AbstractTableModel;

/**
 * Kolumnowa tabela dla tytułów wierszy
 * 
 * @author eplightning <eplightning at outlook dot com>
 */
public class ColumnTable extends JTable {
    
    protected int rows;
    
    public ColumnTable()
    {
        this.rows = 50;
        
        setRowHeight(25);
        setRowSelectionAllowed(false);
        setColumnSelectionAllowed(false);
        setFillsViewportHeight(true);
        setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        setModel(new Model());
    }

    public void setRows(int rows)
    {
        if (rows != this.rows) {
            this.rows = rows;
            tableChanged(new TableModelEvent(getModel()));
        }
    }
    
    protected class Model extends AbstractTableModel {
        @Override
        public int getRowCount()
        {
            return rows;
        }

        @Override
        public int getColumnCount()
        {
            return 1;
        }

        @Override
        public Object getValueAt(int i, int i1)
        {
            if (i1 != 0)
                return null;

            return Integer.toString(i + 1);
        }

        @Override
        public String getColumnName(int i)
        {
            return "W\\K";
        }
    }
}
