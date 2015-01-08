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
import excel.sheet.Cell;
import excel.sheet.CellAccessor;
import excel.sheet.Location;
import excel.sheet.parser.Expression;
import excel.sheet.parser.Parser;
import excel.sheet.parser.ParserException;
import excel.sheet.parser.expressions.AddressExpression;
import excel.sheet.parser.expressions.FunctionExpression;
import excel.sheet.parser.expressions.RangeExpression;
import excel.sheet.token.Tokenizer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.ListIterator;

/**
 * Funkcja sumy
 * 
 * @author eplightning <eplightning at outlook dot com>
 */
public class SumFunction extends FunctionExpression {

    public SumFunction(AddressExpression address)
    {
        super(address, "sum");
    }

    @Override
    public String evaluate(CellAccessor cells, HashSet<Location> callStack, Calculator calculator) throws ParserException
    {
        StringBuilder str = new StringBuilder(1024);
        str.append('(');
        
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
        
        // teraz każdą komórke po kolei evalujemy ...
        Tokenizer tokenizer = new Tokenizer();
        Parser parser = new Parser();
        
        // po kolumnach
        for (int col = colStart; col <= colEnd; col++) {
            // po wierszach
            for (int row = rowStart; row <= rowEnd; row++) {
                if (col != colStart && row != rowStart)
                    str.append('+');
                
                Location loc = new Location(col, row);
                
                Cell cell = cells.get(col, row);
                
                if (cell == null || cell.isOrdinaryText()) {
                    str.append('0');
                } else if (cell.isCalculated()) {
                    // miło
                    str.append(cell.getValue());
                } else {
                    // koszmar
                    Cell newCell = new Cell(cell);

                    HashSet<Location> newStack = (HashSet) callStack.clone();
                    newStack.add(loc);

                    try {
                        ArrayList<Expression> expressions = parser.parse(tokenizer.tokenize(cell.getFormula()));

                        StringBuilder str2 = new StringBuilder();

                        ListIterator<Expression> iterator = expressions.listIterator();

                        while (iterator.hasNext()) {
                            str2.append(iterator.next().evaluate(cells, newStack, calculator));
                        }

                        String result = calculator.calculateExpression(str2.toString());

                        newCell.setValue(result);
                    } catch (Exception e) {
                        newCell.setValue("");
                        newCell.setError(e.getMessage());
                    }

                    newCell.setCalculated(true);

                    cells.set(col, row, newCell);
                    
                    str.append(newCell.getValue());
                }
            }
        }
        
        str.append(')');
        
        return str.toString();
    }
    
}
