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

/**
 * Komórka
 * 
 * @author eplightning <eplightning at outlook dot com>
 */
public class Cell implements Cloneable {
    
    protected boolean calculated;
    protected String error;
    protected String formula;
    protected String value;

    public Cell()
    {
        error = null;
        calculated = false;
        formula = null;
        value = null;
    }
    
    public Cell(Cell other)
    {
        this.calculated = false;
        this.value = null;
        this.formula = other.formula;
        this.error = null;
    }
    
    public String getFormula()
    {
        return formula;
    }

    public void setFormula(String formula)
    {
        this.formula = formula;
    }

    public boolean isCalculated()
    {
        return calculated;
    }

    public void setCalculated(boolean calculated)
    {
        this.calculated = calculated;
    }

    public String getError()
    {
        return error;
    }

    public void setError(String error)
    {
        this.error = error;
    }

    public String getValue()
    {
        if (value == null || value.isEmpty())
            return "0";
        
        return value;
    }

    public void setValue(String value)
    {
        this.value = value;
    }
    
    public boolean isOrdinaryText()
    {
        return formula.length() <= 0 || formula.charAt(0) != '=';
    }
    
    @Override
    public String toString()
    {
        if (isOrdinaryText() || !isCalculated()) {
            return getFormula();
        }
        
        if (error != null)
            return getError();

        return getValue();
    }
}
