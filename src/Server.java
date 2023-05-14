import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private static ArbolBinarioBusqueda arbolClientes = new ArbolBinarioBusqueda();
    private static ArbolBinarioBusqueda arbolUsuarios = new ArbolBinarioBusqueda();
    private static ArbolAVL arbolPlatillos = new ArbolAVL();


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
                platillo.setNombre((String) jsonObject.get("nombre"));
                platillo.setCalorias((Integer) jsonObject.get("calorias"));
                platillo.setTiempo((Integer) jsonObject.get("tiempo"));
                platillo.setPrecio((Integer) jsonObject.get("precio"));
                arbolPlatillos.insert(platillo);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) throws IOException {
        cargarClientes("clientes.xml");
        cargarUsuarios("usuarios.xml");
        cargarPlatillos("platillos.json");

        ServerSocket serverSocket = new ServerSocket(01234);
        Socket clientSocket = serverSocket.accept();
        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

        String inputLine;
        while ((inputLine = in.readLine()) != null) {
            System.out.println(inputLine);

            /*
            out.println("Echo from server: " + inputLine);

             */
        }
    }
    private static void validarLogin(){
        String usernameIn;
        String passwordIn;
    }
}
