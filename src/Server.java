import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
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
    private static JSONController jsonController = new JSONController();


    /**
     * Función para cargar el XML de los clientes y crear el arbol binario de busqueda
     * @param nombreArchivo
     */
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

    /**
     * Función para cargar el XML de los Usuarios administradores y crear el arbol binario de busqueda
     * @param nombreArchivo
     */
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

    /**
     * Función para cargar el archivo JSon de los platillos y crear el arbolAVL
     * @param nombreArchivo
     */
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

    /**
     * Función para utilizar los sockets para enviar y recibir mensajes
     * @param args
     * @throws IOException
     * @throws ClassNotFoundException
     */
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
            }
            if (mensaje.equals("agregarAdmin")) {
                nuevoAdmi();
            } if (mensaje.equals("agregarPlatillo")){
                nuevoPlatillo();
            }
            if (mensaje.equals("validarUsuario")) {
                findUser();
            }
            if (mensaje.equals("editUser")) {
                editUsuer();
            }
            if (mensaje.equals("deleteUser")) {
                deletetUser();
            }
            /*
            if(mensaje.equals("buscarPlatillo")){
                buscarPlatillo();
            }

             */
        }
    }
    /*
    public void buscarPlatillo() throws IOException, ClassNotFoundException {
        String username = (String) in.readObject();
        Usuario user = arbolPlatillos.buscar(arbolPlatillos.)
        if (user != null){
            out.writeObject(true);
            out.writeObject(user.getUsername());
            out.writeObject(user.getPassword());
            out.writeObject(user.getRol());
            System.out.println(user.getUsername());
        }else{
            out.writeObject(false);
            System.out.println("No existe >:(");
        }
    }

     */

    /**
     * Recibe la informacion de MasterApp y la procesa creando un nuevo objeto de tipo Platillo,
     * para luego agregarlo al json
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private static void nuevoPlatillo() throws IOException, ClassNotFoundException {
        String newNombre = (String) in.readObject();
        Integer newCalorias = (Integer) in.readObject();
        Integer newTiempo = (Integer) in.readObject();
        Integer newPrecio = (Integer) in.readObject();

        Platillos platillos = new Platillos();
        platillos.setNombre(newNombre);
        platillos.setCalorias(newCalorias);
        platillos.setTiempo(newTiempo);
        platillos.setPrecio(newPrecio);

        jsonController.saveJson(platillos.getNombre(), platillos.getCalorias(), platillos.getTiempo(), platillos.getPrecio());

    }


    /**
     * Función con Socket para eliminar un Usuario del Árbol Binario de Búsqueda
     * @throws IOException
     * @throws ClassNotFoundException
     */

    private static void deletetUser() throws IOException, ClassNotFoundException {
        boolean ready = arbolUsuarios.eliminar("Alejandro");
        if(ready){
            System.out.println("Done: The user was deleted");
            out.writeObject(ready);
        }else{
            System.out.println("No se eliminó :(");
        }


    }



    /**
     * Función con Socket para buscar un Usuario del Árbol Binario de Búsqueda
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private static void findUser() throws IOException, ClassNotFoundException {
        String username = (String) in.readObject();
        Usuario user = arbolUsuarios.buscar(arbolUsuarios.getRaiz(), username);
        if (user != null){
            out.writeObject(true);
            out.writeObject(user.getUsername());
            out.writeObject(user.getPassword());
            out.writeObject(user.getRol());
            System.out.println(user.getUsername());
        }else{
            out.writeObject(false);
            System.out.println("No existe >:(");
        }
    }

    /**
     * Función con Socket para editar un Usuario del Árbol Binario de Búsqueda
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private static void editUsuer() throws IOException, ClassNotFoundException {
        Usuario usertemp = arbolUsuarios.buscar(arbolUsuarios.getRaiz(), "Fernanda");
        usertemp.setUsername((String) in.readObject());
        usertemp.setPassword((String) in.readObject());
        usertemp.setRol((String) in.readObject());
        out.writeObject(true);
        System.out.println("done");
    }

    /**
     * Función para crear un nuevo admin con la informacion recibida de MasterApp para agregarlo al archivo XML
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private static void nuevoAdmi() throws IOException, ClassNotFoundException {
        String newUsuario = (String) in.readObject();
        String newPassword = (String) in.readObject();

        Usuario usuario = new Usuario();
        usuario.setUsername(newUsuario);
        usuario.setPassword(newPassword);
        usuario.setRol("Administrador");


        try {
            agregarUsuarioAXML("usuarios.xml", usuario);
            out.writeObject("Administrador agregado con éxito");
            System.out.println(newUsuario);
            System.out.println(newPassword);
        } catch (Exception e) {
            out.writeObject("Error al agregar el administrador");
        }
    }

    /**
     * Función para agregar usuarios al XML
     * @param nombreArchivo
     * @param usuario
     * @throws Exception
     */
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
    /*
    private static void deletetUser() throws IOException, ClassNotFoundException {
        String usuario = "Alejandro";

        try {
            removeUserByUsername("usuarios.xml", usuario);
            out.writeObject("Administrador eliminado con éxito");
        } catch (Exception e) {
            out.writeObject("Error al eliminar el administrador");
        }
    }

    public static void removeUserByUsername(String filePath, String username) {
        try {
            // Crear una instancia del DocumentBuilderFactory
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

            // Crear un objeto DocumentBuilder a partir del DocumentBuilderFactory
            DocumentBuilder builder = factory.newDocumentBuilder();

            // Parsear el archivo XML y obtener el objeto Document
            Document doc = builder.parse(new File(filePath));

            // Obtener todos los elementos "user" en el archivo XML
            NodeList userList = doc.getElementsByTagName("usuario");

            // Recorrer la lista de usuarios y buscar el usuario con el "username" especificado
            for (int i = 0; i < userList.getLength(); i++) {
                Element userElement = (Element) userList.item(i);
                String userUsername = userElement.getAttribute("usuario");
                if (username.equals(userUsername)) {
                    // Eliminar el nodo "user" encontrado
                    userElement.getParentNode().removeChild(userElement);
                    System.out.println("Usuario eliminado exitosamente del archivo XML.");
                    break;
                }
            }

            // Guardar los cambios en el archivo XML
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(filePath));
            transformer.transform(source, result);

        } catch (ParserConfigurationException | IOException | SAXException | TransformerException e) {
            e.printStackTrace();
        }
    }

     */


    /**
     * Función para verificar que la informacion ingresada es existente en alguno de los arboles binarios de busqueda
     * @throws IOException
     * @throws ClassNotFoundException
     */
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
