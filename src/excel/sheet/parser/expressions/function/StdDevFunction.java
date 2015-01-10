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
import java.util.HashSet;

/**
 * Funkcja standardowego odchylenia
 * 
 * @author eplightning <eplightning at outlook dot com>
 */
public class StdDevFunction extends FunctionExpression {
    
    public StdDevFunction(AddressExpression address)
    {
        super(address, "std_dev");
    }

    @Override
    public String evaluate(CellAccessor cells, HashSet<Location> callStack, Calculator calculator) throws ParserException
    {
        StringBuilder str = new StringBuilder(1024);
        str.append('(');
        
        // wariancja
        VarFunction sum = new VarFunction(address);
        str.append(sum.evaluate(cells, callStack, calculator));
        
        // pierwiastek (trzy iteracje)
        str.append("~3");
        str.append(')');
        
        return str.toString();
    }
}
