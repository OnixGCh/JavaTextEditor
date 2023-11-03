import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.*;
import javax.swing.filechooser.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.Document;
import javax.swing.text.EditorKit;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleConstants.ColorConstants;
import javax.swing.text.StyledDocument;
import javax.swing.text.StyledEditorKit;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.rtf.RTFEditorKit;

public class TextEditor<Mediator> extends JFrame implements ActionListener, CaretListener, ChangeListener, KeyListener {

	ArrayList<JTextPane> textPanes = new ArrayList<JTextPane>();
	//JTextPane textArea[];
	//JScrollPane textAreaScrollPane[];
	ArrayList<JScrollPane> scrollPanes = new ArrayList<JScrollPane>();
	JScrollPane mainScrollPane;
	JLabel fontLabel;
	JLabel fontIndentLabel;
	JLabel lineSpacingLabel;
	JSpinner fontSizeSpinner;
	JTextField fontSizeSpinnerTextField;
	JSpinner fontIndentSpinner;
	JTextField fontIndentSpinnerTextField;
	JSpinner lineSpacingSpinner;
	JTextField lineSpacingSpinnerTextField;
	JButton fontColorButton;
	JButton fontBoldButton;
	JButton fontItalicButton;
	JButton fontUnderlineButton;
	JButton imageButton;
	JButton hyperlinkButton;
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
	String lastCopied = "";
	
	JScrollPane currentScrollPane;
	JTextPane currentTextPane;

	TextEditor() {
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setTitle("Poop Text Editor");
		this.setLayout(new BorderLayout());
		
		headerPanel = new JPanel();
		headerPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 15));
		headerPanel.setBackground(new Color(255, 255, 255));
		headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
		
		bodyPanel = new JPanel();
		bodyPanel.setLayout(new FlowLayout());
		bodyPanel.setBackground(new Color(230, 230, 230));
		
		mainScrollPane = new JScrollPane(bodyPanel);
		mainScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		mainScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		
		attr = new SimpleAttributeSet();
		StyleConstants.setFontSize(attr, 14);
		StyleConstants.setFontFamily(attr, "Times New Roman");
		StyleConstants.setBold(attr, false);
		StyleConstants.setItalic(attr, false);
		StyleConstants.setUnderline(attr, false);
		StyleConstants.setLeftIndent(attr, 0f);
		StyleConstants.setLineSpacing(attr, 0f);
		
		CreateNewPage(1);
		
		//------------------------------------------------------------------------------------------------------

		fontColorButton = new JButton("Color");
		fontColorButton.addActionListener(this);
		fontColorButton.setBackground(new Color(255, 255, 255));
		
		String[] fonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();

		fontFamilyBox = new JComboBox<Object>(fonts);
		fontFamilyBox.addActionListener(this);
		fontFamilyBox.setEditable(true);
		fontFamilyBox.setSelectedItem("Times New Roman");
		fontFamilyBoxTextField = (JTextField)fontFamilyBox.getEditor().getEditorComponent();
				
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
					try {
						currentTextPane.setCharacterAttributes(attr, Boolean.TRUE);
					} catch (NullPointerException e1) {}
		
				} else {
					justSelected = false;
				}
			}

		});
		
		fontBoldButton = new JButton("Bold");
		fontBoldButton.addActionListener(this);
		fontBoldButton.setBackground(new Color(255, 255, 255));
		
		fontItalicButton = new JButton("Italic");
		fontItalicButton.addActionListener(this);
		fontItalicButton.setBackground(new Color(255, 255, 255));
		
		fontUnderlineButton = new JButton("Underline");
		fontUnderlineButton.addActionListener(this);
		fontUnderlineButton.setBackground(new Color(255, 255, 255));
		
		imageButton = new JButton("Image...");
		imageButton.addActionListener(this);
		imageButton.setBackground(new Color(255, 255, 255));
		
		hyperlinkButton = new JButton("Link");
		hyperlinkButton.addActionListener(this);
		hyperlinkButton.setBackground(new Color(255, 255, 255));
		
		fontIndentLabel = new JLabel("Indent:");
		fontIndentLabel.setFont(new Font("Arial", Font.PLAIN, 16));
			
		fontIndentSpinner = new JSpinner();
		fontIndentSpinner.setPreferredSize(new Dimension(50, 25));
		fontIndentSpinner.setValue(0f);
		fontIndentSpinnerTextField = ((JSpinner.DefaultEditor)fontIndentSpinner.getEditor()).getTextField();
		fontIndentSpinner.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				
				if (justSelected != true) {
					
					fontIndentSpinnerTextField.setForeground(null);
					StyleConstants.setLeftIndent(attr,  Float.valueOf(String.valueOf(fontIndentSpinner.getValue())));					
					try {
						currentTextPane.setParagraphAttributes(attr, Boolean.TRUE);
					} catch (NullPointerException e1) {}
		
				} else {
					justSelected = false;
				}
			}

		});
		
		lineSpacingLabel = new JLabel("Line spacing:");
		lineSpacingLabel.setFont(new Font("Arial", Font.PLAIN, 16));
		
		SpinnerModel model = new SpinnerNumberModel(0, 0, 10, 0.1f);
		lineSpacingSpinner = new JSpinner();
		lineSpacingSpinner.setPreferredSize(new Dimension(50, 25));
		lineSpacingSpinner.setValue(0f);
		lineSpacingSpinner.setModel(model);
		lineSpacingSpinnerTextField = ((JSpinner.DefaultEditor)fontIndentSpinner.getEditor()).getTextField();
		lineSpacingSpinner.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				
				if (justSelected != true) {
					
					lineSpacingSpinnerTextField.setForeground(null);
					StyleConstants.setLineSpacing(attr, Float.valueOf(String.valueOf(lineSpacingSpinner.getValue())));
					//currentTextPane.select(start, end);
					try {
						currentTextPane.setParagraphAttributes(attr, Boolean.TRUE);
					} catch (NullPointerException e1) {}
					
		
				} else {
					justSelected = false;
				}
			}

		});
		
		

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
		headerPanel.add(fontIndentLabel);
		headerPanel.add(fontIndentSpinner);
		headerPanel.add(lineSpacingLabel);
		headerPanel.add(lineSpacingSpinner);
		headerPanel.add(imageButton);
		//headerPanel.add(hyperlinkButton);
		
		this.add(mainScrollPane, BorderLayout.CENTER);		
		this.setSize(1100, 1100);
		this.setLocationRelativeTo(null);
		this.setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		if (e.getSource() == fontColorButton) {
			JColorChooser colorChooser = new JColorChooser();

			Color color = colorChooser.showDialog(null, "Choose a color", Color.black);

			currentScrollPane.setForeground(color);
		}

		// ---------------------------------------------------------------------------------------------------------------------------
		// ----------------------SET FONT FAMILY--------------------------------------------------------------------------------------
		// ---------------------------------------------------------------------------------------------------------------------------

		if (e.getSource() == fontFamilyBox) {

			if (justSelected != true) {

				if (fontFamilyBoxTextField != null)
					fontFamilyBoxTextField.setForeground(null);
				StyleConstants.setFontFamily(attr, (String) fontFamilyBox.getSelectedItem());				
				try {
					currentTextPane.setCharacterAttributes(attr, rootPaneCheckingEnabled);
				} catch (NullPointerException e1) {}

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
			} else {

				fontBoldButton.setBackground(new Color(255, 255, 255));
				StyleConstants.setBold(attr, false);
			}
			
			try {
				currentTextPane.setCharacterAttributes(attr, rootPaneCheckingEnabled);
			} catch (NullPointerException e1) {}
		}

		// ---------------------------------------------------------------------------------------------------------------------------
		// ----------------------SET ITALIC FONT-------------------------------------------------------------------------------------
		// ---------------------------------------------------------------------------------------------------------------------------

		if (e.getSource() == fontItalicButton) {

			if (fontItalicButton.getBackground().equals(new Color(255, 255, 255))) {

				fontItalicButton.setBackground(new Color(230, 230, 230));
				StyleConstants.setItalic(attr, true);
			} else {

				fontItalicButton.setBackground(new Color(255, 255, 255));
				StyleConstants.setItalic(attr, false);
			}
			
			try {
				currentTextPane.setCharacterAttributes(attr, rootPaneCheckingEnabled);
			} catch (NullPointerException e1) {}
		}

		// ---------------------------------------------------------------------------------------------------------------------------
		// ----------------------SET UNDERLINE FONT-------------------------------------------------------------------------------------
		// ---------------------------------------------------------------------------------------------------------------------------

		if (e.getSource() == fontUnderlineButton) {

			if (fontUnderlineButton.getBackground().equals(new Color(255, 255, 255))) {

				fontUnderlineButton.setBackground(new Color(230, 230, 230));
				StyleConstants.setUnderline(attr, true);
			} else {

				fontUnderlineButton.setBackground(new Color(255, 255, 255));
				StyleConstants.setUnderline(attr, false);				
			}
			
			try {
				currentTextPane.setCharacterAttributes(attr, rootPaneCheckingEnabled);
			} catch (NullPointerException e1) {}
		}
		
		// ---------------------------------------------------------------------------------------------------------------------------
		// ----------------------IMAGE-------------------------------------------------------------------------------------
		// ---------------------------------------------------------------------------------------------------------------------------

		if (e.getSource() == imageButton) {

			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setCurrentDirectory(new File("."));
			FileNameExtensionFilter filter = new FileNameExtensionFilter("Image files", "png", "Image files", "jpg");
			fileChooser.setFileFilter(filter);

			int response = fileChooser.showOpenDialog(null);

			if (response == JFileChooser.APPROVE_OPTION) {
				InputStream file = null;

				currentTextPane.insertIcon ( new ImageIcon (fileChooser.getSelectedFile().getAbsolutePath()) );				
			}
		}
		
		// ---------------------------------------------------------------------------------------------------------------------------
		// ----------------------LINK-------------------------------------------------------------------------------------
		// ---------------------------------------------------------------------------------------------------------------------------

		if (e.getSource() == hyperlinkButton) {
			
			HTMLEditorKit htmlkit = new HTMLEditorKit();
			currentTextPane.setEditorKit(htmlkit);
			try {
				htmlkit.insertHTML((HTMLDocument)currentTextPane.getDocument(), 0, "<html> Website : <a href=\"\">http://www.google.com/</a></html>", 0, 0, null);
			} catch (BadLocationException | IOException e1) {
				e1.printStackTrace();
			}
			
			EditorKit kit = new StyledEditorKit();
			currentTextPane.setEditorKit(kit);
			//currentTextPane.setText("<html> Website : <a href=\"\">http://www.google.com/</a></html>");
			//currentTextPane.insertIcon(new ImageIcon(fileChooser.getSelectedFile().getAbsolutePath()));
		}

		// ---------------------------------------------------------------------------------------------------------------------------
		// ----------------------NEW-------------------------------------------------------------------------------------------------
		// ---------------------------------------------------------------------------------------------------------------------------

		if (e.getSource() == newItem) {
			
			bodyPanel.removeAll();
			textPanes.removeAll(textPanes);
			scrollPanes.removeAll(scrollPanes);

			StyleConstants.setFontSize(attr, 14);
			StyleConstants.setFontFamily(attr, "Times New Roman");
			StyleConstants.setBold(attr, false);
			attr.addAttribute(StyleConstants.Bold, false);
			attr.addAttribute(StyleConstants.Italic, false);
			attr.addAttribute(StyleConstants.Underline, false);
			attr.addAttribute(StyleConstants.LeftIndent, 0f);
			attr.addAttribute(StyleConstants.LineSpacing, 0f);
			attr.removeAttribute(StyleConstants.Foreground);

			CreateNewPage(1);
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
		StyledDocument doc = textArea.getStyledDocument();
		
		currentTextPane = textArea;
		int index = 0;

		for (JTextPane element : textPanes) {

			if (currentTextPane == element)
				break;
			
			index++;
		}

		currentScrollPane = scrollPanes.get(index);
		
		//System.out.println("Caret: " + currentTextPane.getCaretPosition());

		if (textArea.getSelectedText() == null) {

			if (textArea.getText().length() > 0 && textArea.getCaretPosition() != caretPosition) {
				
				start = textArea.getCaretPosition();
				end = textArea.getCaretPosition();

				attr.addAttribute(StyleConstants.Bold, doc.getCharacterElement(textArea.getCaretPosition() - 1)
						.getAttributes().getAttribute(StyleConstants.Bold));
				attr.addAttribute(StyleConstants.Italic, doc.getCharacterElement(textArea.getCaretPosition() - 1)
						.getAttributes().getAttribute(StyleConstants.Italic));			
				attr.addAttribute(StyleConstants.Underline, doc.getCharacterElement(textArea.getCaretPosition() - 1)
							.getAttributes().getAttribute(StyleConstants.Underline));
				
				//---------------------------------------------------------------------------------------------------------------------

				// FONT SIZE
				justSelected = true;
				fontSizeSpinnerTextField.setForeground(null);
				if(textArea.getStyledDocument().getCharacterElement(textArea.getCaretPosition() - 1)
								.getAttributes().getAttribute(StyleConstants.FontSize) != null)
					fontSizeSpinner
							.setValue(textArea.getStyledDocument().getCharacterElement(textArea.getCaretPosition() - 1)
									.getAttributes().getAttribute(StyleConstants.FontSize));
				else
					fontSizeSpinnerTextField.setForeground(new Color(255, 255, 255));
				
				// FONT INDENT
				justSelected = true;
				fontIndentSpinnerTextField.setForeground(null);
				if(textArea.getStyledDocument().getParagraphElement(textArea.getCaretPosition() - 1)
								.getAttributes().getAttribute(StyleConstants.LeftIndent) != null)
					fontIndentSpinner
								.setValue(Float.valueOf(String.valueOf(textArea.getStyledDocument().getParagraphElement(textArea.getCaretPosition() - 1)
								.getAttributes().getAttribute(StyleConstants.LeftIndent))));
				else
					fontIndentSpinnerTextField.setForeground(new Color(255, 255, 255));
				
				// LINE SPACING
				justSelected = true;
				lineSpacingSpinnerTextField.setForeground(null);
				if(textArea.getStyledDocument().getParagraphElement(textArea.getCaretPosition() - 1)
									.getAttributes().getAttribute(StyleConstants.LineSpacing) != null)
					lineSpacingSpinner
							.setValue(Float.valueOf(String.valueOf(textArea.getStyledDocument().getParagraphElement(textArea.getCaretPosition() - 1)
									.getAttributes().getAttribute(StyleConstants.LineSpacing))));
				else
					lineSpacingSpinnerTextField.setForeground(new Color(255, 255, 255));

				// FONT FAMILY
				justSelected = true;
				fontFamilyBoxTextField.setForeground(null);
				fontFamilyBox.setSelectedItem(
						(String) textArea.getStyledDocument().getCharacterElement(textArea.getCaretPosition() - 1)
								.getAttributes().getAttribute(StyleConstants.FontFamily));

				// FONT BOLD
				if(textArea.getStyledDocument().getCharacterElement(textArea.getCaretPosition() - 1)
						.getAttributes().getAttribute(StyleConstants.Bold) != null)
					if ((Boolean) textArea.getStyledDocument().getCharacterElement(textArea.getCaretPosition() - 1)
							.getAttributes().getAttribute(StyleConstants.Bold)) {
						fontBoldButton.setBackground(new Color(230, 230, 230));
					} else {
						fontBoldButton.setBackground(new Color(255, 255, 255));
					}
				else
					fontBoldButton.setBackground(new Color(255, 255, 255));

				// FONT ITALIC
				if(textArea.getStyledDocument().getCharacterElement(textArea.getCaretPosition() - 1)
						.getAttributes().getAttribute(StyleConstants.Italic) != null)
					if ((Boolean) textArea.getStyledDocument().getCharacterElement(textArea.getCaretPosition() - 1)
							.getAttributes().getAttribute(StyleConstants.Italic)) {
						fontItalicButton.setBackground(new Color(230, 230, 230));
					} else {
						fontItalicButton.setBackground(new Color(255, 255, 255));
					}
				else
					fontItalicButton.setBackground(new Color(255, 255, 255));

				// FONT UNDERLINE
				if(textArea.getStyledDocument().getCharacterElement(textArea.getCaretPosition() - 1)
						.getAttributes().getAttribute(StyleConstants.Underline) != null)
					if ((Boolean) textArea.getStyledDocument().getCharacterElement(textArea.getCaretPosition() - 1)
							.getAttributes().getAttribute(StyleConstants.Underline))
						fontUnderlineButton.setBackground(new Color(230, 230, 230));
					else
						fontUnderlineButton.setBackground(new Color(255, 255, 255));
				else
					fontUnderlineButton.setBackground(new Color(255, 255, 255));

			}

		} else {

			start = textArea.getSelectionStart();
			end = textArea.getSelectionEnd();
			
			//---------------------------------------------------------------------------------------------------------------------
			
			attr.addAttribute(StyleConstants.Bold,
					doc.getCharacterElement(start).getAttributes().getAttribute(StyleConstants.Bold));
			attr.addAttribute(StyleConstants.Italic,
					doc.getCharacterElement(start).getAttributes().getAttribute(StyleConstants.Italic));
			attr.addAttribute(StyleConstants.Underline,
						doc.getCharacterElement(start).getAttributes().getAttribute(StyleConstants.Underline));
			
			//---------------------------------------------------------------------------------------------------------------------
			
			// FONT SIZE
			justSelected = true;
			fontSizeSpinnerTextField.setForeground(null);
			int fontSize = 0;
			if(doc.getCharacterElement(start).getAttributes().getAttribute(StyleConstants.FontSize) != null)
				fontSize = (int) doc.getCharacterElement(start).getAttributes().getAttribute(StyleConstants.FontSize);
			fontSizeSpinner.setValue(fontSize);

			for (int i = start + 1; i < end; i++) {
				if ((int) doc.getCharacterElement(i).getAttributes()
						.getAttribute(StyleConstants.FontSize) != fontSize) {
					fontSizeSpinnerTextField.setForeground(Color.white);
					break;
				}
			}
			
			// FONT INDENT
			justSelected = true;
			fontIndentSpinnerTextField.setForeground(null);
			float fontIndent = 0f;
			if (doc.getParagraphElement(start).getAttributes().getAttribute(StyleConstants.LeftIndent) != null)
				fontIndent = Float.valueOf(String.valueOf(
						doc.getParagraphElement(start).getAttributes().getAttribute(StyleConstants.LeftIndent)));
			fontIndentSpinner.setValue(fontIndent);
			
			if(doc.getParagraphElement(start).getAttributes().getAttribute(StyleConstants.LeftIndent) != null)
				for (int i = start + 1; i < end; i++) {
					if (Float.valueOf(String.valueOf(doc.getParagraphElement(i).getAttributes()
							.getAttribute(StyleConstants.LeftIndent))) != fontIndent) {
						fontIndentSpinnerTextField.setForeground(Color.white);
						break;
					}
				}
			
			// LINE SPACING
			justSelected = true;
			lineSpacingSpinnerTextField.setForeground(null);
			float lineSpacing = 0f;
			if (doc.getParagraphElement(start).getAttributes().getAttribute(StyleConstants.LineSpacing) != null)
				lineSpacing = Float.valueOf(String.valueOf(
						doc.getParagraphElement(start).getAttributes().getAttribute(StyleConstants.LineSpacing)));				
			lineSpacingSpinner.setValue(lineSpacing);

			if(doc.getParagraphElement(start).getAttributes().getAttribute(StyleConstants.LineSpacing) != null)
				for (int i = start + 1; i < end; i++) {
					if (Float.valueOf(String.valueOf(doc.getParagraphElement(i).getAttributes()
							.getAttribute(StyleConstants.LineSpacing))) != lineSpacing) {
						lineSpacingSpinnerTextField.setForeground(Color.white);
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
	public void stateChanged(ChangeEvent e) {
			
		BoundedRangeModel model = (BoundedRangeModel) e.getSource();
		int extent = model.getExtent();
		int maximum = model.getMaximum();
		int value = model.getValue();
		
		if(value <= 0 && extent >= maximum)
			return;
		
		//-------------------------------------------------------------------------------
		JScrollPane tempScrollPane;
		JTextPane tempTextPane;
		int index = 0;
		
		for (JScrollPane element : scrollPanes) {
							
			if(e.getSource() == element.getVerticalScrollBar().getModel()) {
				tempScrollPane = element;	
				break;
			}
			index++;
		}
		
		tempTextPane = textPanes.get(index);
		
		if(tempTextPane.getText().equals(lastCopied))
			return;
		
		//-------------------------------------------------------------------------------
		
		int lastLineBreak;
		String copy = "";
		try {		
			lastLineBreak = tempTextPane.getDocument().getText(0, tempTextPane.getDocument().getLength())
					.lastIndexOf('\n');
		
			
			/*System.out.println(tempTextPane.getStyledDocument().getText(lastLineBreak + 1,
					tempTextPane.getDocument().getLength() - lastLineBreak - 1));*/
			
			copy = tempTextPane.getStyledDocument().getText(lastLineBreak + 1,
					tempTextPane.getDocument().getLength() - lastLineBreak);
			
			lastCopied = copy;
		
			tempTextPane.getDocument().remove(lastLineBreak,
					tempTextPane.getDocument().getLength() - lastLineBreak);
			
			//System.out.println("Lenth: " + tempTextPane.getDocument().getLength());
			
		} catch (BadLocationException e1) {
			
			System.out.println("Error on index: " + index);
			e1.printStackTrace();
		}
		
		//-------------------------------------------------------------------------------
		
		if(index == textPanes.size() - 1) {
					
			//System.out.println("Creating new page");
			CreateNewPage(index + 2);		
		}
		
		//System.out.println("Transfering text to index: " + (index + 1));
		textPanes.get(index + 1).setText(copy + textPanes.get(index + 1).getText());
	}
	
	@Override
	public void keyPressed(KeyEvent e) {
		
		if((e.getKeyCode() == KeyEvent.VK_BACK_SPACE || e.getKeyCode() == KeyEvent.VK_DELETE) && textPanes.size() > 1 && currentTextPane.getText().length() == 0) {
			
			textPanes.remove(textPanes.size() - 1);
			scrollPanes.remove(scrollPanes.size() - 1);
			bodyPanel.remove(bodyPanel.getComponentCount() - 1);
			
			currentScrollPane = scrollPanes.get(scrollPanes.size() - 1);
			currentTextPane = textPanes.get(textPanes.size() - 1);
						
			SwingUtilities.updateComponentTreeUI(this);
			currentScrollPane.requestFocus();
			
		}
		
	}

	public void CreateNewPage(int pageNumber) {
		
		JTextPane newTextPane = new JTextPane();
		newTextPane.setCharacterAttributes(attr, rootPaneCheckingEnabled);
		newTextPane.addCaretListener(this);
		newTextPane.addKeyListener(this);
		newTextPane.setEditable(true);
		
		Action action = new AbstractAction() {

			@Override
			public void actionPerformed(ActionEvent e) {
				
				//System.out.println(newTextPane.getSelectedText());
				
				try 
			    {
			        //Clipboard clpbrd = Toolkit.getDefaultToolkit().getSystemClipboard();
					RTFEditorKit rtfek = new RTFEditorKit();
					HTMLEditorKit kit = new HTMLEditorKit();
					
			        ByteArrayOutputStream bos  = new ByteArrayOutputStream();
			        Writer writer = new StringWriter();
			        
			        rtfek.write ( bos, newTextPane.getStyledDocument(), 0, 0);
			        kit.write ( writer, newTextPane.getStyledDocument(), 0, 0);
			        
			        System.out.println(writer.toString());
			        
			        ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
			        
			        newTextPane.setText("");
			        
			        rtfek.read(bis, newTextPane.getStyledDocument(), 0);
			        bos.flush();
			    }
			    catch ( IOException | BadLocationException e1 ) 
			    {
			        e1.printStackTrace();
			    }
				
			}};
		KeyStroke keyStroke = KeyStroke.getKeyStroke("control C");
		InputMap im = newTextPane.getInputMap();
		newTextPane.getActionMap().put(im.get(keyStroke), action);
		/*newTextPane.addHyperlinkListener(new HyperlinkListener() {

            @Override
            public void hyperlinkUpdate(HyperlinkEvent e) {
            	
            	System.out.println("YAY");
            	
                if (!HyperlinkEvent.EventType.ACTIVATED.equals(e.getEventType()))
                    return;
                if ("action:close".equals(e.getDescription())) {
                	
                } else {
                    if (Desktop.isDesktopSupported()) {
                        try {
                            Desktop.getDesktop().browse(e.getURL().toURI());
                        } catch (Exception ignore) {
                        }
                    }
                }
            }
        });*/
		textPanes.add(newTextPane);
		
		EditorKit kit = new StyledEditorKit();
		newTextPane.setEditorKit(kit);

		Border emptyBorder = new EmptyBorder(0, 0, 0, 0);
		Border lineBorder = new LineBorder(Color.white, 0) {
			@Override
			public void paintBorder(final Component c, final Graphics g, final int x, final int y, final int width,
					final int height) {
				// super.paintBorder(c, g, x, y, width, height);
				final boolean doSimple = true;
				if (doSimple) {
					g.setColor(Color.white);
					g.fillRect(x, y, width, height);
				}
			}
		};
		Border border = new CompoundBorder(emptyBorder, lineBorder);
		
		JScrollPane newScrollPane = new JScrollPane(newTextPane,
				ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		newScrollPane.setPreferredSize(new Dimension(620, 877));
		newScrollPane.setBorder(border);
		scrollPanes.add(newScrollPane);
		
		JPanel newPanel = new JPanel();
		newPanel.setPreferredSize(new Dimension(620, 920));
		newPanel.setBackground(Color.white);
		newPanel.setLayout(new FlowLayout(FlowLayout.LEADING, 0, 0));
		
		JLabel newLabel = new JLabel(String.valueOf(pageNumber));
		newLabel.setForeground(Color.black);
		newLabel.setFont(new Font("Arial", Font.PLAIN, 24));
		newLabel.setBorder(new EmptyBorder(5, 580, 5, 5));
		
		newPanel.add(newScrollPane);
		newPanel.add(newLabel);
		bodyPanel.add(newPanel);

		newScrollPane.getVerticalScrollBar().getModel().addChangeListener(this);
	
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				SwingUtilities.updateComponentTreeUI(bodyPanel);
				pack();
			}
		});
	
		currentScrollPane = newScrollPane;
		currentTextPane = newTextPane;
		currentTextPane.requestFocus();
	}
	
	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void keyReleased(KeyEvent e) {
	}
}