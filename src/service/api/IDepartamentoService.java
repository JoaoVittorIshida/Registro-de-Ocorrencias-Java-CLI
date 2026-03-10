package service.api;

import java.util.List;

import exception.AuthException;
import model.Departamento;

public interface IDepartamentoService {
	
	//Interface do DepartamentoService, registra os métodos que devemos ter na DepartamentoService.
	
    void criarDepartamento(Departamento departamento) throws AuthException, Exception;
    void alterarDepartamento(Departamento departamento) throws AuthException, Exception;
    void deletarDepartamento(int codigo) throws AuthException, Exception;
    Departamento buscarPorId(int id) throws Exception;
    List<Departamento> listarDepartamentos();
}
