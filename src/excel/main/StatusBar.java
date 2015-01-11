/*
 * The MIT License
 *
 * Copyright 2014 eplightning.
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
package excel.main;

import java.awt.Dimension;
import java.awt.FlowLayout;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

/**
 * Status bar bo Swing nie ma swojego lol
 * 
 * @author eplightning
 */
public class StatusBar extends JPanel {

    protected JLabel state;

    public StatusBar()
    {
        setLayout(new FlowLayout(FlowLayout.LEADING));
        setPreferredSize(new Dimension(-1, 30));

        state = new JLabel("Gotowe", JLabel.LEFT);
        add(state);
    }

    public void setState(final String newState)
    {
        Runnable operation = new Runnable() {
            @Override
            public void run()
            {
                state.setText(newState);
            }
        };

        if (SwingUtilities.isEventDispatchThread()) {
            operation.run();
        } else {
            SwingUtilities.invokeLater(operation);
        }
    }
}
