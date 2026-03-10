package dao.api;

import java.sql.SQLException;
import java.util.List;

import model.Funcionario;

public interface IFuncionarioDAO {
	
	//Interface da nossa classe que se comunica com o banco quando se trata de um Funcionario.	

	
    void salvar(Funcionario funcionario) throws SQLException;
    void atualizar(Funcionario funcionario) throws SQLException;
    void deletar(int matricula) throws SQLException; // <- NOVO MÉTODO
    Funcionario buscarPorMatricula(int matricula) throws SQLException;
    List<Funcionario> buscarTodos() throws SQLException;
    List<Funcionario> buscarPorDepartamento(int idDepartamento) throws SQLException;

}
