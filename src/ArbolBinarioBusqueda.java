public class ArbolBinarioBusqueda {
    private NodoArbol raiz;

    public ArbolBinarioBusqueda() {
        raiz = null;
    }

    public void insertar(Usuario usuario) {
        if (raiz == null) {
            raiz = new NodoArbol(usuario);
        } else {
            insertar(raiz, usuario);
        }
    }

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



    public Usuario buscar(String username, String password) {
        return buscar(raiz, new Usuario(username, password, null));
    }

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

    private class NodoArbol {
        private Usuario usuario;
        private NodoArbol izquierdo;
        private NodoArbol derecho;

        public NodoArbol(Usuario usuario) {
            this.usuario = usuario;
            this.izquierdo = null;
            this.derecho = null;
        }
    }
}
