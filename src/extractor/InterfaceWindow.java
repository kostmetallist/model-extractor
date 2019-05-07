package extractor;

import java.awt.GridLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.io.OutputStream;
import java.util.concurrent.TimeUnit;
import java.util.List;

import model.*;
import model.log4j.*;
import model.mxml.*;
import model.xes.*;

public class InterfaceWindow extends JFrame {

    private static final long serialVersionUID = 3236307626298226499L;
    private String workingDirectory = System.getProperty("user.dir");
    private VisualMode vMode = VisualMode.TRANSITION_SYSTEM;
    private long dotTimeoutMillis = 60000;
    
    private Canonical associatedModel = null;
    private List<ReferencedSequence> refSeqList = null;
    private TransSystem transSystem = null;
    private List<ActivityMap> actMapAtlas = null;
    
    private static enum VisualMode {
        TRANSITION_SYSTEM,
        ACTIVITY_MAP
    }
    
    private class LogStream extends OutputStream {
        private JTextArea logTextArea;
        
        public LogStream(JTextArea logTextArea) {
            this.logTextArea = logTextArea;
        }
         
        @Override
        public void write(int b) throws IOException {
            
            logTextArea.append(String.valueOf((char)b));
            logTextArea.setCaretPosition(logTextArea.getDocument().getLength());
        }
    }

    public InterfaceWindow() {
        
        super("Test model extraction");
        final InterfaceWindow thisInterface = this;
        
        final JTabbedPane tabbedPane = new JTabbedPane();
        final JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        
        final JPanel controlPanel = new JPanel(new GridLayout(5, 2, 10, 8));
        final JLabel fileChooseLabel = new JLabel("Specify event log file ");
        controlPanel.add(fileChooseLabel);
        final JButton buttonOpenFile = new JButton("Open file...");
        buttonOpenFile.addActionListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                
                JFileChooser fileChooser = new JFileChooser();
                FileNameExtensionFilter filter = 
                    new FileNameExtensionFilter("*.xes, *.mxml, *.log", 
                            "xes", "mxml", "log");
                fileChooser.setFileFilter(filter);
                
                int eCode = fileChooser.showOpenDialog(controlPanel);
                if (eCode != JFileChooser.APPROVE_OPTION) {
                    
                    System.out.println("no file has been chosen; aborting");
                    return;
                }
                
                String path = fileChooser.getSelectedFile().getAbsolutePath();
                System.out.println("opening file " + path + "...");
                String extension = path.substring(
                        path.lastIndexOf(".")+1, path.length());
                System.out.println("detected ." + extension + " extension");
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
                
                System.out.println("successfully extracted initial model");
                
                // renewing model data 
                thisInterface.vMode = VisualMode.TRANSITION_SYSTEM;
                thisInterface.refSeqList = null;
                thisInterface.transSystem = null;
                thisInterface.actMapAtlas = null;
            }
        });
        
        controlPanel.add(buttonOpenFile);
        final JLabel refineLabel = new JLabel("Apply model refinement?");
        controlPanel.add(refineLabel);
        final JButton refineButton = new JButton("Refine");
        
        controlPanel.add(refineButton);
        final JLabel generalizeLabel = new JLabel("Generalize model?");
        controlPanel.add(generalizeLabel);
        final JButton generalizeButton = new JButton("Generalize");
        generalizeButton.addActionListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                
                thisInterface.vMode = VisualMode.ACTIVITY_MAP;                
                thisInterface.refSeqList = 
                        ReferencedSequence.generalizeModel(associatedModel);
                
                System.out.println("successfully generalized model");
                System.out.println("--former cases number: " + 
                        associatedModel.getTaggedSequences().size());
                System.out.println("--generalized cases number: " + 
                        refSeqList.size());
            }
        });
        
        controlPanel.add(generalizeButton);
        
        final JLabel exportPdfLabel = new JLabel("Export to PDF?");
        controlPanel.add(exportPdfLabel);
        final JButton exportPdfButton = new JButton("Export");
        exportPdfButton.addActionListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                
                System.out.println("translating to PDF...");
                String outputBasePath = workingDirectory + File.separator;
                int artifactNumber = 0;
                
                if (thisInterface.vMode == VisualMode.TRANSITION_SYSTEM) {
                    
                    artifactNumber = 1;
                    transSystem = new TransSystem();
                    transSystem.emulateCanonical(associatedModel); 
                    GraphManager.transSystemToDot(transSystem, 
                            outputBasePath + "result0.dot");
                }
                
                else {
                    
                    List<ActivityMap> actMaps = ActivityMap.
                        emulateReferencedSequences(thisInterface.refSeqList);
                    artifactNumber = actMaps.size();
                    thisInterface.actMapAtlas = actMaps;
                           
                    for (int i = 0; i < artifactNumber; ++i) {
                        GraphManager.activityMapToDot(actMaps.get(i), 
                                outputBasePath + "result" + i + ".dot");
                    }
                }
                
                try {
                    for (int i = 0; i < artifactNumber; ++i) {

                        System.out.println("PDFying " + i + "th artifact...");
                        Process dotProgram = new ProcessBuilder(
                                "dot.exe", "-Tpdf", "-O", 
                                outputBasePath + "result" + i + ".dot").start();
                        boolean waitStatus = dotProgram.waitFor(
                                dotTimeoutMillis, TimeUnit.MILLISECONDS);
                        
                        if (waitStatus) {
                            System.out.println(i + "th artifact is translated");
                        }
                        
                        else {
                            System.out.println("dot to pdf translation " + 
                                    "timeout exceeded; aborting");
                            dotProgram.destroy();
                        }
                    }
                    
                    System.out.println("translation is done");
                }
                
                catch (IOException e) {
                    e.printStackTrace();
                }
                
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        
        controlPanel.add(exportPdfButton);
        
        final JLabel genTestsLabel = new JLabel("Generate test cases?");
        controlPanel.add(genTestsLabel);
        final JButton genTestsButton = new JButton("Generate");
        genTestsButton.addActionListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                
                System.out.println("preparing for test case generating...");
                if (thisInterface.vMode == VisualMode.TRANSITION_SYSTEM) {
                    
                    thisInterface.transSystem.exportTestData(workingDirectory + 
                            File.separator + "testcases0.xml");
                    System.out.println("testcases0.xml file is ready");
                }
                
                else {
                    for (int i = 0; i < thisInterface.actMapAtlas.size(); ++i) {
                        actMapAtlas.get(i).exportTestData(workingDirectory + 
                            File.separator + "testcases" + i + ".xml");
                        System.out.println("testcases" + i + 
                                ".xml file is ready");
                    }
                }
            }
        });
        
        controlPanel.add(genTestsButton);
        final JTextArea logTextArea = 
                new JTextArea("===========< PROGRAM LOG >===========", 40, 80);
        logTextArea.setEditable(false);
        PrintStream printStream = 
                new PrintStream(new LogStream(logTextArea));
//        this.stdOut = System.out;
        
        // redirecting standard streams to log area stream
        System.setErr(printStream);
        System.setOut(printStream);
        final JScrollPane logPane = new JScrollPane(logTextArea);
        System.out.println();
        
        mainPanel.add(controlPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        mainPanel.add(logPane);
        
        // ---------------------Settings (general)------------------------- //
        
        final JPanel settingsGeneralPanel = 
                new JPanel(new GridLayout(6, 1, 0, 10));
        
        final JLabel workingDirLabel = new JLabel("Working directory");
        settingsGeneralPanel.add(workingDirLabel);
        final JPanel workingDirPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        final JTextField workingDirField = 
                new JTextField(this.workingDirectory);
        workingDirField.setEditable(false);
        workingDirPanel.add(workingDirField);
        final JButton changeDirButton = new JButton("Change...");
        changeDirButton.addActionListener(new ActionListener() {
            
            @Override 
            public void actionPerformed(ActionEvent actionEvent) {
                
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                
                int eCode = fileChooser.showOpenDialog(settingsGeneralPanel);
                if (eCode != JFileChooser.APPROVE_OPTION) {
                    
                    System.out.println("no directory's been chosen; aborting");
                    return;
                }
                
                thisInterface.workingDirectory = 
                        fileChooser.getSelectedFile().getAbsolutePath();
                System.out.println("setting " + 
                        thisInterface.workingDirectory + " as a working dir");
                workingDirField.setText(workingDirectory);
            }
        });
        workingDirPanel.add(changeDirButton);
        settingsGeneralPanel.add(workingDirPanel);
        
        final JLabel log4jMappingLabel = new JLabel("Mapping for log4j logs");
        settingsGeneralPanel.add(log4jMappingLabel);
        final JTextField log4jMappingField = 
                new JTextField("%d %t %C %M %pid %m");
        settingsGeneralPanel.add(log4jMappingField);
        
        final JLabel timestampFormatLabel = new JLabel("Timestamp pattern");
        settingsGeneralPanel.add(timestampFormatLabel);
        final JTextField timestampFormatField = 
                new JTextField("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        settingsGeneralPanel.add(timestampFormatField);
        
        // --------------------Settings (filtration)----------------------- //
        
        final JPanel settingsFiltrationPanel = 
                new JPanel(new GridLayout(4, 2, 10, 10));
        final JLabel prefixFreqThresholdLabel = 
                new JLabel("Prefix frequency threshold (%) ");
        settingsFiltrationPanel.add(prefixFreqThresholdLabel);
        final JTextField prefixFreqThresholdField = new JTextField("15");
        settingsFiltrationPanel.add(prefixFreqThresholdField);
        
        final JLabel postfixFreqThresholdLabel = 
                new JLabel("Postfix frequency threshold (%) ");
        settingsFiltrationPanel.add(postfixFreqThresholdLabel);
        final JTextField postfixFreqThresholdField = new JTextField("10");
        settingsFiltrationPanel.add(postfixFreqThresholdField);
        
        final JLabel missedEventFreqThresholdLabel = 
                new JLabel("Missed event frequency threshold (%) ");
        settingsFiltrationPanel.add(missedEventFreqThresholdLabel);
        final JTextField missedEventFreqThresholdField = new JTextField("15");
        settingsFiltrationPanel.add(missedEventFreqThresholdField);
        
        final JLabel timeDeviationLabel = 
                new JLabel("Acceptable missed event time deviation (ms)");
        settingsFiltrationPanel.add(timeDeviationLabel);
        final JTextField timeDeviationField = new JTextField("512");
        settingsFiltrationPanel.add(timeDeviationField);
        
        tabbedPane.addTab("Main", mainPanel);
        tabbedPane.addTab("Settings (general)", settingsGeneralPanel);
        tabbedPane.addTab("Settings (filtration)", settingsFiltrationPanel);
        this.add(tabbedPane);
        
        refineButton.addActionListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                
                associatedModel.setDeltaT(Long.parseLong(
                    timeDeviationField.getText()));
                associatedModel.setPrefixFrequencyThreshold(
                    Double.parseDouble(prefixFreqThresholdField.getText()));
                associatedModel.setPostfixFrequencyThreshold(
                    Double.parseDouble(postfixFreqThresholdField.getText()));
                associatedModel.setMissedFrequencyThreshold(
                    Double.parseDouble(
                        missedEventFreqThresholdField.getText()));
                associatedModel.refineData();
            }
        });
        
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setPreferredSize(new Dimension(500, 600));
        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }
}