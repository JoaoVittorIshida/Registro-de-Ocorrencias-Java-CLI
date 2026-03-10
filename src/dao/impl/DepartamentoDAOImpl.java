package dao.impl;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import dao.api.IDepartamentoDAO;
import model.Departamento;
import util.ConnectionFactory;

public class DepartamentoDAOImpl implements IDepartamentoDAO {

    @Override
    public void salvar(Departamento departamento) throws SQLException {
    	
    	//Método de inserção de dados de departamentos.
    	//Apenas recebe o que deve ser inserido, validações são executadas pelo Service.
    	
        String sql = "INSERT INTO departamentos (nome, descricao, status) VALUES (?, ?, ?)";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, departamento.getNome());
            stmt.setString(2, departamento.getDescricao());
            stmt.setString(3, departamento.getStatus());
            stmt.executeUpdate();
        }
    }

    @Override
    public void atualizar(Departamento departamento) throws SQLException {
    	
    	//Método de atualização de dados de departamentos.
    	//Apenas recebe o que deve ser atualizado, validações são executadas pelo Service.
    	
        String sql = "UPDATE departamentos SET nome = ?, descricao = ?, status = ? WHERE codigo = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, departamento.getNome());
            stmt.setString(2, departamento.getDescricao());
            stmt.setString(3, departamento.getStatus());
            stmt.setInt(4, departamento.getCodigo());
            stmt.executeUpdate();
        }
    }

    @Override
    public void deletar(int codigo) throws SQLException {
    	
    	//Método de exclusão de dados de departamentos.
    	//Apenas recebe o que deve ser excluido, validações são executadas pelo Service.
    	
        String sql = "DELETE FROM departamentos WHERE codigo = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, codigo);
            stmt.executeUpdate();
        }
    }

    @Override
    public Departamento buscarPorId(int id) throws SQLException {
    	
    	//Método de busca de dados de departamentos.
    	//Recebe o ID a ser buscado.
    	
        String sql = "SELECT * FROM departamentos WHERE codigo = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToDepartamento(rs);
                }
            }
        }
        return null;
    }

    @Override
    public List<Departamento> buscarTodos() throws SQLException {
    	
    	//Método que busca todos os departamentos.
    	//Retorna uma lista de departamentos.
    	
        List<Departamento> departamentos = new ArrayList<>();
        String sql = "SELECT * FROM departamentos ORDER BY nome";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                departamentos.add(mapRowToDepartamento(rs));
            }
        }
        return departamentos;
    }

    private Departamento mapRowToDepartamento(ResultSet rs) throws SQLException {
    	
    	//Traduz o que veio do banco e mapeia para um objeto de departamento, para ser retornado.
    	
        Departamento depto = new Departamento();
        depto.setCodigo(rs.getInt("codigo"));
        depto.setNome(rs.getString("nome"));
        depto.setDescricao(rs.getString("descricao"));
        depto.setStatus(rs.getString("status"));
        return depto;
    }
}
