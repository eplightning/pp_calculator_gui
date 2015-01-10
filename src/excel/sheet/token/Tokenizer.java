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
package excel.sheet.token;

import excel.sheet.token.tokens.*;
import java.util.ArrayList;

/**
 * Tokenizer
 * 
 * @author eplightning <eplightning at outlook dot com>
 */
public class Tokenizer {
    
    /**
     * Wystąpienie kropki
     */
    protected boolean dotBefore;
    
    /**
     * Ilość otwartych funkcji
     */
    protected int openedFunctions;
    
    /**
     * String builder dla wyrażeń kalk
     */
    protected StringBuilder calc;
    
    /**
     * String builder dla nazw funkcji
     */
    protected StringBuilder functionName;
    
    /**
     * String builder dla liczb naturalnych
     */
    protected StringBuilder integer;
    
    /**
     * Wynik
     */
    protected ArrayList<Token> tokens;
    
    public Tokenizer()
    {
        integer = new StringBuilder();
        functionName = new StringBuilder();
        calc = new StringBuilder();
    }
    
    public ArrayList<Token> tokenize(String input) throws TokenizerException
    {
        // przywracamy do czystego stanu
        openedFunctions = 0;
        dotBefore = false;
        tokens = new ArrayList<>();
        integer.setLength(0);
        functionName.setLength(0);
        calc.setLength(0);
        
        // znak po znaku
        int len = input.length();
        char chr;
        int it = 0;
        
        if (len > 0 && input.charAt(0) == '=')
            it = 1;
        
        for (; it < len; it++) {
            chr = input.charAt(it);
            
            // stan 1: zewnątrz
            if (openedFunctions <= 0) {
                if (isNameCharacter(chr)) {
                    if (calc.length() > 0)
                        finishCalcPortion();
                    
                    functionName.append(chr);
                } else if (isBracketOpen(chr) && functionName.length() > 0) {
                    finishFunctionStart(chr);
                    
                    // przechodzimy stan parsowania wyrażeń excela
                    openedFunctions = 1;
                } else {
                    if (functionName.length() > 0)
                        throw new TokenizerException("Function name must followed by bracket (without any whitespaces in between)", it);
                    
                    calc.append(chr);
                }
            // stan 2: wewnątrz - wyrażenia arkusza
            } else {
                if (isBracketEnd(chr)) {
                    if (integer.length() > 0)
                        finishNumber();
                    
                    if (functionName.length() > 0)
                        throw new TokenizerException("Function name must followed by bracket (without any whitespaces in between)", it);
                    
                    if (dotBefore)
                        throw new TokenizerException("Odd number of dots", it);
                    
                    finishFunctionEnd(chr);
                    
                    openedFunctions--;
                } else if (isWhitespace(chr)) {
                    if (integer.length() > 0)
                        finishNumber();
                    
                    if (functionName.length() > 0)
                        throw new TokenizerException("Function name must followed by bracket (without any whitespaces in between)", it);
                    
                    if (dotBefore)
                        throw new TokenizerException("Odd number of dots", it);
                } else if (isNameCharacter(chr)) {
                    if (integer.length() > 0)
                        throw new TokenizerException("Letter found while reading number", it);
                    
                    if (dotBefore)
                        throw new TokenizerException("Odd number of dots", it);
                    
                    functionName.append(chr);
                } else if (isBracketOpen(chr)) {
                    if (functionName.length() < 0)
                        throw new TokenizerException("Bracket opened without function name read", it);
                    
                    openedFunctions++;
                    finishFunctionStart(chr);
                } else if (isComma(chr)) {
                    if (integer.length() > 0)
                        finishNumber();
                    
                    if (functionName.length() > 0)
                        throw new TokenizerException("Function name must followed by bracket (without any whitespaces in between)", it);
                    
                    if (dotBefore)
                        throw new TokenizerException("Odd number of dots", it);
                    
                    finishSimpleToken(TokenType.COMMA);
                } else if (isDot(chr)) {
                    if (integer.length() > 0)
                        finishNumber();
                    
                    if (functionName.length() > 0)
                        throw new TokenizerException("Function name must followed by bracket (without any whitespaces in between)", it);
                    
                    if (dotBefore)
                        finishSimpleToken(TokenType.RANGE);
                    
                    dotBefore = !dotBefore;
                } else if (isDigit(chr)) {
                    if (functionName.length() > 0)
                        throw new TokenizerException("Function name must followed by bracket (without any whitespaces in between)", it);
                    
                    if (dotBefore)
                        throw new TokenizerException("Odd number of dots");
                    
                    integer.append(chr);
                } else {
                    throw new TokenizerException("Invalid character inside function", it);
                }
            }
        }
        
        // kończymy
        if (openedFunctions > 0) {
            throw new TokenizerException(String.format("Missing closing bracket for %d functions", openedFunctions));
        } else {
            if (functionName.length() > 0)
                throw new TokenizerException("Unexpected string at the end of formula");
            
            if (calc.length() > 0)
                finishCalcPortion();
        }
        
        return tokens;
    }
    
    protected void finishCalcPortion()
    {
        tokens.add(new CalcToken(calc.toString()));
        calc.setLength(0);
    }
    
    protected void finishFunctionEnd(char bracket)
    {
        tokens.add(new FunctionEndToken(bracket == ']'));
    }
    
    protected void finishFunctionStart(char bracket)
    {
        tokens.add(new FunctionToken(functionName.toString(), bracket == '['));
        functionName.setLength(0);
    }
    
    protected void finishNumber()
    {
        tokens.add(new IntegerToken(integer.toString()));
        integer.setLength(0);
    }
    
    protected void finishSimpleToken(TokenType type)
    {
        tokens.add(new SimpleToken(type));
    }
    
    protected boolean isBracketEnd(char chr)
    {
        return (chr == ')' || chr == ']');
    }
    
    protected boolean isBracketOpen(char chr)
    {
        return (chr == '(' || chr == '[');
    }
    
    protected boolean isComma(char chr)
    {
        return chr == ',';
    }
    
    protected boolean isDigit(char chr)
    {
        return (chr >= '0' && chr <= '9');
    }
    
    protected boolean isDot(char chr)
    {
        return chr == '.';
    }
    
    protected boolean isNameCharacter(char chr)
    {
        return ((chr >= 'a' && chr <= 'z') || (chr >= 'A' && chr <= 'Z') || chr == '$' || chr == '_');
    }
    
    protected boolean isWhitespace(char chr)
    {
        return (chr == ' ' || chr == '\n' || chr == '\r');
    }
}
