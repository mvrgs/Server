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
        while(true){
            clientSocket = serverSocket.accept();
            out = new ObjectOutputStream(clientSocket.getOutputStream());
            in = new ObjectInputStream(clientSocket.getInputStream());

            String mensaje = (String) in.readObject();

            if (mensaje.equals("validarLogin")){
                inicioSesion();

            }

        }

       /*
        while ((inputLine = in.readLine()) != null) {
            System.out.println(inputLine);


            out.println("Echo from server: " + inputLine);

             */
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
