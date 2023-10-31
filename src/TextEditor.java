import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.filechooser.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.EditorKit;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleConstants.ColorConstants;
import javax.swing.text.StyledDocument;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;

public class TextEditor extends JFrame implements ActionListener, CaretListener, AdjustmentListener, KeyListener {

	ArrayList<JTextPane> textPanes = new ArrayList<JTextPane>();
	//JTextPane textArea[];
	//JScrollPane textAreaScrollPane[];
	ArrayList<JScrollPane> scrollPanes = new ArrayList<JScrollPane>();
	JScrollPane mainScrollPane;
	JLabel fontLabel;
	JSpinner fontSizeSpinner;
	JTextField fontSizeSpinnerTextField;
	JButton fontColorButton;
	JButton fontBoldButton;
	JButton fontItalicButton;
	JButton fontUnderlineButton;
	JComboBox<?> fontFamilyBox;
	JTextField fontFamilyBoxTextField;
	JPanel headerPanel;
	JPanel headerInnerPanel;
	JPanel bodyPanel;

	JMenuBar menuBar;
	JMenu fileMenu;
	JMenuItem newItem;
	JMenuItem openItem;
	JMenuItem saveItem;
	JMenuItem exitItem;
	
	SimpleAttributeSet attr;
	
	int caretPosition = 0;
	int start;
	int end;
	boolean justSelected = false;
	
	JTextPane currentTextPane;

	TextEditor() {
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setTitle("Poop Text Editor");
		this.setSize(700, 900);
		this.setLayout(new BorderLayout());
		this.setLocationRelativeTo(null);

		headerPanel = new JPanel();
		headerPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 15));
		headerPanel.setBackground(new Color(255, 255, 255));
		headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
		
		bodyPanel = new JPanel();
		bodyPanel.setLayout(new FlowLayout());
		bodyPanel.setBackground(new Color(230, 230, 230));
		
		textPanes.add(new JTextPane());
		currentTextPane = textPanes.get(0);
				
		attr = new SimpleAttributeSet();
		StyleConstants.setFontSize(attr, 14);
		StyleConstants.setFontFamily(attr, "Times New Roman");
		
		textPanes.get(0).setCharacterAttributes(attr, rootPaneCheckingEnabled);
		//textPanes.get(0).setPreferredSize(new Dimension(620, 877));
		textPanes.get(0).addCaretListener(this);
		textPanes.get(0).addKeyListener(this);
		/*textPanes.get(0).addCaretListener(new CaretListener() {

			@Override
			public void caretUpdate(CaretEvent e) {
				
				
		});*/
		
		scrollPanes.add(new JScrollPane(textPanes.get(0), ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER));
		scrollPanes.get(0).setPreferredSize(new Dimension(620, 877));
		scrollPanes.get(0).getVerticalScrollBar().addAdjustmentListener(this);
		
		//scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		
		//scrollPane = new JScrollPane(textArea);
		//scrollPane.setPreferredSize(new Dimension(620, 877));
		//scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		//scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		//scrollPane.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener() {
		   // @Override
		    //public void adjustmentValueChanged(AdjustmentEvent ae) {
		        //int extent = scrollPane.getVerticalScrollBar().getModel().getExtent();
		        //System.out.println("Value: " + (scrollPane.getVerticalScrollBar().getValue()+extent) + " Max: " + scrollPane.getVerticalScrollBar().getMaximum());
		        //if(textArea.getText().length() > 0) {
		        	//textArea.remove(textArea.getCaretPosition() - 2);
					//try {
						//int lastLineBreak = textArea.getDocument().getText(0, textArea.getDocument().getLength()).lastIndexOf('\n');
						//textArea.getDocument().remove(lastLineBreak, textArea.getDocument().getLength() - lastLineBreak);
					//} catch (BadLocationException e) {
						//e.printStackTrace();
					//}		        	
		        //}
		    //}
		//});
		
		mainScrollPane = new JScrollPane(bodyPanel);
		mainScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		mainScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		
		fontLabel = new JLabel("Font: ");

		fontSizeSpinner = new JSpinner();
		fontSizeSpinner.setPreferredSize(new Dimension(50, 25));
		fontSizeSpinner.setValue(14);
		fontSizeSpinnerTextField = ((JSpinner.DefaultEditor)fontSizeSpinner.getEditor()).getTextField();
		fontSizeSpinner.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				
				if (justSelected != true) {
					
					fontSizeSpinnerTextField.setForeground(null);
					StyleConstants.setFontSize(attr, (int) fontSizeSpinner.getValue());
					currentTextPane.setCharacterAttributes(attr, Boolean.TRUE);
		
				} else {
					justSelected = false;
				}
			}

		});

		fontColorButton = new JButton("Color");
		fontColorButton.addActionListener(this);
		fontColorButton.setBackground(new Color(255, 255, 255));
		
		fontBoldButton = new JButton("Bold");
		fontBoldButton.addActionListener(this);
		fontBoldButton.setBackground(new Color(255, 255, 255));
		
		fontItalicButton = new JButton("Italic");
		fontItalicButton.addActionListener(this);
		fontItalicButton.setBackground(new Color(255, 255, 255));
		
		fontUnderlineButton = new JButton("Underline");
		fontUnderlineButton.addActionListener(this);
		fontUnderlineButton.setBackground(new Color(255, 255, 255));

		String[] fonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();

		fontFamilyBox = new JComboBox<Object>(fonts);
		fontFamilyBox.addActionListener(this);
		fontFamilyBox.setEditable(true);
		fontFamilyBox.setSelectedItem("Times New Roman");
		fontFamilyBoxTextField = (JTextField)fontFamilyBox.getEditor().getEditorComponent();

		// ----- menubar -----

		menuBar = new JMenuBar();
		fileMenu = new JMenu("File");
		newItem = new JMenuItem("New");
		openItem = new JMenuItem("Open");
		saveItem = new JMenuItem("Save");
		exitItem = new JMenuItem("Exit");
		
		newItem.addActionListener(this);
		openItem.addActionListener(this);
		saveItem.addActionListener(this);
		exitItem.addActionListener(this);

		fileMenu.add(newItem);
		fileMenu.add(openItem);
		fileMenu.add(saveItem);
		fileMenu.add(exitItem);
		menuBar.add(fileMenu);

		// ----- /menubar -----

		this.setJMenuBar(menuBar);
		
		this.add(headerPanel, BorderLayout.NORTH);
		//headerPanel.add(fontLabel);
		
		headerPanel.add(fontColorButton);
		headerPanel.add(fontFamilyBox);
		headerPanel.add(fontSizeSpinner);
		headerPanel.add(fontBoldButton);
		headerPanel.add(fontItalicButton);
		headerPanel.add(fontUnderlineButton);
		
		this.add(mainScrollPane, BorderLayout.CENTER);
		//bodyPanel.add(mainScrollPane, BorderLayout.CENTER);
		bodyPanel.add(scrollPanes.get(0));
		
		this.setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		if (e.getSource() == fontColorButton) {
			JColorChooser colorChooser = new JColorChooser();

			Color color = colorChooser.showDialog(null, "Choose a color", Color.black);

			currentTextPane.setForeground(color);
		}

		// ---------------------------------------------------------------------------------------------------------------------------
		// ----------------------SET FONT FAMILY--------------------------------------------------------------------------------------
		// ---------------------------------------------------------------------------------------------------------------------------

		if (e.getSource() == fontFamilyBox) {

			if (justSelected != true) {

				if (fontFamilyBoxTextField != null)
					fontFamilyBoxTextField.setForeground(null);
				StyleConstants.setFontFamily(attr, (String) fontFamilyBox.getSelectedItem());
				currentTextPane.setCharacterAttributes(attr, rootPaneCheckingEnabled);

			} else {
				justSelected = false;
			}
		}

		// ---------------------------------------------------------------------------------------------------------------------------
		// ----------------------SET BOLD FONT----------------------------------------------------------------------------------------
		// ---------------------------------------------------------------------------------------------------------------------------

		if (e.getSource() == fontBoldButton) {

			if (fontBoldButton.getBackground().equals(new Color(255, 255, 255))) {

				fontBoldButton.setBackground(new Color(230, 230, 230));
				StyleConstants.setBold(attr, true);
				currentTextPane.setCharacterAttributes(attr, rootPaneCheckingEnabled);

			} else {

				fontBoldButton.setBackground(new Color(255, 255, 255));
				StyleConstants.setBold(attr, false);
				currentTextPane.setCharacterAttributes(attr, rootPaneCheckingEnabled);
			}

		}

		// ---------------------------------------------------------------------------------------------------------------------------
		// ----------------------SET ITALIC FONT-------------------------------------------------------------------------------------
		// ---------------------------------------------------------------------------------------------------------------------------

		if (e.getSource() == fontItalicButton) {

			if (fontItalicButton.getBackground().equals(new Color(255, 255, 255))) {

				fontItalicButton.setBackground(new Color(230, 230, 230));
				StyleConstants.setItalic(attr, true);
				currentTextPane.setCharacterAttributes(attr, rootPaneCheckingEnabled);

			} else {

				fontItalicButton.setBackground(new Color(255, 255, 255));
				StyleConstants.setItalic(attr, false);
				currentTextPane.setCharacterAttributes(attr, rootPaneCheckingEnabled);
			}
		}

		// ---------------------------------------------------------------------------------------------------------------------------
		// ----------------------SET UNDERLINE FONT-------------------------------------------------------------------------------------
		// ---------------------------------------------------------------------------------------------------------------------------

		if (e.getSource() == fontUnderlineButton) {

			if (fontUnderlineButton.getBackground().equals(new Color(255, 255, 255))) {

				fontUnderlineButton.setBackground(new Color(230, 230, 230));
				StyleConstants.setUnderline(attr, true);
				currentTextPane.setCharacterAttributes(attr, rootPaneCheckingEnabled);

			} else {

				fontUnderlineButton.setBackground(new Color(255, 255, 255));
				StyleConstants.setUnderline(attr, false);
				currentTextPane.setCharacterAttributes(attr, rootPaneCheckingEnabled);
			}
		}

		// ---------------------------------------------------------------------------------------------------------------------------
		// ----------------------NEW-------------------------------------------------------------------------------------------------
		// ---------------------------------------------------------------------------------------------------------------------------

		if (e.getSource() == newItem) {

			currentTextPane.setText("");

			StyleConstants.setFontSize(attr, 14);
			StyleConstants.setFontFamily(attr, "Times New Roman");
			attr.removeAttribute(StyleConstants.Bold);
			attr.removeAttribute(StyleConstants.Italic);
			attr.removeAttribute(StyleConstants.Underline);
			attr.removeAttribute(StyleConstants.Foreground);

			currentTextPane.setCharacterAttributes(attr, true);

		}

		// ---------------------------------------------------------------------------------------------------------------------------
		// ----------------------OPEN-------------------------------------------------------------------------------------------------
		// ---------------------------------------------------------------------------------------------------------------------------

		if (e.getSource() == openItem) {
			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setCurrentDirectory(new File("."));
			FileNameExtensionFilter filter = new FileNameExtensionFilter("Text files", "txt");
			fileChooser.setFileFilter(filter);

			int response = fileChooser.showOpenDialog(null);

			if (response == JFileChooser.APPROVE_OPTION) {
				InputStream file = null;

				try {
					file = new FileInputStream(fileChooser.getSelectedFile().getAbsolutePath());
				} catch (FileNotFoundException e1) {
					e1.printStackTrace();
				}

				try {

					EditorKit editorKit = new HTMLEditorKit();
					StyledDocument doc = (StyledDocument) editorKit.createDefaultDocument();

					currentTextPane.setEditorKit(editorKit);
					System.out.println("read file");
					currentTextPane.read(file, doc);
					System.out.println("set doc");
					currentTextPane.setDocument(doc);
					System.out.println("new doc");

				} catch (IOException e1) {
					e1.printStackTrace();
				} finally {

					try {
						file.close();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			}
		}

		// ---------------------------------------------------------------------------------------------------------------------------
		// ----------------------SAVE-------------------------------------------------------------------------------------------------
		// ---------------------------------------------------------------------------------------------------------------------------

		if (e.getSource() == saveItem) {
			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setCurrentDirectory(new File("."));

			int response = fileChooser.showSaveDialog(null);

			if (response == JFileChooser.APPROVE_OPTION) {
				File file;
				PrintWriter fileOut = null;

				file = new File(fileChooser.getSelectedFile().getAbsolutePath());
				try {
					fileOut = new PrintWriter(file);

					EditorKit editorKit = new HTMLEditorKit();
					editorKit.write(fileOut, textPanes.get(0).getStyledDocument(), 0,
							currentTextPane.getStyledDocument().getLength());

				} catch (IOException | BadLocationException e1) {
					e1.printStackTrace();
				} finally {
					fileOut.close();
				}
			}
		}

		// ---------------------------------------------------------------------------------------------------------------------------
		// ----------------------EXIT-------------------------------------------------------------------------------------------------
		// ---------------------------------------------------------------------------------------------------------------------------

		if (e.getSource() == exitItem) {
			System.exit(0);
		}
	}

	@Override
	public void caretUpdate(CaretEvent e) {

		
		JTextPane textArea = (JTextPane) e.getSource();
		currentTextPane = textArea;
		StyledDocument doc = textArea.getStyledDocument();
		
		//System.out.println(textArea.getCaretPosition());

		if (textArea.getSelectedText() == null) {

			if (textArea.getText().length() > 0 && textArea.getCaretPosition() != caretPosition) {

				attr.addAttribute(StyleConstants.Bold, doc.getCharacterElement(textArea.getCaretPosition() - 1)
						.getAttributes().getAttribute(StyleConstants.Bold));
				attr.addAttribute(StyleConstants.Italic, doc.getCharacterElement(textArea.getCaretPosition() - 1)
						.getAttributes().getAttribute(StyleConstants.Italic));
				Object und = doc.getCharacterElement(textArea.getCaretPosition() - 1).getAttributes()
						.getAttribute(StyleConstants.Underline);
				if (und == null)
					attr.addAttribute(StyleConstants.Underline, false);
				else
					attr.addAttribute(StyleConstants.Underline, doc.getCharacterElement(textArea.getCaretPosition() - 1)
							.getAttributes().getAttribute(StyleConstants.Underline));

				// FONT SIZE
				justSelected = true;
				fontSizeSpinnerTextField.setForeground(null);
				fontSizeSpinner
						.setValue(textArea.getStyledDocument().getCharacterElement(textArea.getCaretPosition() - 1)
								.getAttributes().getAttribute(StyleConstants.FontSize));

				// FONT FAMILY
				justSelected = true;
				fontFamilyBoxTextField.setForeground(null);
				fontFamilyBox.setSelectedItem(
						(String) textArea.getStyledDocument().getCharacterElement(textArea.getCaretPosition() - 1)
								.getAttributes().getAttribute(StyleConstants.FontFamily));

				// FONT BOLD
				if ((Boolean) textArea.getStyledDocument().getCharacterElement(textArea.getCaretPosition() - 1)
						.getAttributes().getAttribute(StyleConstants.Bold)) {
					fontBoldButton.setBackground(new Color(230, 230, 230));
				} else {
					fontBoldButton.setBackground(new Color(255, 255, 255));
				}

				// FONT ITALIC
				if ((Boolean) textArea.getStyledDocument().getCharacterElement(textArea.getCaretPosition() - 1)
						.getAttributes().getAttribute(StyleConstants.Italic)) {
					fontItalicButton.setBackground(new Color(230, 230, 230));
				} else {
					fontItalicButton.setBackground(new Color(255, 255, 255));
				}

				// FONT UNDERLINE
				if (textArea.getStyledDocument().getCharacterElement(textArea.getCaretPosition() - 1).getAttributes()
						.getAttribute(StyleConstants.Underline) == null)
					fontUnderlineButton.setBackground(new Color(255, 255, 255));
				else {
					if ((Boolean) textArea.getStyledDocument().getCharacterElement(textArea.getCaretPosition() - 1)
							.getAttributes().getAttribute(StyleConstants.Underline))
						fontUnderlineButton.setBackground(new Color(230, 230, 230));
					else
						fontUnderlineButton.setBackground(new Color(255, 255, 255));
				}
			}

		} else {

			start = textArea.getSelectionStart();
			end = textArea.getSelectionEnd();
			attr.addAttribute(StyleConstants.Bold,
					doc.getCharacterElement(start).getAttributes().getAttribute(StyleConstants.Bold));
			attr.addAttribute(StyleConstants.Italic,
					doc.getCharacterElement(start).getAttributes().getAttribute(StyleConstants.Italic));
			Object und = doc.getCharacterElement(start).getAttributes().getAttribute(StyleConstants.Underline);
			if (und == null)
				attr.addAttribute(StyleConstants.Underline, false);
			else
				attr.addAttribute(StyleConstants.Underline,
						doc.getCharacterElement(start).getAttributes().getAttribute(StyleConstants.Underline));

			// FONT SIZE
			justSelected = true;
			fontSizeSpinnerTextField.setForeground(null);
			int fontSize = (int) doc.getCharacterElement(start).getAttributes().getAttribute(StyleConstants.FontSize);
			fontSizeSpinner.setValue(fontSize);

			for (int i = start + 1; i < end; i++) {
				if ((int) doc.getCharacterElement(i).getAttributes()
						.getAttribute(StyleConstants.FontSize) != fontSize) {
					fontSizeSpinnerTextField.setForeground(Color.white);
					break;
				}
			}

			// FONT FAMILY
			justSelected = true;
			fontFamilyBoxTextField.setForeground(null);
			String fontFamily = (String) textArea.getStyledDocument()
					.getCharacterElement(textArea.getCaretPosition() - 1).getAttributes()
					.getAttribute(StyleConstants.FontFamily);
			fontFamilyBox.setSelectedItem(fontFamily);

			for (int i = start + 1; i < end; i++) {
				if (doc.getCharacterElement(i).getAttributes().getAttribute(StyleConstants.FontFamily) != fontFamily) {
					fontFamilyBoxTextField.setForeground(Color.white);
					break;
				}
			}

			// FONT BOLD
			Boolean fontBold = (Boolean) textArea.getStyledDocument()
					.getCharacterElement(textArea.getCaretPosition() - 1).getAttributes()
					.getAttribute(StyleConstants.Bold);
			if (fontBold)
				fontBoldButton.setBackground(new Color(230, 230, 230));
			else
				fontBoldButton.setBackground(new Color(255, 255, 255));

			for (int i = start + 1; i < end; i++) {
				if (doc.getCharacterElement(i).getAttributes().getAttribute(StyleConstants.Bold) != fontBold) {
					fontBoldButton.setBackground(new Color(255, 255, 255));
					break;
				}
			}

			// FONT ITALIC
			Boolean fontItalic = (Boolean) textArea.getStyledDocument()
					.getCharacterElement(textArea.getCaretPosition() - 1).getAttributes()
					.getAttribute(StyleConstants.Italic);
			if (fontItalic)
				fontItalicButton.setBackground(new Color(230, 230, 230));
			else
				fontItalicButton.setBackground(new Color(255, 255, 255));

			for (int i = start + 1; i < end; i++) {
				if (doc.getCharacterElement(i).getAttributes().getAttribute(StyleConstants.Italic) != fontItalic) {
					fontItalicButton.setBackground(new Color(255, 255, 255));
					break;
				}
			}

			// FONT UNDERLINE
			Boolean fontUnderline = (Boolean) textArea.getStyledDocument()
					.getCharacterElement(textArea.getCaretPosition() - 1).getAttributes()
					.getAttribute(StyleConstants.Underline);
			if (fontUnderline == null)
				fontUnderlineButton.setBackground(new Color(255, 255, 255));
			else {
				if (fontUnderline == true)
					fontUnderlineButton.setBackground(new Color(230, 230, 230));
				else
					fontUnderlineButton.setBackground(new Color(255, 255, 255));
			}

			for (int i = start + 1; i < end; i++) {
				if (doc.getCharacterElement(i).getAttributes()
						.getAttribute(StyleConstants.Underline) != fontUnderline) {
					fontUnderlineButton.setBackground(new Color(255, 255, 255));
					break;
				}
			}
		}

		caretPosition = textArea.getCaretPosition();
	}

	@Override
	public void adjustmentValueChanged(AdjustmentEvent e) {

		//System.out.println("Reached");

		// JScrollPane scrollPane = (JScrollPane) e.getSource();
		// int extent = scrollPane.getVerticalScrollBar().getModel().getExtent();
		// System.out.println("Value: " + (scrollPane.getVerticalScrollBar().getValue()
		// + extent) + " Max: " + scrollPane.getVerticalScrollBar().getMaximum());

		if (currentTextPane.getText().length() > 0) {

			if (currentTextPane == textPanes.get(textPanes.size() - 1)) {
				
				try {
					int lastLineBreak = currentTextPane.getDocument()
							.getText(1, currentTextPane.getDocument().getLength()).lastIndexOf('\n');
					System.out.println(currentTextPane.getDocument().getText(lastLineBreak, currentTextPane.getDocument().getLength() - lastLineBreak));
					currentTextPane.getDocument().remove(lastLineBreak,
							currentTextPane.getDocument().getLength() - lastLineBreak);

					JTextPane newTextPane = new JTextPane();
					newTextPane.setCharacterAttributes(attr, rootPaneCheckingEnabled);
					newTextPane.addCaretListener(this);
					newTextPane.addKeyListener(this);
					textPanes.add(newTextPane);
					currentTextPane = newTextPane;

					JScrollPane newScrollPane = new JScrollPane(newTextPane,
							ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
							ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
					scrollPanes.add(newScrollPane);
					newScrollPane.setPreferredSize(new Dimension(620, 877));
					newScrollPane.getVerticalScrollBar().addAdjustmentListener(this);

					bodyPanel.add(newScrollPane);

					SwingUtilities.updateComponentTreeUI(this);
					
					currentTextPane.requestFocus();

				} catch (BadLocationException e1) {
					e1.printStackTrace();
				}
				
			} else {

				System.out.println("Not last");
				
				try {
					int lastLineBreak = currentTextPane.getDocument().getText(0, currentTextPane.getDocument().getLength()).lastIndexOf('\n');
					currentTextPane.getDocument().remove(lastLineBreak, currentTextPane.getDocument().getLength() - lastLineBreak);
				} catch (BadLocationException e1) {
					e1.printStackTrace();
				}
			}
		}

	}

	@Override
	public void keyPressed(KeyEvent e) {
		
		if((e.getKeyCode() == KeyEvent.VK_BACK_SPACE || e.getKeyCode() == KeyEvent.VK_DELETE) && textPanes.size() > 1) {
			
			textPanes.remove(textPanes.size() - 1);
			scrollPanes.remove(scrollPanes.size() - 1);
			bodyPanel.remove(bodyPanel.getComponentCount() - 1);
			
			currentTextPane = textPanes.get(textPanes.size() - 1);
			
			SwingUtilities.updateComponentTreeUI(this);
			currentTextPane.requestFocus();
			
		}
		
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
}