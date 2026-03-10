package exception;

public class AuthException extends Exception {
	
	// Esta é uma classe de Erro customizada.
	// A gente usa ela SÓ quando um usuário tenta fazer algo que não tem permissão.
	// Por exemplo, um funcionário comum tentando criar um departamento.
	//
	// Isso ajuda a dar mensagens de erro mais claras no programa, como "Acesso Negado",
	// e a tratar erros de permissão de forma diferente de outros tipos de erro.
	
    public AuthException(String message) {
        super(message);
    }
}
