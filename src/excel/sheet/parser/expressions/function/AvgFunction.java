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
package excel.sheet.parser.expressions.function;

import excel.calc.Calculator;
import excel.sheet.CellAccessor;
import excel.sheet.Location;
import excel.sheet.parser.ParserException;
import excel.sheet.parser.expressions.AddressExpression;
import excel.sheet.parser.expressions.FunctionExpression;
import excel.sheet.parser.expressions.RangeExpression;
import java.util.HashSet;

/**
 * Funkcja średniej
 * 
 * @author eplightning <eplightning at outlook dot com>
 */
public class AvgFunction extends FunctionExpression {
    
    public AvgFunction(AddressExpression address)
    {
        super(address, "avg");
    }

    @Override
    public String evaluate(CellAccessor cells, HashSet<Location> callStack, Calculator calculator) throws ParserException
    {
        StringBuilder str = new StringBuilder(1024);
        str.append('(');
        
        // suma
        SumFunction sum = new SumFunction(address);
        str.append(sum.evaluate(cells, callStack, calculator));
        
        int colStart, colEnd, rowStart, rowEnd;
        
        // kolumny
        if (address.getLeft() instanceof RangeExpression) {
            RangeExpression range = (RangeExpression) address.getLeft();
            
            colStart = range.getLeft().evaluateAsInt(cells, callStack, calculator);
            colEnd = range.getRight().evaluateAsInt(cells, callStack, calculator);
        } else {
            colStart = address.getLeft().evaluateAsInt(cells, callStack, calculator);
            colEnd = colStart;
        }
        
        // wiersze
        if (address.getRight() instanceof RangeExpression) {
            RangeExpression range = (RangeExpression) address.getRight();
            
            rowStart = range.getLeft().evaluateAsInt(cells, callStack, calculator);
            rowEnd = range.getRight().evaluateAsInt(cells, callStack, calculator);
        } else {
            rowStart = address.getRight().evaluateAsInt(cells, callStack, calculator);
            rowEnd = rowStart;
        }
        
        if (rowStart > rowEnd || colStart > colEnd || colStart <= 0 || rowStart <= 0)
            throw new ParserException("Address range in invalid format");
        
        // dzielimy przez wielkość
        str.append('/');
        
        str.append((rowEnd - rowStart + 1) * (colEnd - colStart + 1));
        
        str.append(')');
        
        return str.toString();
    }
}
