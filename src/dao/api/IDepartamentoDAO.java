package dao.api;

import java.sql.SQLException;
import java.util.List;

import model.Departamento;

public interface IDepartamentoDAO {
	
	//Interface da nossa classe que se comunica com o banco quando se trata de departamento.	
	
    void salvar(Departamento departamento) throws SQLException;
    void atualizar(Departamento departamento) throws SQLException;
    void deletar(int codigo) throws SQLException;
    Departamento buscarPorId(int id) throws SQLException;
    List<Departamento> buscarTodos() throws SQLException;
    
}
