package extractor;

import java.awt.GridLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.io.IOException;

import model.*;
import model.log4j.*;
import model.mxml.*;
import model.xes.*;

public class InterfaceWindow extends JFrame {

    private static final long serialVersionUID = 3236307626298226499L;
    private Canonical associatedModel = null;

    public InterfaceWindow() {
        
        super("Test model extraction");
        
        final JTabbedPane tabbedPane = new JTabbedPane();
        final JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayout(4, 2, 1, 1));
        final JLabel fileChooseLabel = new JLabel("Event log file: ");
        mainPanel.add(fileChooseLabel);
        final JFileChooser fileChooser = new JFileChooser();
        FileNameExtensionFilter filter = 
            new FileNameExtensionFilter("*.xes, *.mxml, *.log", 
                    "xes", "mxml", "log");
        fileChooser.setFileFilter(filter);
        final JButton buttonOpenFile = new JButton("Open file...");
        buttonOpenFile.addActionListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                
                int eCode = fileChooser.showOpenDialog(mainPanel);
                if (fileChooser.getSelectedFile() == null) {
                    System.out.println("no file has been chosen: abort");
                    return;
                }
                
                String path = null;
                if (eCode == JFileChooser.APPROVE_OPTION) {
                    
                    path = fileChooser.getSelectedFile().getAbsolutePath();
                    System.out.println("opening file " + path + "...");
                }
                
                String extension = path.substring(
                        path.lastIndexOf(".")+1, path.length());
                switch (extension) {
                
                    case "log":
                        
                        LogLog4j log4j = LogLog4j.extractLogLog4j(path);
                        associatedModel = Translator.castLog4j(log4j);
                        break;
                        
                    case "mxml":
                        
                        LogMXML logMXML = LogMXML.extractLogMXML(path);
                        associatedModel = Translator.castMXML(logMXML);     
                        break;
                        
                    case "xes": 
                        
                        LogXES logXES = LogXES.extractLogXES(path);
                        associatedModel = Translator.castXES(logXES);
                        break;
                        
                    default: 
                        System.out.println("error: unknown file format");
                        return;
                }
            }
        });
        
        mainPanel.add(buttonOpenFile);        
        final JButton refineButton = new JButton("Refine model");
        refineButton.addActionListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                associatedModel.refineData();
            }
        });
        
        mainPanel.add(refineButton);
        final JButton visualizeButton = new JButton("Visualize Model");
        visualizeButton.addActionListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                
                TransSystem tSys = new TransSystem();
                tSys.emulateCanonical(associatedModel);
                GraphManager.transSystemToDot(tSys, "data/RESULT.dot");
                try {
                    Process dotProgram = new ProcessBuilder(
                        "dot.exe", "-Tpdf", "-oresult.pdf", "data/RESULT.dot").
                            start();
                    System.out.println("translating to PDF...");
                }
                
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        
        mainPanel.add(visualizeButton);
        final JPanel settingsPanel = new JPanel();
        
        tabbedPane.addTab("Main", mainPanel);
        tabbedPane.addTab("Settings", settingsPanel);
        this.add(tabbedPane);
        
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setPreferredSize(new Dimension(300, 560));
        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }
}
