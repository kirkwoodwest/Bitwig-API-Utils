package com.kirkwoodwest.utils;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class FileChooser {
  private int state = JFileChooser.ERROR_OPTION;

  public FileChooser() {
    EventQueue.invokeLater(new Runnable() {
      @Override
      public void run() {
        try {
          UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
        }

        JFileChooser chooser = new JFileChooser();
        chooser.addPropertyChangeListener(new PropertyChangeListener() {
          @Override
          public void propertyChange(PropertyChangeEvent evt) {
            System.out.println(evt.getPropertyName());
          }
        });
        chooser.addActionListener(new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            if (JFileChooser.CANCEL_SELECTION.equals(e.getActionCommand())) {
              state = JFileChooser.CANCEL_OPTION;
              SwingUtilities.windowForComponent((JFileChooser) e.getSource()).dispose();
            } else if (JFileChooser.APPROVE_SELECTION.equals(e.getActionCommand())) {
              state = JFileChooser.APPROVE_OPTION;
              SwingUtilities.windowForComponent((JFileChooser) e.getSource()).dispose();
            }
          }
        });
        JDialog dialog = new JDialog();
        dialog.setAlwaysOnTop(true);
        dialog.setTitle("Open it sucker");
        dialog.setModal(true);
        dialog.add(chooser);
        dialog.pack();
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
        switch (state) {

          case JFileChooser.APPROVE_OPTION:
            System.out.println("approved");
            break;
          case JFileChooser.CANCEL_OPTION:
            System.out.println("cancled");
            break;
          default:
            System.out.println("Broken");
            break;

        }
      }
    });
  }
}