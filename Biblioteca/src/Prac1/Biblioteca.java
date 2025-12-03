package Prac1;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Biblioteca {

    private JFrame frame;
    private JPanel mainPanel;
    private CardLayout cardLayout;
    
    // Archivos para persistencia
    private static final String LIBROS_FILE = "libros.dat";
    private static final String AUTORES_FILE = "autores.dat";
    private static final String ESTUDIANTES_FILE = "estudiantes.dat";
    
    // Estructuras de datos para almacenar información
    private List<Libro> libros;
    private List<Autor> autores;
    private List<Estudiante> estudiantes;
    private DefaultListModel<String> listModelLibros;
    private DefaultTableModel tableModelAutores;
    private DefaultTableModel tableModelEstudiantes;
    
    // Componentes de UI
    private JList<String> listaLibros;
    private JTable tablaAutores;
    private JTable tablaEstudiantes;
    private JTextField[] camposEstudiante;

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    Biblioteca window = new Biblioteca();
                    window.frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public Biblioteca() {
        cargarDatos(); // Cargar datos persistentes
        initialize();
    }

    // ==================== PERSISTENCIA DE OBJETOS ====================
    
    private void cargarDatos() {
        // Cargar libros
        libros = cargarDesdeArchivo(LIBROS_FILE);
        if (libros == null) {
            libros = new ArrayList<>();
            // Datos iniciales si no hay archivo
            libros.add(new Libro("Cien años de soledad", "Gabriel García Márquez", "1967", "Novela"));
            libros.add(new Libro("1984", "George Orwell", "1949", "Ciencia Ficción"));
        }
        
        // Cargar autores
        autores = cargarDesdeArchivo(AUTORES_FILE);
        if (autores == null) {
            autores = new ArrayList<>();
            // Datos iniciales si no hay archivo
            autores.add(new Autor("Gabriel García Márquez", "Colombiano", 45, "1927"));
            autores.add(new Autor("Jane Austen", "Británica", 6, "1775"));
        }
        
        // Cargar estudiantes
        estudiantes = cargarDesdeArchivo(ESTUDIANTES_FILE);
        if (estudiantes == null) {
            estudiantes = new ArrayList<>();
            // Datos iniciales si no hay archivo
            estudiantes.add(new Estudiante("2023001", "Ana García López", "Ing. Sistemas", "2023", "555-0101"));
            estudiantes.add(new Estudiante("2023002", "Carlos Rodríguez", "Medicina", "2023", "555-0102"));
        }
        
        // Inicializar modelos
        listModelLibros = new DefaultListModel<>();
        actualizarListaLibros();
        
        String[] columnasAutores = {"Nombre", "Nacionalidad", "Libros", "Año Nac."};
        tableModelAutores = new DefaultTableModel(columnasAutores, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        actualizarTablaAutores();
        
        String[] columnasEstudiantes = {"Matrícula", "Nombre", "Carrera", "Año"};
        tableModelEstudiantes = new DefaultTableModel(columnasEstudiantes, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        actualizarTablaEstudiantes();
    }
    
    @SuppressWarnings("unchecked")
    private <T> List<T> cargarDesdeArchivo(String filename) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename))) {
            return (List<T>) ois.readObject();
        } catch (FileNotFoundException e) {
            System.out.println("Archivo no encontrado: " + filename + ". Se creará uno nuevo.");
            return null;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    private <T> void guardarEnArchivo(List<T> lista, String filename) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            oos.writeObject(lista);
            System.out.println("Datos guardados en: " + filename);
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, 
                "Error al guardar datos en: " + filename, 
                "Error de Persistencia", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void guardarTodo() {
        guardarEnArchivo(libros, LIBROS_FILE);
        guardarEnArchivo(autores, AUTORES_FILE);
        guardarEnArchivo(estudiantes, ESTUDIANTES_FILE);
    }

    private void actualizarListaLibros() {
        listModelLibros.clear();
        for (Libro libro : libros) {
            listModelLibros.addElement(libro.getTitulo() + " - " + libro.getAutor());
        }
    }

    private void actualizarTablaAutores() {
        tableModelAutores.setRowCount(0);
        for (Autor autor : autores) {
            tableModelAutores.addRow(new Object[]{
                autor.getNombre(),
                autor.getNacionalidad(),
                autor.getCantidadLibros(),
                autor.getAnioNacimiento()
            });
        }
    }

    private void actualizarTablaEstudiantes() {
        tableModelEstudiantes.setRowCount(0);
        for (Estudiante estudiante : estudiantes) {
            tableModelEstudiantes.addRow(new Object[]{
                estudiante.getMatricula(),
                estudiante.getNombre(),
                estudiante.getCarrera(),
                estudiante.getAnioIngreso()
            });
        }
    }

    // ==================== INTERFAZ DE USUARIO ====================
    
    private void initialize() {
        frame = new JFrame();
        frame.setBounds(100, 100, 1000, 700);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setTitle("Sistema de Biblioteca - Con Persistencia");
        
        // Menú principal
        JMenuBar menuBar = new JMenuBar();
        
        // Menú Archivo
        JMenu menuArchivo = new JMenu("Archivo");
        JMenuItem menuItemGuardar = new JMenuItem("Guardar Todo");
        JMenuItem menuItemSalir = new JMenuItem("Salir");
        
        menuItemGuardar.addActionListener(e -> guardarTodo());
        menuItemSalir.addActionListener(e -> {
            guardarTodo();
            System.exit(0);
        });
        
        menuArchivo.add(menuItemGuardar);
        menuArchivo.addSeparator();
        menuArchivo.add(menuItemSalir);
        
        // Menú Navegación
        JMenu menuNavegacion = new JMenu("Navegación");
        JMenuItem menuItemInicio = new JMenuItem("Inicio");
        JMenuItem menuItemLibros = new JMenuItem("Libros");
        JMenuItem menuItemAutores = new JMenuItem("Autores");
        JMenuItem menuItemEstudiantes = new JMenuItem("Estudiantes");
        
        menuItemInicio.addActionListener(e -> cardLayout.show(mainPanel, "Menu"));
        menuItemLibros.addActionListener(e -> {
            actualizarListaLibros();
            cardLayout.show(mainPanel, "Libros");
        });
        menuItemAutores.addActionListener(e -> {
            actualizarTablaAutores();
            cardLayout.show(mainPanel, "Autores");
        });
        menuItemEstudiantes.addActionListener(e -> {
            actualizarTablaEstudiantes();
            cardLayout.show(mainPanel, "Estudiantes");
        });
        
        menuNavegacion.add(menuItemInicio);
        menuNavegacion.add(menuItemLibros);
        menuNavegacion.add(menuItemAutores);
        menuNavegacion.add(menuItemEstudiantes);
        
        // Menú Ayuda
        JMenu menuAyuda = new JMenu("Ayuda");
        JMenuItem menuItemAcercaDe = new JMenuItem("Acerca de");
        menuItemAcercaDe.addActionListener(e -> mostrarAcercaDe());
        menuAyuda.add(menuItemAcercaDe);
        
        menuBar.add(menuArchivo);
        menuBar.add(menuNavegacion);
        menuBar.add(menuAyuda);
        
        frame.setJMenuBar(menuBar);
        
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
       
        crearPanelMenu();
        crearPanelBiblioteca();
        crearPanelLibros();
        crearPanelAutores();
        crearPanelEstudiantes();
        
        frame.getContentPane().add(mainPanel);
        cardLayout.show(mainPanel, "Menu");
    }

    private void mostrarAcercaDe() {
        JOptionPane.showMessageDialog(frame,
            "Sistema de Biblioteca v1.0\n" +
            "Con Persistencia de Objetos\n" +
            "© 2024 - Práctica 6\n" +
            "Datos guardados en archivos: \n" +
            "- libros.dat\n" +
            "- autores.dat\n" +
            "- estudiantes.dat",
            "Acerca del Sistema",
            JOptionPane.INFORMATION_MESSAGE);
    }

    private GradientPaint crearDegradadoMoradoEsmeralda() {
        return new GradientPaint(0, 0, new Color(75, 0, 130), 1000, 700, new Color(0, 128, 128));
    }

    private void crearPanelMenu() {
        JPanel panelMenu = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setPaint(crearDegradadoMoradoEsmeralda());
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        panelMenu.setLayout(new BorderLayout());
        
        JLabel lblHeader = new JLabel("MENÚ PRINCIPAL", JLabel.CENTER);
        lblHeader.setFont(new Font("Arial", Font.BOLD, 32));
        lblHeader.setForeground(Color.WHITE);
        lblHeader.setBorder(BorderFactory.createEmptyBorder(40, 0, 50, 0));
        
        JPanel panelBotones = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setPaint(new GradientPaint(0, 0, new Color(75, 0, 130, 150), 
                                              getWidth(), getHeight(), new Color(0, 128, 128, 150)));
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        panelBotones.setLayout(new GridLayout(2, 2, 30, 30));
        panelBotones.setBorder(BorderFactory.createEmptyBorder(0, 100, 100, 100));
        panelBotones.setOpaque(false);
        
        JButton btnBiblioteca = crearBotonMenu("BIBLIOTECA", new Color(128, 0, 128), new Color(0, 100, 100));
        JButton btnLibros = crearBotonMenu("LIBROS", new Color(147, 112, 219), new Color(64, 224, 208));
        JButton btnAutores = crearBotonMenu("AUTORES", new Color(186, 85, 211), new Color(32, 178, 170));
        JButton btnEstudiantes = crearBotonMenu("ESTUDIANTES", new Color(138, 43, 226), new Color(0, 139, 139));
        
        btnBiblioteca.addActionListener(e -> cardLayout.show(mainPanel, "Biblioteca"));
        btnLibros.addActionListener(e -> {
            actualizarListaLibros();
            cardLayout.show(mainPanel, "Libros");
        });
        btnAutores.addActionListener(e -> {
            actualizarTablaAutores();
            cardLayout.show(mainPanel, "Autores");
        });
        btnEstudiantes.addActionListener(e -> {
            actualizarTablaEstudiantes();
            cardLayout.show(mainPanel, "Estudiantes");
        });
        
        panelBotones.add(btnBiblioteca);
        panelBotones.add(btnLibros);
        panelBotones.add(btnAutores);
        panelBotones.add(btnEstudiantes);
        
        panelMenu.add(lblHeader, BorderLayout.NORTH);
        panelMenu.add(panelBotones, BorderLayout.CENTER);
        
        // Botón de guardar en el menú principal
        JPanel panelInferior = new JPanel();
        panelInferior.setOpaque(false);
        JButton btnGuardarMenu = new JButton("💾 Guardar Todos los Datos");
        btnGuardarMenu.addActionListener(e -> {
            guardarTodo();
            JOptionPane.showMessageDialog(frame, "Todos los datos han sido guardados exitosamente!");
        });
        btnGuardarMenu.setFont(new Font("Arial", Font.BOLD, 14));
        btnGuardarMenu.setBackground(new Color(50, 205, 50));
        btnGuardarMenu.setForeground(Color.WHITE);
        panelInferior.add(btnGuardarMenu);
        
        panelMenu.add(panelInferior, BorderLayout.SOUTH);
        
        mainPanel.add(panelMenu, "Menu");
    }

    private JButton crearBotonMenu(String texto, Color colorInicio, Color colorFin) {
        JButton boton = new JButton(texto) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                GradientPaint gradient = new GradientPaint(0, 0, colorInicio, getWidth(), getHeight(), colorFin);
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);
                super.paintComponent(g);
            }
        };
        
        boton.setFont(new Font("Arial", Font.BOLD, 20));
        boton.setForeground(Color.WHITE);
        boton.setFocusPainted(false);
        boton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        boton.setContentAreaFilled(false);
        boton.setPreferredSize(new Dimension(250, 100));
        
        boton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                boton.setForeground(new Color(255, 255, 224));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                boton.setForeground(Color.WHITE);
            }
        });
        
        return boton;
    }

    private void crearPanelBiblioteca() {
        JPanel panel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setPaint(crearDegradadoMoradoEsmeralda());
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                GradientPaint gradient = new GradientPaint(0, 0, new Color(75, 0, 130), 
                                                          getWidth(), 0, new Color(0, 128, 128));
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        headerPanel.setOpaque(false);
        
        JButton btnRegresar = crearBotonRegresar();
        btnRegresar.addActionListener(e -> cardLayout.show(mainPanel, "Menu"));
        
        JLabel lblTitulo = new JLabel("MÓDULO BIBLIOTECA - INFORMACIÓN GENERAL", JLabel.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 22));
        lblTitulo.setForeground(Color.WHITE);
        
        headerPanel.add(btnRegresar, BorderLayout.WEST);
        headerPanel.add(lblTitulo, BorderLayout.CENTER);
        
        // Panel de estadísticas
        JPanel statsPanel = new JPanel(new GridLayout(2, 2, 20, 20));
        statsPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        statsPanel.setOpaque(false);
        
        statsPanel.add(crearTarjetaEstadistica("📚 Total Libros", String.valueOf(libros.size()), new Color(147, 112, 219)));
        statsPanel.add(crearTarjetaEstadistica("🎓 Estudiantes Registrados", String.valueOf(estudiantes.size()), new Color(64, 224, 208)));
        statsPanel.add(crearTarjetaEstadistica("✍ Autores Registrados", String.valueOf(autores.size()), new Color(186, 85, 211)));
        statsPanel.add(crearTarjetaEstadistica("📋 Préstamos Activos", "67", new Color(32, 178, 170)));
        
        // Panel de archivos persistentes
        JPanel archivosPanel = new JPanel(new BorderLayout());
        archivosPanel.setBorder(BorderFactory.createTitledBorder("💾 Archivos de Persistencia"));
        archivosPanel.setBackground(new Color(255, 255, 255, 150));
        archivosPanel.setOpaque(true);
        
        JTextArea infoArchivos = new JTextArea();
        infoArchivos.setText("Archivos de datos guardados:\n\n" +
                           "• libros.dat - " + libros.size() + " libros\n" +
                           "• autores.dat - " + autores.size() + " autores\n" +
                           "• estudiantes.dat - " + estudiantes.size() + " estudiantes\n\n" +
                           "Los datos se guardan automáticamente al salir\n" +
                           "y se pueden guardar manualmente desde el menú.");
        infoArchivos.setFont(new Font("Monospaced", Font.PLAIN, 12));
        infoArchivos.setEditable(false);
        infoArchivos.setBackground(new Color(240, 248, 255));
        
        archivosPanel.add(new JScrollPane(infoArchivos), BorderLayout.CENTER);
        
        JPanel panelCentral = new JPanel(new BorderLayout());
        panelCentral.setOpaque(false);
        panelCentral.add(statsPanel, BorderLayout.CENTER);
        panelCentral.add(archivosPanel, BorderLayout.SOUTH);
        
        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(panelCentral, BorderLayout.CENTER);
        
        mainPanel.add(panel, "Biblioteca");
    }

    private JPanel crearTarjetaEstadistica(String titulo, String valor, Color colorBase) {
        JPanel tarjeta = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                GradientPaint gradient = new GradientPaint(0, 0, colorBase, 
                                                          getWidth(), getHeight(), 
                                                          new Color(colorBase.getRed(), colorBase.getGreen(), colorBase.getBlue(), 150));
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
            }
        };
        
        tarjeta.setLayout(new BorderLayout());
        tarjeta.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        tarjeta.setOpaque(false);
        tarjeta.setPreferredSize(new Dimension(200, 150));
        
        JLabel lblTitulo = new JLabel(titulo, JLabel.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 16));
        lblTitulo.setForeground(Color.WHITE);
        
        JLabel lblValor = new JLabel(valor, JLabel.CENTER);
        lblValor.setFont(new Font("Arial", Font.BOLD, 28));
        lblValor.setForeground(Color.WHITE);
        
        tarjeta.add(lblTitulo, BorderLayout.NORTH);
        tarjeta.add(lblValor, BorderLayout.CENTER);
        
        return tarjeta;
    }

    private void crearPanelLibros() {
        JPanel panel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setPaint(crearDegradadoMoradoEsmeralda());
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        
        // Header
        JPanel headerPanel = crearHeaderPanel("MÓDULO LIBROS - GESTIÓN DE LIBROS");
        
        // Panel principal dividido
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(600);
        splitPane.setOpaque(false);
        
        // Panel izquierdo - Lista de libros
        JPanel panelLibros = new JPanel(new BorderLayout());
        panelLibros.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        panelLibros.setBackground(new Color(255, 255, 255, 200));
        
        listaLibros = new JList<>(listModelLibros);
        listaLibros.setFont(new Font("Arial", Font.PLAIN, 14));
        listaLibros.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listaLibros.setBackground(new Color(240, 248, 255));
        
        JScrollPane scrollLibros = new JScrollPane(listaLibros);
        scrollLibros.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(75, 0, 130), 2),
            "📚 Catálogo de Libros Disponibles"
        ));
        
        panelLibros.add(scrollLibros, BorderLayout.CENTER);
        
        // Panel derecho - Botones de acción
        JPanel panelAcciones = new JPanel();
        panelAcciones.setLayout(new BoxLayout(panelAcciones, BoxLayout.Y_AXIS));
        panelAcciones.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        panelAcciones.setBackground(new Color(255, 255, 255, 200));
        
        JButton btnLibrosAutor = crearBotonAccion("📋 Libros por Autor", new Color(147, 112, 219));
        JButton btnAgregarLibro = crearBotonAccion("➕ Agregar Libro", new Color(64, 224, 208));
        JButton btnEditarLibro = crearBotonAccion("✏ Editar Libro", new Color(186, 85, 211));
        JButton btnEliminarLibro = crearBotonAccion("🗑 Eliminar Libro", new Color(32, 178, 170));
        JButton btnBuscarLibro = crearBotonAccion("🔍 Buscar Libro", new Color(75, 0, 130));
        JButton btnDetallesLibro = crearBotonAccion("📖 Ver Detalles", new Color(138, 43, 226));
        JButton btnGuardarLibros = crearBotonAccion("💾 Guardar Libros", new Color(50, 205, 50));
        
        // Implementación de funcionalidades de botones
        btnLibrosAutor.addActionListener(e -> mostrarLibrosPorAutor());
        btnAgregarLibro.addActionListener(e -> agregarLibro());
        btnEditarLibro.addActionListener(e -> editarLibro());
        btnEliminarLibro.addActionListener(e -> eliminarLibro());
        btnBuscarLibro.addActionListener(e -> buscarLibro());
        btnDetallesLibro.addActionListener(e -> mostrarDetallesLibro());
        btnGuardarLibros.addActionListener(e -> {
            guardarEnArchivo(libros, LIBROS_FILE);
            JOptionPane.showMessageDialog(frame, "Libros guardados exitosamente!");
        });
        
        panelAcciones.add(btnLibrosAutor);
        panelAcciones.add(Box.createRigidArea(new Dimension(0, 10)));
        panelAcciones.add(btnAgregarLibro);
        panelAcciones.add(Box.createRigidArea(new Dimension(0, 10)));
        panelAcciones.add(btnEditarLibro);
        panelAcciones.add(Box.createRigidArea(new Dimension(0, 10)));
        panelAcciones.add(btnEliminarLibro);
        panelAcciones.add(Box.createRigidArea(new Dimension(0, 10)));
        panelAcciones.add(btnBuscarLibro);
        panelAcciones.add(Box.createRigidArea(new Dimension(0, 10)));
        panelAcciones.add(btnDetallesLibro);
        panelAcciones.add(Box.createRigidArea(new Dimension(0, 10)));
        panelAcciones.add(btnGuardarLibros);
        
        splitPane.setLeftComponent(panelLibros);
        splitPane.setRightComponent(panelAcciones);
        
        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(splitPane, BorderLayout.CENTER);
        
        mainPanel.add(panel, "Libros");
    }

    private void crearPanelAutores() {
        JPanel panel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setPaint(crearDegradadoMoradoEsmeralda());
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        
        JPanel headerPanel = crearHeaderPanel("MÓDULO AUTORES - GESTIÓN DE AUTORES");
        
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(600);
        splitPane.setOpaque(false);
        
        JPanel panelAutores = new JPanel(new BorderLayout());
        panelAutores.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        panelAutores.setBackground(new Color(255, 255, 255, 200));
        
        tablaAutores = new JTable(tableModelAutores);
        tablaAutores.setFont(new Font("Arial", Font.PLAIN, 12));
        tablaAutores.setRowHeight(25);
        tablaAutores.setBackground(new Color(240, 248, 255));
        tablaAutores.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        JScrollPane scrollAutores = new JScrollPane(tablaAutores);
        scrollAutores.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(75, 0, 130), 2),
            "✍ Lista de Autores Registrados"
        ));
        
        panelAutores.add(scrollAutores, BorderLayout.CENTER);
        
        JPanel panelAcciones = new JPanel();
        panelAcciones.setLayout(new BoxLayout(panelAcciones, BoxLayout.Y_AXIS));
        panelAcciones.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        panelAcciones.setBackground(new Color(255, 255, 255, 200));
        
        JButton btnVerLibrosAutor = crearBotonAccion("📚 Ver Libros del Autor", new Color(147, 112, 219));
        JButton btnAgregarAutor = crearBotonAccion("➕ Agregar Autor", new Color(64, 224, 208));
        JButton btnEditarAutor = crearBotonAccion("✏ Editar Autor", new Color(186, 85, 211));
        JButton btnEliminarAutor = crearBotonAccion("🗑 Eliminar Autor", new Color(32, 178, 170));
        JButton btnDetallesAutor = crearBotonAccion("👤 Ver Detalles Autor", new Color(75, 0, 130));
        JButton btnGuardarAutores = crearBotonAccion("💾 Guardar Autores", new Color(50, 205, 50));
        
        // Implementación de funcionalidades de botones
        btnVerLibrosAutor.addActionListener(e -> verLibrosDelAutor());
        btnAgregarAutor.addActionListener(e -> agregarAutor());
        btnEditarAutor.addActionListener(e -> editarAutor());
        btnEliminarAutor.addActionListener(e -> eliminarAutor());
        btnDetallesAutor.addActionListener(e -> mostrarDetallesAutor());
        btnGuardarAutores.addActionListener(e -> {
            guardarEnArchivo(autores, AUTORES_FILE);
            JOptionPane.showMessageDialog(frame, "Autores guardados exitosamente!");
        });
        
        panelAcciones.add(btnVerLibrosAutor);
        panelAcciones.add(Box.createRigidArea(new Dimension(0, 10)));
        panelAcciones.add(btnAgregarAutor);
        panelAcciones.add(Box.createRigidArea(new Dimension(0, 10)));
        panelAcciones.add(btnEditarAutor);
        panelAcciones.add(Box.createRigidArea(new Dimension(0, 10)));
        panelAcciones.add(btnEliminarAutor);
        panelAcciones.add(Box.createRigidArea(new Dimension(0, 10)));
        panelAcciones.add(btnDetallesAutor);
        panelAcciones.add(Box.createRigidArea(new Dimension(0, 10)));
        panelAcciones.add(btnGuardarAutores);
        
        splitPane.setLeftComponent(panelAutores);
        splitPane.setRightComponent(panelAcciones);
        
        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(splitPane, BorderLayout.CENTER);
        
        mainPanel.add(panel, "Autores");
    }

    private void crearPanelEstudiantes() {
        JPanel panel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setPaint(crearDegradadoMoradoEsmeralda());
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        
        JPanel headerPanel = crearHeaderPanel("MÓDULO ESTUDIANTES - REGISTRO Y GESTIÓN");
        
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(500);
        splitPane.setOpaque(false);
        
        JPanel panelEstudiantes = new JPanel(new BorderLayout());
        panelEstudiantes.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        panelEstudiantes.setBackground(new Color(255, 255, 255, 200));
        
        tablaEstudiantes = new JTable(tableModelEstudiantes);
        tablaEstudiantes.setFont(new Font("Arial", Font.PLAIN, 12));
        tablaEstudiantes.setRowHeight(25);
        tablaEstudiantes.setBackground(new Color(240, 248, 255));
        tablaEstudiantes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        JScrollPane scrollEstudiantes = new JScrollPane(tablaEstudiantes);
        scrollEstudiantes.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(75, 0, 130), 2),
            "🎓 Estudiantes Registrados en Biblioteca"
        ));
        
        panelEstudiantes.add(scrollEstudiantes, BorderLayout.CENTER);
        
        JPanel panelDerecho = new JPanel();
        panelDerecho.setLayout(new BoxLayout(panelDerecho, BoxLayout.Y_AXIS));
        panelDerecho.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        panelDerecho.setBackground(new Color(255, 255, 255, 200));
        
        JPanel panelFormulario = new JPanel(new GridLayout(5, 2, 10, 10));
        panelFormulario.setBorder(BorderFactory.createTitledBorder("📝 Registrar Nuevo Estudiante"));
        panelFormulario.setBackground(new Color(255, 255, 255, 200));
        
        camposEstudiante = new JTextField[5];
        panelFormulario.add(new JLabel("Matrícula:"));
        camposEstudiante[0] = new JTextField();
        panelFormulario.add(camposEstudiante[0]);
        panelFormulario.add(new JLabel("Nombre:"));
        camposEstudiante[1] = new JTextField();
        panelFormulario.add(camposEstudiante[1]);
        panelFormulario.add(new JLabel("Carrera:"));
        camposEstudiante[2] = new JTextField();
        panelFormulario.add(camposEstudiante[2]);
        panelFormulario.add(new JLabel("Teléfono:"));
        camposEstudiante[3] = new JTextField();
        panelFormulario.add(camposEstudiante[3]);
        panelFormulario.add(new JLabel("Año Ingreso:"));
        camposEstudiante[4] = new JTextField();
        panelFormulario.add(camposEstudiante[4]);
        
        JPanel panelBotones = new JPanel(new GridLayout(5, 1, 10, 10));
        panelBotones.setBorder(BorderFactory.createTitledBorder("⚡ Acciones Rápidas"));
        panelBotones.setBackground(new Color(255, 255, 255, 200));
        
        JButton btnGuardar = crearBotonAccion("💾 Guardar Estudiante", new Color(147, 112, 219));
        JButton btnBuscar = crearBotonAccion("🔍 Buscar Estudiante", new Color(64, 224, 208));
        JButton btnVerPrestamos = crearBotonAccion("📋 Ver Préstamos", new Color(186, 85, 211));
        JButton btnLimpiar = crearBotonAccion("🗑 Limpiar Campos", new Color(32, 178, 170));
        JButton btnGuardarEstudiantes = crearBotonAccion("💾 Guardar Estudiantes", new Color(50, 205, 50));
        
        // Implementación de funcionalidades de botones
        btnGuardar.addActionListener(e -> guardarEstudiante());
        btnBuscar.addActionListener(e -> buscarEstudiante());
        btnVerPrestamos.addActionListener(e -> verPrestamosEstudiante());
        btnLimpiar.addActionListener(e -> limpiarCamposEstudiante());
        btnGuardarEstudiantes.addActionListener(e -> {
            guardarEnArchivo(estudiantes, ESTUDIANTES_FILE);
            JOptionPane.showMessageDialog(frame, "Estudiantes guardados exitosamente!");
        });
        
        panelBotones.add(btnGuardar);
        panelBotones.add(btnBuscar);
        panelBotones.add(btnVerPrestamos);
        panelBotones.add(btnLimpiar);
        panelBotones.add(btnGuardarEstudiantes);
        
        panelDerecho.add(panelFormulario);
        panelDerecho.add(Box.createRigidArea(new Dimension(0, 15)));
        panelDerecho.add(panelBotones);
        
        splitPane.setLeftComponent(panelEstudiantes);
        splitPane.setRightComponent(panelDerecho);
        
        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(splitPane, BorderLayout.CENTER);
        
        mainPanel.add(panel, "Estudiantes");
    }

    // ==================== MÉTODOS DE FUNCIONALIDAD ====================
    
    // MÉTODOS DE FUNCIONALIDAD PARA LIBROS
    private void mostrarLibrosPorAutor() {
        String autor = JOptionPane.showInputDialog(frame, "Ingrese el nombre del autor:");
        if (autor != null && !autor.trim().isEmpty()) {
            StringBuilder librosDelAutor = new StringBuilder("📚 Libros de " + autor + ":\n\n");
            int contador = 0;
            
            for (Libro libro : libros) {
                if (libro.getAutor().equalsIgnoreCase(autor)) {
                    librosDelAutor.append("• ").append(libro.getTitulo())
                                 .append(" (").append(libro.getAnioPublicacion()).append(")\n");
                    contador++;
                }
            }
            
            if (contador > 0) {
                librosDelAutor.append("\nTotal: ").append(contador).append(" libros");
                JOptionPane.showMessageDialog(frame, librosDelAutor.toString(), 
                    "Libros por Autor", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(frame, 
                    "No se encontraron libros para el autor: " + autor,
                    "Búsqueda Sin Resultados", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    private void agregarLibro() {
        JTextField txtTitulo = new JTextField();
        JTextField txtAutor = new JTextField();
        JTextField txtAnio = new JTextField();
        JTextField txtGenero = new JTextField();

        Object[] message = {
            "Título:", txtTitulo,
            "Autor:", txtAutor,
            "Año de publicación:", txtAnio,
            "Género:", txtGenero
        };

        int option = JOptionPane.showConfirmDialog(frame, message, 
            "Agregar Nuevo Libro", JOptionPane.OK_CANCEL_OPTION);
        
        if (option == JOptionPane.OK_OPTION) {
            if (!txtTitulo.getText().trim().isEmpty() && !txtAutor.getText().trim().isEmpty()) {
                Libro nuevoLibro = new Libro(
                    txtTitulo.getText().trim(),
                    txtAutor.getText().trim(),
                    txtAnio.getText().trim(),
                    txtGenero.getText().trim()
                );
                libros.add(nuevoLibro);
                actualizarListaLibros();
                JOptionPane.showMessageDialog(frame, "Libro agregado exitosamente!");
            } else {
                JOptionPane.showMessageDialog(frame, 
                    "Título y Autor son campos obligatorios", 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void editarLibro() {
        int selectedIndex = listaLibros.getSelectedIndex();
        if (selectedIndex >= 0 && selectedIndex < libros.size()) {
            Libro libro = libros.get(selectedIndex);
            
            JTextField txtTitulo = new JTextField(libro.getTitulo());
            JTextField txtAutor = new JTextField(libro.getAutor());
            JTextField txtAnio = new JTextField(libro.getAnioPublicacion());
            JTextField txtGenero = new JTextField(libro.getGenero());

            Object[] message = {
                "Título:", txtTitulo,
                "Autor:", txtAutor,
                "Año de publicación:", txtAnio,
                "Género:", txtGenero
            };

            int option = JOptionPane.showConfirmDialog(frame, message, 
                "Editar Libro", JOptionPane.OK_CANCEL_OPTION);
            
            if (option == JOptionPane.OK_OPTION) {
                if (!txtTitulo.getText().trim().isEmpty() && !txtAutor.getText().trim().isEmpty()) {
                    libro.setTitulo(txtTitulo.getText().trim());
                    libro.setAutor(txtAutor.getText().trim());
                    libro.setAnioPublicacion(txtAnio.getText().trim());
                    libro.setGenero(txtGenero.getText().trim());
                    actualizarListaLibros();
                    JOptionPane.showMessageDialog(frame, "Libro editado exitosamente!");
                } else {
                    JOptionPane.showMessageDialog(frame, 
                        "Título y Autor son campos obligatorios", 
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(frame, 
                "Por favor, selecciona un libro de la lista.",
                "Selección Requerida", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void eliminarLibro() {
        int selectedIndex = listaLibros.getSelectedIndex();
        if (selectedIndex >= 0 && selectedIndex < libros.size()) {
            Libro libro = libros.get(selectedIndex);
            int confirm = JOptionPane.showConfirmDialog(frame,
                "¿Estás seguro de que quieres eliminar este libro?\n" + 
                libro.getTitulo(),
                "Confirmar Eliminación", JOptionPane.YES_NO_OPTION);
            
            if (confirm == JOptionPane.YES_OPTION) {
                libros.remove(selectedIndex);
                actualizarListaLibros();
                JOptionPane.showMessageDialog(frame, "Libro eliminado exitosamente!");
            }
        } else {
            JOptionPane.showMessageDialog(frame, 
                "Por favor, selecciona un libro de la lista.",
                "Selección Requerida", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void buscarLibro() {
        String criterio = JOptionPane.showInputDialog(frame, 
            "Buscar libro por título o autor:");
        
        if (criterio != null && !criterio.trim().isEmpty()) {
            StringBuilder resultados = new StringBuilder("🔍 Resultados de búsqueda:\n\n");
            int contador = 0;
            
            for (Libro libro : libros) {
                if (libro.getTitulo().toLowerCase().contains(criterio.toLowerCase()) ||
                    libro.getAutor().toLowerCase().contains(criterio.toLowerCase())) {
                    resultados.append("• ").append(libro.getTitulo())
                             .append(" - ").append(libro.getAutor())
                             .append(" (").append(libro.getAnioPublicacion()).append(")\n");
                    contador++;
                }
            }
            
            if (contador > 0) {
                resultados.append("\nTotal encontrados: ").append(contador);
                JOptionPane.showMessageDialog(frame, resultados.toString(), 
                    "Resultados de Búsqueda", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(frame, 
                    "No se encontraron libros que coincidan con: " + criterio,
                    "Búsqueda Sin Resultados", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    private void mostrarDetallesLibro() {
        int selectedIndex = listaLibros.getSelectedIndex();
        if (selectedIndex >= 0 && selectedIndex < libros.size()) {
            Libro libro = libros.get(selectedIndex);
            String detalles = "📖 Detalles del Libro:\n\n" +
                "• Título: " + libro.getTitulo() + "\n" +
                "• Autor: " + libro.getAutor() + "\n" +
                "• Año de publicación: " + libro.getAnioPublicacion() + "\n" +
                "• Género: " + libro.getGenero() + "\n" +
                "• Disponible: Sí\n" +
                "• Estado: Excelente";
            
            JOptionPane.showMessageDialog(frame, detalles, 
                "Detalles del Libro", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(frame, 
                "Por favor, selecciona un libro de la lista.",
                "Selección Requerida", JOptionPane.WARNING_MESSAGE);
        }
    }

    // MÉTODOS DE FUNCIONALIDAD PARA AUTORES
    private void verLibrosDelAutor() {
        int filaSeleccionada = tablaAutores.getSelectedRow();
        if (filaSeleccionada >= 0 && filaSeleccionada < autores.size()) {
            String autor = (String) tableModelAutores.getValueAt(filaSeleccionada, 0);
            StringBuilder librosDelAutor = new StringBuilder("📚 Libros de " + autor + ":\n\n");
            int contador = 0;
            
            for (Libro libro : libros) {
                if (libro.getAutor().equalsIgnoreCase(autor)) {
                    librosDelAutor.append("• ").append(libro.getTitulo())
                                 .append(" (").append(libro.getAnioPublicacion()).append(")\n");
                    contador++;
                }
            }
            
            if (contador > 0) {
                librosDelAutor.append("\nTotal: ").append(contador).append(" libros");
                JOptionPane.showMessageDialog(frame, librosDelAutor.toString(), 
                    "Libros del Autor", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(frame, 
                    "No se encontraron libros para este autor.",
                    "Sin Libros", JOptionPane.INFORMATION_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(frame, 
                "Por favor, selecciona un autor de la lista.",
                "Selección Requerida", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void agregarAutor() {
        JTextField txtNombre = new JTextField();
        JTextField txtNacionalidad = new JTextField();
        JTextField txtCantidadLibros = new JTextField("0");
        JTextField txtAnioNacimiento = new JTextField();

        Object[] message = {
            "Nombre:", txtNombre,
            "Nacionalidad:", txtNacionalidad,
            "Cantidad de Libros:", txtCantidadLibros,
            "Año de Nacimiento:", txtAnioNacimiento
        };

        int option = JOptionPane.showConfirmDialog(frame, message, 
            "Agregar Nuevo Autor", JOptionPane.OK_CANCEL_OPTION);
        
        if (option == JOptionPane.OK_OPTION) {
            if (!txtNombre.getText().trim().isEmpty()) {
                try {
                    int cantidadLibros = Integer.parseInt(txtCantidadLibros.getText().trim());
                    Autor nuevoAutor = new Autor(
                        txtNombre.getText().trim(),
                        txtNacionalidad.getText().trim(),
                        cantidadLibros,
                        txtAnioNacimiento.getText().trim()
                    );
                    autores.add(nuevoAutor);
                    actualizarTablaAutores();
                    JOptionPane.showMessageDialog(frame, "Autor agregado exitosamente!");
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(frame, 
                        "La cantidad de libros debe ser un número válido", 
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(frame, 
                    "El nombre es un campo obligatorio", 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void editarAutor() {
        int filaSeleccionada = tablaAutores.getSelectedRow();
        if (filaSeleccionada >= 0 && filaSeleccionada < autores.size()) {
            Autor autor = autores.get(filaSeleccionada);
            
            JTextField txtNombre = new JTextField(autor.getNombre());
            JTextField txtNacionalidad = new JTextField(autor.getNacionalidad());
            JTextField txtCantidadLibros = new JTextField(String.valueOf(autor.getCantidadLibros()));
            JTextField txtAnioNacimiento = new JTextField(autor.getAnioNacimiento());

            Object[] message = {
                "Nombre:", txtNombre,
                "Nacionalidad:", txtNacionalidad,
                "Cantidad de Libros:", txtCantidadLibros,
                "Año de Nacimiento:", txtAnioNacimiento
            };

            int option = JOptionPane.showConfirmDialog(frame, message, 
                "Editar Autor", JOptionPane.OK_CANCEL_OPTION);
            
            if (option == JOptionPane.OK_OPTION) {
                if (!txtNombre.getText().trim().isEmpty()) {
                    try {
                        int cantidadLibros = Integer.parseInt(txtCantidadLibros.getText().trim());
                        autor.setNombre(txtNombre.getText().trim());
                        autor.setNacionalidad(txtNacionalidad.getText().trim());
                        autor.setCantidadLibros(cantidadLibros);
                        autor.setAnioNacimiento(txtAnioNacimiento.getText().trim());
                        actualizarTablaAutores();
                        JOptionPane.showMessageDialog(frame, "Autor editado exitosamente!");
                    } catch (NumberFormatException e) {
                        JOptionPane.showMessageDialog(frame, 
                            "La cantidad de libros debe ser un número válido", 
                            "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(frame, 
                        "El nombre es un campo obligatorio", 
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(frame, 
                "Por favor, selecciona un autor de la lista.",
                "Selección Requerida", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void eliminarAutor() {
        int filaSeleccionada = tablaAutores.getSelectedRow();
        if (filaSeleccionada >= 0 && filaSeleccionada < autores.size()) {
            Autor autor = autores.get(filaSeleccionada);
            int confirm = JOptionPane.showConfirmDialog(frame,
                "¿Estás seguro de que quieres eliminar este autor?\n" + 
                autor.getNombre(),
                "Confirmar Eliminación", JOptionPane.YES_NO_OPTION);
            
            if (confirm == JOptionPane.YES_OPTION) {
                autores.remove(filaSeleccionada);
                actualizarTablaAutores();
                JOptionPane.showMessageDialog(frame, "Autor eliminado exitosamente!");
            }
        } else {
            JOptionPane.showMessageDialog(frame, 
                "Por favor, selecciona un autor de la lista.",
                "Selección Requerida", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void mostrarDetallesAutor() {
        int filaSeleccionada = tablaAutores.getSelectedRow();
        if (filaSeleccionada >= 0 && filaSeleccionada < autores.size()) {
            Autor autor = autores.get(filaSeleccionada);
            String detalles = "👤 Detalles del Autor:\n\n" +
                "• Nombre: " + autor.getNombre() + "\n" +
                "• Nacionalidad: " + autor.getNacionalidad() + "\n" +
                "• Cantidad de Libros: " + autor.getCantidadLibros() + "\n" +
                "• Año de Nacimiento: " + autor.getAnioNacimiento() + "\n" +
                "• Estado: Activo";
            
            JOptionPane.showMessageDialog(frame, detalles, 
                "Detalles del Autor", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(frame, 
                "Por favor, selecciona un autor de la lista.",
                "Selección Requerida", JOptionPane.WARNING_MESSAGE);
        }
    }

    // MÉTODOS DE FUNCIONALIDAD PARA ESTUDIANTES
    private void guardarEstudiante() {
        String matricula = camposEstudiante[0].getText().trim();
        String nombre = camposEstudiante[1].getText().trim();
        String carrera = camposEstudiante[2].getText().trim();
        String telefono = camposEstudiante[3].getText().trim();
        String anioIngreso = camposEstudiante[4].getText().trim();
        
        if (matricula.isEmpty() || nombre.isEmpty() || carrera.isEmpty() || anioIngreso.isEmpty()) {
            JOptionPane.showMessageDialog(frame, 
                "Matrícula, Nombre, Carrera y Año Ingreso son campos obligatorios",
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Verificar si la matrícula ya existe
        for (Estudiante est : estudiantes) {
            if (est.getMatricula().equals(matricula)) {
                JOptionPane.showMessageDialog(frame, 
                    "La matrícula ya existe. Use una matrícula diferente.",
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
        
        Estudiante nuevoEstudiante = new Estudiante(matricula, nombre, carrera, anioIngreso, telefono);
        estudiantes.add(nuevoEstudiante);
        actualizarTablaEstudiantes();
        limpiarCamposEstudiante();
        JOptionPane.showMessageDialog(frame, "Estudiante guardado exitosamente!");
    }

    private void buscarEstudiante() {
        String criterio = JOptionPane.showInputDialog(frame, 
            "Buscar estudiante por matrícula o nombre:");
        
        if (criterio != null && !criterio.trim().isEmpty()) {
            StringBuilder resultados = new StringBuilder("🔍 Resultados de búsqueda:\n\n");
            int contador = 0;
            
            for (Estudiante estudiante : estudiantes) {
                if (estudiante.getMatricula().toLowerCase().contains(criterio.toLowerCase()) ||
                    estudiante.getNombre().toLowerCase().contains(criterio.toLowerCase())) {
                    resultados.append("• ").append(estudiante.getMatricula())
                             .append(" - ").append(estudiante.getNombre())
                             .append(" (").append(estudiante.getCarrera()).append(")\n");
                    contador++;
                }
            }
            
            if (contador > 0) {
                resultados.append("\nTotal encontrados: ").append(contador);
                JOptionPane.showMessageDialog(frame, resultados.toString(), 
                    "Resultados de Búsqueda", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(frame, 
                    "No se encontraron estudiantes que coincidan con: " + criterio,
                    "Búsqueda Sin Resultados", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    private void verPrestamosEstudiante() {
        int filaSeleccionada = tablaEstudiantes.getSelectedRow();
        if (filaSeleccionada >= 0 && filaSeleccionada < estudiantes.size()) {
            Estudiante estudiante = estudiantes.get(filaSeleccionada);
            
            String prestamos = "📋 Préstamos de " + estudiante.getNombre() + 
                             " (" + estudiante.getMatricula() + "):\n\n" +
                "• Cien años de soledad (Vence: 15/12/2024)\n" +
                "• 1984 (Vence: 20/12/2024)\n\n" +
                "Total préstamos activos: 2\n" +
                "Estado: Al día";
            
            JOptionPane.showMessageDialog(frame, prestamos, 
                "Préstamos del Estudiante", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(frame, 
                "Por favor, selecciona un estudiante de la lista.",
                "Selección Requerida", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void limpiarCamposEstudiante() {
        for (JTextField campo : camposEstudiante) {
            campo.setText("");
        }
    }

    private JPanel crearHeaderPanel(String titulo) {
        JPanel headerPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                GradientPaint gradient = new GradientPaint(0, 0, new Color(75, 0, 130), 
                                                          getWidth(), 0, new Color(0, 128, 128));
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        headerPanel.setOpaque(false);
        
        JButton btnRegresar = crearBotonRegresar();
        btnRegresar.addActionListener(e -> cardLayout.show(mainPanel, "Menu"));
        
        JLabel lblTitulo = new JLabel(titulo, JLabel.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 22));
        lblTitulo.setForeground(Color.WHITE);
        
        headerPanel.add(btnRegresar, BorderLayout.WEST);
        headerPanel.add(lblTitulo, BorderLayout.CENTER);
        
        return headerPanel;
    }

    private JButton crearBotonAccion(String texto, Color colorBase) {
        JButton boton = new JButton(texto) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                GradientPaint gradient = new GradientPaint(0, 0, colorBase, 
                                                          getWidth(), getHeight(), 
                                                          new Color(colorBase.getRed(), colorBase.getGreen(), colorBase.getBlue(), 200));
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                super.paintComponent(g);
            }
        };
        
        boton.setFont(new Font("Arial", Font.BOLD, 14));
        boton.setForeground(Color.WHITE);
        boton.setFocusPainted(false);
        boton.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        boton.setContentAreaFilled(false);
        boton.setMaximumSize(new Dimension(200, 45));
        boton.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        boton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                boton.setForeground(new Color(255, 255, 224));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                boton.setForeground(Color.WHITE);
            }
        });
        
        return boton;
    }

    private JButton crearBotonRegresar() {
        JButton btnRegresar = new JButton("← Menú Principal") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                GradientPaint gradient = new GradientPaint(0, 0, new Color(255, 255, 255, 200), 
                                                          getWidth(), getHeight(), 
                                                          new Color(200, 200, 200, 200));
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                super.paintComponent(g);
            }
        };
        
        btnRegresar.setFont(new Font("Arial", Font.BOLD, 14));
        btnRegresar.setForeground(new Color(75, 0, 130));
        btnRegresar.setFocusPainted(false);
        btnRegresar.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        btnRegresar.setContentAreaFilled(false);
        
        return btnRegresar;
    }

    // ==================== CLASES SERIALIZABLES ====================
    
    static class Libro implements Serializable {
        private static final long serialVersionUID = 1L;
        private String titulo;
        private String autor;
        private String anioPublicacion;
        private String genero;
        
        public Libro(String titulo, String autor, String anioPublicacion, String genero) {
            this.titulo = titulo;
            this.autor = autor;
            this.anioPublicacion = anioPublicacion;
            this.genero = genero;
        }
        
        // Getters y Setters
        public String getTitulo() { return titulo; }
        public void setTitulo(String titulo) { this.titulo = titulo; }
        public String getAutor() { return autor; }
        public void setAutor(String autor) { this.autor = autor; }
        public String getAnioPublicacion() { return anioPublicacion; }
        public void setAnioPublicacion(String anioPublicacion) { this.anioPublicacion = anioPublicacion; }
        public String getGenero() { return genero; }
        public void setGenero(String genero) { this.genero = genero; }
    }

    static class Autor implements Serializable {
        private static final long serialVersionUID = 2L;
        private String nombre;
        private String nacionalidad;
        private int cantidadLibros;
        private String anioNacimiento;
        
        public Autor(String nombre, String nacionalidad, int cantidadLibros, String anioNacimiento) {
            this.nombre = nombre;
            this.nacionalidad = nacionalidad;
            this.cantidadLibros = cantidadLibros;
            this.anioNacimiento = anioNacimiento;
        }
        
        // Getters y Setters
        public String getNombre() { return nombre; }
        public void setNombre(String nombre) { this.nombre = nombre; }
        public String getNacionalidad() { return nacionalidad; }
        public void setNacionalidad(String nacionalidad) { this.nacionalidad = nacionalidad; }
        public int getCantidadLibros() { return cantidadLibros; }
        public void setCantidadLibros(int cantidadLibros) { this.cantidadLibros = cantidadLibros; }
        public String getAnioNacimiento() { return anioNacimiento; }
        public void setAnioNacimiento(String anioNacimiento) { this.anioNacimiento = anioNacimiento; }
    }

    static class Estudiante implements Serializable {
        private static final long serialVersionUID = 3L;
        private String matricula;
        private String nombre;
        private String carrera;
        private String anioIngreso;
        private String telefono;
        
        public Estudiante(String matricula, String nombre, String carrera, String anioIngreso, String telefono) {
            this.matricula = matricula;
            this.nombre = nombre;
            this.carrera = carrera;
            this.anioIngreso = anioIngreso;
            this.telefono = telefono;
        }
        
        // Getters y Setters
        public String getMatricula() { return matricula; }
        public void setMatricula(String matricula) { this.matricula = matricula; }
        public String getNombre() { return nombre; }
        public void setNombre(String nombre) { this.nombre = nombre; }
        public String getCarrera() { return carrera; }
        public void setCarrera(String carrera) { this.carrera = carrera; }
        public String getAnioIngreso() { return anioIngreso; }
        public void setAnioIngreso(String anioIngreso) { this.anioIngreso = anioIngreso; }
        public String getTelefono() { return telefono; }
        public void setTelefono(String telefono) { this.telefono = telefono; }
    }
}