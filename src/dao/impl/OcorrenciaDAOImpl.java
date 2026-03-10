package dao.impl;
import model.Departamento;
import model.Diretor;
import model.Funcionario;
import model.Gerente;
import model.Ocorrencia;
import util.ConnectionFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import dao.api.IOcorrenciaDAO;

public class OcorrenciaDAOImpl implements IOcorrenciaDAO {

    @Override
    public void salvar(Ocorrencia ocorrencia) throws SQLException {
        String sql = "INSERT INTO ocorrencias (descricao, data_ocorrencia, id_departamento_reportante, " +
                     "matricula_funcionario_alocado, data_limite_solucao, status_temporario, status_definitivo) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, ocorrencia.getDescricao());
            stmt.setDate(2, Date.valueOf(ocorrencia.getDataOcorrencia()));
            stmt.setInt(3, ocorrencia.getDepartamentoReportante().getCodigo());
            stmt.setInt(4, ocorrencia.getFuncionarioAlocado().getMatricula());
            stmt.setDate(5, Date.valueOf(ocorrencia.getDataLimiteSolucao()));
            stmt.setString(6, ocorrencia.getStatusTemporario());
            stmt.setString(7, ocorrencia.getStatusDefinitivo());

            stmt.executeUpdate();
        }
    }

    @Override
    public void atualizarStatus(int numeroOcorrencia, String statusTemporario, String statusDefinitivo) throws SQLException {
        String sql = "UPDATE ocorrencias SET status_temporario = ?, status_definitivo = ? WHERE numero = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, statusTemporario);
            stmt.setString(2, statusDefinitivo);
            stmt.setInt(3, numeroOcorrencia);
            stmt.executeUpdate();
        }
    }

    @Override
    public Ocorrencia buscarPorNumero(int numero) throws SQLException {
        String sql = getSelectOcorrenciaCompletaSQL() + " WHERE o.numero = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, numero);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToOcorrencia(rs);
                }
            }
        }
        return null;
    }

    @Override
    public List<Ocorrencia> buscarTodos() throws SQLException {
        List<Ocorrencia> ocorrencias = new ArrayList<>();
        String sql = getSelectOcorrenciaCompletaSQL() + " ORDER BY o.data_ocorrencia DESC";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                ocorrencias.add(mapRowToOcorrencia(rs));
            }
        }
        return ocorrencias;
    }

    @Override
    public List<Ocorrencia> buscarPorFuncionario(int matricula) throws SQLException {
        List<Ocorrencia> ocorrencias = new ArrayList<>();
        String sql = getSelectOcorrenciaCompletaSQL() + " WHERE o.matricula_funcionario_alocado = ? ORDER BY o.data_ocorrencia DESC";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, matricula);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ocorrencias.add(mapRowToOcorrencia(rs));
                }
            }
        }
        return ocorrencias;
    }
    
    @Override
    public List<Ocorrencia> buscarPorDepartamentoReportante(int idDepartamento) throws SQLException {
        List<Ocorrencia> ocorrencias = new ArrayList<>();
        String sql = getSelectOcorrenciaCompletaSQL() + " WHERE o.id_departamento_reportante = ? ORDER BY o.data_ocorrencia DESC";
        
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idDepartamento);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ocorrencias.add(mapRowToOcorrencia(rs));
                }
            }
        }
        return ocorrencias;
    }

    /**
     * Retorna a query SQL base para buscar ocorrências com todos os dados relacionados.
     * Usamos apelidos (aliases) para evitar ambiguidade nos nomes das colunas.
     */
    private String getSelectOcorrenciaCompletaSQL() {
        return "SELECT " +
                "o.numero, o.descricao, o.data_ocorrencia, o.data_limite_solucao, o.status_temporario, o.status_definitivo, " +
                "dr.codigo AS depto_rep_codigo, dr.nome AS depto_rep_nome, dr.descricao AS depto_rep_desc, dr.status AS depto_rep_status, " +
                "fa.matricula AS func_alo_matricula, fa.nome AS func_alo_nome, fa.status AS func_alo_status, fa.tipo_funcionario, " +
                "da.codigo AS func_depto_codigo, da.nome AS func_depto_nome " +
                "FROM ocorrencias o " +
                "LEFT JOIN departamentos dr ON o.id_departamento_reportante = dr.codigo " +
                "LEFT JOIN funcionarios fa ON o.matricula_funcionario_alocado = fa.matricula " +
                "LEFT JOIN departamentos da ON fa.id_departamento = da.codigo";
    }

    /**
     * Mapeia uma linha do ResultSet para um objeto Ocorrencia completo.
     */
    private Ocorrencia mapRowToOcorrencia(ResultSet rs) throws SQLException {
        Ocorrencia ocorrencia = new Ocorrencia();
        ocorrencia.setNumero(rs.getInt("numero"));
        ocorrencia.setDescricao(rs.getString("descricao"));
        ocorrencia.setDataOcorrencia(rs.getDate("data_ocorrencia").toLocalDate());
        ocorrencia.setDataLimiteSolucao(rs.getDate("data_limite_solucao").toLocalDate());
        ocorrencia.setStatusTemporario(rs.getString("status_temporario"));
        ocorrencia.setStatusDefinitivo(rs.getString("status_definitivo"));

        // Mapeia o Departamento Reportante
        Departamento deptoReportante = new Departamento();
        deptoReportante.setCodigo(rs.getInt("depto_rep_codigo"));
        deptoReportante.setNome(rs.getString("depto_rep_nome"));
        deptoReportante.setDescricao(rs.getString("depto_rep_desc"));
        deptoReportante.setStatus(rs.getString("depto_rep_status"));
        ocorrencia.setDepartamentoReportante(deptoReportante);

        // Mapeia o Funcionário Alocado (e seu departamento)
        Funcionario funcAlocado;
        String tipoFunc = rs.getString("tipo_funcionario");
        if ("DIRETOR".equals(tipoFunc)) {
            funcAlocado = new Diretor();
        } else if ("GERENTE".equals(tipoFunc)) {
            funcAlocado = new Gerente();
        } else {
            funcAlocado = new Funcionario();
        }
        
        funcAlocado.setMatricula(rs.getInt("func_alo_matricula"));
        funcAlocado.setNome(rs.getString("func_alo_nome"));
        funcAlocado.setStatus(rs.getString("func_alo_status"));
        
        // Mapeia o departamento do funcionário alocado
        int funcDeptoCodigo = rs.getInt("func_depto_codigo");
        if (!rs.wasNull()) {
            Departamento deptoDoFunc = new Departamento();
            deptoDoFunc.setCodigo(funcDeptoCodigo);
            deptoDoFunc.setNome(rs.getString("func_depto_nome"));
            funcAlocado.setDepartamento(deptoDoFunc);
        }
        
        ocorrencia.setFuncionarioAlocado(funcAlocado);

        return ocorrencia;
    }
}