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
package excel.exportimport;

import excel.sheet.Location;

/**
 * Adres w strukturze import/exportu sheet'ów
 * 
 * @author eplightning <eplightning at outlook dot com>
 */
public class ExportImportLocation {
    
    protected int column;
    protected int row;

    public ExportImportLocation(int col, int row)
    {
        this.column = col;
        this.row = row;
    }
    
    public ExportImportLocation(Location loc)
    {
        this.column = loc.getColumn();
        this.row = loc.getRow();
    }
    
    @Override
    public int hashCode()
    {
        return row * 1000 + column;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == null) {
            return false;
        }
        
        if (getClass() != obj.getClass()) {
            return false;
        }
        
        final ExportImportLocation other = (ExportImportLocation) obj;
        
        if (this.column != other.column) {
            return false;
        }
        
        return this.row == other.row;
    }
    
    public int getColumn()
    {
        return column;
    }

    public int getRow()
    {
        return row;
    }

    public void setColumn(int column)
    {
        this.column = column;
    }

    public void setRow(int row)
    {
        this.row = row;
    }
}
