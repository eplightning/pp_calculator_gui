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
package excel.sheet.parser.expressions;

import excel.sheet.Cell;
import excel.sheet.CellAccessor;
import excel.sheet.Location;
import excel.sheet.parser.Expression;
import excel.sheet.parser.ParserException;
import java.util.HashSet;

/**
 * Wyrażenie adresu
 * 
 * @author eplightning <eplightning at outlook dot com>
 */
public class AddressExpression implements Expression {

    protected Expression left;
    protected boolean    leftClosed;
    protected Expression right;
    protected boolean    rightClosed;
    
    public AddressExpression(Expression left, Expression right, boolean leftClosed, boolean rightClosed)
    {
        this.left = left;
        this.right = right;
        this.leftClosed = leftClosed;
        this.rightClosed = rightClosed;
    }
    
    @Override
    public int evaluateAsInt(CellAccessor cells, HashSet<Location> callStack) throws ParserException
    {
        String out = evaluate(cells, callStack);
        
        return Integer.parseInt(out, 10);
    }

    @Override
    public String evaluate(CellAccessor cells, HashSet<Location> callStack) throws ParserException
    {
        if (left instanceof RangeExpression || right instanceof RangeExpression)
            throw new ParserException("Range addresses cannot be evaluated");
        
        int col = left.evaluateAsInt(cells, callStack);
        int row = right.evaluateAsInt(cells, callStack);
        Location loc = new Location(col, row);
        
        if (col < 1 || row < 1)
            throw new ParserException("Address must be greater than (0, 0)");
        
        if (callStack.contains(loc))
            throw new ParserException("Cyclic dependency detected");
        
        Cell cell = cells.get(col, row);
        
        String out;
        
        if (cell == null) {
            out = "";
        } else if (cell.isOrdinaryText()) {
            out = cell.getFormula();
        } else if (cell.isCalculated()) {
            out = cell.getValue();
        } else {
            Cell newCell = new Cell(cell);
            
            HashSet<Location> newStack = (HashSet) callStack.clone();
            newStack.add(loc);
            
            // try {
            // TODO: some parsing and shi'
            // } catch (Exception e) {
            //     newCell.setValue("");
            //     newCell.setError(e.getMessage());
            // }
            
            newCell.setCalculated(true);
            
            cells.set(col, row, newCell);
            out = newCell.getValue();
        }
        
        return out;
    }

    @Override
    public void relativeMove(int col, int row)
    {
        if (left instanceof NumExpression) {
            if (col != 0) {
                NumExpression expr = (NumExpression) left;
                expr.setValue(expr.getValue() + col);
            }
        } else {
            left.relativeMove(col, row);
        }
        
        if (right instanceof NumExpression) {
            if (row != 0) {
                NumExpression expr = (NumExpression) left;
                expr.setValue(expr.getValue() + row);
            }
        } else {
            right.relativeMove(col, row);
        }
    }

    @Override
    public String evaluateAsFormula()
    {
        StringBuilder str = new StringBuilder(20);
        
        str.append('$');
        str.append(leftClosed ? '[' : '(');
        str.append(left.evaluateAsFormula());
        str.append(", ");
        str.append(right.evaluateAsFormula());
        str.append(rightClosed ? ']' : ')');
        
        return str.toString();
    }
    
}
