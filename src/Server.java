import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {
    private static ArbolBinarioBusqueda arbolClientes = new ArbolBinarioBusqueda();
    private static ArbolBinarioBusqueda arbolUsuarios = new ArbolBinarioBusqueda();
    private static ArbolAVL arbolPlatillos = new ArbolAVL();
    private static ArrayList<String> platillosLista;
    static Socket clientSocket;
    static ObjectOutputStream out;
    static ObjectInputStream in;




    private static void cargarClientes(String nombreArchivo) {
        try {
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            Document doc = docBuilder.parse(new File(nombreArchivo));
            doc.getDocumentElement().normalize();
            NodeList listaUsuarios = doc.getElementsByTagName("usuario");

            for (int i = 0; i < listaUsuarios.getLength(); i++) {
                Node nodoUsuario = listaUsuarios.item(i);

                if (nodoUsuario.getNodeType() == Node.ELEMENT_NODE) {
                    Element elementoUsuario = (Element) nodoUsuario;
                    String username = elementoUsuario.getElementsByTagName("username").item(0).getTextContent();
                    String password = elementoUsuario.getElementsByTagName("password").item(0).getTextContent();
                    String rol = elementoUsuario.getElementsByTagName("rol").item(0).getTextContent();
                    Usuario usuario = new Usuario(username, password, rol);
                    arbolClientes.insertar(usuario);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private static void cargarUsuarios(String nombreArchivo) {
        try {
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            Document doc = docBuilder.parse(new File(nombreArchivo));
            doc.getDocumentElement().normalize();
            NodeList listaUsuarios = doc.getElementsByTagName("usuario");

            for (int i = 0; i < listaUsuarios.getLength(); i++) {
                Node nodoUsuario = listaUsuarios.item(i);

                if (nodoUsuario.getNodeType() == Node.ELEMENT_NODE) {
                    Element elementoUsuario = (Element) nodoUsuario;
                    String username = elementoUsuario.getElementsByTagName("username").item(0).getTextContent();
                    String password = elementoUsuario.getElementsByTagName("password").item(0).getTextContent();
                    String rol = elementoUsuario.getElementsByTagName("rol").item(0).getTextContent();
                    Usuario usuario = new Usuario(username, password, rol);
                    arbolUsuarios.insertar(usuario);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private static void cargarPlatillos(String nombreArchivo) {
        try {
            JSONParser parser = new JSONParser();
            FileReader reader = new FileReader(nombreArchivo);
            JSONArray jsonArray = (JSONArray) parser.parse(reader);

            for (Object obj : jsonArray) {
                JSONObject jsonObject = (JSONObject) obj;
                Platillos platillo = new Platillos();
                platillo.setNombre((String) jsonObject.get("Nombre"));
                Long caloLong = (Long) jsonObject.get("Calorias");
                Integer calorias = caloLong.intValue();
                platillo.setCalorias(calorias);
                Long timeLong = (Long) jsonObject.get("Tiempo");
                Integer tiempo = timeLong.intValue();
                platillo.setTiempo(tiempo);
                Long precioLong = (Long) jsonObject.get("Precio");
                Integer precio = precioLong.intValue();
                platillo.setPrecio(precio);
                arbolPlatillos.insert(platillo);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private static void cargarListaPlatillos(String nombreArchivo)  {
        try{
            JSONParser parser = new JSONParser();
            FileReader reader = new FileReader(nombreArchivo);
            JSONArray jsonArray = (JSONArray) parser.parse(reader);
            platillosLista= new ArrayList<>();
            for (Object obj : jsonArray){
                JSONObject jsonObject = (JSONObject) obj;
                platillosLista.add((String) jsonObject.get("Nombre"));
                System.out.println(platillosLista);
                ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
                out.writeObject(platillosLista);


            }
        }catch (Exception e) {
            e.printStackTrace();
        }

    }
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        cargarClientes("clientes.xml");
        cargarUsuarios("usuarios.xml");
        cargarPlatillos("platillos.json");
        ServerSocket serverSocket = new ServerSocket(01234);
        while (true) {
            clientSocket = serverSocket.accept();
            out = new ObjectOutputStream(clientSocket.getOutputStream());
            in = new ObjectInputStream(clientSocket.getInputStream());

            String mensaje = (String) in.readObject();
            System.out.println(mensaje);

            if (mensaje.equals("validarLogin")) {
                inicioSesion();
            } if (mensaje.equals("agregarAdmin")) {
                nuevoAdmi();
            }



        }
    }

    private static void nuevoAdmi() throws IOException, ClassNotFoundException {
        String newUsuario = (String) in.readObject();
        String newPassword = (String) in.readObject();

        Usuario usuario = new Usuario();
        usuario.setUsername(newUsuario);
        usuario.setPassword(newPassword);
        usuario.setRol("Administrador");
        System.out.println(newUsuario);
        System.out.println(newPassword);

        try {
            agregarUsuarioAXML("usuarios.xml", usuario);
            out.writeObject("Administrador agregado con éxito");
        } catch (Exception e) {
            out.writeObject("Error al agregar el administrador");
        }
    }

    public static void agregarUsuarioAXML(String nombreArchivo, Usuario usuario) throws Exception {
        // Crear una fábrica de constructores de documentos
        DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();

        // Leer el archivo XML existente
        Document doc = docBuilder.parse(new File(nombreArchivo));

        // Obtener la raíz del documento
        Element raiz = doc.getDocumentElement();

        // Crear un nuevo elemento para el usuario
        Element nuevoUsuario = doc.createElement("usuario");
        raiz.appendChild(nuevoUsuario);

        Element username = doc.createElement("username");
        username.appendChild(doc.createTextNode(usuario.getUsername()));
        nuevoUsuario.appendChild(username);

        Element password = doc.createElement("password");
        password.appendChild(doc.createTextNode(usuario.getPassword()));
        nuevoUsuario.appendChild(password);

        Element rol = doc.createElement("rol");
        rol.appendChild(doc.createTextNode(usuario.getRol()));
        nuevoUsuario.appendChild(rol);

        // Guardar los cambios en el archivo XML
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(new FileOutputStream(nombreArchivo));
        transformer.transform(source, result);
    }

    private static void inicioSesion() throws IOException, ClassNotFoundException {
        String usernameIn= (String) in.readObject();
        String passwordIn = (String) in.readObject();

        System.out.println("Usuario:"+usernameIn);
        System.out.println("Password:"+passwordIn);

        // Buscar el usuario en ambos árboles
        Usuario usuario = arbolUsuarios.buscar(usernameIn, passwordIn);
        if (usuario == null) {
            usuario = arbolClientes.buscar(usernameIn, passwordIn);
        }

        // Validar en qué árbol se encuentra el usuario y abrir la ventana correspondiente
        if (usuario != null) {
            if (arbolUsuarios.buscar(usernameIn,passwordIn) != null) {
                out.writeObject("Administrador");

            } else if (arbolClientes.buscar(usernameIn,passwordIn) != null) {
                out.writeObject("Cliente");
            }
        } else {
            out.writeObject("Incorrectos");
        }


    }
}
