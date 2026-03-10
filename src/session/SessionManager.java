package session;

import model.Funcionario;

public class SessionManager {

	
	//SessionManager é a nossa classe que guarda o funcionário que está logado.
	//Possui alguns métodos que retornam o próprio funcionário, sendo essencial para validações nos services.
	
    private static Funcionario usuarioLogado;

    public static Funcionario getUsuarioLogado() {
        return usuarioLogado;
    }

    public static void setUsuarioLogado(Funcionario usuario) {
        usuarioLogado = usuario;
    }

    public static void clearSession() {
        usuarioLogado = null;
    }

    public static boolean hasSession() {
        return usuarioLogado != null;
    }
}
