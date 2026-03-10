package model;

public class Departamento {

	//Model de funcionário.
	//Além dos parâmetros, contém um método que retorna em string formatada algumas informações do objeto.
	
    private int codigo;
    private String nome;
    private String descricao;
    private String status;

    public Departamento() { }

    // Getters e Setters
    public int getCodigo() { return codigo; }
    public void setCodigo(int codigo) { this.codigo = codigo; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    @Override
    public String toString() {
        return String.format("Depto Cód: %d, Nome: %s, Status: %s", codigo, nome, status);
    }
}
