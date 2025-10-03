package proyectoprogra.gui.frames;

import proyectoprogra.model.Viaje;
import proyectoprogra.service.ViajeService;
import proyectoprogra.utils.ViajeManager;
import proyectoprogra.utils.ViajeTableModel;
import proyectoprogra.utils.UIConstants;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.event.CellEditorListener;
import java.util.EventObject;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.ComponentEvent;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import org.kordamp.ikonli.swing.FontIcon;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import com.toedter.calendar.JCalendar;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;

public class ViajeFrame extends JFrame {

    private JPanel mainPanel;
    private JPanel headerPanel;
    private JPanel contentPanel;
    private JPanel formPanel;
    private JPanel tablePanel;
    private JPanel searchPanel;
    private JPanel buttonPanel;
    
    private JTextField origenField;
    private JTextField destinoField;
    private JTextField fechaSalidaField;
    private JTextField fechaLlegadaField;
    private JButton fechaSalidaButton;
    private JButton fechaLlegadaButton;
    private Date fechaSalidaSeleccionada;
    private Date fechaLlegadaSeleccionada;
    private JComboBox<String> estadoCombo;
    
    private JTextField searchField;
    
    private JLabel origenErrorLabel;
    private JLabel destinoErrorLabel;
    private JLabel fechaSalidaErrorLabel;
    private JLabel fechaLlegadaErrorLabel;
    
    private JButton agregarButton;
    private JButton actualizarButton;
    private JButton limpiarButton;
    private JButton buscarButton;
    private JButton refrescarButton;
    private JButton descargarJsonButton;
    private int viajeEditando = -1;
    
    private JTable viajesTable;
    private ViajeTableModel tableModel;
    private JScrollPane tableScrollPane;
    
    private SimpleDateFormat dateFormat = new SimpleDateFormat(UIConstants.DEPARTURE_DATE_PLACEHOLDER);

    public ViajeFrame() {
        initComponents();
        setupModernInterface();
        
        ViajeManager.inicializarDesdeBaseDatos();
        refrescarTabla();
    }

    private void initComponents() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle(UIConstants.APP_TITLE);
        setSize(UIConstants.FRAME_WIDTH, UIConstants.FRAME_HEIGHT);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(UIConstants.MIN_FRAME_WIDTH, UIConstants.MIN_FRAME_HEIGHT));
    }

    private void setupModernInterface() {
        createModernComponents();
        layoutModernComponents();
        setupEventHandlers();
        setupTable();
        setVisible(true);
    }
    
    private void createModernComponents() {
        mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBackground(UIConstants.BACKGROUND_COLOR);
        mainPanel.setBorder(new EmptyBorder(UIConstants.PADDING, UIConstants.PADDING, 
                                          UIConstants.PADDING, UIConstants.PADDING));
        
        headerPanel = createHeaderPanel();
        contentPanel = new JPanel();
        contentPanel.setLayout(new BorderLayout(UIConstants.PADDING, UIConstants.PADDING));
        contentPanel.setBackground(UIConstants.BACKGROUND_COLOR);
        
        formPanel = createFormPanel();
        searchPanel = createSearchPanel();
        tablePanel = createTablePanel();
        buttonPanel = createButtonPanel();
    }
    
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 10));
        panel.setBackground(UIConstants.PRIMARY_COLOR);
        panel.setBorder(new EmptyBorder(UIConstants.PADDING, UIConstants.PADDING, 
                                      UIConstants.PADDING, UIConstants.PADDING));
        
        try {
            ImageIcon logoIcon = new ImageIcon("assets/logo_umg.png");
            Image logoImage = logoIcon.getImage();
            Image scaledLogo = logoImage.getScaledInstance(60, 60, Image.SCALE_SMOOTH);
            ImageIcon scaledLogoIcon = new ImageIcon(scaledLogo);
            JLabel logoLabel = new JLabel(scaledLogoIcon);
            logoLabel.setBorder(new EmptyBorder(0, 0, 0, 15));
            panel.add(logoLabel);
        } catch (Exception e) {
            System.err.println(UIConstants.LOGO_ERROR_MESSAGE + e.getMessage());
        }
        
        JLabel titleLabel = new JLabel(UIConstants.APP_MAIN_TITLE);
        titleLabel.setFont(UIConstants.TITLE_FONT);
        titleLabel.setForeground(Color.WHITE);
        
        panel.add(titleLabel);
        return panel;
    }
    
    private JPanel createFormPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UIConstants.BORDER_COLOR),
            new EmptyBorder(UIConstants.PADDING, UIConstants.PADDING, 
                          UIConstants.PADDING, UIConstants.PADDING)
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        JLabel formTitle = new JLabel(UIConstants.NEW_TRIP_FORM_TITLE);
        formTitle.setFont(UIConstants.SUBTITLE_FONT);
        formTitle.setForeground(UIConstants.PRIMARY_COLOR);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 4;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(formTitle, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.weightx = 0.0;
        JLabel origenLabel = new JLabel(UIConstants.ORIGIN_LABEL);
        origenLabel.setFont(UIConstants.BODY_FONT);
        panel.add(origenLabel, gbc);
        
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = 1.0;
        origenField = createStyledTextField(UIConstants.ORIGIN_PLACEHOLDER);
        panel.add(origenField, gbc);
        
        gbc.gridx = 2; gbc.weightx = 0.0;
        origenErrorLabel = createErrorLabel();
        panel.add(origenErrorLabel, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2; gbc.anchor = GridBagConstraints.EAST;
        gbc.weightx = 0.0;
        JLabel destinoLabel = new JLabel(UIConstants.DESTINATION_LABEL);
        destinoLabel.setFont(UIConstants.BODY_FONT);
        panel.add(destinoLabel, gbc);
        
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = 1.0;
        destinoField = createStyledTextField(UIConstants.DESTINATION_PLACEHOLDER);
        panel.add(destinoField, gbc);
        
        gbc.gridx = 2; gbc.weightx = 0.0;
        destinoErrorLabel = createErrorLabel();
        panel.add(destinoErrorLabel, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3; gbc.anchor = GridBagConstraints.EAST;
        gbc.weightx = 0.0;
        JLabel fechaSalidaLabel = new JLabel(UIConstants.DEPARTURE_DATE_LABEL);
        fechaSalidaLabel.setFont(UIConstants.BODY_FONT);
        panel.add(fechaSalidaLabel, gbc);
        
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = 1.0;
        fechaSalidaField = createStyledTextField("");
        fechaSalidaField.setEditable(false);
        fechaSalidaField.setBackground(new Color(248, 249, 250));
        panel.add(fechaSalidaField, gbc);
        
        gbc.gridx = 2; gbc.weightx = 0.0;
        fechaSalidaButton = createDatePickerButtonWithIcon();
        panel.add(fechaSalidaButton, gbc);
        
        gbc.gridx = 3; gbc.weightx = 0.0;
        fechaSalidaErrorLabel = createErrorLabel();
        panel.add(fechaSalidaErrorLabel, gbc);
        
        gbc.gridx = 0; gbc.gridy = 4; gbc.anchor = GridBagConstraints.EAST;
        gbc.weightx = 0.0;
        JLabel fechaLlegadaLabel = new JLabel(UIConstants.ARRIVAL_DATE_LABEL);
        fechaLlegadaLabel.setFont(UIConstants.BODY_FONT);
        panel.add(fechaLlegadaLabel, gbc);
        
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = 1.0;
        fechaLlegadaField = createStyledTextField("");
        fechaLlegadaField.setEditable(false);
        fechaLlegadaField.setBackground(new Color(248, 249, 250));
        panel.add(fechaLlegadaField, gbc);
        
        gbc.gridx = 2; gbc.weightx = 0.0;
        fechaLlegadaButton = createDatePickerButtonWithIcon();
        panel.add(fechaLlegadaButton, gbc);
        
        gbc.gridx = 3; gbc.weightx = 0.0;
        fechaLlegadaErrorLabel = createErrorLabel();
        panel.add(fechaLlegadaErrorLabel, gbc);
        
        gbc.gridx = 0; gbc.gridy = 5; gbc.anchor = GridBagConstraints.EAST;
        gbc.weightx = 0.0;
        JLabel estadoLabel = new JLabel(UIConstants.STATUS_LABEL);
        estadoLabel.setFont(UIConstants.BODY_FONT);
        panel.add(estadoLabel, gbc);
        
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = 1.0;
        estadoCombo = new JComboBox<>(new String[]{Viaje.ESTADO_PENDIENTE, Viaje.ESTADO_EN_CURSO});
        estadoCombo.setFont(UIConstants.BODY_FONT);
        estadoCombo.setPreferredSize(new Dimension(250, 45));
        panel.add(estadoCombo, gbc);
        
        return panel;
    }
    
    private JPanel createSearchPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout(UIConstants.PADDING, UIConstants.PADDING));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UIConstants.BORDER_COLOR),
            new EmptyBorder(UIConstants.SMALL_PADDING, UIConstants.PADDING, 
                          UIConstants.SMALL_PADDING, UIConstants.PADDING)
        ));
        
        JPanel labelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        labelPanel.setBackground(Color.WHITE);
        JLabel searchLabel = new JLabel(UIConstants.SEARCH_LABEL);
        searchLabel.setFont(UIConstants.BODY_FONT);
        labelPanel.add(searchLabel);
        
        JPanel inputButtonPanel = new JPanel(new BorderLayout(UIConstants.PADDING, 0));
        inputButtonPanel.setBackground(Color.WHITE);
        
        searchField = createStyledTextField(UIConstants.SEARCH_PLACEHOLDER);
        searchField.setPreferredSize(new Dimension(300, 45));
        searchField.setMinimumSize(new Dimension(200, 40));
        searchField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        inputButtonPanel.add(searchField, BorderLayout.CENTER);
        
        buscarButton = createStyledButton(UIConstants.SEARCH_BUTTON_TEXT, UIConstants.PRIMARY_COLOR);
        
        FontIcon searchIcon = FontIcon.of(FontAwesomeSolid.SEARCH);
        searchIcon.setIconSize(16);
        searchIcon.setIconColor(Color.WHITE);
        buscarButton.setIcon(searchIcon);
        inputButtonPanel.add(buscarButton, BorderLayout.EAST);
        
        panel.add(labelPanel, BorderLayout.NORTH);
        panel.add(inputButtonPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createTablePanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UIConstants.BORDER_COLOR),
            new EmptyBorder(UIConstants.PADDING, UIConstants.PADDING, 
                          UIConstants.PADDING, UIConstants.PADDING)
        ));
        
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(UIConstants.BACKGROUND_COLOR);
        topPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        
        JLabel tableTitle = new JLabel(UIConstants.TRIPS_TABLE_LABEL);
        tableTitle.setFont(UIConstants.HEADER_FONT);
        tableTitle.setForeground(UIConstants.PRIMARY_COLOR);
        
        FontIcon planeIcon = FontIcon.of(FontAwesomeSolid.PLANE);
        planeIcon.setIconSize(18);
        planeIcon.setIconColor(UIConstants.PRIMARY_COLOR);
        tableTitle.setIcon(planeIcon);
        tableTitle.setIconTextGap(8);
        
        JPanel botonesPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        botonesPanel.setBackground(UIConstants.BACKGROUND_COLOR);
        
        descargarJsonButton = createStyledButton(UIConstants.DOWNLOAD_JSON_BUTTON_TEXT, Color.BLACK);
        descargarJsonButton.setPreferredSize(new Dimension(160, 35));
        
        FontIcon jsonIcon = FontIcon.of(FontAwesomeSolid.FILE_CODE);
        jsonIcon.setIconSize(14);
        jsonIcon.setIconColor(Color.WHITE);
        descargarJsonButton.setIcon(jsonIcon);
        
        JButton descargarExcelButton = createStyledButton(UIConstants.DOWNLOAD_EXCEL_BUTTON_TEXT, new Color(34, 139, 34));
        descargarExcelButton.setPreferredSize(new Dimension(160, 35));
        
        FontIcon excelIcon = FontIcon.of(FontAwesomeSolid.FILE_EXCEL);
        excelIcon.setIconSize(14);
        excelIcon.setIconColor(Color.WHITE);
        descargarExcelButton.setIcon(excelIcon);
        
        botonesPanel.add(descargarExcelButton);
        botonesPanel.add(descargarJsonButton);
        
        JPanel responsiveTopPanel = new JPanel();
        responsiveTopPanel.setBackground(UIConstants.BACKGROUND_COLOR);
        responsiveTopPanel.setLayout(new BoxLayout(responsiveTopPanel, BoxLayout.Y_AXIS));
        
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(UIConstants.BACKGROUND_COLOR);
        titlePanel.add(tableTitle, BorderLayout.WEST);
        
        JPanel buttonsPanel = new JPanel(new BorderLayout());
        buttonsPanel.setBackground(UIConstants.BACKGROUND_COLOR);
        buttonsPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
        
        JPanel buttonsFlowPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonsFlowPanel.setBackground(UIConstants.BACKGROUND_COLOR);
        buttonsFlowPanel.add(descargarExcelButton);
        buttonsFlowPanel.add(descargarJsonButton);
        
        buttonsPanel.add(buttonsFlowPanel, BorderLayout.EAST);
        
        responsiveTopPanel.add(titlePanel);
        responsiveTopPanel.add(buttonsPanel);
        
        topPanel.add(responsiveTopPanel, BorderLayout.CENTER);
        
        panel.add(topPanel, BorderLayout.NORTH);
        
        tableModel = new ViajeTableModel();
        viajesTable = new JTable(tableModel);
        viajesTable.setFont(UIConstants.BODY_FONT);
        viajesTable.setRowHeight(35);
        viajesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        viajesTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        viajesTable.getTableHeader().setBackground(new Color(240, 240, 240));
        viajesTable.getTableHeader().setForeground(new Color(70, 70, 70));
        viajesTable.getTableHeader().setPreferredSize(new Dimension(0, 35));
        
        
        tableScrollPane = new JScrollPane(viajesTable);
        tableScrollPane.setPreferredSize(new Dimension(800, 300));
        tableScrollPane.setMinimumSize(new Dimension(400, 200));
        tableScrollPane.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        panel.add(tableScrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.CENTER, UIConstants.PADDING, UIConstants.PADDING));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UIConstants.BORDER_COLOR),
            new EmptyBorder(UIConstants.PADDING, UIConstants.PADDING, 
                          UIConstants.PADDING, UIConstants.PADDING)
        ));
        
        agregarButton = createStyledButton(UIConstants.ADD_BUTTON_TEXT, UIConstants.SUCCESS_COLOR);
        actualizarButton = createStyledButton(UIConstants.UPDATE_BUTTON_TEXT, UIConstants.SUCCESS_COLOR);
        limpiarButton = createStyledButton(UIConstants.CLEAR_BUTTON_TEXT, UIConstants.SECONDARY_COLOR);
        refrescarButton = createStyledButton(UIConstants.REFRESH_BUTTON_TEXT, UIConstants.WARNING_COLOR);
        
        actualizarButton.setVisible(false);
        
        FontIcon plusIcon = FontIcon.of(FontAwesomeSolid.PLUS);
        plusIcon.setIconSize(16);
        plusIcon.setIconColor(Color.WHITE);
        
        FontIcon editIcon = FontIcon.of(FontAwesomeSolid.EDIT);
        editIcon.setIconSize(16);
        editIcon.setIconColor(Color.WHITE);
        
        FontIcon trashIcon = FontIcon.of(FontAwesomeSolid.TRASH_ALT);
        trashIcon.setIconSize(16);
        trashIcon.setIconColor(Color.WHITE);
        
        FontIcon syncIcon = FontIcon.of(FontAwesomeSolid.SYNC_ALT);
        syncIcon.setIconSize(16);
        syncIcon.setIconColor(Color.WHITE);
        
        agregarButton.setIcon(plusIcon);
        actualizarButton.setIcon(editIcon);
        limpiarButton.setIcon(trashIcon);
        refrescarButton.setIcon(syncIcon);
        
        panel.add(agregarButton);
        panel.add(actualizarButton);
        panel.add(limpiarButton);
        panel.add(refrescarButton);
        
        return panel;
    }
    
    private void layoutModernComponents() {
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        
        contentPanel.setLayout(new BorderLayout(UIConstants.PADDING, UIConstants.PADDING));
        
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BorderLayout(UIConstants.PADDING, UIConstants.PADDING));
        leftPanel.setBackground(UIConstants.BACKGROUND_COLOR);
        leftPanel.add(formPanel, BorderLayout.CENTER);
        leftPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BorderLayout(UIConstants.PADDING, UIConstants.PADDING));
        rightPanel.setBackground(UIConstants.BACKGROUND_COLOR);
        rightPanel.add(searchPanel, BorderLayout.NORTH);
        rightPanel.add(tablePanel, BorderLayout.CENTER);
        
        contentPanel.add(leftPanel, BorderLayout.WEST);
        contentPanel.add(rightPanel, BorderLayout.CENTER);
        
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        setContentPane(mainPanel);
        
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                adjustLayoutForSize(getWidth(), getHeight());
            }
        });
    }
    
    private void setupEventHandlers() {
        agregarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                agregarViaje();
            }
        });
        
        actualizarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                actualizarViaje();
            }
        });
        
        limpiarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                limpiarFormulario();
            }
        });
        
        buscarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                buscarViajes();
            }
        });
        
        refrescarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                refrescarTabla();
            }
        });
        
        searchField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                buscarViajes();
            }
        });
        
        fechaSalidaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                abrirDatePicker(UIConstants.DEPARTURE_DATE_LABEL, fechaSalidaSeleccionada, fechaSalidaField, true);
            }
        });
        
        fechaLlegadaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                abrirDatePicker(UIConstants.ARRIVAL_DATE_LABEL, fechaLlegadaSeleccionada, fechaLlegadaField, false);
            }
        });
        
        descargarJsonButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                descargarJSON();
            }
        });
    }
    
    private void setupTable() {
        TableRowSorter<ViajeTableModel> sorter = new TableRowSorter<>(tableModel);
        viajesTable.setRowSorter(sorter);
        
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        
        for (int i = 0; i < viajesTable.getColumnCount(); i++) {
            viajesTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
        
        viajesTable.getColumnModel().getColumn(0).setPreferredWidth(60);   // ID (fijo)
        viajesTable.getColumnModel().getColumn(1).setPreferredWidth(100);  // Origen (mínimo)
        viajesTable.getColumnModel().getColumn(2).setPreferredWidth(100);  // Destino (mínimo)
        viajesTable.getColumnModel().getColumn(3).setPreferredWidth(140);  // Fecha Salida (mínimo)
        viajesTable.getColumnModel().getColumn(4).setPreferredWidth(140);  // Fecha Llegada (mínimo)
        viajesTable.getColumnModel().getColumn(5).setPreferredWidth(100);  // Estado (mínimo)
        viajesTable.getColumnModel().getColumn(6).setPreferredWidth(120);  // Acciones (mínimo para botones)
        
        viajesTable.getColumnModel().getColumn(0).setResizable(false);
        
        viajesTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        viajesTable.setFillsViewportHeight(true);
        viajesTable.setDefaultEditor(Object.class, null);
        
        ButtonEditor buttonEditor = new ButtonEditor();
        viajesTable.getColumnModel().getColumn(6).setCellRenderer(buttonEditor);
        viajesTable.getColumnModel().getColumn(6).setCellEditor(buttonEditor);
        
        
        viajesTable.setSelectionBackground(Color.WHITE);
        viajesTable.setSelectionForeground(Color.BLACK);
        
        viajesTable.repaint();
        
        tableScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        tableScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
    }
    
    private void agregarViaje() {
        if (!validarCampos()) {
            return;
        }
        
        Viaje viaje = new Viaje();
        viaje.setOrigen(origenField.getText());
        viaje.setDestino(destinoField.getText());
        viaje.setFechaSalida(fechaSalidaSeleccionada);
        viaje.setFechaLlegada(fechaLlegadaSeleccionada);
        viaje.setEstado((String) estadoCombo.getSelectedItem());
        
        if (ViajeManager.agregarViaje(viaje)) {
            limpiarFormulario();
            refrescarTabla();
            mostrarMensaje(UIConstants.SUCCESS_ADD_TRIP, UIConstants.SUCCESS_TITLE, JOptionPane.INFORMATION_MESSAGE);
        } else {
            mostrarMensaje(UIConstants.ERROR_ADD_TRIP, UIConstants.ERROR_TITLE, JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void actualizarViaje() {
        if (!validarCampos() || viajeEditando == -1) {
            return;
        }
        
        try {
            Viaje viajeActualizado = new Viaje();
            viajeActualizado.setId(viajeEditando);
            viajeActualizado.setOrigen(origenField.getText());
            viajeActualizado.setDestino(destinoField.getText());
            viajeActualizado.setFechaSalida(fechaSalidaSeleccionada);
            viajeActualizado.setFechaLlegada(fechaLlegadaSeleccionada);
            viajeActualizado.setEstado((String) estadoCombo.getSelectedItem());
            
            ViajeService viajeService = new ViajeService();
            Viaje viajeGuardado = viajeService.update(viajeActualizado);
            
            if (viajeGuardado != null) {
                refrescarTabla();
                limpiarFormulario();
                resetearBotones();
                mostrarMensaje(UIConstants.SUCCESS_UPDATE_TRIP, UIConstants.SUCCESS_TITLE, JOptionPane.INFORMATION_MESSAGE);
            } else {
                mostrarMensaje(UIConstants.ERROR_UPDATE_TRIP, UIConstants.ERROR_TITLE, JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            mostrarMensaje(UIConstants.ERROR_UPDATE_TRIP + ": " + e.getMessage(), UIConstants.ERROR_TITLE, JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void resetearBotones() {
        agregarButton.setVisible(true);
        actualizarButton.setVisible(false);
        viajeEditando = -1;
    }
    
    private boolean validarCampos() {
        boolean valido = true;
        limpiarErrores();
        
        if (origenField.getText().trim().isEmpty()) {
            origenErrorLabel.setText(UIConstants.REQUIRED_FIELD_ERROR);
            origenErrorLabel.setVisible(true);
            valido = false;
        }
        
        if (destinoField.getText().trim().isEmpty()) {
            destinoErrorLabel.setText(UIConstants.REQUIRED_FIELD_ERROR);
            destinoErrorLabel.setVisible(true);
            valido = false;
        }
        
        if (fechaSalidaSeleccionada == null) {
            fechaSalidaErrorLabel.setText(UIConstants.REQUIRED_FIELD_ERROR);
            fechaSalidaErrorLabel.setVisible(true);
            valido = false;
        }
        
        if (fechaLlegadaSeleccionada == null) {
            fechaLlegadaErrorLabel.setText(UIConstants.REQUIRED_FIELD_ERROR);
            fechaLlegadaErrorLabel.setVisible(true);
            valido = false;
        }
        
        return valido;
    }
    
    private void limpiarFormulario() {
        origenField.setText("");
        destinoField.setText("");
        fechaSalidaField.setText("");
        fechaLlegadaField.setText("");
        fechaSalidaSeleccionada = null;
        fechaLlegadaSeleccionada = null;
        estadoCombo.setSelectedIndex(0);
        limpiarErrores();
        resetearBotones();
        origenField.requestFocus();
    }
    
    private void limpiarErrores() {
        origenErrorLabel.setVisible(false);
        destinoErrorLabel.setVisible(false);
        fechaSalidaErrorLabel.setVisible(false);
        fechaLlegadaErrorLabel.setVisible(false);
    }
    
    private void buscarViajes() {
        String busqueda = searchField.getText().trim();
        List<Viaje> resultados;
        
        if (busqueda.isEmpty()) {
            resultados = ViajeManager.obtenerTodosLosViajes();
        } else {
            resultados = ViajeManager.buscarViajes(busqueda);
        }
        
        tableModel.setViajes(resultados);
    }
    
    private void refrescarTabla() {
        ViajeManager.inicializarDesdeBaseDatos();
        tableModel.refrescarTabla();
        searchField.setText("");
        
        viajesTable.revalidate();
        viajesTable.repaint();
        
        if (viajesTable.getColumnCount() > 6) {
            viajesTable.getColumnModel().getColumn(6).setCellRenderer(new ButtonEditor());
            viajesTable.getColumnModel().getColumn(6).setCellEditor(new ButtonEditor());
        }
    }
    
    private void mostrarMensaje(String mensaje, String titulo, int tipo) {
        JOptionPane.showMessageDialog(this, mensaje, titulo, tipo);
    }
    
    private class ButtonEditor implements TableCellRenderer, TableCellEditor {
        private JPanel buttonPanel;
        private JButton editButton;
        private JButton deleteButton;
        private int currentRow;
        
        public ButtonEditor() {
            buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
            buttonPanel.setOpaque(true);
            buttonPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
            
            editButton = new JButton();
            editButton.setPreferredSize(new Dimension(30, 25));
            editButton.setBackground(new Color(255, 193, 7));
            editButton.setForeground(Color.WHITE);
            editButton.setFocusPainted(false);
            editButton.setBorderPainted(false);
            editButton.setOpaque(true);
            
            FontIcon pencilIcon = FontIcon.of(FontAwesomeSolid.EDIT);
            pencilIcon.setIconSize(12);
            pencilIcon.setIconColor(Color.WHITE);
            editButton.setIcon(pencilIcon);
            editButton.setToolTipText(UIConstants.EDIT_TOOLTIP);
            
            deleteButton = new JButton();
            deleteButton.setPreferredSize(new Dimension(30, 25));
            deleteButton.setBackground(new Color(220, 53, 69));
            deleteButton.setForeground(Color.WHITE);
            deleteButton.setFocusPainted(false);
            deleteButton.setBorderPainted(false);
            deleteButton.setOpaque(true);
            
            FontIcon trashIcon = FontIcon.of(FontAwesomeSolid.TRASH_ALT);
            trashIcon.setIconSize(12);
            trashIcon.setIconColor(Color.WHITE);
            deleteButton.setIcon(trashIcon);
            deleteButton.setToolTipText(UIConstants.DELETE_TOOLTIP);
            
            editButton.addActionListener(e -> {
                editarViaje(currentRow);
                stopCellEditing();
            });
            
            deleteButton.addActionListener(e -> {
                eliminarViaje(currentRow);
                stopCellEditing();
            });
            
            buttonPanel.add(editButton);
            buttonPanel.add(deleteButton);
        }
        
        private void updateActionListeners() {
            for (java.awt.event.ActionListener al : editButton.getActionListeners()) {
                editButton.removeActionListener(al);
            }
            for (java.awt.event.ActionListener al : deleteButton.getActionListeners()) {
                deleteButton.removeActionListener(al);
            }
            
            editButton.addActionListener(e -> {
                editarViaje(currentRow);
                stopCellEditing();
            });
            
            deleteButton.addActionListener(e -> {
                eliminarViaje(currentRow);
                stopCellEditing();
            });
        }
        
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, 
                                                     boolean hasFocus, int row, int column) {
            currentRow = row;
            
            updateActionListeners();
            
            buttonPanel.setBackground(Color.WHITE);
            
            return buttonPanel;
        }
        
        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            currentRow = row;
            updateActionListeners();
            return buttonPanel;
        }
        
        @Override
        public Object getCellEditorValue() {
            return null;
        }
        
        @Override
        public boolean isCellEditable(EventObject anEvent) {
            return true;
        }
        
        @Override
        public boolean shouldSelectCell(EventObject anEvent) {
            return false;
        }
        
        @Override
        public boolean stopCellEditing() {
            return true;
        }
        
        @Override
        public void cancelCellEditing() {
        }
        
        @Override
        public void addCellEditorListener(CellEditorListener l) {
        }
        
        @Override
        public void removeCellEditorListener(CellEditorListener l) {
        }
    }
    
    
    private void adjustLayoutForSize(int width, int height) {
        if (width < 700) {
            contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
            contentPanel.removeAll();
            JPanel topPanel = new JPanel(new BorderLayout(UIConstants.PADDING, UIConstants.PADDING));
            topPanel.setBackground(UIConstants.BACKGROUND_COLOR);
            topPanel.add(formPanel, BorderLayout.CENTER);
            topPanel.add(buttonPanel, BorderLayout.SOUTH);
            
            JPanel bottomPanel = new JPanel(new BorderLayout(UIConstants.PADDING, UIConstants.PADDING));
            bottomPanel.setBackground(UIConstants.BACKGROUND_COLOR);
            bottomPanel.add(searchPanel, BorderLayout.NORTH);
            bottomPanel.add(tablePanel, BorderLayout.CENTER);
            
            contentPanel.add(topPanel);
            contentPanel.add(Box.createVerticalStrut(UIConstants.PADDING));
            contentPanel.add(bottomPanel);
        } else {
            contentPanel.setLayout(new BorderLayout(UIConstants.PADDING, UIConstants.PADDING));
            contentPanel.removeAll();
            JPanel leftPanel = new JPanel(new BorderLayout(UIConstants.PADDING, UIConstants.PADDING));
            leftPanel.setBackground(UIConstants.BACKGROUND_COLOR);
            leftPanel.add(formPanel, BorderLayout.CENTER);
            leftPanel.add(buttonPanel, BorderLayout.SOUTH);
            
            JPanel rightPanel = new JPanel(new BorderLayout(UIConstants.PADDING, UIConstants.PADDING));
            rightPanel.setBackground(UIConstants.BACKGROUND_COLOR);
            rightPanel.add(searchPanel, BorderLayout.NORTH);
            rightPanel.add(tablePanel, BorderLayout.CENTER);
            
            contentPanel.add(leftPanel, BorderLayout.WEST);
            contentPanel.add(rightPanel, BorderLayout.CENTER);
        }
        
        contentPanel.revalidate();
        contentPanel.repaint();
        
        adjustTableColumns(width);
    }
    
    private void adjustTableColumns(int frameWidth) {
        if (viajesTable != null) {
            int idWidth = 60;
            int actionsWidth = 120;
            int minOriginWidth = 100;
            int minDestinoWidth = 100;
            int minFechaWidth = 140;
            int minEstadoWidth = 100;
            
            int minTotalWidth = idWidth + minOriginWidth + minDestinoWidth + 
                              minFechaWidth + minFechaWidth + minEstadoWidth + actionsWidth;
            
            int availableWidth = frameWidth - 200;
            
            if (availableWidth >= minTotalWidth) {
                int extraWidth = availableWidth - minTotalWidth;
                int extraPerColumn = extraWidth / 5;
                
                viajesTable.getColumnModel().getColumn(0).setPreferredWidth(idWidth);
                viajesTable.getColumnModel().getColumn(1).setPreferredWidth(minOriginWidth + extraPerColumn);
                viajesTable.getColumnModel().getColumn(2).setPreferredWidth(minDestinoWidth + extraPerColumn);
                viajesTable.getColumnModel().getColumn(3).setPreferredWidth(minFechaWidth + extraPerColumn);
                viajesTable.getColumnModel().getColumn(4).setPreferredWidth(minFechaWidth + extraPerColumn);
                viajesTable.getColumnModel().getColumn(5).setPreferredWidth(minEstadoWidth + extraPerColumn);
                viajesTable.getColumnModel().getColumn(6).setPreferredWidth(actionsWidth);
            } else {
                viajesTable.getColumnModel().getColumn(0).setPreferredWidth(idWidth);
                viajesTable.getColumnModel().getColumn(1).setPreferredWidth(minOriginWidth);
                viajesTable.getColumnModel().getColumn(2).setPreferredWidth(minDestinoWidth);
                viajesTable.getColumnModel().getColumn(3).setPreferredWidth(minFechaWidth);
                viajesTable.getColumnModel().getColumn(4).setPreferredWidth(minFechaWidth);
                viajesTable.getColumnModel().getColumn(5).setPreferredWidth(minEstadoWidth);
                viajesTable.getColumnModel().getColumn(6).setPreferredWidth(actionsWidth);
            }
            
            viajesTable.revalidate();
            viajesTable.repaint();
        }
    }
    
    private JTextField createStyledTextField(String placeholder) {
        JTextField field = new JTextField();
        field.setPreferredSize(new Dimension(250, 45));
        field.setMinimumSize(new Dimension(200, 40));
        field.setFont(UIConstants.BODY_FONT);
        
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UIConstants.BORDER_COLOR, 1),
            new EmptyBorder(8, 10, 8, 10)
        ));
        
        if (placeholder != null && !placeholder.isEmpty()) {
            field.setToolTipText(placeholder);
        }
        
        return field;
    }
    
    private JButton createStyledButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(120, 40));
        button.setMinimumSize(new Dimension(100, 35));
        button.setFont(UIConstants.BODY_FONT);
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(color.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(color);
            }
        });
        
        return button;
    }
    
    private JLabel createErrorLabel() {
        JLabel label = new JLabel("");
        label.setFont(UIConstants.SMALL_FONT);
        label.setForeground(UIConstants.DANGER_COLOR);
        label.setVisible(false);
        return label;
    }
    
    private JButton createDatePickerButtonWithIcon() {
        JButton button = new JButton();
        button.setPreferredSize(new Dimension(45, 45));
        button.setMinimumSize(new Dimension(40, 40));
        button.setBackground(UIConstants.PRIMARY_COLOR);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        
        FontIcon calendarIcon = FontIcon.of(FontAwesomeSolid.CALENDAR_ALT);
        calendarIcon.setIconSize(18);
        calendarIcon.setIconColor(Color.WHITE);
        button.setIcon(calendarIcon);
        
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(UIConstants.PRIMARY_COLOR.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(UIConstants.PRIMARY_COLOR);
            }
        });
        
        return button;
    }
    
    
    private void abrirDatePicker(String titulo, Date fechaActual, JTextField campoTexto, boolean esFechaSalida) {
        JDialog dialog = new JDialog(this, titulo, true);
        dialog.setSize(450, 480);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        JPanel calendarPanel = new JPanel(new BorderLayout());
        calendarPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1), 
            UIConstants.SELECT_DATE_TITLE, 
            javax.swing.border.TitledBorder.LEFT, 
            javax.swing.border.TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 12),
            new Color(70, 70, 70)
        ));
        
        JCalendar calendar = new JCalendar();
        if (fechaActual != null) {
            calendar.setDate(fechaActual);
        }
        
        calendar.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        calendar.setDecorationBackgroundColor(new Color(245, 245, 245));
        calendar.setDecorationBackgroundVisible(true);
        calendar.setWeekOfYearVisible(false);
        
        calendar.setForeground(new Color(51, 51, 51));
        calendar.setBackground(Color.WHITE);
        
        customizeCalendarComboBoxes(calendar);
        
        calendarPanel.add(calendar, BorderLayout.CENTER);
        
        JPanel timePanel = new JPanel(new BorderLayout());
        timePanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1), 
            UIConstants.SELECT_TIME_TITLE, 
            javax.swing.border.TitledBorder.LEFT, 
            javax.swing.border.TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 12),
            new Color(70, 70, 70)
        ));
        timePanel.setPreferredSize(new Dimension(400, 70));
        timePanel.setMinimumSize(new Dimension(400, 70));
        
        JPanel timeControls = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        timeControls.setBorder(new EmptyBorder(8, 15, 8, 15));
        
        String[] horas = new String[24];
        for (int i = 0; i < 24; i++) {
            horas[i] = String.format("%02d", i);
        }
        JComboBox<String> horaCombo = new JComboBox<>(horas);
        horaCombo.setPreferredSize(new Dimension(70, 35));
        horaCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        horaCombo.setBackground(Color.WHITE);
        horaCombo.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));
        
        JLabel dosPuntos = new JLabel(":");
        dosPuntos.setFont(new Font("Segoe UI", Font.BOLD, 16));
        dosPuntos.setForeground(new Color(70, 70, 70));
        
        String[] minutos = new String[60];
        for (int i = 0; i < 60; i++) {
            minutos[i] = String.format("%02d", i);
        }
        JComboBox<String> minutoCombo = new JComboBox<>(minutos);
        minutoCombo.setPreferredSize(new Dimension(70, 35));
        minutoCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        minutoCombo.setBackground(Color.WHITE);
        minutoCombo.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));
        
        JLabel horaLabel = new JLabel(UIConstants.TIME_LABEL);
        horaLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        horaLabel.setForeground(new Color(70, 70, 70));
        
        timeControls.add(horaLabel);
        timeControls.add(horaCombo);
        timeControls.add(dosPuntos);
        timeControls.add(minutoCombo);
        
        timePanel.add(timeControls, BorderLayout.CENTER);
        
        Date fechaInicialHora = fechaActual != null ? fechaActual : new Date();
        Calendar calHora = Calendar.getInstance();
        calHora.setTime(fechaInicialHora);
        horaCombo.setSelectedIndex(calHora.get(Calendar.HOUR_OF_DAY));
        minutoCombo.setSelectedIndex(calHora.get(Calendar.MINUTE));
        
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton okButton = createStyledButton(UIConstants.ACCEPT_BUTTON_TEXT, UIConstants.SUCCESS_COLOR);
        JButton cancelButton = createStyledButton(UIConstants.CANCEL_BUTTON_TEXT, UIConstants.SECONDARY_COLOR);
        
        FontIcon checkIcon = FontIcon.of(FontAwesomeSolid.CHECK);
        checkIcon.setIconSize(16);
        checkIcon.setIconColor(Color.WHITE);
        
        FontIcon timesIcon = FontIcon.of(FontAwesomeSolid.TIMES);
        timesIcon.setIconSize(16);
        timesIcon.setIconColor(Color.WHITE);
        
        okButton.setIcon(checkIcon);
        cancelButton.setIcon(timesIcon);
        
        okButton.addActionListener(e -> {
            try {
                Date fechaSeleccionada = calendar.getDate();
                
                int hora = horaCombo.getSelectedIndex();
                int minuto = minutoCombo.getSelectedIndex();
                
                Calendar fechaHora = Calendar.getInstance();
                fechaHora.setTime(fechaSeleccionada);
                fechaHora.set(Calendar.HOUR_OF_DAY, hora);
                fechaHora.set(Calendar.MINUTE, minuto);
                fechaHora.set(Calendar.SECOND, 0);
                fechaHora.set(Calendar.MILLISECOND, 0);
                
                Date fechaCompleta = fechaHora.getTime();
                
                if (esFechaSalida && fechaLlegadaSeleccionada != null && 
                    fechaCompleta.after(fechaLlegadaSeleccionada)) {
                    JOptionPane.showMessageDialog(dialog, 
                        UIConstants.ERROR_DEPARTURE_AFTER_ARRIVAL, 
                        UIConstants.ERROR_TITLE, JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                if (!esFechaSalida && fechaSalidaSeleccionada != null && 
                    fechaCompleta.before(fechaSalidaSeleccionada)) {
                    JOptionPane.showMessageDialog(dialog, 
                        UIConstants.ERROR_ARRIVAL_BEFORE_DEPARTURE, 
                        UIConstants.ERROR_TITLE, JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                if (esFechaSalida) {
                    fechaSalidaSeleccionada = fechaCompleta;
                } else {
                    fechaLlegadaSeleccionada = fechaCompleta;
                }
                
                campoTexto.setText(dateFormat.format(fechaCompleta));
                dialog.dispose();
                
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, UIConstants.ERROR_PROCESS_DATE, UIConstants.ERROR_TITLE, JOptionPane.ERROR_MESSAGE);
            }
        });
        
        cancelButton.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        
        mainPanel.add(calendarPanel, BorderLayout.NORTH);
        mainPanel.add(timePanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.add(mainPanel);
        dialog.setVisible(true);
    }
    
    private void customizeCalendarComboBoxes(JCalendar calendar) {
        try {
            java.lang.reflect.Field monthComboBoxField = calendar.getClass().getDeclaredField("monthComboBox");
            monthComboBoxField.setAccessible(true);
            JComboBox<?> monthComboBox = (JComboBox<?>) monthComboBoxField.get(calendar);
            
            java.lang.reflect.Field yearComboBoxField = calendar.getClass().getDeclaredField("yearComboBox");
            yearComboBoxField.setAccessible(true);
            JComboBox<?> yearComboBox = (JComboBox<?>) yearComboBoxField.get(calendar);
            
            if (monthComboBox != null) {
                monthComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                monthComboBox.setBackground(Color.WHITE);
                monthComboBox.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));
                monthComboBox.setPreferredSize(new Dimension(120, 32));
            }
            
            if (yearComboBox != null) {
                yearComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                yearComboBox.setBackground(Color.WHITE);
                yearComboBox.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));
                yearComboBox.setPreferredSize(new Dimension(90, 32));
            }
        } catch (Exception e) {
            System.out.println(UIConstants.CALENDAR_COMBOBOX_ERROR + e.getMessage());
        }
    }
    
    private void editarViaje(int row) {
        int modelRow = viajesTable.convertRowIndexToModel(row);
        
        Viaje viaje = ViajeManager.obtenerViaje(modelRow);
        if (viaje != null) {
            viajeEditando = viaje.getId();
            
            origenField.setText(viaje.getOrigen());
            destinoField.setText(viaje.getDestino());
            
            fechaSalidaSeleccionada = viaje.getFechaSalida();
            fechaLlegadaSeleccionada = viaje.getFechaLlegada();
            
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            fechaSalidaField.setText(formatter.format(viaje.getFechaSalida()));
            fechaLlegadaField.setText(formatter.format(viaje.getFechaLlegada()));
            
            estadoCombo.setSelectedItem(viaje.getEstado());
            
            agregarButton.setVisible(false);
            actualizarButton.setVisible(true);
        }
    }
    
    private void eliminarViaje(int row) {
        int modelRow = viajesTable.convertRowIndexToModel(row);
        
        int confirmacion = JOptionPane.showOptionDialog(
            this,
            UIConstants.DELETE_CONFIRMATION,
            UIConstants.DELETE_CONFIRMATION_TITLE,
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,
            new String[]{UIConstants.YES_OPTION, UIConstants.NO_OPTION},
            UIConstants.NO_OPTION
        );
        
        if (confirmacion == 0) {
            if (ViajeManager.eliminarViaje(modelRow)) {
                refrescarTabla();
                mostrarMensaje(UIConstants.SUCCESS_DELETE_TRIP, UIConstants.SUCCESS_TITLE, JOptionPane.INFORMATION_MESSAGE);
            } else {
                mostrarMensaje(UIConstants.ERROR_DELETE_TRIP, UIConstants.ERROR_TITLE, JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void descargarJSON() {
        try {
            List<Viaje> viajes = ViajeManager.obtenerTodosLosViajes();
            
            if (viajes.isEmpty()) {
                mostrarMensaje("No hay viajes para exportar", UIConstants.ERROR_TITLE, JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            SimpleDateFormat timestampFormat = new SimpleDateFormat("ddMMyyyy_HHmmss");
            String timestamp = timestampFormat.format(new Date());
            String fileName = "ReporteViajes_" + timestamp + ".json";
            
            StringBuilder json = new StringBuilder();
            json.append("[\n");
            
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            
            for (int i = 0; i < viajes.size(); i++) {
                Viaje viaje = viajes.get(i);
                json.append("  {\n");
                json.append("    \"id\": ").append(viaje.getId()).append(",\n");
                json.append("    \"origen\": \"").append(escapeJson(viaje.getOrigen())).append("\",\n");
                json.append("    \"destino\": \"").append(escapeJson(viaje.getDestino())).append("\",\n");
                json.append("    \"fechaSalida\": \"").append(dateFormat.format(viaje.getFechaSalida())).append("\",\n");
                json.append("    \"fechaLlegada\": \"").append(dateFormat.format(viaje.getFechaLlegada())).append("\",\n");
                json.append("    \"estado\": \"").append(escapeJson(viaje.getEstado())).append("\"\n");
                
                if (i < viajes.size() - 1) {
                    json.append("  },\n");
                } else {
                    json.append("  }\n");
                }
            }
            
            json.append("]");
            
            String userHome = System.getProperty("user.home");
            String filePath = Paths.get(userHome, "Downloads", fileName).toString();
            
            try (FileWriter writer = new FileWriter(filePath)) {
                writer.write(json.toString());
                mostrarMensaje("Archivo JSON generado exitosamente:\n" + filePath, UIConstants.SUCCESS_TITLE, JOptionPane.INFORMATION_MESSAGE);
            }
            
        } catch (IOException e) {
            mostrarMensaje("Error al generar el archivo JSON: " + e.getMessage(), UIConstants.ERROR_TITLE, JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private String escapeJson(String text) {
        if (text == null) return "";
        return text.replace("\\", "\\\\")
                  .replace("\"", "\\\"")
                  .replace("\b", "\\b")
                  .replace("\f", "\\f")
                  .replace("\n", "\\n")
                  .replace("\r", "\\r")
                  .replace("\t", "\\t");
    }

    public static void main(String args[]) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new ViajeFrame().setVisible(true);
            }
        });
    }
}
