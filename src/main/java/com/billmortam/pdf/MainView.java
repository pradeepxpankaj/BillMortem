package com.billmortam.pdf;


import billmortam.cache.CacheManager;
import billmortam.cache.FileSpecification;
import billmortam.cache.RecentFileCacheManager;
import billmortam.pdf.Pdf;
import billmortam.pdf.PdfBoxReader;
import billmortam.pdf.PdfReader;
import billmortam.pdf.PdfReaderException;
import com.billmortam.pdf.util.PdfFileOpener;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.util.Set;

/**
 * Created by pp00344204 on 13/06/17.
 */
public class MainView {
    private JPanel panel1;
    private JButton button1;
    private JList lstRecentFilePaths;
    private JCheckBox useCacheCheckBox;
    private JLabel lblLogo;
    private final JFrame frame;
    private PdfFileOpener opener;
    private boolean canUseCache = true;

    private MainView(JFrame frame) {
        this.frame = frame;
        lstRecentFilePaths.setModel(getPathListModel(getRecentFilePath()));
        useCacheCheckBox.setSelected(true);
        initPdfFileOpener(frame);
        setFileChooserClickLister(button1);
        setRecentFileMouseClickListener();
        setCacheCheckBoxListener();
        setLogo();

    }

    private void setLogo() {
        try {
            lblLogo.setIcon(new ImageIcon(getImage("img_logo.png")));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Image getImage(String filename) throws IOException {
        String file = new File(PdfReaderView.RESOURCE_DIR + "/" + filename).getCanonicalPath();
//        Image img = ImageIO.read(new File(file));
        Image img = ImageIO.read(getClass().getResource("/img_logo.png") );
        img = img.getScaledInstance(284, 205, 2);
        return img;
    }

    private void setCacheCheckBoxListener() {
        useCacheCheckBox.addItemListener(e -> canUseCache = e.getStateChange() == ItemEvent.SELECTED);
    }

    private DefaultListModel<FileSpecification> getPathListModel(Set<FileSpecification> recentFilePath) {
        DefaultListModel<FileSpecification> model = new DefaultListModel<>();
        if (recentFilePath != null) {
            recentFilePath.forEach(model::addElement);
        }
        return model;
    }

    private Set<FileSpecification> getRecentFilePath() {
        CacheManager storage = RecentFileCacheManager.getManager();
        return (Set<FileSpecification>) storage.read(RecentFileCacheManager.RECENT_FILE_STORAGE_FILE);
    }

    private void setRecentFileMouseClickListener() {
        MouseListener mouseListener = new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int index = lstRecentFilePaths.locationToIndex(e.getPoint());
                    System.out.println("Double clicked on Item " + index);
                    opener.readFile(lstRecentFilePaths.getModel().getElementAt(index).toString());
                }
            }
        };
        lstRecentFilePaths.addMouseListener(mouseListener);
    }

    public static void main(String[] args) {
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        JFrame frame = new JFrame("MainView");
        frame.setLocation(dim.width / 2 - frame.getSize().width / 2 - 200, dim.height / 2 - frame.getSize().height / 2 - 200);
        frame.setContentPane(new MainView(frame).panel1);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(600, 600);
        frame.pack();
        frame.setVisible(true);
//        new MainView(frame);
    }

    private void setFileChooserClickLister(JButton btnFileChooser) {
        btnFileChooser.addActionListener(e -> {
//            openFileChooserDialog(frame);
            opener.open();
        });
    }

    private void initPdfFileOpener(JFrame frame) {
        opener = PdfFileOpener.getOpener(frame);
        opener.setListener((data) -> {
            if (data != null) {
                openPdfReader(data);
            }
        });
        opener.shouldCache(true);
    }

    private void openPdfReader(Pdf pdf) {
        close();
//        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        JFrame frame = new JFrame("PDF Reader");
//        frame.setLocation(dim.width/2-frame.getSize().width/2 - 200, dim.height/2-frame.getSize().height/2  -200);

        frame.setContentPane(new PdfReaderView(frame, pdf, canUseCache).panel1);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(500, 800);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
//        frame.setUndecorated(true);
        frame.pack();
        frame.setVisible(true);
    }

    private void close() {
        frame.dispose();
    }

    private String getChooseFilePath(JFileChooser fc) {
        File f = fc.getSelectedFile();
        return f.getPath();
    }

    private Pdf readDocument(final String filepath) {
        PdfReader reader = PdfBoxReader.getReader();
        try {
            return reader.read(filepath);
        } catch (PdfReaderException e1) {
            switch (e1.getExceptionType()) {
                case PdfReaderException.ExceptionType.INVALID_PDF_FILE:
                    onInvalidFileSelected();
                    break;
                case PdfReaderException.ExceptionType.PASSWORD_PROTECTED:
                    String message = "This Pdf is passwrod protected. Please enter password";
                    openPasswordInputDialog(message, filepath);
                    break;
            }
        }
        return null;
    }


    private Pdf readProtectedDocument(String filepath, String password) {
        PdfReader reader = PdfBoxReader.getReader();
        try {
            return reader.read(filepath, password);
        } catch (PdfReaderException e1) {
            switch (e1.getExceptionType()) {
                case PdfReaderException.ExceptionType.INVALID_PASSWORD:
                    String message = "Password is invalid. Please enter correct password";
                    openPasswordInputDialog(message, filepath);
                    break;
            }
        }
        return null;
    }

    private void openPasswordInputDialog(String message, final String filepath) {
        PdfPasswordGraber.PasswordListener listener = new PdfPasswordGraber.PasswordListener() {
            @Override
            public void grabPassword(String password) {
//                System.out.print("Password is: " + password);
                Pdf documentData = readProtectedDocument(filepath, password);
                if (documentData != null) {
//                    System.out.print(documentData);
                    documentData.setPassword(password);
                    openPdfReader(documentData);
                }
//                close();
            }

            @Override
            public void onDialogCancel() {
            }
        };

        PdfPasswordGraber dialog = new PdfPasswordGraber(listener, message);
        dialog.pack();
        dialog.setVisible(true);
        // System.exit(0);
    }

    private void onInvalidFileSelected() {
        JOptionPane.showMessageDialog(frame, "Please select a pdf file");
    }


}
