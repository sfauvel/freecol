/**
 *  Copyright (C) 2002-2013   The FreeCol Team
 *
 *  This file is part of FreeCol.
 *
 *  FreeCol is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  FreeCol is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with FreeCol.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.sf.freecol.client.gui.panel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;
import java.util.logging.Logger;

import javax.swing.JComboBox;
import javax.swing.JTextArea;

import net.miginfocom.swing.MigLayout;
import net.sf.freecol.client.FreeColClient;
import net.sf.freecol.client.gui.GUI;
import net.sf.freecol.client.gui.i18n.Messages;
import net.sf.freecol.common.model.GoodsType;


/**
 * The panel that allows a choice of goods amount.
 */
public final class SelectAmountDialog extends FreeColOldDialog<Integer> implements ActionListener {

    @SuppressWarnings("unused")
    private static Logger logger = Logger.getLogger(SelectAmountDialog.class.getName());

    private static final int SELECT_CANCEL = -1;

    private static final int[] amounts = {20, 40, 50, 60, 80, 100};

    private final JTextArea question;

    private final JComboBox comboBox;

    /**
     * The constructor to use.
     */
    @SuppressWarnings("unchecked") // FIXME in Java7
    public SelectAmountDialog(FreeColClient freeColClient, GoodsType goodsType,
        int available, int defaultAmount, boolean needToPay) {
        super(freeColClient, new MigLayout("wrap 1", "", ""));

        setFocusCycleRoot(true);

        question = GUI.getDefaultTextArea(Messages.message("goodsTransfer.text"));

        if (needToPay) {
            final int gold = getMyPlayer().getGold();
            int price = getMyPlayer().getMarket().getCostToBuy(goodsType);
            available = Math.min(available, gold/price);
        }

        int defaultIndex = -1;
        Vector<Integer> values = new Vector<Integer>();
        for (int index = 0; index < amounts.length; index++) {
            if (amounts[index] < available) {
                if (amounts[index] == defaultAmount) defaultIndex = index;
                values.add(amounts[index]);
            } else {
                if (available == defaultAmount) defaultIndex = index;
                values.add(available);
                break;
            }
        }
        if (defaultAmount > 0 && defaultIndex < 0) {
            for (int index = 0; index < values.size(); index++) {
                if (defaultAmount < values.get(index)) {
                    values.insertElementAt(new Integer(defaultAmount), index);
                    defaultIndex = index;
                    break;
                }
            }
        }

        comboBox = new JComboBox(values);
        comboBox.setEditable(true);
        if (defaultIndex >= 0) comboBox.setSelectedIndex(defaultIndex);

        okButton.addActionListener(this);

        cancelButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent event) {
                    setResponse(new Integer(SELECT_CANCEL));
                }
            });

        add(question);
        add(comboBox, "wrap 20, growx");
        add(okButton, "span, split 2, tag ok");
        add(cancelButton, "tag cancel");
        
        setSize(getPreferredSize());

    }

    public void requestFocus() {
        cancelButton.requestFocus();
    }


    // Interface ActionListener

    /**
     * {@inheritDoc}
     */
    public void actionPerformed(ActionEvent event) {
        if (OK.equals(event.getActionCommand())) {
            Object item = comboBox.getSelectedItem();
            if (item instanceof Integer) {
                setResponse((Integer) item);
            } else if (item instanceof String) {
                try {
                    setResponse(Integer.valueOf((String) item));
                } catch (NumberFormatException e) {
                    // do nothing
                }
            }
        } else {
            super.actionPerformed(event);
        }
    }
}
