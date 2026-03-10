package dao.api;

import java.sql.SQLException;
import java.util.List;

import model.Ocorrencia;

public interface IOcorrenciaDAO {
	
	//Interface da nossa classe que se comunica com o banco quando se trata de uma Ocorrencia.	

	
    void salvar(Ocorrencia ocorrencia) throws SQLException;
    void atualizarStatus(int numeroOcorrencia, String statusTemporario, String statusDefinitivo) throws SQLException;
    Ocorrencia buscarPorNumero(int numero) throws SQLException;
    List<Ocorrencia> buscarTodos() throws SQLException;
    List<Ocorrencia> buscarPorFuncionario(int matricula) throws SQLException;
    List<Ocorrencia> buscarPorDepartamentoReportante(int idDepartamento) throws SQLException;

}
