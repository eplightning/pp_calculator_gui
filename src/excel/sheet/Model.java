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

import java.util.HashMap;
import javax.swing.table.AbstractTableModel;

/**
 * Model tabeli
 * 
 * @author eplightning <eplightning at outlook dot com>
 */
public class Model extends AbstractTableModel {

    protected HashMap<Location, Cell> cells;
    protected int rows;
    protected int columns;
    
    public Model()
    {
        cells = new HashMap<>();
    }
    
    public HashMap<Location, Cell> getCells()
    {
        return cells;
    }

    public int getRows()
    {
        return rows;
    }

    public void setRows(int rows)
    {
        this.rows = rows;
    }

    public int getColumns()
    {
        return columns;
    }

    public void setColumns(int columns)
    {
        this.columns = columns;
    }
    
    @Override
    public int getRowCount()
    {
        return rows > 50 ? rows : 50;
    }

    @Override
    public int getColumnCount()
    {
        return columns > 50 ? columns : 50;
    }
    
    @Override
    public String getColumnName(int i)
    {
        return Integer.toString(i + 1);
    }
    
    @Override
    public Object getValueAt(int i, int i1)
    {
        return cells.get(new Location(i1 + 1, i + 1));
    }

    @Override
    public void setValueAt(Object o, int i, int i1)
    {
        Cell cell = new Cell();
        
        cell.setFormula(o.toString());
        
        cells.put(new Location(i1 + 1, i + 1), cell);
    }

    @Override
    public boolean isCellEditable(int i, int i1)
    {
        return true;
    }
    
}
