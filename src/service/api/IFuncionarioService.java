package service.api;

import java.util.List;

import exception.AuthException;
import model.Funcionario;

public interface IFuncionarioService {
	
	
	//Interface do FuncionarioService, registra os métodos que devemos ter na FuncionarioService.

    void criarFuncionario(Funcionario funcionario) throws AuthException, Exception;
    void alterarFuncionario(Funcionario funcionario) throws AuthException, Exception; 
    void deletarFuncionario(int matricula) throws AuthException, Exception;      
    Funcionario buscarPorMatricula(int matricula) throws Exception;            
    List<Funcionario> listarTodos();
    List<Funcionario> listarFuncionariosDoMeuDepartamento() throws AuthException, Exception;

}