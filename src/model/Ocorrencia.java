package model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

//Model de Ocorrencia.
//Além dos parâmetros, contém um método que retorna em string formatada algumas informações do objeto.

public class Ocorrencia {

    private int numero;
    private String descricao;
    private LocalDate dataOcorrencia;
    private LocalDate dataLimiteSolucao;
    private String statusTemporario;
    private String statusDefinitivo;
    private Departamento departamentoReportante;
    private Funcionario funcionarioAlocado;

    public Ocorrencia() { }

    // Getters e Setters
    public int getNumero() { return numero; }
    public void setNumero(int numero) { this.numero = numero; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public LocalDate getDataOcorrencia() { return dataOcorrencia; }
    public void setDataOcorrencia(LocalDate dataOcorrencia) { this.dataOcorrencia = dataOcorrencia; }
    public LocalDate getDataLimiteSolucao() { return dataLimiteSolucao; }
    public void setDataLimiteSolucao(LocalDate dataLimiteSolucao) { this.dataLimiteSolucao = dataLimiteSolucao; }
    public String getStatusTemporario() { return statusTemporario; }
    public void setStatusTemporario(String statusTemporario) { this.statusTemporario = statusTemporario; }
    public String getStatusDefinitivo() { return statusDefinitivo; }
    public void setStatusDefinitivo(String statusDefinitivo) { this.statusDefinitivo = statusDefinitivo; }
    public Departamento getDepartamentoReportante() { return departamentoReportante; }
    public void setDepartamentoReportante(Departamento departamentoReportante) { this.departamentoReportante = departamentoReportante; }
    public Funcionario getFuncionarioAlocado() { return funcionarioAlocado; }
    public void setFuncionarioAlocado(Funcionario funcionarioAlocado) { this.funcionarioAlocado = funcionarioAlocado; }
    
    @Override
    public String toString() {
    	
    	//Método interno da Model que imprime seus valores num modelo padrão.
    	//Retorna a String formatada para ser exibida.
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        
        String deptoNome = (departamentoReportante != null && departamentoReportante.getNome() != null) ? departamentoReportante.getNome() : "N/A";
        String funcNome = (funcionarioAlocado != null && funcionarioAlocado.getNome() != null) ? funcionarioAlocado.getNome() : "N/A";

        return String.format(
            "Ocorrência Nº: %d\n" +
            "  Descrição: %s\n" +
            "  Data: %s\n" +
            "  Depto Reportante: %s\n" +
            "  Funcionário Alocado: %s\n" +
            "  Data Limite: %s\n" +
            "  Status (Funcionário): %s\n" +
            "  Status (Gerente): %s",
            numero,
            descricao,
            dataOcorrencia.format(formatter),
            deptoNome,
            funcNome,
            dataLimiteSolucao.format(formatter),
            statusTemporario,
            statusDefinitivo
        );
    }
}
