package dao.impl;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import dao.api.IFuncionarioDAO;
import model.Departamento;
import model.Diretor;
import model.Funcionario;
import model.Gerente;
import util.ConnectionFactory;

public class FuncionarioDAOImpl implements IFuncionarioDAO {

    @Override
    public void salvar(Funcionario funcionario) throws SQLException {
    	
    	//Método que insere um funcionário recebido.
    	
        String sql = "INSERT INTO funcionarios (nome, id_departamento, status, tipo_funcionario) VALUES (?, ?, ?, ?)";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, funcionario.getNome());
            stmt.setInt(2, funcionario.getDepartamento() != null ? funcionario.getDepartamento().getCodigo() : null);
            stmt.setString(3, funcionario.getStatus());
            stmt.setString(4, getTipo(funcionario));
            stmt.executeUpdate();
        }
    }

    @Override
    public void atualizar(Funcionario funcionario) throws SQLException {
    	
    	//Método que atualiza um funcionário especifico,
    	
        String sql = "UPDATE funcionarios SET nome = ?, id_departamento = ?, status = ? WHERE matricula = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, funcionario.getNome());
            stmt.setInt(2, funcionario.getDepartamento() != null ? funcionario.getDepartamento().getCodigo() : null);
            stmt.setString(3, funcionario.getStatus());
            stmt.setInt(4, funcionario.getMatricula());
            stmt.executeUpdate();
        }
    }

    @Override
    public Funcionario buscarPorMatricula(int matricula) throws SQLException {
    	
    	//Realiza a busca de funcionário de acordo com a matricula.
    	//Retorna o funcionario especifico.
    	
        String sql = "SELECT f.*, d.codigo as depto_codigo, d.nome as depto_nome, d.descricao as depto_desc, d.status as depto_status " +
                     "FROM funcionarios f LEFT JOIN departamentos d ON f.id_departamento = d.codigo " +
                     "WHERE f.matricula = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, matricula);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToFuncionario(rs);
                }
            }
        }
        return null;
    }

    @Override
    public List<Funcionario> buscarTodos() throws SQLException {
    	
    	//Método que busca todos os funcionários do sistema.
    	//Retorna lista de funcionários.
    	
        List<Funcionario> funcionarios = new ArrayList<>();
        String sql = "SELECT f.*, d.codigo as depto_codigo, d.nome as depto_nome, d.descricao as depto_desc, d.status as depto_status " +
                     "FROM funcionarios f LEFT JOIN departamentos d ON f.id_departamento = d.codigo ORDER BY f.nome";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                funcionarios.add(mapRowToFuncionario(rs));
            }
        }
        return funcionarios;
    }
    
    @Override
    public List<Funcionario> buscarPorDepartamento(int idDepartamento) throws SQLException {
    	
    	//Método de busca de funcionarios pelo ID de um departamento.
    	//Recebe o ID do departamento e retorna funcionarios naquele ID de departamento.
    	
        List<Funcionario> funcionarios = new ArrayList<>();
        String sql = "SELECT f.*, d.codigo as depto_codigo, d.nome as depto_nome, d.descricao as depto_desc, d.status as depto_status " +
                     "FROM funcionarios f LEFT JOIN departamentos d ON f.id_departamento = d.codigo " +
                     "WHERE f.id_departamento = ? ORDER BY f.nome";
        
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idDepartamento);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    funcionarios.add(mapRowToFuncionario(rs));
                }
            }
        }
        return funcionarios;
    }

    @Override
    public void deletar(int matricula) throws SQLException {
    	
    	//Método que deleta os dados de um funcionario.
    	//Recebe o ID a ser excluido.
    	
        String sql = "DELETE FROM funcionarios WHERE matricula = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, matricula);
            stmt.executeUpdate();
        }
    }
    
    private String getTipo(Funcionario f) {
    	
    	//Retorna o tipo do funcionario em String dependendo do tipo de classe.
    	
        if (f instanceof Diretor) return "DIRETOR";
        if (f instanceof Gerente) return "GERENTE";
        return "COMUM";
    }
    
    

    private Funcionario mapRowToFuncionario(ResultSet rs) throws SQLException {
    	
    	//Método que recebe o resultado do SQL e converte num funcionário.
    	
        String tipo = rs.getString("tipo_funcionario");
        Funcionario func;

        if ("DIRETOR".equals(tipo)) {
            func = new Diretor();
        } else if ("GERENTE".equals(tipo)) {
            func = new Gerente();
        } else {
            func = new Funcionario();
        }

        func.setMatricula(rs.getInt("matricula"));
        func.setNome(rs.getString("nome"));
        func.setStatus(rs.getString("status"));

        int deptoCodigo = rs.getInt("depto_codigo");
        if (!rs.wasNull()) {
            Departamento depto = new Departamento();
            depto.setCodigo(deptoCodigo);
            depto.setNome(rs.getString("depto_nome"));
            depto.setDescricao(rs.getString("depto_desc"));
            depto.setStatus(rs.getString("depto_status"));
            func.setDepartamento(depto);
        }

        return func;
    }
}
