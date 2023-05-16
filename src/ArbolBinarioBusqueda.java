public class ArbolBinarioBusqueda {
    private NodoArbol raiz;

    /**
     * Constructores de la clase ArbolBinarioBusqueda
     */
    public ArbolBinarioBusqueda() {
        raiz = null;
    }
    public boolean isEmpty(){
        return this.raiz == null;
    }

    /**
     * Función principal para insertar un nodo
     * @param usuario
     */
    public void insertar(Usuario usuario) {
        if (raiz == null) {
            raiz = new NodoArbol(usuario);
        } else {
            insertar(raiz, usuario);
        }
    }

    /**
     * Función para auxiliar insertar un nodo
     * @param nodo
     * @param usuario
     */
    private void insertar(NodoArbol nodo, Usuario usuario) {
        if (nodo.usuario.getUsername().compareTo(usuario.getUsername()) > 0) {
            if (nodo.izquierdo == null) {
                nodo.izquierdo = new NodoArbol(usuario);
            } else {
                insertar(nodo.izquierdo, usuario);
            }
        } else if (nodo.usuario.getUsername().compareTo(usuario.getUsername()) < 0) {
            if (nodo.derecho == null) {
                nodo.derecho = new NodoArbol(usuario);
            } else {
                insertar(nodo.derecho, usuario);
            }
        }
    }


    /**
     * Función para encontrar un usuario con base al usernad y a su contraseña
     * @param username
     * @param password
     * @return Usuario
     */
    public Usuario buscar(String username, String password) {
        return buscar(raiz, new Usuario(username, password, null));
    }

    /**
     * Función para encontrar un usuario con un objeto de clase Usuario
     * @param nodo
     * @param usuario
     * @return Usuario
     */
    private Usuario buscar(NodoArbol nodo, Usuario usuario) {
        if (nodo == null) {
            return null;
        }

        if (nodo.usuario.getUsername().equals(usuario.getUsername()) &&
                nodo.usuario.getPassword().equals(usuario.getPassword())) {
            return nodo.usuario;
        }

        if (nodo.usuario.getUsername().compareTo(usuario.getUsername()) > 0) {
            return buscar(nodo.izquierdo, usuario);
        } else {
            return buscar(nodo.derecho, usuario);
        }
    }

    /**
     * Getter de la raíz
     * @return
     */
    public NodoArbol getRaiz() {
        return raiz;
    }

    /**
     * Función para eliminar un nodo del árbol
     * @param d
     * @return
     */
    public boolean eliminar(String d){
        NodoArbol auxiliar = raiz;
        NodoArbol padre = raiz;
        boolean esHijoIzquierdo = true;
        if(buscarBoo(this.raiz, d)){
            if(auxiliar.izquierdo == null && auxiliar.derecho==null){
                if(auxiliar==raiz){
                    raiz = null;
                }else if(esHijoIzquierdo){
                    padre.izquierdo = null;
                }else{
                    padre.derecho = null;
                }
            }else if(auxiliar.derecho == null){
                if(auxiliar==raiz){
                    raiz = auxiliar.izquierdo;
                }else if(esHijoIzquierdo){
                    padre.izquierdo = auxiliar.izquierdo;
                }else{
                    padre.derecho = auxiliar.izquierdo;
                }
            }else if(auxiliar.izquierdo == null){
                if(auxiliar==raiz){
                    raiz = auxiliar.derecho;
                }else if(esHijoIzquierdo){
                    padre.derecho = auxiliar.derecho;
                }else{
                    padre.izquierdo = auxiliar.derecho;
                }
            }else{
                NodoArbol reemplazo = obtenerNodoReemplazo(auxiliar);
                if(auxiliar == raiz){
                    raiz = reemplazo;
                }else if(esHijoIzquierdo){
                    padre.izquierdo = reemplazo;
                }else{
                    padre.derecho = reemplazo;
                }
                reemplazo.izquierdo=auxiliar.izquierdo;
            }
            return true;
        }else{
            return false;
        }
    }

    /**
     * Método encargado de devolvernos el nodo reemplazo
     * @param nodoReem
     * @return NodoArbol
     */
    public NodoArbol obtenerNodoReemplazo(NodoArbol nodoReem){
        NodoArbol reemplazarPadre = nodoReem;
        NodoArbol reemplazo = nodoReem;
        NodoArbol auxiliar = nodoReem.derecho;
        while(auxiliar!=null){
            reemplazarPadre = reemplazo;
            reemplazo=auxiliar;
            auxiliar=auxiliar.izquierdo;
        }if(reemplazo!=nodoReem.derecho){
            reemplazarPadre.izquierdo=reemplazo.derecho;
            reemplazo.derecho = nodoReem.derecho;
        }
        System.out.println("El nodo reemplazo es: "+ reemplazo);
        return reemplazo;
    }

    /**
     * Método para buscar un usuario a partir del username (en string)
     * @param nodo
     * @param usuario
     * @return Usuario
     */
    public Usuario buscar(NodoArbol nodo, String usuario) {
        if (nodo == null) {
            return null;
        }

        if (usuario.equals(nodo.usuario.getUsername())){
            return nodo.usuario;
        }

        if (nodo.usuario.getUsername().compareTo(usuario) > 0) {
            return buscar(nodo.izquierdo, usuario);
        } else {
            return buscar(nodo.derecho, usuario);
        }
    }

    /**
     * Función para asegurarse que el nodo a buscar exista
     * @param nodo
     * @param usuario
     * @return boolean
     */
    public boolean buscarBoo(NodoArbol nodo, String usuario) {
        if (nodo == null) {
            return false;
        }

        if (usuario.equals(nodo.usuario.getUsername())){
            return true;
        }

        if (nodo.usuario.getUsername().compareTo(usuario) > 0) {
            return buscarBoo(nodo.izquierdo, usuario);
        } else {
            return buscarBoo(nodo.derecho, usuario);
        }
    }

    /**
     * Clase NodoArbol
     */
    private class NodoArbol {
        private Usuario usuario;
        private NodoArbol izquierdo;
        private NodoArbol derecho;

        /**
         * Constructor de la clase NodoArbol
         * @param usuario
         */
        public NodoArbol(Usuario usuario) {
            this.usuario = usuario;
            this.izquierdo = null;
            this.derecho = null;
        }
    }
}
